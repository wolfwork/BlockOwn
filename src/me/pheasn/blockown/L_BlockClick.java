package me.pheasn.blockown;

import org.bukkit.ChatColor;
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
		if (event.getClickedBlock() != null) {
			if (plugin.playerSettings.isBlacklisted(event.getPlayer(),
					plugin.owning.getOwner(event.getClickedBlock()), event
							.getClickedBlock().getType().name())) {
				event.setCancelled(true);
				plugin.say(event.getPlayer(),ChatColor.RED,"You aren't allowed to do that.");
			}
		}
	}
}
