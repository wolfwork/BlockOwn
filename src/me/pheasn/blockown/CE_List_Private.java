package me.pheasn.blockown;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_List_Private implements CommandExecutor {
	private BlockOwn plugin;

	public CE_List_Private(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_List_Private.0")); //$NON-NLS-1$
			LinkedList<Material> privateBlocks = plugin.getPlayerSettings().getPrivateList(player);
			for(Material privateBlock : privateBlocks){
				plugin.say(player, ChatColor.GREEN, privateBlock.name());
			}
			return true;
		}else{
			plugin.con(ChatColor.RED,Messages.getString("CE_List_Private.1")); //$NON-NLS-1$
			return true;
		}
	}

}
