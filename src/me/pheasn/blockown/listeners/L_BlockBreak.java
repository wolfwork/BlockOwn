package me.pheasn.blockown.listeners;

import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class L_BlockBreak implements Listener {
	private BlockOwn plugin;

	public L_BlockBreak(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (plugin.getOwning().getOwner(event.getBlock()) != null) {
			plugin.getOwning().removeOwner(event.getBlock());
			if (!Setting.ENABLE_OWNED_BLOCK_DROPS.getBoolean(plugin)) {
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
			}
		}

	}
}
