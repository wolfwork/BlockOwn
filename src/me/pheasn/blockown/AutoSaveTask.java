package me.pheasn.blockown;

import java.util.TimerTask;
import java.util.logging.Level;

public class AutoSaveTask extends TimerTask {
	private BlockOwn plugin;

	public AutoSaveTask(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if(plugin.owning.save()){
			plugin.getLogger().log(Level.FINEST, "Owners saved.");
		}else{
			plugin.getLogger().log(Level.WARNING,"Owners couldn't be saved.");
		}
	}

}
