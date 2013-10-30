package me.pheasn.blockown;

import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
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
			if (Setting.PERMISSION_NEEDED_FOR_PROTECT_COMMAND
					.getBoolean(plugin)
					&& !player.hasPermission(Permission.PROTECT.toString())) {
				plugin.say(player, ChatColor.RED,
						Messages.getString("CE_Protect.3")); //$NON-NLS-1$
				return true;
			}
			Block target = BOPlayer.getInstance(player).getTargetBlock();
			if (args.length == 1 && target != null) {
				if (plugin.playerSettings.isBlacklisted(args[0],
						player.getName(), target.getType().name())) {
					plugin.say(player, ChatColor.YELLOW,
							Messages.getString("CE_Protect.2")); //$NON-NLS-1$
					return true;
				}
			} else if (args.length == 2) {
				if (plugin.playerSettings.isBlacklisted(args[1],
						player.getName(), args[0])) {
					plugin.say(player, ChatColor.YELLOW,
							Messages.getString("CE_Protect.4")); //$NON-NLS-1$
					return true;
				}
			} else if (Setting.ENABLE_ECONOMY.getBoolean(plugin)) {
				return false;
			}
			if (Setting.ENABLE_ECONOMY.getBoolean(plugin)
					&& plugin.getEconomy() != null) {
				if (plugin.getEconomy().getBalance(player.getName()) < Setting.PRICE_PROTECT
						.getDouble(plugin)) {
					plugin.say(player, ChatColor.RED,
							Messages.getString("CE_Protect.5")); //$NON-NLS-1$
					return true;
				} else {
					plugin.say(player, ChatColor.YELLOW,
							Messages.getString("CE_Protect.11") //$NON-NLS-1$
									+ Setting.PRICE_PROTECT.getDouble(plugin)
									+ " " //$NON-NLS-1$
									+ plugin.getEconomy().currencyNamePlural());
					plugin.getEconomy().withdrawPlayer(player.getName(),
							Setting.PRICE_PROTECT.getDouble(plugin));
				}
			}
			String protectName;
			if (target != null) {
				String blockName = target.getType().name();
				if (args.length == 1) {
					protectName = args[0];
					if (args[0].equalsIgnoreCase("all")) { //$NON-NLS-1$
						args[0] = PlayerSettings.ALL_PLAYERS;
						protectName = Messages.getString("CE_Protect.0"); //$NON-NLS-1$
					}
					plugin.playerSettings.blacklistAdd(player, target.getType()
							.name(), args[0]);
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
						protectName = Messages.getString("CE_Protect.1"); //$NON-NLS-1$
					}
					plugin.playerSettings.blacklistAdd(player,
							args[0].toUpperCase(), args[1]);
					sendSuccessMessage(player, blockName, protectName);
					return true;
				} else {
					plugin.say(player, ChatColor.RED,
							Messages.getString("CE_Protect.6")); //$NON-NLS-1$
					return false;
				}
			} else {
				plugin.say(player, ChatColor.RED,
						Messages.getString("CE_Protect.7")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(Messages.getString("CE_Protect.8")); //$NON-NLS-1$
			return false;
		}
	}

	private void sendSuccessMessage(Player player, String blockName,
			String protectName) {
		plugin.say(
				player,
				ChatColor.GREEN,
				Messages.getString("CE_Protect.9") + blockName.toLowerCase() //$NON-NLS-1$
						+ Messages.getString("CE_Protect.10") + protectName + "."); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
