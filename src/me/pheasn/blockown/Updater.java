package me.pheasn.blockown;

import java.io.*;
import java.net.URL;
import java.util.Timer;

import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Updater extends Thread {
	private BlockOwn plugin;

	public Updater(BlockOwn plugin) {
		this.plugin = plugin;
	}

	public boolean updateAvailible() {
		try {
			URL url = new URL("http://api.bukget.org/3/plugins/bukkit/" //$NON-NLS-1$
					+ plugin.getName().toLowerCase() + "/latest"); //$NON-NLS-1$
			InputStreamReader ir = new InputStreamReader(url.openStream());
			JSONParser json = new JSONParser();
			Object node = json.parse(ir);
			JSONObject object = (JSONObject) node;
			JSONObject latest = (JSONObject) ((JSONArray) object
					.get("versions")).get(0); //$NON-NLS-1$
			if (VersionCompare.compare(latest.get("version").toString(), plugin //$NON-NLS-1$
					.getDescription().getVersion()) == 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public void run() {
		Timer timer = new Timer();
		timer.schedule(new UpdateCheckTask(this), 100, plugin.getConfig()
				.getLong(Setting.AUTOUPDATE_INTERVAL.toString()) * 1000);
	}

	public void update() {
		try {

			File to = new File(
					"./plugins/" + plugin.getServer().getUpdateFolder() + "/" //$NON-NLS-1$ //$NON-NLS-2$
							+ plugin.getName() + ".jar").getAbsoluteFile(); //$NON-NLS-1$
			if (to.exists()) {
				to.delete();
			}
			to.createNewFile();
			URL url = new URL("http://api.bukget.org/3/plugins/bukkit/" //$NON-NLS-1$
					+ plugin.getName().toLowerCase() + "/latest"); //$NON-NLS-1$
			InputStreamReader ir = new InputStreamReader(url.openStream());
			JSONParser json = new JSONParser();
			Object node = json.parse(ir);
			JSONObject object = (JSONObject) node;
			JSONObject latest = (JSONObject) ((JSONArray) object
					.get("versions")).get(0); //$NON-NLS-1$
			InputStream in = new URL((String) latest.get("download")).openStream(); //$NON-NLS-1$
			OutputStream out = new FileOutputStream(to);
			int fetched;
			byte[] buffer = new byte[4096];
			while ((fetched = in.read(buffer)) != -1) {
				out.write(buffer, 0, fetched);
			}
			out.close();
			in.close();
			ir.close();
			plugin.con(ChatColor.GREEN, Messages.getString("Updater.12")); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
