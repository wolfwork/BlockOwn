package com.github.pheasn.blockown.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.pheasn.OfflineUser;
import com.github.pheasn.blockown.BlockOwn;
import com.github.pheasn.blockown.Messages;
import com.github.pheasn.blockown.BlockOwn.Permission;

public class CE_MakePoor implements CommandExecutor {
	private BlockOwn plugin;

	public CE_MakePoor(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (args.length == 1) {
			OfflineUser user = OfflineUser.getInstance(args[0]);
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.hasPermission(Permission.MAKE_POOR.toString()) || player.hasPermission(Permission.ADMIN.toString())) {
					plugin.getOwning().deleteOwningsOf(user);
					plugin.say(player, ChatColor.GREEN, Messages.getString("CE_MakePoor.success", user.getName())); //$NON-NLS-1$
					return true;
				} else {
					plugin.say(player, ChatColor.RED, Messages.getString("noPermission")); //$NON-NLS-1$
					return true;
				}
			} else {
				plugin.getOwning().deleteOwningsOf(user);
				plugin.con(ChatColor.GREEN,	Messages.getString("CE_MakePoor.success", user.getName())); //$NON-NLS-1$
				return true;
			}
		} else {
			plugin.tell(sender, ChatColor.RED, Messages.getString("countArgs")); //$NON-NLS-1$
			return false;
		}
	}
}
