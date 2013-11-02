package me.pheasn.blockown;

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
		}
	}
}
