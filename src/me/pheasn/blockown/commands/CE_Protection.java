package me.pheasn.blockown.commands;

import java.util.LinkedList;

import me.pheasn.blockown.BOPlayer;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;
import me.pheasn.blockown.PlayerSettings;

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
				if (BOPlayer.getInstance(player).getTargetBlock() != null) {
					sendLists(
							player,
							plugin.getPlayerSettings().getProtection(
									BOPlayer.getInstance(player)
											.getTargetBlock().getType().name(),
									player));
					return true;
				} else {
					return false;
				}
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("all")) { //$NON-NLS-1$
					sendLists(
							player,
							plugin.getPlayerSettings().getProtection(
									PlayerSettings.ALL_BLOCKS, player));
				} else {
					sendLists(
							player,
							plugin.getPlayerSettings().getProtection(
									args[0].toUpperCase(), player));
				}
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

	private void sendLists(Player player, LinkedList<String> protectionList) {
		plugin.say(player, ChatColor.GREEN,
				Messages.getString("CE_Protection.listTitle")); //$NON-NLS-1$
		for (String blacklistedPlayer : protectionList) {
			plugin.say(player, ChatColor.RED, blacklistedPlayer);
		}
	}
}
