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
							&& owner.getName().equalsIgnoreCase(player.getName())) {
						plugin.owning.removeOwner(target);
						plugin.say(player, ChatColor.GREEN,
								"This block is not yours anymore.");
						return true;
					} else {
						plugin.say(player, ChatColor.RED,
								"You can't unown a block that doesn't belong to you.");
					}
				}
			} else {
				plugin.con(ChatColor.RED, "This command is just for players.");
			}
		} catch (Exception ex) {

		}
		return false;
	}

}
