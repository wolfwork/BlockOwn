package me.pheasn.updater;

import java.util.Set;
import java.util.TimerTask;

import me.pheasn.PheasnPlugin;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

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
				if (plugin.getConfig().getBoolean(
						Setting.BROADCAST_TO_OPERATORS.toString()) == true) {
					this.broadcastToOps(Messages
							.getString("UpdateCheckTask.0")); //$NON-NLS-1$
				}
			}
		} else {
			plugin.con(ChatColor.YELLOW,
					Messages.getString("UpdateCheckTask.1")); //$NON-NLS-1$
			if (plugin.getConfig().getBoolean(
					Setting.BROADCAST_TO_OPERATORS.toString()) == true) {
				this.broadcastToOps(Messages.getString("UpdateCheckTask.1")); //$NON-NLS-1$
			}
		}
	}

	private void broadcastToOps(String message) {
		Set<OfflinePlayer> ops = plugin.getServer().getOperators();
		for (OfflinePlayer op : ops) {
			if (op.isOnline()) {
				plugin.say(op.getPlayer(), ChatColor.GREEN, message);
			}
		}
	}
}
