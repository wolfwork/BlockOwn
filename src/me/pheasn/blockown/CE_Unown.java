package me.pheasn.blockown;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_Unown implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Unown(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				Block target = player.getTargetBlock(null, 200);

				if (target != null) {
					OfflinePlayer owner = plugin.owning.getOwner(target);
					if (owner != null
							&& owner.getName().equalsIgnoreCase(
									player.getName())) {
						plugin.owning.removeOwner(target);
						plugin.say(player, ChatColor.GREEN,
								Messages.getString("CE_Unown.0")); //$NON-NLS-1$
						return true;
					} else {
						plugin.say(player, ChatColor.RED,
								Messages.getString("CE_Unown.1")); //$NON-NLS-1$
					}
				}
			} else {
				plugin.con(ChatColor.RED, Messages.getString("CE_Unown.2")); //$NON-NLS-1$
			}
		} catch (Exception ex) {

		}
		return false;
	}

}
