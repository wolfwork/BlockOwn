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
				plugin.owning.setOwner(player.getTargetBlock(null, 200), player);
				plugin.say(player, ChatColor.GREEN,"This block is yours now.");
				return true;
			}
			if (owner.getName().equalsIgnoreCase(player.getName())) {
				plugin.say(player, ChatColor.YELLOW, "This block is already yours.");
				return true;
			} else {
				plugin.say(player,ChatColor.RED,"This block is property of "+owner.getName()+".");
				return false;
			}
		}else{
			plugin.con(ChatColor.RED,"This command is just for players.");
			return false;
		}
	}

}
