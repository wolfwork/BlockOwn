package com.github.pheasn.blockown.owning;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

import com.github.pheasn.OfflineUser;
import com.github.pheasn.blockown.BlockOwn;
import com.github.pheasn.blockown.Messages;

public class ImportThread extends Thread {
	private BlockOwn plugin;
	private Owning oldOwning;
	private CommandSender sender;

	public ImportThread(CommandSender sender, BlockOwn plugin,
			Owning oldOwning) {
		this.plugin = plugin;
		this.oldOwning = oldOwning;
		this.sender = sender;
	}

	@Override
	public void run() {
		HashMap<Block, OfflineUser> oldOwnings = oldOwning.getOwnings();
		for (Entry<Block, OfflineUser> entry : oldOwnings.entrySet()) {
			if (plugin.getOwning().getOwner(entry.getKey()) == null) {
				plugin.getOwning().setOwner(entry.getKey(), entry.getValue());
			}
		}
		oldOwning.save();
		plugin.tell(sender, ChatColor.GREEN, Messages.getString("ImportThread.success")); //$NON-NLS-1$
	}
}
