package me.pheasn.blockown;

import java.io.File;
import java.util.Timer;

import me.pheasn.blockown.BlockOwn.Setting;

public class UpdateThread extends Thread {
	private Timer timer;
	private BlockOwn plugin;
	private File file;

	public UpdateThread(BlockOwn plugin, File file) {
		this.plugin = plugin;
		this.file = file;
	}

	@Override
	public void run() {
		super.run();
		timer = new Timer();
		timer.schedule(
				new UpdateCheckTask(plugin, file),
				1000,
				plugin.getConfig().getLong(
						Setting.AUTOUPDATE_INTERVAL.toString()) * 1000);
	}

	@Override
	public void interrupt() {
		timer.cancel();
		super.interrupt();
	}

}
