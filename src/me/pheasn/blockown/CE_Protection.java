package me.pheasn.blockown;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			if (args.length == 0) {
				if (player.getTargetBlock(null, 200) != null) {
					sendLists(player, plugin.playerSettings.getProtection(
							player, player.getTargetBlock(null, 200).getType()
									.name()));
					return true;
				} else {
					return false;
				}
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("all")) { //$NON-NLS-1$
					sendLists(player, plugin.playerSettings.getProtection(
							player, PlayerSettings.ALL_BLOCKS));
				} else {
					sendLists(
							player,
							plugin.playerSettings.getProtection(player,
									args[0].toUpperCase()));
				}
				return true;
			} else {
				plugin.con(Messages.getString("CE_Protection.1")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("CE_Protection.2")); //$NON-NLS-1$
			return true;
		}
	}

	private void sendLists(Player player, LinkedList<String> protectionList) {
		plugin.say(player, ChatColor.GREEN,
				Messages.getString("CE_Protection.3")); //$NON-NLS-1$
		for (String blacklistedPlayer : protectionList) {
			plugin.say(player, ChatColor.RED, blacklistedPlayer);
		}
	}
}
