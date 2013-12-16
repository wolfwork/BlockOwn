package com.github.pheasn.blockown.commands;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.pheasn.Material;
import com.github.pheasn.User;
import com.github.pheasn.blockown.BlockOwn;
import com.github.pheasn.blockown.Messages;

public class CE_List_Private implements CommandExecutor {
	private BlockOwn plugin;

	public CE_List_Private(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = User.getInstance(player);
			plugin.say(player, ChatColor.YELLOW,
					Messages.getString("CE_List_Private.listTitle")); //$NON-NLS-1$
			LinkedList<Material> privateBlocks = plugin.getPlayerSettings().getPrivateList(user.getOfflineUser());
			for (Material privateBlock : privateBlocks) {
				plugin.say(player, ChatColor.GREEN, privateBlock.name());
			}
			return true;
		} else {
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return true;
		}
	}

}
