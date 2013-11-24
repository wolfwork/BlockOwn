package me.pheasn.blockown;

import java.util.TimerTask;
import java.util.logging.Level;

import me.pheasn.blockown.owning.ClassicOwning;

public class AutoSaveTask extends TimerTask {
	private BlockOwn plugin;

	public AutoSaveTask(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if (plugin.getOwning() instanceof ClassicOwning) {
			if (plugin.getOwning().save()) {
				plugin.getLogger().log(Level.FINEST, Messages.getString("AutoSaveTask.success")); //$NON-NLS-1$
			} else {
				plugin.getLogger().log(Level.WARNING, Messages.getString("AutoSaveTask.failure")); //$NON-NLS-1$
			}
			plugin.getPlayerSettings().save();
		}
	}
}
