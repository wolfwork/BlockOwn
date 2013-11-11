package me.pheasn.blockown.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_List_Protected implements CommandExecutor {
	private BlockOwn plugin;

	public CE_List_Protected(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (Setting.ENABLE_AUTOMATIC_UNIVERSAL_PROTECTION
					.getBoolean(plugin)) {
				plugin.say(player, ChatColor.YELLOW, Messages
						.getString("CE_List_Protected.everythingProtected")); //$NON-NLS-1$
				return true;
			}
			HashMap<String, LinkedList<String>> protections = plugin
					.getPlayerSettings().getRawBlacklists(player);
			if (Setting.ENABLE_AUTOMATIC_CHEST_PROTECTION.getBoolean(plugin)) {
				plugin.say(player, ChatColor.YELLOW,
						Messages.getString("CE_List_Protected.chestsProtected")); //$NON-NLS-1$
			}
			for (Entry<String, LinkedList<String>> entry : protections
					.entrySet()) {
				plugin.say(player, ChatColor.YELLOW, Messages.getString(
						"CE_List_Protected.protectedMaterial", entry.getKey())); //$NON-NLS-1$
				for (String material : entry.getValue()) {
					plugin.say(player, ChatColor.GREEN, material);
				}
			}
			return true;
		} else {
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return true;
		}
	}

}
