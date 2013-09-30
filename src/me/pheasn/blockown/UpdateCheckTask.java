package me.pheasn.blockown;

import java.util.TimerTask;

public class UpdateCheckTask extends TimerTask {
	private Updater u;

	public UpdateCheckTask(Updater updater) {
		this.u = updater;
	}

	@Override
	public void run() {
		if (u.updateAvailible()) {
			u.update();
		}
	}

}
