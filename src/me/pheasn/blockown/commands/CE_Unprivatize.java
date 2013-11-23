package me.pheasn.blockown.commands;

import me.pheasn.Material;
import me.pheasn.User;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_Unprivatize implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Unprivatize(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = User.getInstance(player);
			if (args.length == 1) {
				Material material = Material.getMaterial(args[0]);
				if (material != null) {
					plugin.getPlayerSettings().removePrivate(material, user);
					plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Unprivatize.success", material.name())); //$NON-NLS-1$
					return true;
				} else {
					plugin.say(player, ChatColor.RED, Messages.getString("CE_Unprivatize.invalidMaterial")); //$NON-NLS-1$
					return false;
				}
			} else if (args.length == 0) {
				Block target = user.getTargetBlock();
				if (target != null) {
					plugin.getPlayerSettings().removePrivate(Material.getMaterial(target.getType()), user);
					plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Unprivatize.success", target.getType().name())); //$NON-NLS-1$
					return true;
				} else {
					return false;
				}
			} else {
				plugin.say(player, ChatColor.RED, Messages.getString("countArgs")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return true;
		}
	}
}
