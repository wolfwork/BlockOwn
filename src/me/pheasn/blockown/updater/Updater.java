package me.pheasn.blockown.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.Timer;

import me.pheasn.blockown.PheasnPlugin;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
	private final String API_RELEASE_TYPE_VALUE = "releaseType"; //$NON-NLS-1$

	private final String API_QUERY = "/servermods/files?projectIds="; //$NON-NLS-1$
	private final String API_HOST = "https://api.curseforge.com"; //$NON-NLS-1$

	public enum ReleaseType {
		RELEASE(3), //$NON-NLS-1$
		BETA(2), //$NON-NLS-1$
		ALPHA(1); //$NON-NLS-1$

		private int i;

		private ReleaseType(int i) {
			this.i = i;
		}

		public static ReleaseType get(String s) {
			if (s == null) {
				return null;
			}
			switch (s.toLowerCase()) {
			case ("release"):
				return RELEASE;
			case ("beta"):
				return BETA;
			case ("alpha"):
				return ALPHA;
			}
			return null;
		}

		public int getInt() {
			return i;
		}
	}

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
				for (int i = array.size() - 1; i >= 0; i--) {
					JSONObject version = (JSONObject) array.get(i);
					String versionLink = (String) version.get(API_LINK_VALUE);
					String versionName = (String) version.get(API_NAME_VALUE);
					ReleaseType releaseType = ReleaseType.get((String) version
							.get(API_RELEASE_TYPE_VALUE));
					ReleaseType prefType = ReleaseType.get(Setting.RELEASE_TYPE
							.getString(plugin));
					if (prefType == null) {
						prefType = ReleaseType.RELEASE;
					}
					if (releaseType != null
							&& releaseType.getInt() >= prefType.getInt()) {
						int later = compare(versionName, plugin
								.getDescription().getVersion());
						if (later == 1) {
							this.notifyIfNewMainVersion(versionName, plugin
									.getDescription().getVersion());
							URL dwnurl = new URL(versionLink);
							InputStream in = dwnurl.openStream();
							File file = new File(
									"./plugins/" + plugin.getServer().getUpdateFolder() + "/" + pluginFile.getName()); //$NON-NLS-1$ //$NON-NLS-2$
							file.getParentFile().mkdirs();
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
						} else {
							return false;
						}
					}
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

	private void notifyIfNewMainVersion(String v1, String v2) {
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
				if (Integer.valueOf(v1.toCharArray()[1]) > Integer.valueOf(v2
						.toCharArray()[1])) {
					plugin.con(ChatColor.YELLOW,
							Messages.getString("Updater.0")); //$NON-NLS-1$
					if (Setting.BROADCAST_TO_OPERATORS.getBoolean(plugin)) {
						this.broadcastToOps(Messages.getString("Updater.1")); //$NON-NLS-1$
					}
				}
			} catch (Exception e) {

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