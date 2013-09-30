package me.pheasn.blockown;

import java.util.TimerTask;

public class UpdateCheckTask extends TimerTask {
	private Updater u;
public UpdateCheckTask(Updater updater){
	this.u=updater;
}
	@Override
	public void run() {
		System.out.println("UpdateChecking...");
		if(u.updateAvailible()){
			System.out.println("UpdateStarting..");
			u.update();
		}
	}

}
