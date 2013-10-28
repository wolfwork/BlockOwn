package me.pheasn.blockown;

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
			if (Setting.PERMISSION_NEEDED_FOR_PROTECT_COMMAND
					.getBoolean(plugin)
					&& !player.hasPermission(Permission.PROTECT.toString())) {
				plugin.say(player, ChatColor.RED,
						Messages.getString("CE_Privatize.7")); //$NON-NLS-1$
				return true;
			}
			Block target = player.getTargetBlock(null, 200);
			if (args.length == 0 && target != null) {
				if (plugin.playerSettings.isPrivate(player.getName(), target
						.getType().name())) {
					plugin.say(player, ChatColor.YELLOW,
							Messages.getString("CE_Privatize.8")); //$NON-NLS-1$
					return true;
				}
			} else if (args.length == 1) {
				if (plugin.playerSettings.isPrivate(player.getName(), args[0])) {
					plugin.say(player, ChatColor.YELLOW,
							Messages.getString("CE_Privatize.9")); //$NON-NLS-1$
					return true;
				}
			} else if (Setting.ENABLE_ECONOMY.getBoolean(plugin)) {
				return false;
			}
			if (Setting.ENABLE_ECONOMY.getBoolean(plugin)&&plugin.getEconomy()!=null) {
				if (plugin.getEconomy().getBalance(player.getName()) < Setting.PRICE_PRIVATIZE
						.getDouble(plugin)) {
					plugin.say(player, ChatColor.RED,
							Messages.getString("CE_Privatize.10")); //$NON-NLS-1$
					return true;
				} else {
					plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_Privatize.11") //$NON-NLS-1$
							+ Setting.PRICE_PRIVATIZE.getDouble(plugin) + " " //$NON-NLS-1$
							+ plugin.getEconomy().currencyNamePlural());
					plugin.getEconomy().withdrawPlayer(player.getName(),
							Setting.PRICE_PRIVATIZE.getDouble(plugin));
				}
			}
			if (args.length == 1) {
				Material blockType = Material.getMaterial(args[0]);
				if (blockType != null) {
					plugin.playerSettings.privateListAdd(player.getName(),
							blockType.name());
					plugin.say(player, ChatColor.GREEN,
							Messages.getString("CE_Privatize.0") + args[0] //$NON-NLS-1$
									+ Messages.getString("CE_Privatize.1")); //$NON-NLS-1$
					return true;
				} else {
					plugin.say(player, ChatColor.RED,
							Messages.getString("CE_Privatize.4")); //$NON-NLS-1$
					return false;
				}
			} else if (args.length == 0) {
				if (player.getTargetBlock(null, 200) != null) {
					plugin.playerSettings.privateListAdd(player.getName(),
							player.getTargetBlock(null, 200).getType().name());
					plugin.say(
							player,
							ChatColor.GREEN,
							Messages.getString("CE_Privatize.5") //$NON-NLS-1$
									+ player.getTargetBlock(null, 200)
											.getType().name()
									+ Messages.getString("CE_Privatize.6")); //$NON-NLS-1$
					return true;
				} else {
					return false;
				}
			} else {
				plugin.say(player, ChatColor.RED,
						Messages.getString("CE_Privatize.2")); //$NON-NLS-1$
				return false;
			}

		} else {
			plugin.con(ChatColor.RED, Messages.getString("CE_Privatize.3")); //$NON-NLS-1$
			return true;
		}
	}

}