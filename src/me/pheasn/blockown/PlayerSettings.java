package me.pheasn.blockown;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerSettings {
	private BlockOwn plugin;
	public static final String ALL_PLAYERS = "#all#"; //$NON-NLS-1$
	public static final String ALL_BLOCKS = "#all#"; //$NON-NLS-1$
	private HashMap<String, HashMap<String, LinkedList<String>[]>> blacklists;

	public PlayerSettings(BlockOwn plugin) {
		this.plugin = plugin;
		blacklists = new HashMap<String, HashMap<String, LinkedList<String>[]>>();
		initialize();
	}

	private void initialize() {
		FileConfiguration config = plugin.getConfig();
		if (config.get("PlayerSettings") != null) { //$NON-NLS-1$
			Set<String> keys = config.getConfigurationSection("PlayerSettings") //$NON-NLS-1$
					.getKeys(false);
			for (String player : keys) {
				blacklists.put(player,
						new HashMap<String, LinkedList<String>[]>());
				for (String blockType : config.getConfigurationSection(
						"PlayerSettings." + player).getKeys(false)) { //$NON-NLS-1$
					blacklists.get(player).put(blockType, newLinkedList());
					for (String blacklistedPlayer : config
							.getStringList("PlayerSettings." + player + "." //$NON-NLS-1$ //$NON-NLS-2$
									+ blockType)) {
						blacklists.get(player).get(blockType)[0]
								.add(blacklistedPlayer.toLowerCase());
					}
				}
			}
		}
	}

	public void save() {
		FileConfiguration config = plugin.getConfig();
		for (Entry<String, HashMap<String, LinkedList<String>[]>> entry : blacklists
				.entrySet()) {
			for (Entry<String, LinkedList<String>[]> playerBlacklists : entry
					.getValue().entrySet()) {
				config.set("PlayerSettings." + entry.getKey() + "." //$NON-NLS-1$ //$NON-NLS-2$
						+ playerBlacklists.getKey(),
						playerBlacklists.getValue()[0]);
			}
		}
	}

	private HashMap<String, LinkedList<String>[]> getBlacklists(String player) {
		if (blacklists.containsKey(player)) {
			return blacklists.get(player);
		} else {
			return new HashMap<String, LinkedList<String>[]>();
		}
	}

	@SuppressWarnings("unused")
	private HashMap<String, LinkedList<String>[]> getBlacklists(
			OfflinePlayer player) {
		return getBlacklists(player.getName());
	}

	private LinkedList<String> getBlacklist(String owner, String blockType) {
		if (blacklists.containsKey(owner)) {
			HashMap<String, LinkedList<String>[]> playerBlacklists = blacklists
					.get(owner);
			if (playerBlacklists.containsKey(blockType)) {
				LinkedList<String> blacklistedPlayers = playerBlacklists
						.get(blockType)[0];
				if (!(blockType == ALL_BLOCKS)) {
					if (playerBlacklists.containsKey(ALL_BLOCKS)) {
						blacklistedPlayers.addAll(playerBlacklists
								.get(ALL_BLOCKS)[0]);
					}
				}
				return blacklistedPlayers;
			} else {
				return new LinkedList<String>();
			}
		} else {
			return new LinkedList<String>();
		}
	}

	@SuppressWarnings("unused")
	private LinkedList<String> getBlacklist(OfflinePlayer owner,
			String blockType) {
		return getBlacklist(owner.getName(), blockType);
	}

	public void blacklistAdd(String owner, String blockType,
			String blacklistedPlayer) {
		blacklistedPlayer=blacklistedPlayer.toLowerCase();
		if (!blacklists.containsKey(owner)) {
			blacklists.put(owner, new HashMap<String, LinkedList<String>[]>());
		}
		if (!blacklists.get(owner).containsKey(blockType)) {
			blacklists.get(owner).put(blockType, newLinkedList());
		}
		if (!blacklists.get(owner).get(blockType)[0]
				.contains(blacklistedPlayer)) {
			blacklists.get(owner).get(blockType)[0].add(blacklistedPlayer);
		}
	}

	public void blacklistAdd(OfflinePlayer owner, String blockType,
			String blacklistedPlayer) {
		blacklistAdd(owner.getName(), blockType, blacklistedPlayer);
	}

	public void blacklistRemove(String owner, String blockType,
			String blacklistedPlayer) {
		if (isBlacklisted(blacklistedPlayer, owner, blockType)) {
			blacklists.get(owner).get(blockType)[0].remove(blacklistedPlayer.toLowerCase());
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
		candidate=candidate.toLowerCase();
		try {
			LinkedList<String> blacklist = this.getBlacklist(owner, blockType);
			if (blacklist.contains(candidate)
					|| blacklist.contains(ALL_PLAYERS)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private LinkedList<String>[] newLinkedList() {
		LinkedList<String>[] list = (LinkedList<String>[]) new LinkedList[2];
		list[0] = new LinkedList<String>();
		list[1] = new LinkedList<String>();
		return list;
	}

	@Override
	public void finalize() {
		this.save();
	}
}
