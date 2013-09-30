package me.pheasn.blockown;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerSettings {
	private BlockOwn plugin;
	public static final String ALL_PLAYERS = "#all#"; //$NON-NLS-1$
	public static final String ALL_BLOCKS = "#ALL#"; //$NON-NLS-1$
	private HashMap<String, HashMap<String, LinkedList<String>[]>> lists;

	private boolean outdatedSettings = false;

	// Player > BlockType > 0=Black 1= White > Playername
	public PlayerSettings(BlockOwn plugin) {
		this.plugin = plugin;
		lists = new HashMap<String, HashMap<String, LinkedList<String>[]>>();
		initialize();
	}

	private void initialize() {
		FileConfiguration config = plugin.getConfig();
		if (config.get("PlayerSettings") != null) { //$NON-NLS-1$
			Set<String> keys = config.getConfigurationSection("PlayerSettings") //$NON-NLS-1$
					.getKeys(false);
			for (String player : keys) {
				lists.put(player, new HashMap<String, LinkedList<String>[]>());

				for (String blockType : config.getConfigurationSection(
						"PlayerSettings." + player).getKeys(false)) { //$NON-NLS-1$
					lists.get(player).put(blockType, newLinkedList());
					if (!outdatedSettings
							&& config.getConfigurationSection("PlayerSettings." //$NON-NLS-1$
									+ player + "." + blockType) == null) { //$NON-NLS-1$
						outdatedSettings = true;
					}
					if (outdatedSettings) {
						for (String blacklistedPlayer : config
								.getStringList("PlayerSettings." + player + "." //$NON-NLS-1$ //$NON-NLS-2$
										+ blockType)) {
							lists.get(player).get(blockType)[0]
									.add(blacklistedPlayer.toLowerCase());
						}
					} else {
						for (String blacklistedPlayer : config
								.getStringList("PlayerSettings." + player + "." //$NON-NLS-1$ //$NON-NLS-2$
										+ blockType + ".BLACKLIST")) { //$NON-NLS-1$
							lists.get(player).get(blockType)[0]
									.add(blacklistedPlayer.toLowerCase());
						}
						for (String whitelistedPlayer : config
								.getStringList("PlayerSettings." + player + "." //$NON-NLS-1$ //$NON-NLS-2$
										+ blockType + ".WHITELIST")) { //$NON-NLS-1$
							lists.get(player).get(blockType)[1]
									.add(whitelistedPlayer.toLowerCase());
						}
					}
				}
			}
		}
	}

	public void save() {
		FileConfiguration config = plugin.getConfig();
		for (Entry<String, HashMap<String, LinkedList<String>[]>> entry : lists
				.entrySet()) {
			if (outdatedSettings) {
				config.set("PlayerSettings." + entry.getKey(), null); //$NON-NLS-1$
			}
			for (Entry<String, LinkedList<String>[]> playerBlacklists : entry
					.getValue().entrySet()) {
				config.set("PlayerSettings." + entry.getKey() + "." //$NON-NLS-1$ //$NON-NLS-2$
						+ playerBlacklists.getKey() + ".BLACKLIST", //$NON-NLS-1$
						playerBlacklists.getValue()[0]);
			}
			for (Entry<String, LinkedList<String>[]> playerWhitelists : entry
					.getValue().entrySet()) {
				config.set("PlayerSettings." + entry.getKey() + "." //$NON-NLS-1$ //$NON-NLS-2$
						+ playerWhitelists.getKey() + ".WHITELIST", //$NON-NLS-1$
						playerWhitelists.getValue()[1]);
			}
		}

	}

	public HashMap<String, LinkedList<String>[]> getBlacklists(String player) {
		if (lists.containsKey(player)) {
			return lists.get(player);
		} else {
			return new HashMap<String, LinkedList<String>[]>();
		}
	}

	public HashMap<String, LinkedList<String>[]> getBlacklists(
			OfflinePlayer player) {
		return getBlacklists(player.getName());
	}

	public LinkedList<String> getBlacklist(String owner, String blockType) {
		if (lists.containsKey(owner)) {
			HashMap<String, LinkedList<String>[]> playerBlacklists = lists
					.get(owner);
			LinkedList<String> blacklistedPlayers = new LinkedList<String>();
			if (!(blockType == ALL_BLOCKS)) {
				if (playerBlacklists.containsKey(ALL_BLOCKS)) {
					blacklistedPlayers
							.addAll(playerBlacklists.get(ALL_BLOCKS)[0]);
				}
			}
			if (playerBlacklists.containsKey(blockType)) {
				for (String player : playerBlacklists.get(blockType)[0]) {
					if (!blacklistedPlayers.contains(player)) {
						blacklistedPlayers.add(player);
					}
				}
			}
			return blacklistedPlayers;
		} else {
			return new LinkedList<String>();
		}
	}

	public LinkedList<String> getBlacklist(OfflinePlayer owner, String blockType) {
		return getBlacklist(owner.getName(), blockType);
	}

	public void blacklistAdd(String owner, String blockType,
			String blacklistedPlayer) {
		blacklistedPlayer = blacklistedPlayer.toLowerCase();
		if (!lists.containsKey(owner)) {
			lists.put(owner, new HashMap<String, LinkedList<String>[]>());
		}
		if (!lists.get(owner).containsKey(blockType)) {
			lists.get(owner).put(blockType, newLinkedList());
		}
		if (!lists.get(owner).get(blockType)[0].contains(blacklistedPlayer)) {
			lists.get(owner).get(blockType)[0].add(blacklistedPlayer);
		}
	}

	public void blacklistAdd(OfflinePlayer owner, String blockType,
			String blacklistedPlayer) {
		blacklistAdd(owner.getName(), blockType, blacklistedPlayer);
	}

	public void blacklistRemove(String owner, String blockType,
			String blacklistedPlayer) {
		if (isBlacklisted(blacklistedPlayer, owner, blockType)) {
			lists.get(owner).get(blockType)[0].remove(blacklistedPlayer
					.toLowerCase());
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
		candidate = candidate.toLowerCase();
		if (plugin.getConfig().getBoolean(
				"ServerSettings.enableAutomaticChestProtection") && (blockType.equalsIgnoreCase(Material.CHEST.name()) //$NON-NLS-1$
				|| blockType.equalsIgnoreCase(Material.ENDER_CHEST.name()))) {
			return true;
		}
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

	public HashMap<String, LinkedList<String>[]> getWhitelists(String player) {
		if (lists.containsKey(player)) {
			return lists.get(player);
		} else {
			return new HashMap<String, LinkedList<String>[]>();
		}
	}

	public HashMap<String, LinkedList<String>[]> getWhitelists(
			OfflinePlayer player) {
		return getWhitelists(player.getName());
	}

	public LinkedList<String> getWhitelist(String owner, String blockType) {
		if (lists.containsKey(owner)) {
			HashMap<String, LinkedList<String>[]> playerWhitelists = lists
					.get(owner);
			LinkedList<String> whitelistedPlayers = new LinkedList<String>();
			if (!(blockType == ALL_BLOCKS)) {
				if (playerWhitelists.containsKey(ALL_BLOCKS)) {
					whitelistedPlayers
							.addAll(playerWhitelists.get(ALL_BLOCKS)[1]);
				}
			}
			if (playerWhitelists.containsKey(blockType)) {
				for (String player : playerWhitelists.get(blockType)[1]) {
					if (!whitelistedPlayers.contains(player)) {
						whitelistedPlayers.add(player);
					}
				}
			}
			return whitelistedPlayers;
		} else {
			return new LinkedList<String>();
		}
	}

	public LinkedList<String> getWhitelist(OfflinePlayer owner, String blockType) {
		return getWhitelist(owner.getName(), blockType);
	}

	public void whitelistAdd(String owner, String blockType,
			String whitelistedPlayer) {
		whitelistedPlayer = whitelistedPlayer.toLowerCase();
		if (!lists.containsKey(owner)) {
			lists.put(owner, new HashMap<String, LinkedList<String>[]>());
		}
		if (!lists.get(owner).containsKey(blockType)) {
			lists.get(owner).put(blockType, newLinkedList());
		}
		if (!lists.get(owner).get(blockType)[1].contains(whitelistedPlayer)) {
			lists.get(owner).get(blockType)[1].add(whitelistedPlayer);
		}
	}

	public void whitelistAdd(OfflinePlayer owner, String blockType,
			String whitelistedPlayer) {
		whitelistAdd(owner.getName(), blockType, whitelistedPlayer);
	}

	public void whitelistRemove(String owner, String blockType,
			String whitelistedPlayer) {
		if (isWhitelisted(whitelistedPlayer, owner, blockType)) {
			lists.get(owner).get(blockType)[1].remove(whitelistedPlayer
					.toLowerCase());
		}
	}

	public void whitelistRemove(OfflinePlayer owner, String blockType,
			String whitelistedPlayer) {
		whitelistRemove(owner.getName(), blockType, whitelistedPlayer);
	}

	public boolean isWhitelisted(OfflinePlayer player, OfflinePlayer owner,
			String blockType) {
		try {
			return isWhitelisted(player.getName(), owner.getName(), blockType);
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean isWhitelisted(String candidate, String owner,
			String blockType) {
		candidate = candidate.toLowerCase();
		try {
			LinkedList<String> whitelist = this.getWhitelist(owner, blockType);
			if (whitelist.contains(candidate)
					|| whitelist.contains(ALL_PLAYERS)) {
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
