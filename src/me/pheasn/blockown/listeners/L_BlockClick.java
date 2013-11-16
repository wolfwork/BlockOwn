package me.pheasn.blockown.listeners;

import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class L_BlockClick implements Listener {
	private BlockOwn plugin;

	public L_BlockClick(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockClick(PlayerInteractEvent event) {
		if (Setting.PROTECTION_ENABLE.getBoolean(plugin)) {
			if (event.getClickedBlock() != null) {
				if (Setting.PROTECTION_ADMINS_IGNORE_PROTECTION.getBoolean(plugin)
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
						if (Setting.PROTECTION_ONLY_LEFT_CLICKS.getBoolean(plugin)
								&& !event.getAction().equals(
										Action.LEFT_CLICK_BLOCK)) {
							return;
						}
						event.setCancelled(true);
						if (Setting.PROTECTION_ENABLE_MESSAGES
								.getBoolean(plugin)) {
							plugin.say(event.getPlayer(), ChatColor.RED,
									Messages.getString("L_BlockClick.deny")); //$NON-NLS-1$
						}
					}
				}
			}
		}
	}
}
