package me.pheasn.blockown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerSettings {
	private BlockOwn plugin;
	public static final String ALL_PLAYERS = "#all#";
	public static final String ALL_BLOCKS = "#all#";
	private HashMap<String, HashMap<String, ArrayList<String>>> blacklists;

	public PlayerSettings(BlockOwn plugin) {
		this.plugin = plugin;
		blacklists = new HashMap<String, HashMap<String, ArrayList<String>>>();
		initialize();
	}

	private void initialize() {
		FileConfiguration config = plugin.getConfig();
		if (config.get("PlayerSettings") != null) {
			Set<String> keys = config.getConfigurationSection("PlayerSettings")
					.getKeys(false);
			for (String player : keys) {
				blacklists
						.put(player, new HashMap<String, ArrayList<String>>());
				for (String blockType : config.getConfigurationSection(
						"PlayerSettings." + player).getKeys(false)) {
					blacklists.get(player).put(blockType,
							new ArrayList<String>());
					for (String blacklistedPlayer : config
							.getStringList("PlayerSettings." + player + "."
									+ blockType)) {
						blacklists.get(player).get(blockType)
								.add(blacklistedPlayer);
					}
				}
			}
		}
	}

	public void save() {
		FileConfiguration config = plugin.getConfig();
		for (Entry<String, HashMap<String, ArrayList<String>>> entry : blacklists
				.entrySet()) {
			for (Entry<String, ArrayList<String>> playerBlacklists : entry
					.getValue().entrySet()) {
				config.set("PlayerSettings." + entry.getKey() + "."
						+ playerBlacklists.getKey(),
						playerBlacklists.getValue());
			}
		}
	}

	public HashMap<String, ArrayList<String>> getBlacklists(String player) {
		if (blacklists.containsKey(player)) {
			return blacklists.get(player);
		} else {
			return new HashMap<String, ArrayList<String>>();
		}
	}

	public HashMap<String, ArrayList<String>> getBlacklists(OfflinePlayer player) {
		return getBlacklists(player.getName());
	}

	public ArrayList<String> getBlacklist(String owner, String blockType) {
		if (blacklists.containsKey(owner)) {
			HashMap<String, ArrayList<String>> playerBlacklists = blacklists
					.get(owner);
			if (playerBlacklists.containsKey(blockType)) {
				ArrayList<String> blacklistedPlayers = playerBlacklists
						.get(blockType);
				if (!(blockType == ALL_BLOCKS)) {
					if(playerBlacklists.containsKey(ALL_BLOCKS)){
					blacklistedPlayers.addAll(playerBlacklists.get(ALL_BLOCKS));
					}
				}
				return blacklistedPlayers;
			} else {
				return new ArrayList<String>();
			}
		} else {
			return new ArrayList<String>();
		}
	}

	public ArrayList<String> getBlacklist(OfflinePlayer owner, String blockType) {
		return getBlacklist(owner.getName(), blockType);
	}

	public void blacklistAdd(String owner, String blockType,
			String blacklistedPlayer) {
		if (!blacklists.containsKey(owner)) {
			blacklists.put(owner, new HashMap<String, ArrayList<String>>());
		}
		if (!blacklists.get(owner).containsKey(blockType)) {
			blacklists.get(owner).put(blockType, new ArrayList<String>());
		}
		if (!blacklists.get(owner).get(blockType).contains(blacklistedPlayer)
				&& blacklists.containsKey(owner)) {
			blacklists.get(owner).get(blockType).add(blacklistedPlayer);
		}
	}

	public void blacklistAdd(OfflinePlayer owner, String blockType,
			String blacklistedPlayer) {
		blacklistAdd(owner.getName(), blockType, blacklistedPlayer);
	}

	public void blacklistRemove(String owner, String blockType,
			String blacklistedPlayer) {
		if (isBlacklisted(blacklistedPlayer, owner, blockType)) {
			blacklists.get(owner).get(blockType).remove(blacklistedPlayer);
		}
	}

	public void blacklistRemove(OfflinePlayer owner, String blockType,
			String blacklistedPlayer) {
		blacklistRemove(owner.getName(), blockType, blacklistedPlayer);
	}

	public boolean isBlacklisted(OfflinePlayer player, OfflinePlayer owner,
			String blockType) {
		try {
			return isBlacklisted(player.getName(), owner.getName(), blockType);
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean isBlacklisted(String candidate, String owner,
			String blockType) {
		try {
			if (this.getBlacklist(owner, blockType).contains(candidate)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			plugin.con("Str"+ex.toString());
			return false;
		}
	}

	@Override
	public void finalize() {
		this.save();
	}
}
