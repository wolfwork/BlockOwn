package me.pheasn.blockown;

import java.io.*;
import java.net.URL;
import java.util.Timer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
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
			URL url = new URL("http://api.bukget.org/3/plugins/bukkit/"
					+ plugin.getName().toLowerCase() + "/latest");
			InputStreamReader ir = new InputStreamReader(url.openStream());
			JSONParser json = new JSONParser();
			Object node = json.parse(ir);
			JSONObject object = (JSONObject) node;
			JSONObject latest = (JSONObject) ((JSONArray) object
					.get("versions")).get(0);
			if (VersionCompare.compare(latest.get("version").toString(), plugin
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
		timer.schedule(new UpdateCheckTask(this), 100, 200000);
	}

	public void update() {
		try {
			
			File to = new File("./plugins/"+plugin.getServer().getUpdateFolder() + "/"
					+ plugin.getName() + ".jar").getAbsoluteFile();
			if (to.exists()) {
				to.delete();
			}
			to.createNewFile();
			URL url = new URL("http://api.bukget.org/3/plugins/bukkit/"
					+ plugin.getName().toLowerCase() + "/latest");
			InputStreamReader ir = new InputStreamReader(url.openStream());
			JSONParser json = new JSONParser();
			Object node = json.parse(ir);
			JSONObject object = (JSONObject) node;
			JSONObject latest = (JSONObject) ((JSONArray) object
					.get("versions")).get(0);
			InputStream in = new URL((String) latest.get("download")).openStream();
			OutputStream out = new FileOutputStream(to);
			int fetched;
			byte[] buffer = new byte[4096];
			while ((fetched = in.read(buffer)) != -1) {
				out.write(buffer, 0, fetched);
			}
			out.close();
			in.close();
			ir.close();
		//	PluginManager pluginManager = plugin.getServer().getPluginManager();
			PluginManager pluginManager = Bukkit.getPluginManager();
			plugin.con(ChatColor.GREEN,
					"A new version has been downloaded and will be installed now.");
			InputStream i = plugin.getClass().getClassLoader().getResourceAsStream("dist/UpdateHelper.jar");
			File updateHelperFileTo = new File("./plugins/UpdateHelper.jar");
			FileOutputStream os = new FileOutputStream(updateHelperFileTo);
			int read;
			byte[] helpBuffer = new byte[4096];
			while ((read= i.read(helpBuffer)) != -1) {
				os.write(helpBuffer, 0, read);
			}
			os.flush();
			os.close();
			i.close();
			plugin.getConfig().set("Update-Pending", true);
			pluginManager.loadPlugin(updateHelperFileTo);
			Thread.sleep(1000);
			pluginManager.enablePlugin(pluginManager.getPlugin("UpdateHelper"));
			Thread.sleep(500);
			pluginManager.disablePlugin(plugin);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
