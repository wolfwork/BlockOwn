package me.pheasn.blockown.listeners;

import me.pheasn.Material;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class L_BlockBreak_NoCheck implements Listener {
	private BlockOwn plugin;

	public L_BlockBreak_NoCheck(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (plugin.getOwning().getOwner(event.getBlock()) != null) {
			Block[] blocks = {event.getBlock()};
			if(Material.getDoubleHeightBlocks().contains(event.getBlock().getType())){
				blocks = new Block[] {event.getBlock(), event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(0, 1, 0))};
			}
			for(Block block : blocks){
				plugin.getOwning().removeOwner(block);
				if (!Setting.ENABLE_OWNED_BLOCK_DROPS.getBoolean(plugin)) {
					event.setCancelled(true);
					event.getBlock().setType(org.bukkit.Material.AIR);
				}
			}
		}

	}
}
