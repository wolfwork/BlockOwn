package me.pheasn.blockown.listeners;

import me.pheasn.blockown.BlockOwn;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

public class L_BlockBurn implements Listener {
	private BlockOwn plugin;

	public L_BlockBurn(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if (plugin.getOwning().getOwner(event.getBlock()) != null) {
			plugin.getOwning().removeOwner(event.getBlock());
		}
	}
}
