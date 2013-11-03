package me.pheasn.blockown;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_List_Friends implements CommandExecutor {
	private BlockOwn plugin;

	public CE_List_Friends(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_List_Friends.0")); //$NON-NLS-1$
			LinkedList<String> friends = plugin.getPlayerSettings().getFriendList(player);
			for(String friend : friends){
				plugin.say(player, ChatColor.GREEN, friend);
			}
			return true;
		}else{
			plugin.con(ChatColor.RED,Messages.getString("CE_List_Friends.1")); //$NON-NLS-1$
			return true;
		}
	}

}
