package me.pheasn.blockown;

import java.io.File;
import java.util.TimerTask;

import net.h31ix.updater.Updater;
import net.h31ix.updater.Updater.UpdateType;

public class UpdateCheckTask extends TimerTask {
	private BlockOwn plugin;
	private File file;
	private Updater updater = null;

	public UpdateCheckTask(BlockOwn plugin, File file) {
		this.plugin = plugin;
		this.file = file;
	}

	@Override
	public void run() {
		if (updater != null) {
			plugin.con(updater.getResult().name());
		}
		updater = new Updater(plugin, "blockown", file, UpdateType.DEFAULT, //$NON-NLS-1$
				false);
	}

}
