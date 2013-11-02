package me.pheasn.blockown;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
			if (args.length == 1) {
				Material blockType = Material.getMaterial(args[0]);
				if (blockType != null) {
					plugin.getPlayerSettings().privateListRemove(
							player.getName(), blockType.name());
					plugin.say(player, ChatColor.GREEN,
							Messages.getString("CE_Unprivatize.0") + args[0] //$NON-NLS-1$
									+ Messages.getString("CE_Unprivatize.1")); //$NON-NLS-1$
					return true;
				} else {
					plugin.say(player, ChatColor.RED,
							Messages.getString("CE_Unprivatize.4")); //$NON-NLS-1$
					return false;
				}
			} else if (args.length == 0) {
				if (BOPlayer.getInstance(player).getTargetBlock() != null) {
					plugin.getPlayerSettings().privateListRemove(
							player.getName(),
							BOPlayer.getInstance(player).getTargetBlock()
									.getType().name());
					plugin.say(player, ChatColor.GREEN,
							Messages.getString("CE_Unprivatize.5") //$NON-NLS-1$
									+ BOPlayer.getInstance(player)
											.getTargetBlock().getType().name()
									+ Messages.getString("CE_Unprivatize.6")); //$NON-NLS-1$
					return true;
				} else {
					return false;
				}
			} else {
				plugin.say(player, ChatColor.RED,
						Messages.getString("CE_Unprivatize.2")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("CE_Unprivatize.3")); //$NON-NLS-1$
			return true;
		}
	}
}
