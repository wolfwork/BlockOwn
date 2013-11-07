package me.pheasn.blockown;

import java.util.TimerTask;
import java.util.logging.Level;

import me.pheasn.blockown.owning.Owning.DatabaseType;

public class AutoSaveTask extends TimerTask {
	private BlockOwn plugin;

	public AutoSaveTask(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if (plugin.getOwning().getType().equals(DatabaseType.CLASSIC)) {
			if (plugin.getOwning().save()) {
				plugin.getLogger().log(Level.FINEST,
						Messages.getString("AutoSaveTask.0")); //$NON-NLS-1$
			} else {
				plugin.getLogger().log(Level.WARNING,
						Messages.getString("AutoSaveTask.1")); //$NON-NLS-1$
			}
		}
	}
}
