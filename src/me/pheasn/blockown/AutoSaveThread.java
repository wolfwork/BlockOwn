package me.pheasn.blockown;

import java.util.Timer;

import me.pheasn.blockown.BlockOwn.Setting;

public class AutoSaveThread extends Thread {
	private BlockOwn plugin;
	private Timer timer;

	public AutoSaveThread(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		super.run();
		timer = new Timer();
		timer.schedule(new AutoSaveTask(plugin),
				Setting.AUTOSAVE_INTERVAL.getLong(plugin) * 1000l,
				Setting.AUTOSAVE_INTERVAL.getLong(plugin) * 1000l);
	}

	@Override
	public void interrupt() {
		timer.cancel();
		super.interrupt();
	}
}
