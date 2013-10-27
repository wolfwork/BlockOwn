package me.pheasn.blockown;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			if (args.length == 1) {
				plugin.playerSettings.friendListRemove(args[0],
						player.getName());
				plugin.say(player, ChatColor.GREEN, args[0]
						+ Messages.getString("CE_Unfriend.0")); //$NON-NLS-1$
				return true;
			} else {
				plugin.say(player, ChatColor.RED,
						Messages.getString("CE_Unfriend.1")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("CE_Unfriend.2")); //$NON-NLS-1$
			return true;
		}
	}
}
