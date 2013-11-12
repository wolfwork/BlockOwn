package me.pheasn.blockown.commands;

import me.pheasn.blockown.BOPlayer;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;
import me.pheasn.blockown.Messages;
import me.pheasn.blockown.PlayerSettings;

import org.bukkit.ChatColor;
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
						Messages.getString("noPermission")); //$NON-NLS-1$
				return true;
			}
			String targetTypeName = BOPlayer.getInstance(player)
					.getTargetBlock().getType().name();
			if (args.length == 1 && targetTypeName != null) {
				if (plugin.getPlayerSettings().isBlacklisted(args[0],
						player.getName(), targetTypeName)) {
					plugin.say(player, ChatColor.YELLOW, Messages.getString(
							"CE_Protect.unneccessary", args[0])); //$NON-NLS-1$
					return true;
				}
			} else if (args.length == 2) {
				targetTypeName = args[0];
				if (plugin.getPlayerSettings().isBlacklisted(args[1],
						player.getName(), args[0])) {
					plugin.say(player, ChatColor.YELLOW, Messages.getString(
							"CE_Protect.unneccessary", args[1])); //$NON-NLS-1$
					return true;
				}
			} else if (Setting.ENABLE_ECONOMY.getBoolean(plugin)) {
				return false;
			}
			if (Setting.ENABLE_ECONOMY.getBoolean(plugin)
					&& plugin.getEconomy() != null
					&& Setting.PRICE_PROTECT.getDouble(plugin) > 0.0) {
				if (plugin.getEconomy().getBalance(player.getName()) < Setting.PRICE_PROTECT
						.getDouble(plugin)) {
					plugin.say(player, ChatColor.RED, Messages.getString(
							"CE_Protect.noMoney", Setting.PRICE_PROTECT //$NON-NLS-1$
									.getDouble(plugin), plugin.getEconomy()
									.currencyNamePlural()));
					return true;
				} else {
					plugin.say(player, ChatColor.YELLOW, Messages.getString(
							"CE_Protect.howMuch", Setting.PRICE_PROTECT //$NON-NLS-1$
									.getDouble(plugin), plugin.getEconomy()
									.currencyNamePlural()));
					plugin.getEconomy().withdrawPlayer(player.getName(),
							Setting.PRICE_PROTECT.getDouble(plugin));
				}
			}
			String protectName;
			if (targetTypeName != null) {
				if (args.length == 1) {
					protectName = args[0];
					if (args[0].equalsIgnoreCase("all")) { //$NON-NLS-1$
						args[0] = PlayerSettings.ALL_PLAYERS;
						protectName = Messages
								.getString("CE_Protect.allPlayers"); //$NON-NLS-1$
					}
					plugin.getPlayerSettings().blacklistAdd(player,
							targetTypeName, args[0]);
					sendSuccessMessage(player, targetTypeName, protectName);
					return true;
				} else if (args.length == 2) {
					String blockName = args[0];
					protectName = args[1];
					if (args[0].equalsIgnoreCase("all")) { //$NON-NLS-1$
						args[0] = PlayerSettings.ALL_BLOCKS;
						blockName = "all"; //$NON-NLS-1$
					}
					if (args[1].equalsIgnoreCase("all")) { //$NON-NLS-1$
						args[1] = PlayerSettings.ALL_PLAYERS;
						protectName = Messages
								.getString("CE_Protect.allPlayers"); //$NON-NLS-1$
					}
					plugin.getPlayerSettings().blacklistAdd(player,
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
				"CE_Protect.success", blockName, protectName)); //$NON-NLS-1$ 
	}
}
