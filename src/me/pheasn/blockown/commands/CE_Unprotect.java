package me.pheasn.blockown.commands;

import me.pheasn.blockown.BOPlayer;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;
import me.pheasn.blockown.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_Unprotect implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Unprotect(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			String target = BOPlayer.getInstance(player).getTargetBlock().getType().name();
			String protectName;
			if(args.length==2){
				target=args[0];
			}
			if (target != null) {
				String blockName = target;
				if (args.length == 1) {
					protectName = args[0];
					if (args[0].equalsIgnoreCase("all")) { //$NON-NLS-1$
						args[0] = PlayerSettings.ALL_PLAYERS;
						protectName = Messages
								.getString("CE_Unprotect.allPlayers"); //$NON-NLS-1$
					}
					plugin.getPlayerSettings().blacklistRemove(player,
							target, args[0]);
					sendSuccessMessage(player, blockName, protectName);
					return true;
				} else if (args.length == 2) {
					blockName = args[0];
					protectName = args[1];
					if (args[0].equalsIgnoreCase("all")) { //$NON-NLS-1$
						args[0] = PlayerSettings.ALL_BLOCKS;
						blockName = "all"; //$NON-NLS-1$
					}
					if (args[1].equalsIgnoreCase("all")) { //$NON-NLS-1$
						args[1] = PlayerSettings.ALL_PLAYERS;
						protectName = Messages
								.getString("CE_Unprotect.allPlayers"); //$NON-NLS-1$
					}
					plugin.getPlayerSettings().blacklistRemove(player,
							args[0].toUpperCase(), args[1]);
					sendSuccessMessage(player, blockName, protectName);
					return true;
				} else {
					plugin.say(player, ChatColor.RED,
							Messages.getString("countArgs")); //$NON-NLS-1$
					return false;
				}
			} else {
				plugin.say(player, ChatColor.RED,
						Messages.getString("noTargetBlock")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(Messages.getString("justForPlayers")); //$NON-NLS-1$
			return false;
		}
	}

	private void sendSuccessMessage(Player player, String blockName,
			String protectName) {
		plugin.say(player, ChatColor.GREEN, Messages.getString(
				"CE_Unprotect.success", blockName, protectName)); //$NON-NLS-1$
	}

}
