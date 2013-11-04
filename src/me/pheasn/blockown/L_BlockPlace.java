package me.pheasn.blockown;

import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class L_BlockPlace implements Listener {
	private BlockOwn plugin;

	public L_BlockPlace(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (Setting.PERMISSION_NEEDED_FOR_OWNING.getBoolean(plugin)
				&& !event.getPlayer().hasPermission(Permission.OWN.toString())) {
		} else {
			plugin.getOwning().setOwner(event.getBlockPlaced(),
					event.getPlayer());
		}
	}
}
