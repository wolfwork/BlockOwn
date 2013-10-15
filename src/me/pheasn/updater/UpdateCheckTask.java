package me.pheasn.updater;

import java.util.TimerTask;

import org.bukkit.ChatColor;

public class UpdateCheckTask extends TimerTask {
	private Updater updater;
	private PheasnPlugin plugin;

	public UpdateCheckTask(Updater updater, PheasnPlugin plugin) {
		this.updater = updater;
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if (!plugin.updatePending) {
			if (updater.check()) {
				plugin.con(ChatColor.GREEN,
						Messages.getString("UpdateCheckTask.0")); //$NON-NLS-1$
			}
		} else {
			plugin.con(ChatColor.YELLOW,
					Messages.getString("UpdateCheckTask.1")); //$NON-NLS-1$
		}
	}

}
