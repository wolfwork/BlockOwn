package me.pheasn.blockown;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_MakePoor implements CommandExecutor {
	private BlockOwn plugin;

	public CE_MakePoor(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				if (player.hasPermission("blockown.admin")) { //$NON-NLS-1$
					@SuppressWarnings("unchecked")
					final HashMap<Block, String> ownings =(HashMap<Block, String>) plugin.owning.getOwnings().clone();
					for (Entry<Block, String> entry : ownings.entrySet()) {
						if (entry.getValue().equalsIgnoreCase(args[0])) {
							plugin.owning.removeOwner(entry.getKey());
						}
					}
					plugin.say(player, ChatColor.GREEN, args[0]
							+ Messages.getString("CE_MakePoor.1")); //$NON-NLS-1$
					return true;
				} else {
					return false;
				}
			} else {
				plugin.say(player, ChatColor.RED,
						Messages.getString("CE_MakePoor.2")); //$NON-NLS-1$
				return false;
			}
		} else {
			@SuppressWarnings("unchecked")
			final HashMap<Block, String> ownings =(HashMap<Block, String>) plugin.owning.getOwnings().clone();
			for (Entry<Block, String> entry : ownings.entrySet()) {
				if (entry.getValue().equalsIgnoreCase(args[0])) {
					plugin.owning.removeOwner(entry.getKey());
				}
			}
			plugin.con( ChatColor.GREEN, args[0]
					+ Messages.getString("CE_MakePoor.3")); //$NON-NLS-1$
			return true;
		}
	}
}
