package me.pheasn.blockown;

import java.io.File;
import java.util.TimerTask;

import org.bukkit.ChatColor;

import net.h31ix.updater.Updater;
import net.h31ix.updater.Updater.UpdateType;

public class UpdateCheckTask extends TimerTask {
	private BlockOwn plugin;
	private File file;

	public UpdateCheckTask(BlockOwn plugin, File file) {
		this.plugin = plugin;
		this.file = file;
	}

	@Override
	public void run() {
		if (!plugin.updatePending) {
			new Updater(plugin, "blockown", file, UpdateType.DEFAULT, //$NON-NLS-1$
					false);
		} else {
			plugin.con(ChatColor.GREEN, Messages.getString("UpdateCheckTask.0")); //$NON-NLS-1$
		}
	}

}
