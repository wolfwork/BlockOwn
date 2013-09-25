package me.pheasn.blockown;

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
	public void onClick(PlayerInteractEvent event) {
		if (plugin.getConfig()
				.getBoolean("ServerSettings.enablePlayerSettings")) { //$NON-NLS-1$
			if (event.getClickedBlock() != null) {
				if (plugin.getConfig().getBoolean(
						"ServerSettings.adminsIgnoreProtection")
						&& event.getPlayer().hasPermission("BlockOwn.admin")) {
					return;
				}
				OfflinePlayer owner = plugin.owning.getOwner(event
						.getClickedBlock());
				if (owner != null
						&& !owner.getName().equalsIgnoreCase(
								event.getPlayer().getName())) {
					if (plugin.playerSettings.isBlacklisted(event.getPlayer(),
							owner, event.getClickedBlock().getType().name())
							&& !plugin.playerSettings.isWhitelisted(event
									.getPlayer(), plugin.owning.getOwner(event
									.getClickedBlock()), event
									.getClickedBlock().getType().name())) {
						event.setCancelled(true);
						plugin.say(event.getPlayer(), ChatColor.RED,
								Messages.getString("L_BlockClick.1")); //$NON-NLS-1$
					}
				}
			}
		}
	}
}
