package me.pheasn.blockown;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import me.pheasn.PheasnPlugin;
import me.pheasn.owning.ClassicOwning;
import me.pheasn.owning.ImportThread;
import me.pheasn.owning.MySQLNotConnectingException;
import me.pheasn.owning.Owning;
import me.pheasn.owning.Owning.DatabaseType;
import me.pheasn.owning.SQLOwningLocal;
import me.pheasn.owning.SQLOwningNetwork;
import me.pheasn.updater.Updater;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;
import org.mcstats.Metrics.Plotter;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class BlockOwn extends PheasnPlugin {
	public PlayerSettings playerSettings;
	private File pluginDir;
	private File blockOwnerFile;
	private File settingsFile;
	private Updater updater;
	private Thread autoSaveThread;
	private final int pluginId = 62749;
	private String apiKey;
	private WorldEditPlugin worldEdit = null;

	public enum Setting {
		SETTINGS_VERSION("Settings-Version"), //$NON-NLS-1$
		ENABLE("ServerSettings.enable"), //$NON-NLS-1$
		ENABLE_AUTOUPDATE_old("ServerSettings.enableAutoUpdate"), //$NON-NLS-1$
		API_KEY_old("ServerSettings.apiKey"), //$NON-NLS-1$
		AUTOUPDATE_INTERVAL_old("ServerSettings.autoUpdateInterval"), //$NON-NLS-1$
		AUTOSAVE_INTERVAL("ServerSettings.autoSaveInterval"), //$NON-NLS-1$
		ENABLE_PLAYERSETTINGS("ServerSettings.enablePlayerSettings"), //$NON-NLS-1$
		ENABLE_AUTOUPDATE("ServerSettings.AutoUpdater.enableAutoUpdate"), //$NON-NLS-1$
		BROADCAST_TO_OPERATORS(
				"ServerSettings.AutoUpdater.broadcastToOperators"), //$NON-NLS-1$
		API_KEY("ServerSettings.AutoUpdater.apiKey"), //$NON-NLS-1$
		AUTOUPDATE_INTERVAL("ServerSettings.AutoUpdater.autoUpdateInterval"), //$NON-NLS-1$
		ENABLE_AUTOMATIC_CHEST_PROTECTION(
				"ServerSettings.enableAutomaticChestProtection"), //$NON-NLS-1$
		ENABLE_AUTOMATIC_UNIVERSAL_PROTECTION(
				"ServerSettings.enableAutomaticUniversalProtection"), //$NON-NLS-1$
		ADMINS_IGNORE_PROTECTION("ServerSettings.adminsIgnoreProtection"), //$NON-NLS-1$
		CASCADE_PROTECTION_COMMANDS("ServerSettings.cascadeProtectionCommands"), //$NON-NLS-1$
		DISABLE_IN_WORLDS("ServerSettings.disableInWorlds"), //$NON-NLS-1$
		PERMISSION_NEEDED_FOR_PROTECT_COMMAND(
				"ServerSettings.permissionNeededForProtectCommand"); //$NON-NLS-1$
		private String s;

		private Setting(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return s;
		}

		public boolean getBoolean(PheasnPlugin plugin) {
			return plugin.getConfig().getBoolean(s);
		}

		public List<?> getList(PheasnPlugin plugin) {
			return plugin.getConfig().getList(s);
		}

		public String getString(PheasnPlugin plugin) {
			return plugin.getConfig().getString(s);
		}

		public int getInt(PheasnPlugin plugin) {
			return plugin.getConfig().getInt(s);
		}

		public long getLong(PheasnPlugin plugin) {
			return plugin.getConfig().getLong(s);
		}
	}

	public enum Commands {
		OWNER("owner"), //$NON-NLS-1$
		OWN("own"), //$NON-NLS-1$
		UNOWN("unown"), //$NON-NLS-1$
		BLOCKOWN("blockown"), //$NON-NLS-1$
		PROTECT("protect"), //$NON-NLS-1$
		UNPROTECT("unprotect"), //$NON-NLS-1$
		WHITELIST("whitelist"), //$NON-NLS-1$
		UNWHITELIST("unwhitelist"), //$NON-NLS-1$
		PROTECTION("protection"), //$NON-NLS-1$
		MAKE_POOR("makepoor"); //$NON-NLS-1$
		private String s;

		private Commands(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return s;
		}
	}

	public enum Permission {
		PROTECT("blockown.protect"), ADMIN("blockown.admin");
		private String s;

		private Permission(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return s;
		}
	}

	@Override
	public void onDisable() {
		if (owning != null) {
			this.owning.save();
		}
		if (playerSettings != null) {
			this.playerSettings.save();
		}
		this.saveConfig();
		updater.cancel();
		if (this.autoSaveThread.isAlive()) {
			autoSaveThread.interrupt();
		}
		super.onDisable();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		console = this.getServer().getConsoleSender();
		pluginDir = new File("./plugins/" + this.getName() + "/"); //$NON-NLS-1$ //$NON-NLS-2$
		blockOwnerFile = new File(pluginDir.getPath() + "/blocks.dat"); //$NON-NLS-1$
		settingsFile = new File(pluginDir.getPath() + "/config.yml"); //$NON-NLS-1$
		this.createEnv();
		if (!Setting.ENABLE.getBoolean(this)) {
			this.con(ChatColor.YELLOW, Messages.getString("BlockOwn.40")); //$NON-NLS-1$
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		} else {
			this.con(Messages.getString("BlockOwn.41")); //$NON-NLS-1$
		}
		this.registerCommands();
		this.registerEvents();
		if (!this.establishOwning()) {
			return;
		}
		this.playerSettings = new PlayerSettings(this);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		this.cleanUpOldSettings();
		this.getConfig().set(Setting.SETTINGS_VERSION.toString(),
				this.getDescription().getVersion());
		this.getConfig().set("ServerSettings.AutoUpdater.enableAutoUpdater", //$NON-NLS-1$
				null);
		this.saveConfig();
		this.initializeMetrics();
		this.apiKey = Setting.API_KEY.getString(this);
		// enable AutoUpdater
		updater = new Updater(this, this.pluginId, this.getFile(), apiKey);
		if (Setting.ENABLE_AUTOUPDATE.getBoolean(this)) {
			updater.schedule(100l,
					Setting.AUTOUPDATE_INTERVAL.getLong(this) * 1000);
			this.con(Messages.getString("BlockOwn.93")); //$NON-NLS-1$
		}
		// enable autoSaver
		autoSaveThread = new AutoSaveThread(this);
		if (Setting.AUTOSAVE_INTERVAL.getLong(this) != 0
				&& this.owning.getType().equals(DatabaseType.CLASSIC)) {
			autoSaveThread.start();
		}

		// Soft dependency to WorldEdit
		worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager()
				.getPlugin("WorldEdit"); //$NON-NLS-1$
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (args.length > 0) {
			if (Setting.CASCADE_PROTECTION_COMMANDS.getBoolean(this)) {
				String[] newargs;
				try {
					newargs = new String[args.length - 1];
				} catch (NegativeArraySizeException e) {
					newargs = new String[0];
				}
				for (int i = 1; i < args.length; i++) {
					newargs[i - 1] = args[i];
				}
				if (args[0].equalsIgnoreCase(Commands.WHITELIST.toString())) {
					return new CE_Whitelist(this).onCommand(sender, cmd,
							cmd_label, newargs);
				}
				if (args[0].equalsIgnoreCase(Commands.UNWHITELIST.toString())) {
					return new CE_Unwhitelist(this).onCommand(sender, cmd,
							cmd_label, newargs);
				}
				if (args[0].equalsIgnoreCase(Commands.PROTECT.toString())) {
					return new CE_Protect(this).onCommand(sender, cmd,
							cmd_label, newargs);
				}
				if (args[0].equalsIgnoreCase(Commands.UNPROTECT.toString())) {
					return new CE_Unprotect(this).onCommand(sender, cmd,
							cmd_label, newargs);
				}
				if (args[0].equalsIgnoreCase(Commands.PROTECTION.toString())) {
					return new CE_Protection(this).onCommand(sender, cmd,
							cmd_label, newargs);
				}
			}
			// If sender is a player, check for his permission
			boolean isPlayer = sender instanceof Player;
			if (isPlayer
					&& !((Player) sender).hasPermission(Permission.ADMIN
							.toString())) {
				this.tell(
						sender,
						ChatColor.RED,
						Messages.getString("BlockOwn.44") + String.valueOf(((Player) sender).hasPermission(Permission.ADMIN.toString()))); //$NON-NLS-1$ 
				return false;
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("save")) { //$NON-NLS-1$
					return this.save(sender);
				}
				if (args[0].equalsIgnoreCase("reload")) { //$NON-NLS-1$
					return this.reload(sender);
				}

			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("import")) { //$NON-NLS-1$
					return this.importDB(sender, args);
				}
			}
		}
		return false;
	}

	@Override
	public File getBlockOwnerFile() {
		return blockOwnerFile;
	}

	public WorldEditPlugin getWorldEdit() {
		return worldEdit;
	}

	private boolean save(CommandSender sender) {
		if (this.owning instanceof ClassicOwning) {
			if (this.owning.save()) {
				this.tell(sender, ChatColor.GREEN,
						Messages.getString("BlockOwn.47")); //$NON-NLS-1$
				return true;
			} else {
				this.tell(sender, ChatColor.RED,
						Messages.getString("BlockOwn.48")); //$NON-NLS-1$
				return false;
			}
		} else {
			this.tell(sender, Messages.getString("BlockOwn.100")); //$NON-NLS-1$
			return true;
		}
	}

	private boolean importDB(CommandSender sender, String[] args) {
		if (!args[1].equalsIgnoreCase(owning.getType().toString())) {
			Owning oldOwning = null;
			if (args[1].equalsIgnoreCase(DatabaseType.CLASSIC.toString())) {
				oldOwning = new ClassicOwning(this);
			} else if (args[1].equalsIgnoreCase(DatabaseType.SQL_LOCAL
					.toString())) {
				try {
					oldOwning = new SQLOwningLocal(this);
				} catch (Exception e) {
					return false;
				}
			} else if (args[1].equalsIgnoreCase(DatabaseType.SQL_NETWORK
					.toString())) {

				try {
					oldOwning = new SQLOwningNetwork(this);
				} catch (ClassNotFoundException | MySQLNotConnectingException e) {
					this.tell(sender, ChatColor.RED,
							Messages.getString("BlockOwn.29")); //$NON-NLS-1$
					return false;
				}

			} else {
				return false;
			}
			Thread importThread = new ImportThread(sender, this, oldOwning);
			importThread.start();
			this.tell(sender, ChatColor.GREEN,
					Messages.getString("BlockOwn.30")); //$NON-NLS-1$
			return true;
		} else {
			this.tell(sender, ChatColor.RED, Messages.getString("BlockOwn.32")); //$NON-NLS-1$
			return false;
		}
	}

	private boolean reload(CommandSender sender) {
		this.reloadConfig();
		playerSettings.save();
		this.saveConfig();
		FileConfiguration config = this.getConfig();
		updater.cancel();
		this.apiKey = Setting.API_KEY.getString(this);
		updater = new Updater(this, this.pluginId, this.getFile(), this.apiKey);
		if (Setting.ENABLE_AUTOUPDATE.getBoolean(this)) {
			updater.schedule(100l,
					Setting.AUTOSAVE_INTERVAL.getLong(this) * 1000);
			this.con(Messages.getString("BlockOwn.14")); //$NON-NLS-1$
		}
		if (autoSaveThread.isAlive()) {
			autoSaveThread.interrupt();
		}
		if (config.getBoolean(me.pheasn.owning.SQLOwning.Setting.MYSQL_ENABLE
				.toString())) {
			if (config.getString(
					me.pheasn.owning.SQLOwning.Setting.MYSQL_TYPE.toString())
					.equalsIgnoreCase(DatabaseType.SQL_LOCAL.toString())
					&& owning.getType() != DatabaseType.SQL_LOCAL) {
				owning.save();
				try {
					owning = new SQLOwningLocal(this);
				} catch (Exception e) {
					this.tell(sender, ChatColor.RED,
							Messages.getString("BlockOwn.1")); //$NON-NLS-1$
					this.getServer().getPluginManager().disablePlugin(this);
				}
			} else if (owning.getType() != DatabaseType.SQL_NETWORK) {
				owning.save();
				try {
					owning = new SQLOwningNetwork(this);
				} catch (Exception e) {
					this.tell(sender, ChatColor.RED,
							Messages.getString("BlockOwn.5")); //$NON-NLS-1$
					this.getServer().getPluginManager().disablePlugin(this);
				}
			}
		} else if (owning.getType() != DatabaseType.CLASSIC) {
			owning.save();
			owning = new ClassicOwning(this);
		}
		if (config.getLong(Setting.AUTOSAVE_INTERVAL.toString()) != 0
				&& owning.getType().equals(DatabaseType.CLASSIC)) {
			autoSaveThread = new AutoSaveThread(this);
			autoSaveThread.start();
		}
		this.tell(sender, ChatColor.GREEN, Messages.getString("BlockOwn.50")); //$NON-NLS-1$
		return true;
	}

	private void registerCommands() {
		this.getCommand(Commands.BLOCKOWN.toString()).setExecutor(this);
		this.getCommand(Commands.BLOCKOWN.toString()).setUsage(
				ChatColor.RED + Messages.getString("BlockOwn.6")); //$NON-NLS-1$
		this.getCommand(Commands.BLOCKOWN.toString()).setDescription(
				Messages.getString("BlockOwn.8")); //$NON-NLS-1$
		this.getCommand(Commands.OWN.toString()).setExecutor(new CE_Own(this));
		this.getCommand(Commands.OWN.toString()).setUsage(
				ChatColor.RED + Messages.getString("BlockOwn.11")); //$NON-NLS-1$
		this.getCommand(Commands.OWN.toString()).setDescription(
				Messages.getString("BlockOwn.13")); //$NON-NLS-1$
		this.getCommand(Commands.UNOWN.toString()).setExecutor(
				new CE_Unown(this));
		this.getCommand(Commands.UNOWN.toString()).setUsage(
				ChatColor.RED + Messages.getString("BlockOwn.16")); //$NON-NLS-1$
		this.getCommand(Commands.UNOWN.toString()).setDescription(
				Messages.getString("BlockOwn.18")); //$NON-NLS-1$
		this.getCommand(Commands.OWNER.toString()).setExecutor(
				new CE_Owner(this));
		this.getCommand(Commands.OWNER.toString()).setUsage(
				ChatColor.RED + Messages.getString("BlockOwn.21")); //$NON-NLS-1$
		this.getCommand(Commands.OWNER.toString()).setDescription(
				Messages.getString("BlockOwn.23")); //$NON-NLS-1$
		if (!Setting.CASCADE_PROTECTION_COMMANDS.getBoolean(this)) {
			this.getCommand(Commands.PROTECT.toString()).setExecutor(
					new CE_Protect(this));
			this.getCommand(Commands.PROTECT.toString()).setUsage(
					ChatColor.RED + Messages.getString("BlockOwn.26")); //$NON-NLS-1$
			this.getCommand(Commands.PROTECT.toString()).setDescription(
					Messages.getString("BlockOwn.28")); //$NON-NLS-1$
			this.getCommand(Commands.UNPROTECT.toString()).setExecutor(
					new CE_Unprotect(this));
			this.getCommand(Commands.UNPROTECT.toString()).setUsage(
					ChatColor.RED + Messages.getString("BlockOwn.31")); //$NON-NLS-1$
			this.getCommand(Commands.UNPROTECT.toString()).setDescription(
					Messages.getString("BlockOwn.33")); //$NON-NLS-1$
			this.getCommand(Commands.WHITELIST.toString()).setExecutor(
					new CE_Whitelist(this));
			this.getCommand(Commands.WHITELIST.toString()).setUsage(
					ChatColor.RED
							+ Messages.getString(Messages
									.getString("BlockOwn.2"))); //$NON-NLS-1$
			this.getCommand(Commands.WHITELIST.toString()).setDescription(
					Messages.getString(Messages.getString("BlockOwn.4"))); //$NON-NLS-1$
			this.getCommand(Commands.UNWHITELIST.toString()).setExecutor(
					new CE_Unwhitelist(this));
			this.getCommand(Commands.UNWHITELIST.toString()).setUsage(
					ChatColor.RED
							+ Messages.getString(Messages
									.getString("BlockOwn.9"))); //$NON-NLS-1$
			this.getCommand(Commands.UNWHITELIST.toString()).setDescription(
					Messages.getString(Messages.getString("BlockOwn.12"))); //$NON-NLS-1$
			this.getCommand(Commands.PROTECTION.toString()).setExecutor(
					new CE_Protection(this));
			this.getCommand(Commands.PROTECTION.toString()).setUsage(
					Messages.getString("BlockOwn.17")); //$NON-NLS-1$
			this.getCommand(Commands.PROTECTION.toString()).setDescription(
					Messages.getString("BlockOwn.20")); //$NON-NLS-1$
		} else {
			this.unRegisterBukkitCommand(this.getCommand(Commands.PROTECTION
					.toString()));
			this.unRegisterBukkitCommand(this.getCommand(Commands.WHITELIST
					.toString()));
			this.unRegisterBukkitCommand(this.getCommand(Commands.UNWHITELIST
					.toString()));
			this.unRegisterBukkitCommand(this.getCommand(Commands.PROTECT
					.toString()));
			this.unRegisterBukkitCommand(this.getCommand(Commands.UNPROTECT
					.toString()));
		}
		this.getCommand(Commands.MAKE_POOR.toString()).setExecutor(
				new CE_MakePoor(this));
		this.getCommand(Commands.MAKE_POOR.toString()).setUsage(
				Messages.getString("BlockOwn.3")); //$NON-NLS-1$
		this.getCommand(Commands.MAKE_POOR.toString()).setDescription(
				Messages.getString("BlockOwn.7")); //$NON-NLS-1$
	}

	private void registerEvents() {
		this.getServer().getPluginManager()
				.registerEvents(new L_BlockClick(this), this);
		this.getServer().getPluginManager()
				.registerEvents(new L_BlockPlace(this), this);
		this.getServer().getPluginManager()
				.registerEvents(new L_BlockBreak(this), this);
		this.getServer().getPluginManager()
				.registerEvents(new L_StructureGrow(this), this);
	}

	private void createEnv() {
		if (!pluginDir.exists()) {
			try {
				this.con(ChatColor.YELLOW, Messages.getString("BlockOwn.34")); //$NON-NLS-1$
				pluginDir.mkdir();
				blockOwnerFile.createNewFile();
				this.saveDefaultConfig();
			} catch (IOException ex) {
				this.con(ChatColor.RED, Messages.getString("BlockOwn.35")); //$NON-NLS-1$
			}
		}
		if (!blockOwnerFile.exists()) {
			try {
				this.con(ChatColor.YELLOW, Messages.getString("BlockOwn.36")); //$NON-NLS-1$
				blockOwnerFile.createNewFile();
			} catch (IOException ex) {
				this.con(ChatColor.RED, Messages.getString("BlockOwn.37")); //$NON-NLS-1$
			}
		}
		if (!settingsFile.exists()) {
			this.con(ChatColor.YELLOW, Messages.getString("BlockOwn.38")); //$NON-NLS-1$
			this.saveDefaultConfig();
		}
	}

	private boolean establishOwning() {
		try {
			if (this.getConfig().getBoolean(
					me.pheasn.owning.SQLOwning.Setting.MYSQL_ENABLE.toString())) {
				if (this.getConfig()
						.getString(
								me.pheasn.owning.SQLOwning.Setting.MYSQL_TYPE
										.toString()).equalsIgnoreCase("local")) { //$NON-NLS-1$
					owning = new SQLOwningLocal(this);
				} else {
					owning = new SQLOwningNetwork(this);
				}
			} else {
				owning = new ClassicOwning(this);
			}
			this.con(Messages.getString("BlockOwn.0")); //$NON-NLS-1$
			return true;
		} catch (ClassNotFoundException e1) {
			return false;
		} catch (MySQLNotConnectingException e2) {
			this.con(ChatColor.RED, Messages.getString("BlockOwn.86")); //$NON-NLS-1$
			Bukkit.getPluginManager().disablePlugin(this);
			return false;
		}
	}

	private void cleanUpOldSettings() {
		FileConfiguration config = this.getConfig();
		config.set(Setting.API_KEY_old.toString(), null);
		config.set(Setting.AUTOUPDATE_INTERVAL_old.toString(), null);
		config.set(Setting.ENABLE_AUTOUPDATE_old.toString(), null);
		this.saveConfig();
	}

	private void initializeMetrics() {
		try {
			Metrics metrics = new Metrics(this);

			Graph owningSystemGraph = metrics
					.createGraph("Type of owning system used"); //$NON-NLS-1$
			owningSystemGraph.addPlotter(new Plotter("LocalSQL") { //$NON-NLS-1$
						@Override
						public int getValue() {
							if (owning instanceof SQLOwningLocal) {
								return 1;
							} else {
								return 0;
							}
						}
					});
			owningSystemGraph.addPlotter(new Plotter("NetworkSQL") { //$NON-NLS-1$
						@Override
						public int getValue() {
							if (owning instanceof SQLOwningNetwork) {
								return 1;
							} else {
								return 0;
							}
						}
					});
			owningSystemGraph.addPlotter(new Plotter("Classic") { //$NON-NLS-1$
						@Override
						public int getValue() {
							if (owning instanceof ClassicOwning) {
								return 1;
							} else {
								return 0;
							}
						}
					});
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
	}

	// CREDITS FOR THIS GO TO zeeveener !
	private static Object getPrivateField(Object object, String field)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = object.getClass();
		Field objectField = clazz.getDeclaredField(field);
		objectField.setAccessible(true);
		Object result = objectField.get(object);
		objectField.setAccessible(false);
		return result;
	}

	private void unRegisterBukkitCommand(PluginCommand cmd) {
		try {
			Object result = getPrivateField(
					this.getServer().getPluginManager(), "commandMap"); //$NON-NLS-1$
			SimpleCommandMap commandMap = (SimpleCommandMap) result;
			Object map = getPrivateField(commandMap, "knownCommands"); //$NON-NLS-1$
			@SuppressWarnings("unchecked")
			HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
			knownCommands.remove(cmd.getName());
			for (String alias : cmd.getAliases()) {
				if (knownCommands.containsKey(alias)
						&& knownCommands.get(alias).toString()
								.contains(this.getName())) {
					knownCommands.remove(alias);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
