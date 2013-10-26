package me.pheasn.blockown;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
						for (String blacklistedPlayerName : config
								.getStringList("PlayerSettings." + player + "." //$NON-NLS-1$ //$NON-NLS-2$
										+ blockType)) {
							OfflinePlayer blacklistedPlayer = plugin
									.getServer().getOfflinePlayer(
											blacklistedPlayerName);
							if (blacklistedPlayer != null) {
								blacklistedPlayerName = blacklistedPlayer
										.getName();
								lists.get(player).get(blockType)[0]
										.add(blacklistedPlayerName);
							}
						}
					} else {
						for (String blacklistedPlayerName : config
								.getStringList("PlayerSettings." + player + "." //$NON-NLS-1$ //$NON-NLS-2$
										+ blockType + ".BLACKLIST")) { //$NON-NLS-1$
							OfflinePlayer blacklistedPlayer = plugin
									.getServer().getOfflinePlayer(
											blacklistedPlayerName);
							if (blacklistedPlayer != null) {
								blacklistedPlayerName = blacklistedPlayer
										.getName();
								lists.get(player).get(blockType)[0]
										.add(blacklistedPlayerName);
							}
						}
						for (String whitelistedPlayerName : config
								.getStringList("PlayerSettings." + player + "." //$NON-NLS-1$ //$NON-NLS-2$
										+ blockType + ".WHITELIST")) { //$NON-NLS-1$
							OfflinePlayer whitelistedPlayer = plugin
									.getServer().getOfflinePlayer(
											whitelistedPlayerName);
							if (whitelistedPlayer != null) {
								whitelistedPlayerName = whitelistedPlayer
										.getName();
								lists.get(player).get(blockType)[1]
										.add(whitelistedPlayerName);
							}
						}
					}
				}
			}
		}
	}

	public void save() {
		FileConfiguration config = plugin.getConfig();
		config.set("PlayerSettings", null); //$NON-NLS-1$
		for (Entry<String, HashMap<String, LinkedList<String>[]>> entry : lists
				.entrySet()) {
			for (Entry<String, LinkedList<String>[]> playerBlacklists : entry
					.getValue().entrySet()) {
				if (playerBlacklists.getValue()[0].size() > 0
						|| playerBlacklists.getValue()[1].size() > 0) {
					config.set("PlayerSettings." + entry.getKey() + "." //$NON-NLS-1$ //$NON-NLS-2$
							+ playerBlacklists.getKey() + ".BLACKLIST", //$NON-NLS-1$
							playerBlacklists.getValue()[0]);
				}
			}
			for (Entry<String, LinkedList<String>[]> playerWhitelists : entry
					.getValue().entrySet()) {
				if (playerWhitelists.getValue()[1].size() > 0
						|| playerWhitelists.getValue()[0].size() > 0) {
					config.set("PlayerSettings." + entry.getKey() + "." //$NON-NLS-1$ //$NON-NLS-2$
							+ playerWhitelists.getKey() + ".WHITELIST", //$NON-NLS-1$
							playerWhitelists.getValue()[1]);
				}
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
			String blacklistedPlayerName) {
		OfflinePlayer blacklistedPlayer = plugin.getServer().getOfflinePlayer(
				blacklistedPlayerName);
		if (blacklistedPlayer != null) {
			blacklistedPlayerName = blacklistedPlayer.getName();
			if (!lists.containsKey(owner)) {
				lists.put(owner, new HashMap<String, LinkedList<String>[]>());
			}
			if (!lists.get(owner).containsKey(blockType)) {
				lists.get(owner).put(blockType, newLinkedList());
			}
			if (!lists.get(owner).get(blockType)[0]
					.contains(blacklistedPlayerName)) {
				lists.get(owner).get(blockType)[0].add(blacklistedPlayerName);
			}
		}
	}

	public void blacklistAdd(OfflinePlayer owner, String blockType,
			String blacklistedPlayer) {
		blacklistAdd(owner.getName(), blockType, blacklistedPlayer);
	}

	public void blacklistRemove(String owner, String blockType,
			String blacklistedPlayerName) {
		OfflinePlayer blacklistedPlayer = plugin.getServer().getOfflinePlayer(
				blacklistedPlayerName);
		if (blacklistedPlayer != null) {
			blacklistedPlayerName = blacklistedPlayer.getName();
			if (isBlacklisted(blacklistedPlayerName, owner, blockType)) {
				lists.get(owner).get(blockType)[0].remove(blacklistedPlayerName
						.toLowerCase());
			}
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

	@SuppressWarnings("unchecked")
	public boolean isBlacklisted(String candidateName, String ownerName,
			String blockType) {
		OfflinePlayer candidate = plugin.getServer().getOfflinePlayer(
				candidateName);
		List<String> disabledWorlds = null;
		if (Setting.DISABLE_IN_WORLDS.getList(plugin) != null) {
			disabledWorlds = (List<String>) plugin.getConfig().getList(
					Setting.DISABLE_IN_WORLDS.toString());
			if (candidate.isOnline()) {
				Player candidateOnline = candidate.getPlayer();
				for (String worldName : disabledWorlds) {
					if (worldName.equalsIgnoreCase(candidateOnline.getWorld()
							.getName())) {
						return false;
					}
				}
			}
		}
		if (Setting.ENABLE_AUTOMATIC_UNIVERSAL_PROTECTION.getBoolean(plugin)) {
			return true;
		}
		if (Setting.ENABLE_AUTOMATIC_CHEST_PROTECTION.getBoolean(plugin)
				&& (blockType.equalsIgnoreCase(Material.CHEST.name()) || blockType
						.equalsIgnoreCase(Material.ENDER_CHEST.name()))) {
			return true;
		}

		if (candidate != null) {
			candidateName = candidate.getName();
			try {
				LinkedList<String> blacklist = this.getBlacklist(ownerName,
						blockType);
				if (blacklist.contains(candidateName)
						|| blacklist.contains(ALL_PLAYERS)) {
					return true;
				} else {
					return false;
				}
			} catch (Exception ex) {
				return false;
			}
		} else {
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
			String whitelistedPlayerName) {
		OfflinePlayer whitelistedPlayer = plugin.getServer().getOfflinePlayer(
				whitelistedPlayerName);
		if (whitelistedPlayer != null) {
			whitelistedPlayerName = whitelistedPlayer.getName();
			if (!lists.containsKey(owner)) {
				lists.put(owner, new HashMap<String, LinkedList<String>[]>());
			}
			if (!lists.get(owner).containsKey(blockType)) {
				lists.get(owner).put(blockType, newLinkedList());
			}
			if (!lists.get(owner).get(blockType)[1]
					.contains(whitelistedPlayerName)) {
				lists.get(owner).get(blockType)[1].add(whitelistedPlayerName);
			}
		}
	}

	public void whitelistAdd(OfflinePlayer owner, String blockType,
			String whitelistedPlayer) {
		whitelistAdd(owner.getName(), blockType, whitelistedPlayer);
	}

	public void whitelistRemove(String owner, String blockType,
			String whitelistedPlayerName) {
		OfflinePlayer whitelistedPlayer = plugin.getServer().getOfflinePlayer(
				whitelistedPlayerName);
		if (whitelistedPlayer != null) {
			whitelistedPlayerName = whitelistedPlayer.getName();
			if (isWhitelisted(whitelistedPlayerName, owner, blockType)) {
				lists.get(owner).get(blockType)[1].remove(whitelistedPlayerName
						.toLowerCase());
			}
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

	public boolean isWhitelisted(String candidateName, String owner,
			String blockType) {
		OfflinePlayer candidate = plugin.getServer().getOfflinePlayer(
				candidateName);
		if (candidate != null) {
			candidateName = candidate.getName();
			try {
				LinkedList<String> whitelist = this.getWhitelist(owner,
						blockType);
				if (whitelist.contains(candidateName)
						|| whitelist.contains(ALL_PLAYERS)) {
					return true;
				} else {
					return false;
				}
			} catch (Exception ex) {
				return false;
			}
		} else {
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
