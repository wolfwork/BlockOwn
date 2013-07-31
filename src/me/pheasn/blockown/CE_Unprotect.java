package me.pheasn.blockown;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_Unprotect implements CommandExecutor {
private BlockOwn plugin;
public CE_Unprotect(BlockOwn plugin){
	this.plugin=plugin;
}
@Override
public boolean onCommand(CommandSender sender, Command cmd,
		String cmd_label, String[] args) {
	if (sender instanceof Player) {
		Player player = (Player) sender;
		Block target = player.getTargetBlock(null, 200);
		String protectName;
		if (target != null) {
			String blockName = target.getType().name();
			if (args.length == 1) {
				protectName = args[0];
				if (args[0].equalsIgnoreCase("all")) {
					args[0] = PlayerSettings.ALL_PLAYERS;
					protectName = "all players";
				}
				plugin.playerSettings.blacklistRemove(player, target.getType()
						.name(), args[0]);
				sendSuccessMessage(player, blockName, protectName);
				return true;
			} else if (args.length == 2) {
				blockName = args[0];
				protectName = args[1];
				if (args[0].equalsIgnoreCase("all")) {
					args[0] = PlayerSettings.ALL_BLOCKS;
					blockName = "all";
				}
				if (args[1].equalsIgnoreCase("all")) {
					args[1] = PlayerSettings.ALL_PLAYERS;
					protectName = "all players";
				}
				plugin.playerSettings
						.blacklistRemove(player, args[0], args[1]);
				sendSuccessMessage(player, blockName, protectName);
				return true;
			} else {
				plugin.say(player, ChatColor.RED,
						"Invalid amount of arguments.");
				return false;
			}
		} else {
			plugin.say(player, ChatColor.RED,
					"You need to aim at a block to perform this command.");
			return false;
		}
	} else {
		plugin.con("This command is just for players.");
		return false;
	}
}

private void sendSuccessMessage(Player player, String blockName,
		String protectName) {
	plugin.say(player, ChatColor.GREEN, "Your " + blockName.toLowerCase()
			+ " blocks are no longer protected against " + protectName + ".");
}

}
