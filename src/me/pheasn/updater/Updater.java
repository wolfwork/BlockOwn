package me.pheasn.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;

import me.pheasn.PheasnPlugin;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Updater extends Thread {
	private PheasnPlugin plugin;
	private File pluginFile;
	private final int pluginId;
	private final String apiKey;
	private Timer timer = new Timer();
	private long delay;
	private long interval;

	private final String API_NAME_VALUE = "name"; //$NON-NLS-1$
	private final String API_LINK_VALUE = "downloadUrl"; //$NON-NLS-1$

	private final String API_QUERY = "/servermods/files?projectIds="; //$NON-NLS-1$
	private final String API_HOST = "https://api.curseforge.com"; //$NON-NLS-1$

	public Updater(PheasnPlugin plugin, int pluginId, File file, String apiKey) {
		this.plugin = plugin;
		this.pluginFile = file;
		this.pluginId = pluginId;
		this.apiKey = apiKey;
	}

	public void schedule(long delay, long interval) {
		this.delay = delay;
		this.interval = interval;
		this.start();
	}

	public boolean check() {
		URL url = null;
		try {
			url = new URL(API_HOST + API_QUERY + pluginId);
		} catch (MalformedURLException e) {
		}
		try {
			URLConnection con = url.openConnection();
			if (apiKey != null) {
				con.addRequestProperty("X-API-Key", apiKey); //$NON-NLS-1$
			}
			con.addRequestProperty("User-Agent", plugin.getName() + "/v" //$NON-NLS-1$ //$NON-NLS-2$
					+ plugin.getDescription().getVersion() + " (by " //$NON-NLS-1$
					+ plugin.getDescription().getAuthors().get(0) + ")"); //$NON-NLS-1$

			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String response = reader.readLine();
			JSONArray array = (JSONArray) JSONValue.parse(response);
			if (array.size() > 0) {
				JSONObject latest = (JSONObject) array.get(array.size() - 1);
				String versionLink = (String) latest.get(API_LINK_VALUE);
				String versionName = (String) latest.get(API_NAME_VALUE);
				int later = compare(versionName, plugin.getDescription()
						.getVersion());
				if (later == 1) {
					URL dwnurl = new URL(versionLink);
					InputStream in = dwnurl.openStream();
					File file = new File(
							"./plugins/" + plugin.getServer().getUpdateFolder() + "/" + pluginFile.getName()); //$NON-NLS-1$ //$NON-NLS-2$
					if (file.exists()) {
						file.delete();
					}
					file.createNewFile();
					FileOutputStream out = new FileOutputStream(file);
					int fetched;
					byte[] buffer = new byte[4096];
					while ((fetched = in.read(buffer)) != -1) {
						out.write(buffer, 0, fetched);
					}
					out.close();
					in.close();
					return true;
				}
			}

		} catch (Exception e) {

		}
		return false;
	}

	@Override
	public void run() {
		super.run();
		timer.cancel();
		timer = new Timer();
		timer.schedule(new UpdateCheckTask(this, plugin), delay, interval);
	}

	public static int compare(String v1, String v2) {
		if (v1 != null && v2 != null) {
			v1 = removeNonNumeric(v1);
			v2 = removeNonNumeric(v2);
			while (v1.length() < 3) {
				v1 += "0"; //$NON-NLS-1$
			}
			while (v2.length() < 3) {
				v2 += "0"; //$NON-NLS-1$
			}
			try {
				int version1 = Integer.valueOf(v1);
				int version2 = Integer.valueOf(v2);
				if (version1 > version2) {
					return 1;
				} else if (version1 == version2) {
					return 0;
				} else {
					return -1;
				}
			} catch (Exception e) {
				return -1;
			}
		} else {
			return -1;
		}

	}

	private static String removeNonNumeric(String input) {
		String output = new String();
		for (char c : input.toCharArray()) {
			if (Character.isDigit(c)) {
				output += c;
			}
		}
		return output;

	}

	public void cancel() {
		timer.cancel();
	}
}
