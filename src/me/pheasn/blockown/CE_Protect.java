package me.pheasn.blockown;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_Protect implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Protect(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Block target = player.getTargetBlock(null, 200);
			if (args.length == 1) {
				plugin.playerSettings.blacklistAdd(player, target.getType()
						.name(), args[0]);
				return true;
			} else if (args.length == 2) {
				plugin.playerSettings.blacklistAdd(player, args[1], args[0]);
				return true;
			}
		}
		return false;
	}

}
