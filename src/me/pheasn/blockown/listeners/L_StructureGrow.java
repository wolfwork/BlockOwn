package me.pheasn.blockown.listeners;

import me.pheasn.OfflineUser;
import me.pheasn.blockown.BlockOwn;

import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

public class L_StructureGrow implements Listener {
	private BlockOwn plugin;

	public L_StructureGrow(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onStructureGrow(StructureGrowEvent event) {
		OfflineUser owner = null;
		for (BlockState blockState : event.getBlocks()) {
			if (plugin.getOwning().getOwner(blockState.getBlock()) != null) {
				owner = plugin.getOwning().getOwner(blockState.getBlock());
			}
		}
		if (owner != null) {
			for (BlockState blockState : event.getBlocks()) {
				plugin.getOwning().setOwner(blockState.getBlock(), owner);
			}
		}
	}
}
