package me.pheasn.blockown.commands;

import java.util.LinkedList;

import me.pheasn.OfflineUser;
import me.pheasn.User;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_List_Friends implements CommandExecutor {
	private BlockOwn plugin;

	public CE_List_Friends(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = User.getInstance(player);
			plugin.say(player, ChatColor.YELLOW,
					Messages.getString("CE_List_Friends.listTitle")); //$NON-NLS-1$
			LinkedList<OfflineUser> friends = plugin.getPlayerSettings().getFriendList(user.getOfflineUser());
			for (OfflineUser friend : friends) {
				plugin.say(player, ChatColor.GREEN, friend.getName());
			}
			return true;
		} else {
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return true;
		}
	}

}
