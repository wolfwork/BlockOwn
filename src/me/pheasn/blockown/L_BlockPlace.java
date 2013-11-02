package me.pheasn.blockown;

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
		plugin.getOwning().setOwner(event.getBlockPlaced(), event.getPlayer());
	}
}
