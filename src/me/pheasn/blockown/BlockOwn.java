package me.pheasn.blockown;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import me.pheasn.Base.Use;
import me.pheasn.Database;
import me.pheasn.OwningDatabase;
import me.pheasn.PheasnPlugin;
import me.pheasn.blockown.commands.CE_Friend;
import me.pheasn.blockown.commands.CE_List_Friends;
import me.pheasn.blockown.commands.CE_List_Private;
import me.pheasn.blockown.commands.CE_List_Protected;
import me.pheasn.blockown.commands.CE_MakePoor;
import me.pheasn.blockown.commands.CE_Own;
import me.pheasn.blockown.commands.CE_Owner;
import me.pheasn.blockown.commands.CE_Privatize;
import me.pheasn.blockown.commands.CE_Protect;
import me.pheasn.blockown.commands.CE_Protection;
import me.pheasn.blockown.commands.CE_Unfriend;
import me.pheasn.blockown.commands.CE_Unown;
import me.pheasn.blockown.commands.CE_Unprivatize;
import me.pheasn.blockown.commands.CE_Unprotect;
import me.pheasn.blockown.listeners.L_BlockBreak;
import me.pheasn.blockown.listeners.L_BlockBurn;
import me.pheasn.blockown.listeners.L_BlockClick;
import me.pheasn.blockown.listeners.L_BlockFade;
import me.pheasn.blockown.listeners.L_BlockPlace_Check;
import me.pheasn.blockown.listeners.L_BlockPlace_NoCheck;
import me.pheasn.blockown.listeners.L_StructureGrow;
import me.pheasn.blockown.owning.ClassicOwning;
import me.pheasn.blockown.owning.ImportThread;
import me.pheasn.blockown.owning.MySQLNotConnectingException;
import me.pheasn.blockown.owning.Owning;
import me.pheasn.blockown.owning.Owning.DatabaseType;
import me.pheasn.blockown.owning.SQLOwningLocal;
import me.pheasn.blockown.owning.SQLOwningNetwork;
import me.pheasn.pluginupdater.Updater;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;
import org.mcstats.Metrics.Plotter;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class BlockOwn extends PheasnPlugin {
	private PlayerSettings playerSettings;
	private Owning owning = null;
	private File blockOwnerFile;
	private File protectionsFile;
	private Updater updater;
	private Thread autoSaveThread;
	private final int pluginId = 62749;

	// DEPENDENCIES
	private WorldEditPlugin worldEdit = null;
	private Economy economy = null;

	public enum Setting {
		SETTINGS_VERSION("Settings-Version"), //$NON-NLS-1$

		// ServerSettings
		ENABLE("ServerSettings.enable"), //$NON-NLS-1$	
		DISABLE_IN_WORLDS("ServerSettings.disableInWorlds"), //$NON-NLS-1$
		SERVER_NAME("ServerSettings.serverName"), //$NON-NLS-1$
		AUTOSAVE_INTERVAL("ServerSettings.autoSaveInterval"), //$NON-NLS-1$
		ENABLE_OWNED_BLOCK_DROPS("ServerSettings.enableOwnedBlockDrops"), //$NON-NLS-1$

		// OptionalPermissions
		PERMISSION_NEEDED_OWN_PLACE("ServerSettings.PermissionNeeded.OwnThroughBlockPlace"), //$NON-NLS-1$
		PERMISSION_NEEDED_OWN_COMMAND("ServerSettings.PermissionNeeded.OwnThroughCommand"), //$NON-NLS-1$
		PERMISSION_NEEDED_UNOWN_COMMAND("ServerSettings.PermissionNeeded.UnownThroughCommand"), //$NON-NLS-1$
		PERMISSION_NEEDED_FRIEND_COMMAND("ServerSettings.PermissionNeeded.FriendCommand"), //$NON-NLS-1$
		PERMISSION_NEEDED_PROTECT_AND_PRIVATIZE_COMMAND("ServerSettings.PermissionNeeded.ProtectAndPrivatizeCommand"), //$NON-NLS-1$

		// Protection
		PROTECTION_ENABLE("ServerSettings.Protection.enable"), //$NON-NLS-1$
		PROTECTION_CASCADE("ServerSettings.Protection.cascadeCommands"), //$NON-NLS-1$
		PROTECTION_ENABLE_MESSAGES("ServerSettings.Protection.enableMessages"), //$NON-NLS-1$
		PROTECTION_ONLY_LEFT_CLICKS("ServerSettings.Protection.onlyLeftClicks"), //$NON-NLS-1$
		PROTECTION_AUTO_CHEST("ServerSettings.Protection.autoProtectChests"), //$NON-NLS-1$
		PROTECTION_AUTO_EVERYTHING(	"ServerSettings.Protection.autoProtectEverything"), //$NON-NLS-1$
		PROTECTION_RADIUS("ServerSettings.Protection.radius"), //$NON-NLS-1$

		// ECONOMY
		ECONOMY_ENABLE("ServerSettings.Economy.enable"), //$NON-NLS-1$
		ECONOMY_PRICE_PROTECT("ServerSettings.Economy.protectPrice"), //$NON-NLS-1$
		ECONOMY_PRICE_PRIVATIZE("ServerSettings.Economy.privatizePrice"), //$NON-NLS-1$
		ECONOMY_PRICE_OWN_SELECTION("ServerSettings.Economy.ownSelectionPricePerBlock"), //$NON-NLS-1$

		// very OLD 
		@Deprecated
		ENABLE_AUTOUPDATE_old("ServerSettings.enableAutoUpdate"), //$NON-NLS-1$
		@Deprecated
		ENABLE_AUTOUPDATE_old2("ServerSettings.AutoUpdater.enableAutoUpdate"), //$NON-NLS-1$
		@Deprecated
		API_KEY_old("ServerSettings.apiKey"), //$NON-NLS-1$
		@Deprecated
		AUTOUPDATE_INTERVAL_old("ServerSettings.autoUpdateInterval"), //$NON-NLS-1$

		// Old since 0.7.3
		@Deprecated
		OLD_ENABLE_PLAYERSETTINGS("ServerSettings.enablePlayerSettings"), //$NON-NLS-1$
		@Deprecated
		OLD_PROTECT_ONLY_LEFT_CLICKS("ServerSettings.protectOnlyLeftClicks"), //$NON-NLS-1$
		@Deprecated
		OLD_ENABLE_PROTECTED_MESSAGES("ServerSettings.enableProtectedMessages"), //$NON-NLS-1$
		@Deprecated
		OLD_ENABLE_AUTOMATIC_CHEST_PROTECTION("ServerSettings.enableAutomaticChestProtection"), //$NON-NLS-1$
		@Deprecated
		OLD_ENABLE_AUTOMATIC_UNIVERSAL_PROTECTION("ServerSettings.enableAutomaticUniversalProtection"), //$NON-NLS-1$
		@Deprecated
		OLD_ADMINS_IGNORE_PROTECTION("ServerSettings.adminsIgnoreProtection"), //$NON-NLS-1$
		@Deprecated
		OLD_CASCADE_PROTECTION_COMMANDS("ServerSettings.cascadeProtectionCommands"), //$NON-NLS-1$
		@Deprecated
		OLD_PERMISSION_NEEDED_FOR_PROTECT_COMMAND("ServerSettings.permissionNeededForProtectCommand"), //$NON-NLS-1$
		@Deprecated
		OLD_PERMISSION_NEEDED_FOR_OWN_COMMAND("ServerSettings.permissionNeededForOwnCommand"), //$NON-NLS-1$
		@Deprecated
		OLD_PERMISSION_NEEDED_FOR_OWNING("ServerSettings.permissionNeededForOwning"), //$NON-NLS-1$
		@Deprecated
		OLD_RADIUS_BLOCK_PLACE_DENIED("ServerSettings.radiusBlockPlaceDenied"), //$NON-NLS-1$

		// Old since 0.7.4
		@Deprecated
		OLD_PROTECTION_ADMINS_IGNORE_PROTECTION("ServerSettings.Protection.adminsIgnoreProtection"); //$NON-NLS-1$

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

		public List<String> getStringList(PheasnPlugin plugin) {
			return plugin.getConfig().getStringList(s);
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

		public double getDouble(PheasnPlugin plugin) {
			return plugin.getConfig().getDouble(s);
		}

		/**
		 * Sets the value to the specified Object and
		 * deletes deprecated Setting oldSetting.
		 * @param plugin plugin which provides the config to change
		 * @param value new value
		 * @param oldSetting old, deprecated Setting which is set to null
		 */
		public void update(PheasnPlugin plugin, Object value, Setting oldSetting) {
			plugin.getConfig().set(s, value);
			plugin.getConfig().set(oldSetting.toString(), null);
		}

		public void set(PheasnPlugin plugin, Object value){
			plugin.getConfig().set(s, value);
		}

	}

	public enum Commands {
		OWNER("owner"), //$NON-NLS-1$
		OWN("own"), //$NON-NLS-1$
		UNOWN("unown"), //$NON-NLS-1$
		BLOCKOWN("blockown"), //$NON-NLS-1$
		PROTECT("protect"), //$NON-NLS-1$
		UNPROTECT("unprotect"), //$NON-NLS-1$
		PROTECTION("protection"), //$NON-NLS-1$
		MAKE_POOR("makepoor"), //$NON-NLS-1$
		PRIVATIZE("privatize"), //$NON-NLS-1$
		UNPRIVATIZE("unprivatize"), //$NON-NLS-1$
		FRIEND("friend"), //$NON-NLS-1$
		UNFRIEND("unfriend"); //$NON-NLS-1$
		private String s;

		private Commands(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return s;
		}

		public PluginCommand getCommand(PheasnPlugin plugin) {
			return plugin.getCommand(s);
		}
	}

	public enum Permission {
		PROTECT_AND_PRIVATIZE("blockown.protect"), //$NON-NLS-1$
		ADMIN("blockown.admin"), //$NON-NLS-1$
		OWN_PLACE("blockown.own.place"), //$NON-NLS-1$
		OWN_PLACE_CREATIVE("blockown.own.place.creative"), //$NON-NLS-1$
		OWN_COMMAND("blockown.own.command"), //$NON-NLS-1$
		OWN_COMMAND_CREATIVE("blockown.own.command.creative"), //$NON-NLS-1$
		UNOWN("blockown.unown"), //$NON-NLS-1$
		FRIEND("blockown.friend"), //$NON-NLS-1$
		IGNORE_PROTECTION("blockown.ignore"), //$NON-NLS-1$
		MAKE_POOR("blockown.makepoor"); //$NON-NLS-1$

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
		if (updater != null) {
			updater.cancel();
		}
		if (this.autoSaveThread != null && this.autoSaveThread.isAlive()) {
			autoSaveThread.interrupt();
		}
		super.onDisable();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		console = this.getServer().getConsoleSender();
		if(this.findBase()){
			this.getBase().registerAddon(Use.OWNING, this, true);
			this.getBase().registerAddon(Use.PROTECTION, this, true);
		}
		blockOwnerFile = new File(this.getPluginDirectory().getPath() + "/blocks.dat"); //$NON-NLS-1$
		protectionsFile = new File(this.getPluginDirectory().getPath() + "/playerSettings.yml"); //$NON-NLS-1$
		this.createEnv();
		if (!Setting.ENABLE.getBoolean(this)) {
			this.con(ChatColor.YELLOW,
					Messages.getString("BlockOwn.enabled.false")); //$NON-NLS-1$
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		this.registerCommands();
		this.registerEvents();
		if (!this.establishOwning()) {
			return;
		}
		this.playerSettings = new PlayerSettings(this);
		this.getConfig().options().copyDefaults(true);
		this.cleanUpOldSettings();
		Setting.SETTINGS_VERSION.set(this, this.getDescription().getVersion());
		this.saveConfig();
		this.initializeMetrics();

		// enable AutoUpdater
		updater = new Updater(this, this.pluginId, this.getFile(),
				me.pheasn.pluginupdater.Updater.Setting.API_KEY.getString(this));
		if (me.pheasn.pluginupdater.Updater.Setting.ENABLE_AUTOUPDATE
				.getBoolean(this)) {
			updater.schedule(100l,
					me.pheasn.pluginupdater.Updater.Setting.AUTOUPDATE_INTERVAL
							.getLong(this) * 1000);
			this.con(Messages.getString("BlockOwn.updater.started")); //$NON-NLS-1$
		}

		// enable AutoSaver
		autoSaveThread = new AutoSaveThread(this);
		if (Setting.AUTOSAVE_INTERVAL.getLong(this) != 0) {
			autoSaveThread.start();
		}

		// Soft dependency to WorldEdit
		worldEdit = (WorldEditPlugin) this.getServer().getPluginManager()
				.getPlugin("WorldEdit"); //$NON-NLS-1$
		if (worldEdit != null) {
			this.con(Messages.getString("BlockOwn.dependency.worldedit")); //$NON-NLS-1$
		}

		// Soft dependency to Vault
		try {
			if (this.getServer().getPluginManager().getPlugin("Vault") != null) { //$NON-NLS-1$
				RegisteredServiceProvider<Economy> rsp = this.getServer()
						.getServicesManager().getRegistration(Economy.class);
				if (rsp != null) {
					economy = rsp.getProvider();
				}
				if (economy != null) {
					this.con(Messages.getString("BlockOwn.dependency.vault")); //$NON-NLS-1$
				}
			}
		} catch (NullPointerException e) {
			this.error(e);
		}
		this.con(Messages.getString("BlockOwn.enabled.true")); //$NON-NLS-1$
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (args.length > 0) {
			if (Setting.PROTECTION_CASCADE.getBoolean(this)) {
				String[] newargs;
				try {
					newargs = new String[args.length - 1];
				} catch (NegativeArraySizeException e) {
					newargs = new String[0];
				}
				for (int i = 1; i < args.length; i++) {
					newargs[i - 1] = args[i];
				}
				if (args[0].equalsIgnoreCase(Commands.FRIEND.toString())) {
					return new CE_Friend(this).onCommand(sender, cmd,
							cmd_label, newargs);
				}
				if (args[0].equalsIgnoreCase(Commands.UNFRIEND.toString())) {
					return new CE_Unfriend(this).onCommand(sender, cmd,
							cmd_label, newargs);
				}
				if (args[0].equalsIgnoreCase(Commands.PRIVATIZE.toString())) {
					return new CE_Privatize(this).onCommand(sender, cmd,
							cmd_label, newargs);
				}
				if (args[0].equalsIgnoreCase(Commands.UNPRIVATIZE.toString())) {
					return new CE_Unprivatize(this).onCommand(sender, cmd,
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
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("list")) { //$NON-NLS-1$
					if (args[1].equalsIgnoreCase("private")) { //$NON-NLS-1$
						return new CE_List_Private(this).onCommand(sender, cmd,
								cmd_label, new String[0]);
					}
					if (args[1].equalsIgnoreCase("protected")) { //$NON-NLS-1$
						return new CE_List_Protected(this).onCommand(sender,
								cmd, cmd_label, new String[0]);
					}
					if (args[1].equalsIgnoreCase("friends")) { //$NON-NLS-1$
						return new CE_List_Friends(this).onCommand(sender, cmd,
								cmd_label, new String[0]);
					}
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
						Messages.getString("noPermission") + String.valueOf(((Player) sender).hasPermission(Permission.ADMIN.toString()))); //$NON-NLS-1$ 
				return true;
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

	public File getBlockOwnerFile() {
		return blockOwnerFile;
	}

	public File getProtectionsFile() {
		return this.protectionsFile;
	}

	public WorldEditPlugin getWorldEdit() {
		return this.worldEdit;
	}

	public PlayerSettings getPlayerSettings() {
		return this.playerSettings;
	}

	public Economy getEconomy() {
		return this.economy;
	}

	private boolean save(CommandSender sender) {
		if (this.owning instanceof ClassicOwning) {
			if (this.owning.save()) {
				this.tell(sender, ChatColor.GREEN,
						Messages.getString("BlockOwn.owner.save.success")); //$NON-NLS-1$
				return true;
			} else {
				this.tell(sender, ChatColor.RED,
						Messages.getString("BlockOwn.owner.save.error")); //$NON-NLS-1$
				return false;
			}
		} else {
			this.tell(sender,
					Messages.getString("BlockOwn.owner.save.unneccessary")); //$NON-NLS-1$
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
				} catch (ClassNotFoundException e) {
					this.tell(sender, ChatColor.RED,
							Messages.getString("BlockOwn.import.connectError")); //$NON-NLS-1$
					return false;
				} catch (MySQLNotConnectingException e) {
					this.tell(sender, ChatColor.RED,
							Messages.getString("BlockOwn.import.connectError")); //$NON-NLS-1$
					return false;
				}

			} else {
				return false;
			}
			Thread importThread = new ImportThread(sender, this, oldOwning);
			importThread.start();
			this.tell(sender, ChatColor.GREEN,
					Messages.getString("BlockOwn.import.started")); //$NON-NLS-1$
			return true;
		} else {
			this.tell(sender, ChatColor.RED,
					Messages.getString("BlockOwn.import.unneccessary")); //$NON-NLS-1$
			return false;
		}
	}

	private boolean reload(CommandSender sender) {
		this.reloadConfig();
		playerSettings.save();
		FileConfiguration config = this.getConfig();
		updater.cancel();
		updater = new Updater(this, this.pluginId, this.getFile(),
				me.pheasn.pluginupdater.Updater.Setting.API_KEY.getString(this));
		if (me.pheasn.pluginupdater.Updater.Setting.ENABLE_AUTOUPDATE
				.getBoolean(this)) {
			updater.schedule(100l,
					Setting.AUTOSAVE_INTERVAL.getLong(this) * 1000);
			this.con(Messages.getString("BlockOwn.updater.started")); //$NON-NLS-1$
		}

		if (autoSaveThread.isAlive()) {
			autoSaveThread.interrupt();
		}

		if (config
				.getBoolean(me.pheasn.blockown.owning.SQLOwning.Setting.MYSQL_ENABLE
						.toString())) {
			if (config.getString(
					me.pheasn.blockown.owning.SQLOwning.Setting.MYSQL_TYPE
							.toString()).equalsIgnoreCase(
					DatabaseType.SQL_LOCAL.toString())
					&& owning.getType() != DatabaseType.SQL_LOCAL) {
				owning.save();
				try {
					owning = new SQLOwningLocal(this);
				} catch (Exception e) {
					this.tell(sender, ChatColor.RED, Messages
							.getString("BlockOwn.database.switch.error")); //$NON-NLS-1$
					this.getServer().getPluginManager().disablePlugin(this);
				}
			} else if (owning.getType() != DatabaseType.SQL_NETWORK) {
				owning.save();
				try {
					owning = new SQLOwningNetwork(this);
				} catch (Exception e) {
					this.tell(sender, ChatColor.RED, Messages
							.getString("BlockOwn.database.switch.error")); //$NON-NLS-1$
					this.getServer().getPluginManager().disablePlugin(this);
				}
			}
		} else if (owning.getType() != DatabaseType.CLASSIC) {
			owning.save();
			owning = new ClassicOwning(this);
		}
		if (Setting.AUTOSAVE_INTERVAL.getLong(this) != 0) {
			autoSaveThread = new AutoSaveThread(this);
			autoSaveThread.start();
		}
		this.tell(sender, ChatColor.GREEN,
				Messages.getString("BlockOwn.reload.success")); //$NON-NLS-1$
		return true;
	}

	private void registerCommands() {

		Commands.BLOCKOWN.getCommand(this).setExecutor(this);
		Commands.BLOCKOWN.getCommand(this).setUsage(
				ChatColor.RED + Messages.getString("BlockOwn.usage.blockown")); //$NON-NLS-1$
		Commands.BLOCKOWN.getCommand(this).setDescription(
				Messages.getString("BlockOwn.description.blockown")); //$NON-NLS-1$

		Commands.OWN.getCommand(this).setExecutor(new CE_Own(this));
		Commands.OWN.getCommand(this).setUsage(
				ChatColor.RED + Messages.getString("BlockOwn.usage.own")); //$NON-NLS-1$
		Commands.OWN.getCommand(this).setDescription(
				Messages.getString("BlockOwn.description.own")); //$NON-NLS-1$

		Commands.UNOWN.getCommand(this).setExecutor(new CE_Unown(this));
		Commands.UNOWN.getCommand(this).setUsage(
				ChatColor.RED + Messages.getString("BlockOwn.usage.unown")); //$NON-NLS-1$
		Commands.UNOWN.getCommand(this).setDescription(
				Messages.getString("BlockOwn.description.unown")); //$NON-NLS-1$

		Commands.OWNER.getCommand(this).setExecutor(new CE_Owner(this));
		Commands.OWNER.getCommand(this).setUsage(
				ChatColor.RED + Messages.getString("BlockOwn.usage.owner")); //$NON-NLS-1$
		Commands.OWNER.getCommand(this).setDescription(
				Messages.getString("BlockOwn.description.owner")); //$NON-NLS-1$
		Commands.MAKE_POOR.getCommand(this).setExecutor(new CE_MakePoor(this));
		Commands.MAKE_POOR.getCommand(this).setUsage(
				Messages.getString("BlockOwn.usage.makepoor")); //$NON-NLS-1$
		Commands.MAKE_POOR.getCommand(this).setDescription(
				Messages.getString("BlockOwn.description.makepoor")); //$NON-NLS-1$
		if (!Setting.PROTECTION_CASCADE.getBoolean(this)) {
			Commands.PROTECT.getCommand(this).setExecutor(new CE_Protect(this));
			Commands.PROTECT.getCommand(this).setUsage(
					ChatColor.RED
							+ Messages.getString("BlockOwn.usage.protect")); //$NON-NLS-1$
			Commands.PROTECT.getCommand(this).setDescription(
					Messages.getString("BlockOwn.description.protect")); //$NON-NLS-1$

			Commands.UNPROTECT.getCommand(this).setExecutor(
					new CE_Unprotect(this));
			Commands.UNPROTECT.getCommand(this).setUsage(
					ChatColor.RED
							+ Messages.getString("BlockOwn.usage.unprotect")); //$NON-NLS-1$
			Commands.UNPROTECT.getCommand(this).setDescription(
					Messages.getString("BlockOwn.description.unprotect")); //$NON-NLS-1$

			Commands.PROTECTION.getCommand(this).setExecutor(
					new CE_Protection(this));
			Commands.PROTECTION.getCommand(this).setUsage(
					Messages.getString("BlockOwn.usage.protection")); //$NON-NLS-1$
			Commands.PROTECTION.getCommand(this).setDescription(
					Messages.getString("BlockOwn.description.protection")); //$NON-NLS-1$

			Commands.PRIVATIZE.getCommand(this).setExecutor(
					new CE_Privatize(this));
			Commands.PRIVATIZE.getCommand(this).setUsage(
					Messages.getString("BlockOwn.usage.privatize")); //$NON-NLS-1$
			Commands.PRIVATIZE.getCommand(this).setDescription(
					Messages.getString("BlockOwn.description.privatize")); //$NON-NLS-1$

			Commands.UNPRIVATIZE.getCommand(this).setExecutor(
					new CE_Unprivatize(this));
			Commands.UNPRIVATIZE.getCommand(this).setUsage(
					Messages.getString("BlockOwn.usage.unprivatize")); //$NON-NLS-1$
			Commands.UNPRIVATIZE.getCommand(this).setDescription(
					Messages.getString("BlockOwn.description.unprivatize")); //$NON-NLS-1$

			Commands.FRIEND.getCommand(this).setExecutor(new CE_Friend(this));
			Commands.FRIEND.getCommand(this).setUsage(
					Messages.getString("BlockOwn.usage.friend")); //$NON-NLS-1$
			Commands.FRIEND.getCommand(this).setDescription(
					Messages.getString("BlockOwn.description.friend")); //$NON-NLS-1$

			Commands.UNFRIEND.getCommand(this).setExecutor(
					new CE_Unfriend(this));
			Commands.UNFRIEND.getCommand(this).setUsage(
					Messages.getString("BlockOwn.usage.unfriend")); //$NON-NLS-1$
			Commands.UNFRIEND.getCommand(this).setDescription(
					Messages.getString("BlockOwn.description.unfriend")); //$NON-NLS-1$

		} else {
			this.unRegisterBukkitCommand(Commands.PROTECTION.getCommand(this));
			this.unRegisterBukkitCommand(Commands.PROTECT.getCommand(this));
			this.unRegisterBukkitCommand(Commands.UNPROTECT.getCommand(this));
			this.unRegisterBukkitCommand(Commands.PRIVATIZE.getCommand(this));
			this.unRegisterBukkitCommand(Commands.UNPRIVATIZE.getCommand(this));
			this.unRegisterBukkitCommand(Commands.FRIEND.getCommand(this));
			this.unRegisterBukkitCommand(Commands.UNFRIEND.getCommand(this));
		}
	}

	private void registerEvents() {
		this.getServer().getPluginManager()
				.registerEvents(new L_BlockClick(this), this);
		this.getServer().getPluginManager()
				.registerEvents(new L_BlockBreak(this), this);
		this.getServer().getPluginManager()
				.registerEvents(new L_BlockBurn(this), this);
		this.getServer().getPluginManager()
				.registerEvents(new L_BlockFade(this), this);
		this.getServer().getPluginManager()
				.registerEvents(new L_StructureGrow(this), this);
		if (Setting.PROTECTION_RADIUS.getInt(this) == 0) {
			this.getServer().getPluginManager()
					.registerEvents(new L_BlockPlace_NoCheck(this), this);
		} else {
			this.getServer().getPluginManager()
					.registerEvents(new L_BlockPlace_Check(this), this);
		}
	}

	private void createEnv() {
		if (!this.getPluginDirectory().exists()) {
			try {
				this.con(ChatColor.YELLOW,
						Messages.getString("BlockOwn.prepare.start")); //$NON-NLS-1$
				this.getPluginDirectory().mkdir();
				this.blockOwnerFile.createNewFile();
				this.protectionsFile.createNewFile();
				this.saveDefaultConfig();
				Setting.SETTINGS_VERSION.set(this, this.getDescription().getVersion());
			} catch (IOException ex) {
				this.con(ChatColor.RED,
						Messages.getString("BlockOwn.prepare.error")); //$NON-NLS-1$
			}
		}
		if (!blockOwnerFile.exists()) {
			try {
				this.con(ChatColor.YELLOW,
						Messages.getString("BlockOwn.prepare.new.owner")); //$NON-NLS-1$
				blockOwnerFile.createNewFile();
			} catch (IOException ex) {
				this.con(ChatColor.RED,
						Messages.getString("BlockOwn.prepare.error")); //$NON-NLS-1$
			}
		}
		if (!this.getConfigFile().exists()) {
			this.con(ChatColor.YELLOW,
					Messages.getString("BlockOwn.prepare.new.config")); //$NON-NLS-1$
			this.saveDefaultConfig();
			Setting.SETTINGS_VERSION.set(this, this.getDescription().getVersion());
		}
		if (!protectionsFile.exists()) {
			try {
				this.protectionsFile.createNewFile();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Connects to owning database
	 * @return false if couldn't connect to database.
	 */
	private boolean establishOwning() {
		try {
			if (this.getConfig().getBoolean(
					me.pheasn.blockown.owning.SQLOwning.Setting.MYSQL_ENABLE
							.toString())) {
				if (this.getConfig()
						.getString(
								me.pheasn.blockown.owning.SQLOwning.Setting.MYSQL_TYPE
										.toString()).equalsIgnoreCase("local")) { //$NON-NLS-1$
					owning = new SQLOwningLocal(this);
				} else {
					owning = new SQLOwningNetwork(this);
				}
			} else {
				owning = new ClassicOwning(this);
			}
			this.con(Messages.getString("BlockOwn.database.connect.success")); //$NON-NLS-1$
			return true;
		} catch (ClassNotFoundException e1) {
			return false;
		} catch (MySQLNotConnectingException e2) {
			this.con(ChatColor.RED,
					Messages.getString("BlockOwn.database.connect.error")); //$NON-NLS-1$
			this.getServer().getPluginManager().disablePlugin(this);
			return false;
		}
	}

	private void cleanUpOldSettings() {
		FileConfiguration config = this.getConfig();
		// VERY OLD
		config.set(Setting.API_KEY_old.toString(), null);
		config.set(Setting.AUTOUPDATE_INTERVAL_old.toString(), null);
		config.set(Setting.ENABLE_AUTOUPDATE_old.toString(), null);
		config.set(Setting.ENABLE_AUTOUPDATE_old2.toString(), null);
		this.getConfig().set("ServerSettings.AutoUpdater.enableAutoUpdater", null); //$NON-NLS-1$

		// 0.7.3 clean up
		if(compareVersions(Setting.SETTINGS_VERSION.getString(this), "0.7.3")==-1){
		Setting.PROTECTION_AUTO_CHEST.update(this, Setting.OLD_ENABLE_AUTOMATIC_CHEST_PROTECTION.getBoolean(this),Setting.OLD_ENABLE_AUTOMATIC_CHEST_PROTECTION);
		Setting.PROTECTION_AUTO_EVERYTHING.update(this, Setting.OLD_ENABLE_AUTOMATIC_UNIVERSAL_PROTECTION.getBoolean(this), Setting.OLD_ENABLE_AUTOMATIC_UNIVERSAL_PROTECTION);
		Setting.PROTECTION_CASCADE.update(this,Setting.OLD_CASCADE_PROTECTION_COMMANDS.getBoolean(this), Setting.OLD_CASCADE_PROTECTION_COMMANDS);
		Setting.PROTECTION_ENABLE.update(this, Setting.OLD_ENABLE_PLAYERSETTINGS.getBoolean(this), Setting.OLD_ENABLE_PLAYERSETTINGS);
		Setting.PROTECTION_ENABLE_MESSAGES.update(this, Setting.OLD_ENABLE_PROTECTED_MESSAGES.getBoolean(this), Setting.OLD_ENABLE_PROTECTED_MESSAGES);
		Setting.PROTECTION_ONLY_LEFT_CLICKS.update(this, Setting.OLD_PROTECT_ONLY_LEFT_CLICKS.getBoolean(this), Setting.OLD_PROTECT_ONLY_LEFT_CLICKS);
		Setting.PROTECTION_RADIUS.update(this, Setting.OLD_RADIUS_BLOCK_PLACE_DENIED.getInt(this), Setting.OLD_RADIUS_BLOCK_PLACE_DENIED);
		
		Setting.PERMISSION_NEEDED_OWN_COMMAND.update(this, Setting.OLD_PERMISSION_NEEDED_FOR_OWN_COMMAND.getBoolean(this),Setting.OLD_PERMISSION_NEEDED_FOR_OWN_COMMAND);
		Setting.PERMISSION_NEEDED_OWN_PLACE.update(this, Setting.OLD_PERMISSION_NEEDED_FOR_OWNING.getBoolean(this), Setting.OLD_PERMISSION_NEEDED_FOR_OWNING);
		Setting.PERMISSION_NEEDED_PROTECT_AND_PRIVATIZE_COMMAND.update(this, Setting.OLD_PERMISSION_NEEDED_FOR_PROTECT_COMMAND.getBoolean(this), Setting.OLD_PERMISSION_NEEDED_FOR_PROTECT_COMMAND);
		}

		// 0.7.4
		Setting.OLD_PROTECTION_ADMINS_IGNORE_PROTECTION.set(this, null);

		this.saveConfig();
	}

	/**
	 * Initializes PluginMetrics by Hidendra to submit stats
	 */
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

	public OwningDatabase getOwning() {
		OwningDatabase data;
		return ((data = (OwningDatabase) this.getAddonDatabase(Use.OWNING)) != null) ? data : this.owning;
	}

	@Override
	public String getServerNameSettingPath() {
		return Setting.SERVER_NAME.getString(this);
	}

	@Override
	public Database getDatabase(Use use) {
		switch(use){
			case PROTECTION: return this.playerSettings;
			case OWNING: return this.owning;
			default: return null;
		}
	}

	// Credits for this go to zeeveener !
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
	
	/**
	 * Unregisters a PluginCommand from Bukkit, not revertible!
	 * @param cmd PluginCommand that is going to be unregistered.
	 */
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
