package me.pheasn.blockown;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

public class ImportThread extends Thread {
	private BlockOwn plugin;
	private Owning oldOwning;
	private CommandSender sender;

	public ImportThread(CommandSender sender, BlockOwn plugin, Owning oldOwning) {
		this.plugin = plugin;
		this.oldOwning = oldOwning;
		this.sender = sender;
	}

	@Override
	public void run() {
		HashMap<Block, String> oldOwnings = oldOwning.getOwnings();
		for (Entry<Block, String> entry : oldOwnings.entrySet()) {
			if (plugin.owning.getOwner(entry.getKey()) == null) {
				plugin.owning.setOwner(entry.getKey(), entry.getValue());
			}
		}
		oldOwning.save();
		plugin.tell(sender, ChatColor.GREEN,
				Messages.getString("ImportThread.0")); //$NON-NLS-1$
	}
}