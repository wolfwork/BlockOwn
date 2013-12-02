package me.pheasn.blockown.listeners;

import java.util.ArrayList;
import java.util.Collection;

import me.pheasn.OfflineUser;
import me.pheasn.PheasnPlugin;
import me.pheasn.Region;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class L_BlockBreak_Check implements Listener {
	private BlockOwn plugin;
	protected Collection<ItemStack> drops;
	public L_BlockBreak_Check(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		drops = new ArrayList<ItemStack>();
		if (plugin.getOwning().getOwner(event.getBlock()) != null) {
			plugin.getOwning().removeOwner(event.getBlock());
			if (!Setting.ENABLE_OWNED_BLOCK_DROPS.getBoolean(plugin)) {
				event.setCancelled(true);
				event.getBlock().setType(org.bukkit.Material.AIR);
			}else{
				drops.addAll(event.getBlock().getDrops()); //TODO is always 0
			}
		}
		if(!event.getPlayer().hasPermission(Permission.ADMIN.toString()) && !event.getPlayer().hasPermission(Permission.IGNORE_PROTECTION.toString())){
			Thread checker = new CheckForProtectionThread(plugin, this, event.getBlock().getState(), event.getPlayer());
			checker.start();
		}
	}

	protected synchronized void replaceBlock(ReplaceBlockTask task) {
		plugin.getServer().getScheduler().runTask(plugin, task);
	}
}


class CheckForProtectionThread extends Thread {
	private BlockOwn plugin;
	private BlockState block;
	private Player player;
	private L_BlockBreak_Check listener;

	protected CheckForProtectionThread(BlockOwn plugin, L_BlockBreak_Check listener, BlockState block, Player player) {
		this.plugin = plugin;
		this.block = block;
		this.player = player;
		this.listener = listener;
	}

	@Override
	public void run() {
		int radius = Setting.PROTECTION_RADIUS_RADIUS.getInt(plugin);
		Region region = new Region(block.getLocation().subtract(radius, radius, radius), radius*2 + 1, radius*2 + 1, radius*2 + 1);
		OfflineUser user = OfflineUser.getInstance(player);
		for(Block block : region.getBlocks()){
			if(!plugin.getPlayerSettings().canAccess(user, block)){
				ReplaceBlockTask task = new ReplaceBlockTask(this.block, player, listener.drops);
				listener.replaceBlock(task);
			}
		}
	}
	
}

class ReplaceBlockTask implements Runnable {
	private BlockState block;
	private Player player;
	private Collection<ItemStack> drops;

	public ReplaceBlockTask(BlockState block, Player player, Collection<ItemStack> drops) {
		this.block = block;
		this.player = player;
		this.drops = drops;
	}

	@Override
	public void run() {
		block.getBlock().setType(block.getType());
		if(drops.size()>0){
			for(ItemStack drop : drops){
				try{
					player.getInventory().remove(drop);
				}catch(Exception e){	
				}
			}
		}
		PheasnPlugin.getInstance().con(ChatColor.RED, String.valueOf(drops.size())); //TODO delete
	}
}