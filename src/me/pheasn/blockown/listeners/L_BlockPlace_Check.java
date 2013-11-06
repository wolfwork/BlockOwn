package me.pheasn.blockown.listeners;

import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class L_BlockPlace_Check implements Listener {
	private BlockOwn plugin;

	public L_BlockPlace_Check(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (Setting.PERMISSION_NEEDED_FOR_OWNING.getBoolean(plugin)
				&& !event.getPlayer().hasPermission(Permission.OWN.toString())) {
		} else {
			int radius = Setting.RADIUS_BLOCK_PLACE_DENIED.getInt(plugin);
			Location start = event.getBlockPlaced().getLocation()
					.add(radius, radius, radius);
			World world = event.getBlockPlaced().getWorld();
			OfflinePlayer owner = null;
			for (int x = start.getBlockX(); x >= start.getBlockX()
					- (radius * 2); x--) {
				for (int y = start.getBlockY(); y >= start.getBlockY()
						- (radius * 2); y--) {
					for (int z = start.getBlockZ(); z >= start.getBlockZ()
							- (radius * 2); z--) {
						if ((owner = plugin.owning.getOwner(world.getBlockAt(x,
								y, z))) != null) {
							if (!owner.getName().equalsIgnoreCase(
									event.getPlayer().getName())) {
								event.setCancelled(true);
								return;
							}
						}
					}
				}
			}
			plugin.getOwning().setOwner(event.getBlockPlaced(),
					event.getPlayer());
		}
	}
}
