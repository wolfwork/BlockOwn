package me.pheasn.blockown.listeners;

import java.util.ArrayList;

import me.pheasn.Material;
import me.pheasn.OfflineUser;
import me.pheasn.Region;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class L_BlockPlace_Check implements Listener {
	private BlockOwn plugin;

	public L_BlockPlace_Check(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (Setting.PERMISSION_NEEDED_OWN_PLACE.getBoolean(plugin) && !event.getPlayer().hasPermission(Permission.OWN_PLACE.toString())) {
			if(!(event.getPlayer().getGameMode()==GameMode.CREATIVE && event.getPlayer().hasPermission(Permission.OWN_PLACE_CREATIVE.toString()))){
				return;
			}
		}
		Block [] blocks = {event.getBlockPlaced()};
		if(Setting.ENABLE_OWNING.getBoolean(plugin)){
			blocks = new Block[0];
		}
		ArrayList<org.bukkit.Material> doubleHeight = new ArrayList<org.bukkit.Material>();
		doubleHeight.add(org.bukkit.Material.WOODEN_DOOR);
		doubleHeight.add(org.bukkit.Material.IRON_DOOR);
		//TODO add high flowers
		if(doubleHeight.contains(event.getBlockPlaced().getType())){
			Block [] allblocks = {event.getBlockPlaced(), event.getBlock().getWorld().getBlockAt(event.getBlockPlaced().getLocation().add(0, 0, 1))};
			blocks = allblocks;
		}
		for(Block block : blocks){
			plugin.getOwning().setOwner(block, OfflineUser.getInstance(event.getPlayer()));
		}
		if(!event.getPlayer().hasPermission(Permission.ADMIN.toString()) && !event.getPlayer().hasPermission(Permission.IGNORE_PROTECTION.toString())){
			CheckThread thread = new CheckThread(plugin, this, event.getBlockPlaced(), event.getPlayer());
			thread.offset = blocks.length-1;
			thread.start();
		}
	}

	protected synchronized void removeBlock(reverseBlockTask task) {
		plugin.getServer().getScheduler().runTask(plugin, task);
	}

}

class CheckThread extends Thread {
	private BlockOwn plugin;
	private Block block;
	private Player player;
	private L_BlockPlace_Check listener;
	protected int offset = 0;

	protected CheckThread(BlockOwn plugin, L_BlockPlace_Check listener,	Block block, Player player) {
		this.plugin = plugin;
		this.block = block;
		this.player = player;
		this.listener = listener;
	}

	@Override
	public void run() {
		int radius = Setting.PROTECTION_RADIUS_RADIUS.getInt(plugin);
		Region region = new Region(block.getLocation().subtract(radius, radius, radius), radius *2+1, radius *2+1, radius *2 + 1 + offset);
		OfflineUser owner;
		for(Block block : region.getBlocks()){
			if ((owner = plugin.getOwning().getOwner(block)) != null && !plugin.getPlayerSettings().canAccess(Material.getMaterial(block.getType()), OfflineUser.getInstance(player), owner)) {
				listener.removeBlock(new reverseBlockTask(plugin, this.block, player));
				return;
			}		
		}
	}
}

class reverseBlockTask implements Runnable {
	private Block block;
	private BlockOwn plugin;
	private Player player;

	public reverseBlockTask(BlockOwn plugin, Block block, Player player) {
		this.plugin = plugin;
		this.block = block;
		this.player = player;
	}

	@Override
	public void run() {
		if (!block.getType().equals(org.bukkit.Material.FIRE)) {
			ItemStack items = null;
			if (block.getType().equals(org.bukkit.Material.WOOL)) {
				for (ItemStack itemStack : block.getDrops()) {
					items = itemStack;
					break;
				}
			} else {
				items = new ItemStack(block.getType());
			}
			player.getInventory().addItem(items);
		}
		block.setType(org.bukkit.Material.AIR);
		plugin.getOwning().removeOwner(block);
	}

}
