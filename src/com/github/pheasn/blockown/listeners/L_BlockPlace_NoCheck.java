package com.github.pheasn.blockown.listeners;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.pheasn.Material;
import com.github.pheasn.OfflineUser;
import com.github.pheasn.blockown.BlockOwn;
import com.github.pheasn.blockown.BlockOwn.Permission;
import com.github.pheasn.blockown.BlockOwn.Setting;

public class L_BlockPlace_NoCheck implements Listener {
	private BlockOwn plugin;

	public L_BlockPlace_NoCheck(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!plugin.isOwningPlugin() || Setting.DISABLE_OWNING_IN_WORLDS.getStringList(plugin).contains(event.getBlock().getWorld().getName())) return;
		if (Setting.PERMISSION_NEEDED_OWN_PLACE.getBoolean(plugin) && !event.getPlayer().hasPermission(Permission.OWN_PLACE.toString())) {
			if(!(event.getPlayer().getGameMode()==GameMode.CREATIVE && event.getPlayer().hasPermission(Permission.OWN_PLACE_CREATIVE.toString()))){
				return;
			}
		}
		Block [] blocks = {event.getBlockPlaced()};
		if(Material.getDoubleHeightBlocks().contains(event.getBlockPlaced().getType())){
			blocks = new Block[] {event.getBlockPlaced(), event.getBlock().getWorld().getBlockAt(event.getBlockPlaced().getLocation().add(0, 1, 0))};
		}
		for(Block block : blocks){
			plugin.getOwning().setOwner(block, OfflineUser.getInstance(event.getPlayer()));
		}
	}
}
