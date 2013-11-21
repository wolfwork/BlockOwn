package me.pheasn.blockown.commands;

import me.pheasn.User;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_Privatize implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Privatize(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (Setting.PERMISSION_NEEDED_PROTECT_AND_PRIVATIZE_COMMAND
					.getBoolean(plugin)
					&& !player.hasPermission(Permission.PROTECT_AND_PRIVATIZE.toString())) {
				plugin.say(player, ChatColor.RED,
						Messages.getString("noPermission")); //$NON-NLS-1$
				return true;
			}
			Block target = User.getInstance(player).getTargetBlock();
			if (args.length == 0 && target != null) {
				if (plugin.getPlayerSettings().isPrivate(player.getName(),
						target.getType().name())) {
					plugin.say(player, ChatColor.YELLOW,
							Messages.getString("CE_Privatize.unneccessary")); //$NON-NLS-1$
					return true;
				}
			} else if (args.length == 1) {
				if (plugin.getPlayerSettings().isPrivate(player.getName(),
						args[0])) {
					plugin.say(player, ChatColor.YELLOW,
							Messages.getString("CE_Privatize.unneccessary")); //$NON-NLS-1$
					return true;
				}
			} else if (Setting.ECONOMY_ENABLE.getBoolean(plugin)) {
				return false;
			}
			if (Setting.ECONOMY_ENABLE.getBoolean(plugin)
					&& plugin.getEconomy() != null
					&& Setting.ECONOMY_PRICE_PRIVATIZE.getDouble(plugin) > 0.0) {
				if (plugin.getEconomy().getBalance(player.getName()) < Setting.ECONOMY_PRICE_PRIVATIZE
						.getDouble(plugin)) {
					plugin.say(player, ChatColor.RED, Messages.getString(
							"CE_Privatize.noMoney", Setting.ECONOMY_PRICE_PRIVATIZE //$NON-NLS-1$
									.getDouble(plugin), plugin.getEconomy()
									.currencyNamePlural()));
					return true;
				} else {
					plugin.say(player, ChatColor.YELLOW, Messages.getString(
							"CE_Privatize.howMuch", Setting.ECONOMY_PRICE_PRIVATIZE
									.getDouble(plugin), plugin.getEconomy()
									.currencyNamePlural())); 
					plugin.getEconomy().withdrawPlayer(player.getName(),
							Setting.ECONOMY_PRICE_PRIVATIZE.getDouble(plugin));
				}
			}
			if (args.length == 1) {
				Material blockType = Material.getMaterial(args[0]);
				if (blockType != null) {
					plugin.getPlayerSettings().privateListAdd(player.getName(),
							blockType.name());
					plugin.say(player, ChatColor.GREEN, Messages.getString(
							"CE_Privatize.success", blockType.name())); //$NON-NLS-1$
					return true;
				} else {
					plugin.say(player, ChatColor.RED,
							Messages.getString("CE_Privatize.invalidMaterial")); //$NON-NLS-1$
					return false;
				}
			} else if (args.length == 0) {
				if (User.getInstance(player).getTargetBlock() != null) {
					plugin.getPlayerSettings().privateListAdd(
							player.getName(),
							User.getInstance(player).getTargetBlock()
									.getType().name());
					plugin.say(
							player,
							ChatColor.GREEN,
							Messages.getString("CE_Privatize.success", User
									.getInstance(player).getTargetBlock()
									.getType().name())); 
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
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return true;
		}
	}

}