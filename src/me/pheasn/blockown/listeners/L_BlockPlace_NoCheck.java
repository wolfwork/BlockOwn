package me.pheasn.blockown.listeners;

import me.pheasn.OfflineUser;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class L_BlockPlace_NoCheck implements Listener {
	private BlockOwn plugin;

	public L_BlockPlace_NoCheck(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (Setting.PERMISSION_NEEDED_OWN_PLACE.getBoolean(plugin) && !event.getPlayer().hasPermission(Permission.OWN_PLACE.toString())) {
			if(!(event.getPlayer().getGameMode()==GameMode.CREATIVE && event.getPlayer().hasPermission(Permission.OWN_PLACE_CREATIVE.toString()))){
				return;
			}
		}
		plugin.getOwning().setOwner(event.getBlockPlaced(), OfflineUser.getInstance(event.getPlayer()));
	}
}
