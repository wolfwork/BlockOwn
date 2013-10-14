package me.pheasn.blockown;

import java.io.File;
import java.util.Timer;

import me.pheasn.blockown.BlockOwn.Setting;

public class UpdateThread extends Thread {
	private int task;
	private BlockOwn plugin;
	private File file;
	private Timer timer;
	public UpdateThread(BlockOwn plugin, File file) {
		this.plugin = plugin;
		this.file = file;
	}

	@Override
	public void run() {
		super.run();
		timer =new Timer();
	timer.schedule(new UpdateCheckTask(plugin, file),
						100l,
						(long) (((double)(plugin.getConfig().getLong(
								Setting.AUTOUPDATE_INTERVAL.toString()))) *1000));
	}

	@Override
	public void interrupt() {
plugin.getServer().getScheduler().cancelTask(task);
		super.interrupt();
	}

}
