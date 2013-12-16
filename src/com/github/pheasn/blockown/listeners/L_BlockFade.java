package com.github.pheasn.blockown.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;

import com.github.pheasn.blockown.BlockOwn;

public class L_BlockFade implements Listener {
	private BlockOwn plugin;

	public L_BlockFade(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockFade(BlockFadeEvent event) {
		if(!plugin.isOwningPlugin()) return;
		if (plugin.getOwning().getOwner(event.getBlock()) != null) {
			if (event.getNewState().getType().equals(Material.AIR)) {
				plugin.getOwning().removeOwner(event.getBlock());
			}
		}
	}
}
