package com.github.pheasn.blockown.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

import com.github.pheasn.blockown.BlockOwn;

public class L_BlockBurn implements Listener {
	private BlockOwn plugin;

	public L_BlockBurn(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if(!plugin.isOwningPlugin()) return;
		if (plugin.getOwning().getOwner(event.getBlock()) != null) {
			plugin.getOwning().removeOwner(event.getBlock());
		}
	}
}
