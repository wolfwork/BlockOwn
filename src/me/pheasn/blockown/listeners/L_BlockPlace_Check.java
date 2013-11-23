package me.pheasn.blockown.listeners;

import me.pheasn.Material;
import me.pheasn.OfflineUser;
import me.pheasn.User;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
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
		if (Setting.PERMISSION_NEEDED_OWN_PLACE.getBoolean(plugin)
				&& !event.getPlayer().hasPermission(Permission.OWN_PLACE.toString())) {
			if(!(event.getPlayer().getGameMode()==GameMode.CREATIVE && event.getPlayer().hasPermission(Permission.OWN_PLACE_CREATIVE.toString()))){
				return;
			}
		}
		plugin.getOwning().setOwner(event.getBlockPlaced(),	event.getPlayer());
		new CheckThread(plugin, this, event.getBlockPlaced(), event.getPlayer()).start();
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

	protected CheckThread(BlockOwn plugin, L_BlockPlace_Check listener,
			Block block, Player player) {
		this.plugin = plugin;
		this.block = block;
		this.player = player;
		this.listener = listener;
	}

	@Override
	public void run() {
		int radius = Setting.PROTECTION_RADIUS.getInt(plugin);
		Location end = block.getLocation().add(radius, radius, radius);
		Location start = block.getLocation().subtract(radius, radius, radius);
		World world = block.getWorld();
		OfflineUser owner;
		Block curBlock;
		for (int x = start.getBlockX(); x <= end.getBlockX(); x++) {
			for (int y = start.getBlockY(); y <= end.getBlockY(); y++) {
				for (int z = start.getBlockZ(); z <= end.getBlockZ(); z++) {
					if ((owner = OfflineUser.getInstance(plugin.getOwning().getOwner((curBlock = world.getBlockAt(x, y, z))))) != null
							&& !plugin.getPlayerSettings().canAccess(Material.getMaterial(curBlock.getType()), User.getInstance(player), owner)) {
						listener.removeBlock(new reverseBlockTask(
								plugin, block, player));
						return;
					}
				}
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
