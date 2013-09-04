package me.pheasn.blockown;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_Own implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Own(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			OfflinePlayer owner = plugin.owning.getOwner(player.getTargetBlock(
					null, 200));
			if (owner == null) {
				plugin.owning
						.setOwner(player.getTargetBlock(null, 200), player);
				plugin.say(player, ChatColor.GREEN,
						Messages.getString("CE_Own.0")); //$NON-NLS-1$
				return true;
			}
			if (owner.getName().equalsIgnoreCase(player.getName())) {
				plugin.say(player, ChatColor.YELLOW,
						Messages.getString("CE_Own.1")); //$NON-NLS-1$
				return true;
			} else {
				plugin.say(player, ChatColor.RED,
						Messages.getString("CE_Own.2") //$NON-NLS-1$
								+ owner.getName() + "."); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("CE_Own.4")); //$NON-NLS-1$
			return false;
		}
	}

}
