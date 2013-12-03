package me.pheasn.blockown.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.pheasn.OfflineUser;
import me.pheasn.blockown.BlockOwn;

import org.bukkit.Material;
import org.bukkit.block.Block;
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
		new CheckForOwnerThread(plugin, this, event.getBlocks()).start();
	}
	protected void ownBlocks(Collection<Block> blocks, OfflineUser owner){
		for (Block block: blocks) {
			plugin.getOwning().setOwner(block, owner);
		}
	}
}
class CheckForOwnerThread extends Thread{
	BlockOwn plugin;
	L_StructureGrow listener;
	List<BlockState> blockStates;
	protected CheckForOwnerThread(BlockOwn plugin, L_StructureGrow listener, List<BlockState> blocks){
		this.plugin = plugin;
		this.listener = listener;
		this.blockStates = blocks;
	}

	@Override
	public void run(){
		OfflineUser owner = null;
		for (BlockState blockState : blockStates) {
			if (plugin.getOwning().getOwner(blockState.getBlock()) != null) {
				owner = plugin.getOwning().getOwner(blockState.getBlock());
				break;
			}
		}
		if(owner != null){
			ArrayList<Block> blocks = new ArrayList<Block>();
			for(BlockState state : this.blockStates){
				if(state.getType().equals(Material.AIR)) continue;
				blocks.add(state.getBlock());
			}
			listener.ownBlocks(blocks, owner);
		}
	}
}