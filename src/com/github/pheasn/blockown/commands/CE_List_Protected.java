package com.github.pheasn.blockown.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.pheasn.Material;
import com.github.pheasn.OfflineUser;
import com.github.pheasn.User;
import com.github.pheasn.blockown.BlockOwn;
import com.github.pheasn.blockown.Messages;
import com.github.pheasn.blockown.BlockOwn.Setting;

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
			User user = User.getInstance(player);
			if (Setting.PROTECTION_AUTO_EVERYTHING
					.getBoolean(plugin)) {
				plugin.say(player, ChatColor.YELLOW, Messages
						.getString("CE_List_Protected.everythingProtected")); //$NON-NLS-1$
				return true;
			}
			HashMap<Material, LinkedList<OfflineUser>> protections = plugin
					.getPlayerSettings().getRawBlacklists(user.getOfflineUser());
			if (Setting.PROTECTION_AUTO_CHEST.getBoolean(plugin)) {
				plugin.say(player, ChatColor.YELLOW,
						Messages.getString("CE_List_Protected.chestsProtected")); //$NON-NLS-1$
			}
			for (Entry<Material, LinkedList<OfflineUser>> entry : protections
					.entrySet()) {
				if (entry.getValue().size() > 0) {
					plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_List_Protected.protectedMaterial", entry.getKey().name())); //$NON-NLS-1$
					for (OfflineUser against : entry.getValue()) {
						plugin.say(player, ChatColor.GREEN, against.getName());
					}
				}
			}
			return true;
		} else {
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return true;
		}
	}

}
