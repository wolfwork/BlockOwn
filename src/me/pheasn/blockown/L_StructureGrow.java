package me.pheasn.blockown;

import org.bukkit.OfflinePlayer;
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
		OfflinePlayer offlinePlayer = null;
		for (BlockState blockState : event.getBlocks()) {
			if (plugin.owning.getOwner(blockState.getBlock()) != null) {
				offlinePlayer = plugin.owning.getOwner(blockState.getBlock());
			}
		}
		if (offlinePlayer != null) {
			for (BlockState blockState : event.getBlocks()) {
				plugin.owning.setOwner(blockState.getBlock(), offlinePlayer);
			}
		}
	}
}
