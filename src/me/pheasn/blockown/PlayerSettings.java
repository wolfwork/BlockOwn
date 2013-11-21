package me.pheasn.blockown;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import me.pheasn.PheasnPlugin;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerSettings {
	private BlockOwn plugin;
	public static final String ALL_PLAYERS = "#all#"; //$NON-NLS-1$
	public static final String ALL_BLOCKS = "#ALL#"; //$NON-NLS-1$
	private HashMap<String, LinkedList<Material>> privateLists;
	private HashMap<String, LinkedList<String>> friendLists;
	private HashMap<String, HashMap<String, LinkedList<String>>> blackLists;

	public PlayerSettings(BlockOwn plugin) {
		this.plugin = plugin;
		// lists = new HashMap<String, HashMap<String, LinkedList<String>[]>>();
		privateLists = new HashMap<String, LinkedList<Material>>();
		blackLists = new HashMap<String, HashMap<String, LinkedList<String>>>();
		friendLists = new HashMap<String, LinkedList<String>>();
		if (PheasnPlugin.compareVersions(Setting.SETTINGS_VERSION.getString(plugin), "0.6.0") == -1) { //$NON-NLS-1$
			importOld();
		} else if (PheasnPlugin.compareVersions(Setting.SETTINGS_VERSION.getString(plugin),
				"0.6.2") == -1) { //$NON-NLS-1$
			initialize(plugin.getConfig());
		} else {
			FileConfiguration config = YamlConfiguration
					.loadConfiguration(plugin.getProtectionsFile());
			initialize(config);
		}
	}

	// Initialize for 0.6.0 and 0.6.1
	private void initialize(FileConfiguration config) {

		// ABSOLUTELY PRIVATE TYPES
		if (config.get("PrivateBlocks") != null) { //$NON-NLS-1$
			Set<String> keys = config.getConfigurationSection("PrivateBlocks") //$NON-NLS-1$
					.getKeys(false);
			for (String player : keys) {
				privateLists.put(player, new LinkedList<Material>());
				List<String> privateTypes = config
						.getStringList("PrivateBlocks." + player); //$NON-NLS-1$
				for (String privateTypeName : privateTypes) {
					try {
						Material privateType = Material
								.getMaterial(privateTypeName);
						if (privateType != null) {
							privateLists.get(player).add(privateType);
						}
					} catch (Exception e) {
					}
				}
			}
		}
		// FRIENDLISTS
		if (config.get("FriendLists") != null) { //$NON-NLS-1$
			Set<String> keys = config.getConfigurationSection("FriendLists") //$NON-NLS-1$
					.getKeys(false);
			for (String player : keys) {
				friendLists.put(player, new LinkedList<String>());
				List<String> friendList = config
						.getStringList("FriendLists." + player); //$NON-NLS-1$
				for (String friendName : friendList) {
					try {
						OfflinePlayer friend = plugin.getServer()
								.getOfflinePlayer(friendName);
						if (friend != null) {
							friendLists.get(player).add(friend.getName());
						}
					} catch (Exception e) {
					}
				}
			}
		}
		// BLACKLISTS
		if (config.get("Protections") != null) { //$NON-NLS-1$
			Set<String> keys = config.getConfigurationSection("Protections") //$NON-NLS-1$
					.getKeys(false);
			for (String player : keys) {
				blackLists.put(player,
						new HashMap<String, LinkedList<String>>());
				for (String blockTypeName : config.getConfigurationSection(
						"Protections." + player).getKeys(false)) { //$NON-NLS-1$
					blockTypeName = blockTypeName.toUpperCase();
					blackLists.get(player).put(blockTypeName,
							new LinkedList<String>());
					for (String blacklistedPlayerName : config
							.getStringList("Protections." + player + "." //$NON-NLS-1$ //$NON-NLS-2$
									+ blockTypeName)) {
						OfflinePlayer blacklistedPlayer = plugin.getServer()
								.getOfflinePlayer(blacklistedPlayerName);
						if (blacklistedPlayer != null) {
							blacklistedPlayerName = blacklistedPlayer.getName();
							blackLists.get(player).get(blockTypeName)
									.add(blacklistedPlayerName);
						} else {
						}
					}
				}
			}
		}
	}

	// Initializer for first start of a 0.6+ version
	private void importOld() {
		FileConfiguration config = plugin.getConfig();

		// ABSOLUTELY PRIVATE TYPES
		if (config.get("PrivateBlocks") != null) { //$NON-NLS-1$
			Set<String> keys = config.getConfigurationSection("PrivateBlocks") //$NON-NLS-1$
					.getKeys(false);
			for (String player : keys) {
				privateLists.put(player, new LinkedList<Material>());
				List<String> privateTypes = config
						.getStringList("PrivateBlocks." + player); //$NON-NLS-1$
				for (String privateTypeName : privateTypes) {
					try {
						Material privateType = Material
								.getMaterial(privateTypeName);
						if (privateType != null) {
							privateLists.get(player).add(privateType);
						}
					} catch (Exception e) {
					}
				}
			}
		}
		// BLACKLISTS
		if (config.get("PlayerSettings") != null) { //$NON-NLS-1$
			Set<String> keys = config.getConfigurationSection("PlayerSettings") //$NON-NLS-1$
					.getKeys(false);
			for (String player : keys) {
				blackLists.put(player,
						new HashMap<String, LinkedList<String>>());
				for (String blockTypeName : config.getConfigurationSection(
						"PlayerSettings." + player).getKeys(false)) { //$NON-NLS-1$
					blockTypeName = blockTypeName.toUpperCase();
					blackLists.get(player).put(blockTypeName,
							new LinkedList<String>());
					for (String blacklistedPlayerName : config
							.getStringList("PlayerSettings." + player + "." //$NON-NLS-1$ //$NON-NLS-2$
									+ blockTypeName + ".BLACKLIST")) { //$NON-NLS-1$
						OfflinePlayer blacklistedPlayer = plugin.getServer()
								.getOfflinePlayer(blacklistedPlayerName);
						if (blacklistedPlayer != null) {
							blacklistedPlayerName = blacklistedPlayer.getName();
							blackLists.get(player).get(blockTypeName)
									.add(blacklistedPlayerName);
						}
					}
				}
			}
			config.set("PlayerSettings", null); //$NON-NLS-1$
			this.save();
		}
	}

	// Save method for 0.6+
	public void save() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(plugin.getProtectionsFile());
		// ABSOLUTELY PRIVATE TYPES
		config.set("PrivateBlocks", null); //$NON-NLS-1$
		for (Entry<String, LinkedList<Material>> entry : privateLists
				.entrySet()) {
			if (entry.getValue().size() != 0) {
				ArrayList<String> privateTypeNames = new ArrayList<String>();
				for (Material privateType : entry.getValue()) {
					privateTypeNames.add(privateType.name());
				}
				config.set("PrivateBlocks." + entry.getKey(), privateTypeNames); //$NON-NLS-1$
			}
		}
		// FRIENDLISTS
		config.set("FriendLists", null); //$NON-NLS-1$
		for (Entry<String, LinkedList<String>> friendList : friendLists
				.entrySet()) {
			if (friendList.getValue().size() > 0) {
				config.set("FriendLists." + friendList.getKey(), //$NON-NLS-1$
						friendList.getValue());
			}
		}
		// BLACKLISTS
		config.set("Protections", null); //$NON-NLS-1$
		for (Entry<String, HashMap<String, LinkedList<String>>> playerBlacklists : blackLists
				.entrySet()) {
			for (Entry<String, LinkedList<String>> playerBlacklist : playerBlacklists
					.getValue().entrySet()) {
				if (playerBlacklist.getValue().size() > 0) {
					config.set("Protections." + playerBlacklists.getKey() + "." //$NON-NLS-1$ //$NON-NLS-2$
							+ playerBlacklist.getKey(),
							playerBlacklist.getValue());
				}
			}
		}
		try {
			config.save(plugin.getProtectionsFile());
			plugin.getConfig().set("PrivateBlocks", null); //$NON-NLS-1$
			plugin.getConfig().set("FriendLists", null); //$NON-NLS-1$
			plugin.getConfig().set("Protections", null); //$NON-NLS-1$
		} catch (IOException e) {
		}
	}

	public LinkedList<String> getBlacklist(String ownerName,
			String blockTypeName) {
		try {
			OfflinePlayer owner = plugin.getServer()
					.getOfflinePlayer(ownerName);
			if (owner != null) {
				return this.getBlacklist(owner, blockTypeName);
			} else {
				return new LinkedList<String>();
			}
		} catch (Exception e) {
			return new LinkedList<String>();
		}
	}

	public HashMap<String, LinkedList<String>> getRawBlacklists(String ownerName) {
		OfflinePlayer owner = plugin.getServer().getOfflinePlayer(ownerName);
		if (owner != null) {
			return this.getRawBlacklists(owner);
		} else {
			return new HashMap<String, LinkedList<String>>();
		}
	}

	public HashMap<String, LinkedList<String>> getRawBlacklists(
			OfflinePlayer owner) {
		if (this.blackLists.containsKey(owner.getName())) {
			return this.blackLists.get(owner.getName());
		} else {
			return new HashMap<String, LinkedList<String>>();
		}
	}

	public LinkedList<String> getBlacklist(OfflinePlayer owner, String blockType) {
		if (blackLists.containsKey(owner.getName())) {
			HashMap<String, LinkedList<String>> playerBlacklists = blackLists
					.get(owner.getName());
			LinkedList<String> blacklist = new LinkedList<String>();
			blockType = blockType.toUpperCase();
			if (!(blockType == ALL_BLOCKS)) {
				if (playerBlacklists.containsKey(ALL_BLOCKS)) {
					blacklist.addAll(playerBlacklists.get(ALL_BLOCKS));
				}
			}
			if (playerBlacklists.containsKey(blockType)) {
				for (String player : playerBlacklists.get(blockType)) {
					if (!blacklist.contains(player)) {
						blacklist.add(player);
					}
				}
			}
			return blacklist;
		} else {
			return new LinkedList<String>();
		}
	}

	public void blacklistAdd(String owner, String blockType,
			String blacklistedPlayerName) {
		blockType = blockType.toUpperCase();
		OfflinePlayer blacklistedPlayer = plugin.getServer().getOfflinePlayer(
				blacklistedPlayerName);
		if (blacklistedPlayer != null) {
			blacklistedPlayerName = blacklistedPlayer.getName();
			if (!blackLists.containsKey(owner)) {
				blackLists
						.put(owner, new HashMap<String, LinkedList<String>>());
			}
			if (!blackLists.get(owner).containsKey(blockType)) {
				blackLists.get(owner).put(blockType, new LinkedList<String>());
			}
			if (!blackLists.get(owner).get(blockType)
					.contains(blacklistedPlayerName)) {
				blackLists.get(owner).get(blockType).add(blacklistedPlayerName);
			}
		}
	}

	public void blacklistAdd(OfflinePlayer owner, String blockType,
			String blacklistedPlayer) {
		blacklistAdd(owner.getName(), blockType, blacklistedPlayer);
	}

	public void blacklistRemove(String owner, String blockType,
			String blacklistedPlayerName) {
		blockType = blockType.toUpperCase();
		OfflinePlayer blacklistedPlayer = plugin.getServer().getOfflinePlayer(
				blacklistedPlayerName);
		if (blacklistedPlayer != null) {
			blacklistedPlayerName = blacklistedPlayer.getName();
			try {
				blackLists.get(owner).get(blockType)
						.remove(blacklistedPlayerName);
			} catch (Exception e) {
				return;
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

	public boolean isBlacklisted(String candidateName, String ownerName,
			String blockType) {
		blockType = blockType.toUpperCase();
		OfflinePlayer candidate = plugin.getServer().getOfflinePlayer(
				candidateName);
		if (Setting.DISABLE_IN_WORLDS.getList(plugin) != null) {
			List<String> disabledWorlds = Setting.DISABLE_IN_WORLDS
					.getStringList(plugin);
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
		if (Setting.PROTECTION_AUTO_EVERYTHING.getBoolean(plugin)) {
			return true;
		}
		if (Setting.PROTECTION_AUTO_CHEST.getBoolean(plugin)
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

	public LinkedList<Material> getPrivateList(String playerName) {
		OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
		if (player != null) {
			return getPrivateList(player);
		} else {
			return new LinkedList<Material>();
		}
	}

	public LinkedList<Material> getPrivateList(OfflinePlayer player) {
		if (this.privateLists.containsKey(player.getName())) {
			return this.privateLists.get(player.getName());
		} else {
			return new LinkedList<Material>();
		}
	}

	public void privateListAdd(String playerName, String blockTypeName) {
		try {
			OfflinePlayer player = plugin.getServer().getOfflinePlayer(
					playerName);
			Material blockType = Material.getMaterial(blockTypeName);
			privateListAdd(player, blockType);
		} catch (Exception e) {
			return;
		}
	}

	public void privateListAdd(OfflinePlayer player, Material blockType) {
		if (privateLists.get(player.getName()) == null) {
			privateLists.put(player.getName(), new LinkedList<Material>());
		}
		if (!privateLists.get(player.getName()).contains(blockType)) {
			privateLists.get(player.getName()).add(blockType);
		}

	}

	public void privateListRemove(OfflinePlayer player, Material blockType) {
		if (privateLists.get(player.getName()) != null
				&& privateLists.get(player.getName()).contains(blockType)) {
			privateLists.get(player.getName()).remove(blockType);
		}
	}

	public void privateListRemove(String playerName, String blockTypeName) {
		try {
			OfflinePlayer player = plugin.getServer().getOfflinePlayer(
					playerName);
			Material blockType = Material.getMaterial(blockTypeName);
			privateListRemove(player, blockType);
		} catch (Exception e) {
			return;
		}
	}

	public boolean isPrivate(OfflinePlayer owner, Material blockType) {
		try {
			if (privateLists.containsKey(owner.getName())
					&& privateLists.get(owner.getName()).contains(blockType)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isPrivate(String ownerName, String blockTypeName) {
		try {
			OfflinePlayer owner = plugin.getServer()
					.getOfflinePlayer(ownerName);
			Material blockType = Material.getMaterial(blockTypeName);
			return isPrivate(owner, blockType);
		} catch (Exception e) {
			return false;
		}
	}

	public LinkedList<String> getFriendList(String playerName) {
		OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
		if (player != null) {
			return getFriendList(player);
		} else {
			return new LinkedList<String>();
		}
	}

	public LinkedList<String> getFriendList(OfflinePlayer player) {
		if (this.friendLists.containsKey(player.getName())) {
			return this.friendLists.get(player.getName());
		} else {
			return new LinkedList<String>();
		}
	}

	public void friendListRemove(OfflinePlayer candidate, OfflinePlayer owner) {
		try {
			if (friendLists.get(owner.getName()) != null) {
				if (friendLists.get(owner.getName()).contains(
						candidate.getName())) {
					friendLists.get(owner.getName())
							.remove(candidate.getName());
				}
			}
		} catch (Exception e) {
		}
	}

	public void friendListRemove(String candidateName, String ownerName) {
		OfflinePlayer owner = plugin.getServer().getOfflinePlayer(ownerName);
		OfflinePlayer candidate = plugin.getServer().getOfflinePlayer(
				candidateName);
		this.friendListRemove(candidate, owner);
	}

	public void friendListAdd(OfflinePlayer candidate, OfflinePlayer owner) {
		try {
			if (friendLists.get(owner.getName()) == null) {
				friendLists.put(owner.getName(), new LinkedList<String>());
			}
			if (!friendLists.get(owner.getName()).contains(candidate.getName())) {
				friendLists.get(owner.getName()).add(candidate.getName());
			}
		} catch (Exception e) {
		}
	}

	public void friendListAdd(String candidateName, String ownerName) {
		OfflinePlayer owner = plugin.getServer().getOfflinePlayer(ownerName);
		OfflinePlayer candidate = plugin.getServer().getOfflinePlayer(
				candidateName);
		this.friendListAdd(candidate, owner);
	}

	public boolean isFriend(OfflinePlayer candidate, OfflinePlayer owner) {
		try {
			if (friendLists.get(owner.getName()).contains(candidate.getName())) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isFriend(String candidateName, String ownerName) {
		OfflinePlayer owner = plugin.getServer().getOfflinePlayer(ownerName);
		OfflinePlayer candidate = plugin.getServer().getOfflinePlayer(
				candidateName);
		return this.isFriend(candidate, owner);
	}

	public boolean isProtected(String blockType, OfflinePlayer against,
			OfflinePlayer owner) {
		if (against.getName().equalsIgnoreCase(owner.getName())) {
			return false;
		}
		if (!Setting.PROTECTION_ENABLE.getBoolean(plugin)) {
			return false;
		}
		if (!blockType.equalsIgnoreCase(ALL_BLOCKS)) {
			if (this.isPrivate(owner, Material.getMaterial(blockType))) {
				return true;
			}
		}
		if (friendLists.containsKey(owner.getName())
				&& friendLists.get(owner.getName()).contains(against.getName())) {
			return false;
		}
		if (this.isBlacklisted(against, owner, blockType)) {
			return true;
		}
		return false;
	}

	public LinkedList<String> getProtection(String blockType,
			OfflinePlayer owner) {
		LinkedList<String> protection = new LinkedList<String>();
		if (!Setting.PROTECTION_ENABLE.getBoolean(plugin)) {
			return protection;
		}
		if (!blockType.equalsIgnoreCase(ALL_BLOCKS)) {
			if (this.isPrivate(owner, Material.getMaterial(blockType))) {
				for (OfflinePlayer player : plugin.getServer()
						.getOfflinePlayers()) {
					protection.add(player.getName());
				}
				return protection;
			}
		}
		protection = this.getBlacklist(owner, blockType);
		for (String blackListedPlayerName : this.getBlacklist(owner, blockType)) {
			if (this.friendLists.containsKey(owner.getName())
					&& this.friendLists.get(owner.getName()).contains(
							blackListedPlayerName)) {
				protection.remove(blackListedPlayerName);
			}
		}
		return protection;
	}

	@Override
	public void finalize() {
		this.save();
	}
}
