package com.github.pheasn.blockown.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.pheasn.Material;
import com.github.pheasn.OfflineUser;
import com.github.pheasn.blockown.BlockOwn;
import com.github.pheasn.blockown.Messages;
import com.github.pheasn.blockown.BlockOwn.Permission;
import com.github.pheasn.blockown.BlockOwn.Setting;

public class L_BlockClick implements Listener {
	private BlockOwn plugin;

	public L_BlockClick(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockClick(PlayerInteractEvent event) {
		if (Setting.PROTECTION_ENABLE.getBoolean(plugin)) {
			if (event.getClickedBlock() != null) {
				if (event.getPlayer().hasPermission(Permission.IGNORE_PROTECTION.toString()) || event.getPlayer().hasPermission(Permission.ADMIN.toString())) return;
				OfflineUser owner = plugin.getOwning().getOwner(event.getClickedBlock());
				if (owner != null && !owner.getName().equalsIgnoreCase(event.getPlayer().getName())) {
					if (!plugin.getPlayerSettings().canAccess(Material.getMaterial(event.getClickedBlock().getType()), OfflineUser.getInstance(event.getPlayer()), owner)) {
						if (Setting.PROTECTION_ONLY_LEFT_CLICKS.getBoolean(plugin)	&& !event.getAction().equals(Action.LEFT_CLICK_BLOCK) && 
								!event.getClickedBlock().getType().equals(org.bukkit.Material.CHEST) && !event.getClickedBlock().getType().equals(org.bukkit.Material.ENDER_CHEST)) return;
						event.setCancelled(true);
						if (Setting.PROTECTION_ENABLE_MESSAGES.getBoolean(plugin)) {
							plugin.say(event.getPlayer(), ChatColor.RED, Messages.getString("L_BlockClick.deny", owner.getName())); //$NON-NLS-1$
						}
					}
				}
			}
		}
	}
}
