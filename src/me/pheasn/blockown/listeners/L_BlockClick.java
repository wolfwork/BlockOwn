package me.pheasn.blockown.listeners;

import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class L_BlockClick implements Listener {
	private BlockOwn plugin;

	public L_BlockClick(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockClick(PlayerInteractEvent event) {
		if (Setting.ENABLE_PLAYERSETTINGS.getBoolean(plugin)) {
			if (event.getClickedBlock() != null) {
				if (Setting.ADMINS_IGNORE_PROTECTION.getBoolean(plugin)
						&& event.getPlayer().hasPermission(
								Permission.ADMIN.toString())) {
					return;
				}
				OfflinePlayer owner = plugin.getOwning().getOwner(
						event.getClickedBlock());
				if (owner != null
						&& !owner.getName().equalsIgnoreCase(
								event.getPlayer().getName())) {
					if (plugin.getPlayerSettings().isProtected(
							event.getClickedBlock().getType().name(),
							event.getPlayer(), owner)) {
						event.setCancelled(true);
						if (Setting.ENABLE_PROTECTED_MESSAGES
								.getBoolean(plugin)) {
							plugin.say(event.getPlayer(), ChatColor.RED,
									Messages.getString("L_BlockClick.1")); //$NON-NLS-1$
						}
					}
				}
			}
		}
	}
}