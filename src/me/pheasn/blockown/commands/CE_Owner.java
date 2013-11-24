package me.pheasn.blockown.commands;

import me.pheasn.User;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_Owner implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Owner(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Block target = User.getInstance(player).getTargetBlock();
			if (target != null) {
				if (plugin.getOwning().getOwner(target) != null) {
					plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Owner.success", plugin.getOwning().getOwner(target).getName())); //$NON-NLS-1$
					return true;
				} else {
					plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Owner.noOwner")); //$NON-NLS-1$
					return true;
				}
			} else {
				plugin.say(player, ChatColor.RED, Messages.getString("noTargetBlock")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return false;
		}
	}

}
