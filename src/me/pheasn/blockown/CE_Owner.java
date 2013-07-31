package me.pheasn.blockown;

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
			Block target = player.getTargetBlock(null, 200);
			if (plugin.owning.getOwner(target) != null) {
				plugin.say(player, ChatColor.GREEN, "This block belongs to "
						+ plugin.owning.getOwner(target).getName());
				return true;
			} else {
				plugin.say(player, ChatColor.GREEN,
						"This block belongs to no one");
				return true;
			}
		} else {
			return false;
		}
	}

}
