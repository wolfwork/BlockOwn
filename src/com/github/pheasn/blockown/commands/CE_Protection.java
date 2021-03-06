package com.github.pheasn.blockown.commands;

import java.util.LinkedList;

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

public class CE_Protection implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Protection(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = User.getInstance(player);
			if (args.length == 0) {
				if (User.getInstance(player).getTargetBlock() != null) {
					sendLists(player, plugin.getPlayerSettings().getProtection(Material.getMaterial(user.getTargetBlock().getType()), user.getOfflineUser()));
					return true;
				} else {
					return false;
				}
			} else if (args.length == 1) {
				sendLists(player, plugin.getPlayerSettings().getProtection(Material.getMaterial(args[0]), user.getOfflineUser()));
				return true;
			} else {
				plugin.con(Messages.getString("countArgs")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return true;
		}
	}

	private void sendLists(Player player, LinkedList<OfflineUser> protectionList) {
		plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Protection.listTitle")); //$NON-NLS-1$
		String name;
		for (OfflineUser blacklistedPlayer : protectionList) {
			name = blacklistedPlayer.getName();
			if(name.equalsIgnoreCase(OfflineUser.ALL_PLAYERS.getName())) name = Messages.getString("everyone");
			plugin.say(player, ChatColor.RED, name);
		}
	}
}
