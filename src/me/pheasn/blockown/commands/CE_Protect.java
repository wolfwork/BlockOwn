package me.pheasn.blockown.commands;

import me.pheasn.Material;
import me.pheasn.OfflineUser;
import me.pheasn.User;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;
import me.pheasn.blockown.PlayerSettings;
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
			User user = User.getInstance(player);
			if (Setting.PERMISSION_NEEDED_PROTECT_AND_PRIVATIZE_COMMAND
					.getBoolean(plugin)
					&& !player.hasPermission(Permission.PROTECT_AND_PRIVATIZE.toString())) {
				plugin.say(player, ChatColor.RED,
						Messages.getString("noPermission")); //$NON-NLS-1$
				return true;
			}
			Block target= User.getInstance(player).getTargetBlock();
			if (args.length == 1 && target != null) {
				if (plugin.getPlayerSettings().isBlacklisted(args[0],
						player.getName(), Material.getMaterial(target.getType()))) {
					plugin.say(player, ChatColor.YELLOW, Messages.getString(
							"CE_Protect.unneccessary", args[0])); //$NON-NLS-1$
					return true;
				}
			} else if (args.length == 2) {
				Material targetMaterial = Material.getMaterial(args[0]);
				if (plugin.getPlayerSettings().isBlacklisted(args[1],
						player.getName(), targetMaterial)) {
					plugin.say(player, ChatColor.YELLOW, Messages.getString(
							"CE_Protect.unneccessary", targetMaterial.name())); //$NON-NLS-1$
					return true;
				}
			} else if (Setting.ECONOMY_ENABLE.getBoolean(plugin)) {
				return false;
			}
			if (Setting.ECONOMY_ENABLE.getBoolean(plugin)
					&& plugin.getEconomy() != null
					&& Setting.ECONOMY_PRICE_PROTECT.getDouble(plugin) > 0.0) {
				if (plugin.getEconomy().getBalance(player.getName()) < Setting.ECONOMY_PRICE_PROTECT
						.getDouble(plugin)) {
					plugin.say(player, ChatColor.RED, Messages.getString(
							"CE_Protect.noMoney", Setting.ECONOMY_PRICE_PROTECT //$NON-NLS-1$
									.getDouble(plugin), plugin.getEconomy()
									.currencyNamePlural()));
					return true;
				} else {
					plugin.say(player, ChatColor.YELLOW, Messages.getString(
							"CE_Protect.howMuch", Setting.ECONOMY_PRICE_PROTECT //$NON-NLS-1$
									.getDouble(plugin), plugin.getEconomy()
									.currencyNamePlural()));
					plugin.getEconomy().withdrawPlayer(player.getName(),
							Setting.ECONOMY_PRICE_PROTECT.getDouble(plugin));
				}
			}
			String protectName;
				if (args.length == 1) {
					if(target != null){
						protectName = args[0];
						if (args[0].equalsIgnoreCase("all")) { //$NON-NLS-1$
							args[0] = PlayerSettings.ALL_PLAYERS;
							protectName = Messages.getString("CE_Protect.allPlayers"); //$NON-NLS-1$
						}
						OfflineUser against = OfflineUser.getInstance(args[0]);
						Material targetMaterial = Material.getMaterial(target.getType());
						plugin.getPlayerSettings().addBlacklisted(targetMaterial, against, OfflineUser.getInstance(player.getName()));
						sendSuccessMessage(player, targetMaterial.name(), protectName);
						return true;
					} else {
						plugin.say(player, ChatColor.RED, Messages.getString("noTargetBlock")); //$NON-NLS-1$
						return false;
					}
				} else if (args.length == 2) {
					String blockName = args[0];
					protectName = args[1];
					if (args[0].equalsIgnoreCase("all")) { //$NON-NLS-1$
						args[0] = PlayerSettings.ALL_BLOCKS;
						blockName = "all"; //$NON-NLS-1$
					}
					Material material = Material.getMaterial(args[0]);
					if (args[1].equalsIgnoreCase("all")) { //$NON-NLS-1$
						args[1] = PlayerSettings.ALL_PLAYERS;
						protectName = Messages
								.getString("CE_Protect.allPlayers"); //$NON-NLS-1$
					}
					OfflineUser against = OfflineUser.getInstance(args[1]);
					plugin.getPlayerSettings().addBlacklisted(material, against, user);
					sendSuccessMessage(player, blockName, protectName);
					return true;
				} else {
					plugin.say(player, ChatColor.RED,
							Messages.getString("countArgs")); //$NON-NLS-1$
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
