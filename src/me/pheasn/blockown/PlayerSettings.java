package me.pheasn.blockown;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import me.pheasn.Material;
import me.pheasn.OfflineUser;
import me.pheasn.PheasnPlugin;
import me.pheasn.blockown.BlockOwn.Setting;
import me.pheasn.interfaces.OwningDatabase;
import me.pheasn.interfaces.ProtectionDatabase;
import me.pheasn.interfaces.Protection_TypeBased;
import me.pheasn.interfaces.Base.Use;

import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerSettings extends Protection_TypeBased{
	private BlockOwn plugin;
	private HashMap<OfflineUser, LinkedList<Material>> privateLists;
	private HashMap<OfflineUser, LinkedList<OfflineUser>> friendLists;
	private HashMap<OfflineUser, HashMap<Material, LinkedList<OfflineUser>>> blackLists;

	public PlayerSettings(BlockOwn plugin) {
		this.plugin = plugin;
		privateLists = new HashMap<OfflineUser, LinkedList<Material>>();
		blackLists = new HashMap<OfflineUser, HashMap<Material, LinkedList<OfflineUser>>>();
		friendLists = new HashMap<OfflineUser, LinkedList<OfflineUser>>();
		if (PheasnPlugin.compareVersions(Setting.SETTINGS_VERSION.getString(plugin), "0.6.0") == -1) { //$NON-NLS-1$
			importOld06();
		} else if (PheasnPlugin.compareVersions(Setting.SETTINGS_VERSION.getString(plugin),	"0.6.2") == -1) { //$NON-NLS-1$
			importOld08(plugin.getConfig());
		} else if (PheasnPlugin.compareVersions(Setting.SETTINGS_VERSION.getString(plugin), "0.8") == -1){
			importOld08(YamlConfiguration.loadConfiguration(plugin.getProtectionsFile()));
		} else {
			FileConfiguration config = YamlConfiguration.loadConfiguration(plugin.getProtectionsFile());
			initialize(config);
		}
	}

	// Initialize for 0.8+
	private void initialize(FileConfiguration config) {

		// ABSOLUTELY PRIVATE TYPES
		if (config.get("PrivateBlocks") != null) { //$NON-NLS-1$
			Set<String> keys = config.getConfigurationSection("PrivateBlocks").getKeys(false); //$NON-NLS-1$
			for (String playerName : keys) {
				OfflineUser player = OfflineUser.getInstance(playerName);
				privateLists.put(player, new LinkedList<Material>());
				List<String> privateTypes = config.getStringList("PrivateBlocks." + playerName); //$NON-NLS-1$
				for (String privateTypeName : privateTypes) {
					try {
						Material privateType = Material.getMaterial(privateTypeName);
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
			Set<String> keys = config.getConfigurationSection("FriendLists").getKeys(false); //$NON-NLS-1$
			for (String playerName : keys) {
				OfflineUser player = OfflineUser.getInstance(playerName);
				friendLists.put(player, new LinkedList<OfflineUser>());
				List<String> friendList = config.getStringList("FriendLists." + playerName); //$NON-NLS-1$
				for (String friendName : friendList) {
					try {
						OfflineUser friend = OfflineUser.getInstance(friendName);
						if (friend != null) {
							friendLists.get(player).add(friend);
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
			for (String playerName: keys) {
				OfflineUser player = OfflineUser.getInstance(playerName);
				blackLists.put(player, new HashMap<Material, LinkedList<OfflineUser>>());
				for (String blockTypeName : config.getConfigurationSection(
						"Protections." + playerName).getKeys(false)) { //$NON-NLS-1$
					Material material = Material.getMaterial(blockTypeName);
					if(material != null){
						blackLists.get(player).put(material, new LinkedList<OfflineUser>());
						for (String blacklistedPlayerName : config
								.getStringList("Protections." + playerName + "." //$NON-NLS-1$ //$NON-NLS-2$
										+ blockTypeName)) {
							OfflineUser blacklistedPlayer = OfflineUser.getInstance(blacklistedPlayerName);
							if (blacklistedPlayer != null) {
								blacklistedPlayerName = blacklistedPlayer.getName();
								blackLists.get(player).get(material).add(blacklistedPlayer);
							}
						}
					}
				}
			}
		}
	}

	// Initializer for first start of a 0.6+ version
	@Deprecated
	private void importOld06() {
		FileConfiguration config = plugin.getConfig();

		// ABSOLUTELY PRIVATE TYPES
		if (config.get("PrivateBlocks") != null) { //$NON-NLS-1$
			Set<String> keys = config.getConfigurationSection("PrivateBlocks") //$NON-NLS-1$
					.getKeys(false);
			for (String playerName : keys) {
				OfflineUser player = OfflineUser.getInstance(playerName);
				privateLists.put(player, new LinkedList<Material>());
				List<String> privateTypes = config
						.getStringList("PrivateBlocks." + playerName); //$NON-NLS-1$
				for (String privateTypeName : privateTypes) {
					try {
						Material material = Material.getMaterial(privateTypeName);
						if (material != null) {
							privateLists.get(player).add(material);
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
			for (String playerName : keys) {
				OfflineUser player = OfflineUser.getInstance(playerName);
				blackLists.put(player, new HashMap<Material, LinkedList<OfflineUser>>());
				for (String blockTypeName : config.getConfigurationSection(
						"PlayerSettings." + playerName).getKeys(false)) { //$NON-NLS-1$
					Material material = Material.getMaterial(blockTypeName);
					blackLists.get(player).put(material, new LinkedList<OfflineUser>());
					for (String blacklistedPlayerName : config
							.getStringList("PlayerSettings." + playerName + "." //$NON-NLS-1$ //$NON-NLS-2$
									+ blockTypeName + ".BLACKLIST")) { //$NON-NLS-1$
						OfflineUser blacklistedPlayer = OfflineUser.getInstance(blacklistedPlayerName);
						if (blacklistedPlayer != null) {
							blackLists.get(player).get(material).add(blacklistedPlayer);
						}
					}
				}
			}
			config.set("PlayerSettings", null); //$NON-NLS-1$
			this.save();
		}
	}

	// Initializer for the first start of 0.8+
	@Deprecated
	public void importOld08(FileConfiguration config){ 
		
		// ABSOLUTELY PRIVATE TYPES
		if (config.get("PrivateBlocks") != null) { //$NON-NLS-1$
			Set<String> keys = config.getConfigurationSection("PrivateBlocks").getKeys(false); //$NON-NLS-1$
			for (String playerName : keys) {
				OfflineUser player = OfflineUser.getInstance(playerName);
				privateLists.put(player, new LinkedList<Material>());
				List<String> privateTypes = config.getStringList("PrivateBlocks." + playerName); //$NON-NLS-1$
				for (String privateTypeName : privateTypes) {
					try {
						Material privateType = Material.getMaterial(privateTypeName);
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
			for (String playerName : keys) {
				OfflineUser player = OfflineUser.getInstance(playerName);
				friendLists.put(player, new LinkedList<OfflineUser>());
				List<String> friendList = config.getStringList("FriendLists." + playerName); //$NON-NLS-1$
				for (String friendName : friendList) {
					try {
						OfflineUser friend = OfflineUser.getInstance(friendName);
						if (friend != null) {
							friendLists.get(player).add(friend);
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
			for (String playerName: keys) {
				OfflineUser player = OfflineUser.getInstance(playerName);
				blackLists.put(player, new HashMap<Material, LinkedList<OfflineUser>>());
				for (String blockTypeName : config.getConfigurationSection(
						"Protections." + playerName).getKeys(false)) { //$NON-NLS-1$
					Material material = (blockTypeName.equalsIgnoreCase("#all#")) ?  Material.getMaterial(ProtectionDatabase.ALL_BLOCKS) : Material.getMaterial(blockTypeName);
					if(material != null){
						blackLists.get(player).put(material, new LinkedList<OfflineUser>());
						for (String blacklistedPlayerName : config.getStringList("Protections." + playerName + "." + blockTypeName)) { //$NON-NLS-1$ //$NON-NLS-2$
							OfflineUser blacklistedPlayer = (blacklistedPlayerName.equalsIgnoreCase("#all#")) ? OfflineUser.getInstance(ProtectionDatabase.ALL_PLAYERS) : OfflineUser.getInstance(blacklistedPlayerName);
							if (blacklistedPlayer != null) {
								blackLists.get(player).get(material).add(blacklistedPlayer);
							}
						}
					}
				}
			}
		}
	}

	// Save method for 0.6+
	public void save() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(plugin.getProtectionsFile());
		// ABSOLUTELY PRIVATE TYPES
		config.set("PrivateBlocks", null); //$NON-NLS-1$
		for (Entry<OfflineUser, LinkedList<Material>> entry : privateLists.entrySet()) {
			if (entry.getValue().size() != 0) {
				ArrayList<String> privateTypeNames = new ArrayList<String>();
				for (Material privateType : entry.getValue()) {
					privateTypeNames.add(privateType.name());
				}
				config.set("PrivateBlocks." + entry.getKey().getName(), privateTypeNames); //$NON-NLS-1$
			}
		}
		// FRIENDLISTS
		config.set("FriendLists", null); //$NON-NLS-1$
		for (Entry<OfflineUser, LinkedList<OfflineUser>> friendList : friendLists.entrySet()) {
			if (friendList.getValue().size() > 0) {
				ArrayList<String> friendNames = new ArrayList<String>();
				for(OfflineUser friend : friendList.getValue()){
					friendNames.add(friend.getName());
				}
				config.set("FriendLists." + friendList.getKey().getName(), friendNames); //$NON-NLS-1$
			}
		}
		// BLACKLISTS
		config.set("Protections", null); //$NON-NLS-1$
		for (Entry<OfflineUser, HashMap<Material, LinkedList<OfflineUser>>> playerBlacklists : blackLists.entrySet()) {
			for (Entry<Material, LinkedList<OfflineUser>> playerBlacklist : playerBlacklists.getValue().entrySet()) {
				if (playerBlacklist.getValue().size() > 0) {
					ArrayList<String> blacklistedPlayerNames = new ArrayList<String>();
					for(OfflineUser blacklistedPlayer : playerBlacklist.getValue()){
						blacklistedPlayerNames.add(blacklistedPlayer.getName());
					}
					config.set("Protections." + playerBlacklists.getKey().getName() + "." //$NON-NLS-1$ //$NON-NLS-2$
							+ playerBlacklist.getKey().name(),
							blacklistedPlayerNames);
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

	public HashMap<Material, LinkedList<OfflineUser>> getRawBlacklists(String ownerName) {
		OfflineUser owner = OfflineUser.getInstance(ownerName);
		if (owner != null) {
			return this.getRawBlacklists(owner);
		} else {
			return new HashMap<Material, LinkedList<OfflineUser>>();
		}
	}

	public HashMap<Material, LinkedList<OfflineUser>> getRawBlacklists(
			OfflineUser owner) {
		if (this.blackLists.get(owner) != null) {
			return this.blackLists.get(owner);
		} else {
			return new HashMap<Material, LinkedList<OfflineUser>>();
		}
	}

	public LinkedList<OfflineUser> getBlacklist(String ownerName, Material material) {
		try {
			OfflineUser owner = OfflineUser.getInstance(ownerName);
			if (owner != null) {
				return this.getBlacklist(owner, material);
			} else {
				return new LinkedList<OfflineUser>();
			}
		} catch (Exception e) {
			return new LinkedList<OfflineUser>();
		}
	}

	public LinkedList<OfflineUser> getBlacklist(OfflineUser owner, Material material) {
		HashMap<Material, LinkedList<OfflineUser>> playerBlacklists;
		if ((playerBlacklists = this.blackLists.get(owner)) != null) {
			LinkedList<OfflineUser> blacklist;
			if ((blacklist = playerBlacklists.get(material)) != null) {
				return blacklist;
			}
		}
		return new LinkedList<OfflineUser>();
	}

	public void addBlacklisted(Material material, String blacklistedPlayerName, String ownerName) {
		OfflineUser owner = OfflineUser.getInstance(ownerName);
		OfflineUser blacklistedPlayer = OfflineUser.getInstance(blacklistedPlayerName);
		addBlacklisted(material, blacklistedPlayer, owner);
	}

	public void addBlacklisted(Material material, OfflineUser blacklistedPlayer, OfflineUser owner) {
		if (blacklistedPlayer != null && owner !=null) {
			if (blackLists.get(owner) == null) {
				blackLists.put(owner, new HashMap<Material, LinkedList<OfflineUser>>());
			}
			if (blackLists.get(owner).get(material) == null) {
				blackLists.get(owner).put(material, new LinkedList<OfflineUser>());
			}
			if (!blackLists.get(owner).get(material).contains(blacklistedPlayer)) {
				blackLists.get(owner).get(material).add(blacklistedPlayer);
			}
		}
	}

	public void removeBlacklisted(Material material, String blacklistedPlayerName, String ownerName) {
		OfflineUser blacklistedPlayer = OfflineUser.getInstance(blacklistedPlayerName);
		OfflineUser owner = OfflineUser.getInstance(ownerName);
		removeBlacklisted(material, blacklistedPlayer, owner);
	}

	public void removeBlacklisted(Material material, OfflineUser blacklistedPlayer, OfflineUser owner) {
		if (blacklistedPlayer != null && owner !=null) {
			try {
				blackLists.get(owner).get(material).remove(blacklistedPlayer);
			} catch (Exception e) {
				return;
			}
		}
	}

	public boolean isBlacklisted(OfflineUser candidate, OfflineUser owner,	Material material) {
		if(candidate == null) return false;
		if (Setting.DISABLE_IN_WORLDS.getList(plugin) != null) {
			List<String> disabledWorlds = Setting.DISABLE_IN_WORLDS.getStringList(plugin);
			if (candidate.getOfflinePlayer().isOnline()) {
				for (String worldName : disabledWorlds) {
					if (worldName.equalsIgnoreCase(candidate.getOfflinePlayer().getPlayer().getWorld().getName())) {
						return false;
					}
				}
			}
		}
		if (Setting.PROTECTION_AUTO_EVERYTHING.getBoolean(plugin)) {
			return true;
		}
		if(material == null) return false;
		if (Setting.PROTECTION_AUTO_CHEST.getBoolean(plugin)
				&& (material.name().equals(org.bukkit.Material.CHEST.name()) || material.name().equals(org.bukkit.Material.ENDER_CHEST.name()))) {
			return true;
		}
		if (owner != null) {
			try {
				LinkedList<OfflineUser> blacklist = this.getBlacklist(owner, Material.ALL_BLOCKS);
				if(blacklist != null){
					if(blacklist.contains(candidate) || blacklist.contains(OfflineUser.ALL_PLAYERS)){
						return true;
					}
				}
				blacklist = this.getBlacklist(owner, material);
				if(blacklist != null){
					if (blacklist.contains(candidate) || blacklist.contains(OfflineUser.ALL_PLAYERS)) {
						return true;
					} else {
						return false;
					}
				}else{
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isBlacklisted(String candidateName, String ownerName, Material material) {
		OfflineUser candidate = OfflineUser.getInstance(candidateName);
		OfflineUser owner = OfflineUser.getInstance(ownerName);
		return this.isBlacklisted(candidate, owner, material);
	}

	public LinkedList<Material> getPrivateList(String playerName) {
		OfflineUser player = OfflineUser.getInstance(playerName);
		if (player != null) {
			return getPrivateList(player);
		} else {
			return new LinkedList<Material>();
		}
	}

	public LinkedList<Material> getPrivateList(OfflineUser player) {
		if (this.privateLists.get(player) != null) {
			return this.privateLists.get(player);
		} else {
			return new LinkedList<Material>();
		}
	}

	public void addPrivate(Material material, String owner) {
		try {
			OfflineUser player = OfflineUser.getInstance(owner);
			addPrivate(material, player);
		} catch (Exception e) {
			return;
		}
	}

	public void addPrivate(Material material, OfflineUser owner) {
		if(material != Material.ALL_BLOCKS && owner != OfflineUser.ALL_PLAYERS){
			if (privateLists.get(owner) == null) {
				privateLists.put(owner, new LinkedList<Material>());
			}
			if (!privateLists.get(owner).contains(material)) {
				privateLists.get(owner).add(material);
			}
		}
	}

	public void removePrivate(Material material, OfflineUser owner) {
		if (privateLists.get(owner) != null
				&& privateLists.get(owner).contains(material)) {
			privateLists.get(owner).remove(material);
		}
	}

	public void removePrivate(Material material, String owner) {
		try {
			OfflineUser player = OfflineUser.getInstance(owner);
			removePrivate(material, player);
		} catch (Exception e) {
			return;
		}
	}

	public boolean isPrivate(OfflineUser owner, Material material) {
		try {
			if (privateLists.get(owner) != null	&& privateLists.get(owner).contains(material)) {
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
			OfflineUser owner = OfflineUser.getInstance(ownerName);
			Material material = Material.getMaterial(blockTypeName);
			return isPrivate(owner, material);
		} catch (Exception e) {
			return false;
		}
	}

	public LinkedList<OfflineUser> getFriendList(String playerName) {
		OfflineUser player = OfflineUser.getInstance(playerName);
		if (player != null) {
			return getFriendList(player);
		} else {
			return new LinkedList<OfflineUser>();
		}
	}

	public LinkedList<OfflineUser> getFriendList(OfflineUser player) {
		if (this.friendLists.get(player) != null) {
			return this.friendLists.get(player);
		} else {
			return new LinkedList<OfflineUser>();
		}
	}

	public void removeFriend(OfflineUser candidate, OfflineUser owner) {
		try {
			if (friendLists.get(owner) != null) {
				if (friendLists.get(owner).contains(candidate)){
					friendLists.get(owner).remove(candidate);
				}
			}
		} catch (Exception e) {
		}
	}

	public void removeFriend(String candidateName, String ownerName) {
		OfflineUser owner = OfflineUser.getInstance(ownerName);
		OfflineUser candidate = OfflineUser.getInstance(candidateName);
		this.removeFriend(candidate, owner);
	}

	public void addFriend(OfflineUser candidate, OfflineUser owner) {
		if(candidate.equals(OfflineUser.ALL_PLAYERS)) return;
		try {
			if (friendLists.get(owner) == null) {
				friendLists.put(owner, new LinkedList<OfflineUser>());
			}
			if (!friendLists.get(owner).contains(candidate)) {
				friendLists.get(owner).add(candidate);
			}
		} catch (Exception e) {
		}
	}

	public void addFriend(String candidateName, String ownerName) {
		OfflineUser owner = OfflineUser.getInstance(ownerName);
		OfflineUser candidate = OfflineUser.getInstance(candidateName);
		this.addFriend(candidate, owner);
	}

	public boolean isFriend(OfflineUser candidate, OfflineUser owner) {
		try {
			if (friendLists.get(owner).contains(candidate)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isFriend(String candidateName, String ownerName) {
		OfflineUser owner = OfflineUser.getInstance(ownerName);
		OfflineUser candidate = OfflineUser.getInstance(candidateName);
		return this.isFriend(candidate, owner);
	}

	public boolean canAccess(Material material, OfflineUser candidate, OfflineUser owner) {
		if(owner == null){
			return true;
		}
		if (candidate.getName().equalsIgnoreCase(owner.getName())) {
			return true;
		}
		if (!Setting.PROTECTION_ENABLE.getBoolean(plugin)) {
			return true;
		}
		if (this.isPrivate(owner, material)) {
				return false;
		}
		if(this.isFriend(candidate, owner)){
			return true;
		}
		if (this.isBlacklisted(candidate, owner, material)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canAccess(OfflineUser candidate, Block block) {
		OfflineUser owner = ((OwningDatabase) plugin.getAddonDatabase(Use.OWNING)).getOwner(block);
		if(owner == null){
			return true;
		}
		if (candidate.equals(owner)) {
			return true;
		}
		if (!Setting.PROTECTION_ENABLE.getBoolean(plugin)) {
			return true;
		}
		if (this.isPrivate(owner, Material.getMaterial(block.getType()))) {
			return false;
		}
		if (this.isFriend(candidate, owner)) {
			return true;
		}
		if (this.isBlacklisted(candidate, owner, Material.getMaterial(block.getType()))) {
			return false;
		}
		return true;
	}

	public LinkedList<String> getProtection(Material material,
			OfflineUser owner) {
		LinkedList<OfflineUser> protection = new LinkedList<OfflineUser>();
		if (!Setting.PROTECTION_ENABLE.getBoolean(plugin)) {
			return new LinkedList<String>();
		}
		if (material != Material.ALL_BLOCKS) {
			if (this.isPrivate(owner, material)) {
				LinkedList<String> result = new LinkedList<String>();
				result.add("Everyone");
				return result;
			}
		}
		protection = this.getBlacklist(owner, material);
		for (OfflineUser blacklistedPlayer : this.getBlacklist(owner, material)) {
			if (this.isFriend(blacklistedPlayer, owner)) {
				protection.remove(blacklistedPlayer);
			}
		}
		LinkedList<String> result = new LinkedList<String>();
		for(OfflineUser protectedPlayer : protection){
			result.add(protectedPlayer.getName());
		}
		return result;
	}

	@Override
	public void finalize() {
		this.save();
	}
	
}
