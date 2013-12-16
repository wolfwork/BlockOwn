package com.github.pheasn.blockown.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.pheasn.OfflineUser;
import com.github.pheasn.User;
import com.github.pheasn.blockown.BlockOwn;
import com.github.pheasn.blockown.Messages;

public class CE_Unfriend implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Unfriend(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = User.getInstance(player);
			if (args.length == 1) {
				OfflineUser friend = OfflineUser.getInstance(args[0]);
				if (friend != null) {
					plugin.getPlayerSettings().removeFriend(friend, user.getOfflineUser());
					plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Unfriend.success", friend.getName())); //$NON-NLS-1$
					return true;
				} else {
					plugin.say(player, ChatColor.RED, Messages.getString("invalidPlayer")); //$NON-NLS-1$
					return false;
				}
			} else {
				plugin.say(player, ChatColor.RED, Messages.getString("countArgs")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return true;
		}
	}
}
