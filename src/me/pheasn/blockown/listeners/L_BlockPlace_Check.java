package me.pheasn.blockown.listeners;

import java.util.Timer;
import java.util.TimerTask;

import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class L_BlockPlace_Check implements Listener {
	private BlockOwn plugin;
	private Timer timer;

	public L_BlockPlace_Check(BlockOwn plugin) {
		this.plugin = plugin;
		this.timer = new Timer();
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (Setting.PERMISSION_NEEDED_FOR_OWNING.getBoolean(plugin)
				&& !event.getPlayer().hasPermission(Permission.OWN.toString())) {
		} else {
			plugin.getOwning().setOwner(event.getBlockPlaced(),
					event.getPlayer());
			new CheckThread(plugin, this, event.getBlockPlaced(),
					event.getPlayer()).start();
		}
	}

	protected synchronized void scheduleBlockRemove(reverseBlockTask task) {
		timer.schedule(task, 10l);
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
		int radius = Setting.RADIUS_BLOCK_PLACE_DENIED.getInt(plugin);
		Location start = block.getLocation().add(radius, radius, radius);
		World world = block.getWorld();
		OfflinePlayer owner = null;
		for (int x = start.getBlockX(); x >= start.getBlockX() - (radius * 2); x--) {
			for (int y = start.getBlockY(); y >= start.getBlockY()
					- (radius * 2); y--) {
				for (int z = start.getBlockZ(); z >= start.getBlockZ()
						- (radius * 2); z--) {
					if ((owner = plugin.getOwning().getOwner(
							world.getBlockAt(x, y, z))) != null) {
						if (!owner.getName().equalsIgnoreCase(player.getName())) {
							listener.scheduleBlockRemove(new reverseBlockTask(
									plugin, block, player));
							return;
						}
					}
				}
			}
		}
	}
}

class reverseBlockTask extends TimerTask {
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
		if (!block.getType().equals(Material.FIRE)) {
			ItemStack items = null;
			if (block.getType().equals(Material.WOOL)) {
				for (ItemStack itemStack : block.getDrops()) {
					items = itemStack;
					break;
				}
			} else {
				items = new ItemStack(block.getType());
			}
			player.getInventory().addItem(items);
		}
		block.setType(Material.AIR);
		plugin.getOwning().removeOwner(block);
	}

}
