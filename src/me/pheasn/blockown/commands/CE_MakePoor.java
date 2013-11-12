package me.pheasn.blockown.commands;

import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;
import me.pheasn.blockown.BlockOwn.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_MakePoor implements CommandExecutor {
	private BlockOwn plugin;

	public CE_MakePoor(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		String playerName = plugin.getServer().getOfflinePlayer(args[0])
				.getName();
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				if (player.hasPermission(Permission.ADMIN.toString())) {
					plugin.getOwning().deleteOwningsOf(playerName);
					plugin.say(player, ChatColor.GREEN, Messages.getString(
							"CE_MakePoor.success", playerName)); //$NON-NLS-1$
					return true;
				} else {
					return false;
				}
			} else {
				plugin.say(player, ChatColor.RED,
						Messages.getString("countArgs")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.getOwning().deleteOwningsOf(playerName);
			plugin.con(ChatColor.GREEN,
					Messages.getString("CE_MakePoor.success", playerName)); //$NON-NLS-1$
			return true;
		}
	}
}
