package me.pheasn.blockown;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockOwn extends JavaPlugin {
	private ConsoleCommandSender console;
	public Owning owning;
	public PlayerSettings playerSettings;
	private File pluginDir;
	private File blockOwnerFile;
	private File settingsFile;

	@Override
	public void onDisable() {
		this.owning.save();
		this.playerSettings.save();
		this.saveConfig();
		super.onDisable();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		console = this.getServer().getConsoleSender();
		pluginDir = new File("./plugins/" + this.getName() + "/"); //$NON-NLS-1$ //$NON-NLS-2$
		blockOwnerFile = new File(pluginDir.getPath() + "/blocks.dat"); //$NON-NLS-1$
		settingsFile = new File(pluginDir.getPath() + "/config.yml"); //$NON-NLS-1$
		this.getCommand("blockown").setExecutor(this); //$NON-NLS-1$
		this.getCommand("blockown") //$NON-NLS-1$
				.setUsage(ChatColor.RED + Messages.getString("BlockOwn.6")); //$NON-NLS-1$
		this.getCommand("blockown").setDescription( //$NON-NLS-1$
				Messages.getString("BlockOwn.8")); //$NON-NLS-1$
		this.getCommand("own").setExecutor(new CE_Own(this)); //$NON-NLS-1$
		this.getCommand("own").setUsage( //$NON-NLS-1$
				ChatColor.RED + Messages.getString("BlockOwn.11")); //$NON-NLS-1$
		this.getCommand("own") //$NON-NLS-1$
				.setDescription(Messages.getString("BlockOwn.13")); //$NON-NLS-1$
		this.getCommand("unown").setExecutor(new CE_Unown(this)); //$NON-NLS-1$
		this.getCommand("unown").setUsage( //$NON-NLS-1$
				ChatColor.RED + Messages.getString("BlockOwn.16")); //$NON-NLS-1$
		this.getCommand("unown").setDescription( //$NON-NLS-1$
				Messages.getString("BlockOwn.18")); //$NON-NLS-1$
		this.getCommand("owner").setExecutor(new CE_Owner(this)); //$NON-NLS-1$
		this.getCommand("owner").setUsage( //$NON-NLS-1$
				ChatColor.RED + Messages.getString("BlockOwn.21")); //$NON-NLS-1$
		this.getCommand("owner").setDescription( //$NON-NLS-1$
				Messages.getString("BlockOwn.23")); //$NON-NLS-1$
		this.getCommand("protect").setExecutor(new CE_Protect(this)); //$NON-NLS-1$
		this.getCommand("protect") //$NON-NLS-1$
				.setUsage(ChatColor.RED + Messages.getString("BlockOwn.26")); //$NON-NLS-1$
		this.getCommand("protect") //$NON-NLS-1$
				.setDescription(Messages.getString("BlockOwn.28")); //$NON-NLS-1$
		this.getCommand("unprotect").setExecutor(new CE_Unprotect(this)); //$NON-NLS-1$
		this.getCommand("unprotect") //$NON-NLS-1$
				.setUsage(ChatColor.RED + Messages.getString("BlockOwn.31")); //$NON-NLS-1$
		this.getCommand("unprotect") //$NON-NLS-1$
				.setDescription(Messages.getString("BlockOwn.33")); //$NON-NLS-1$
		this.getCommand("whitelist").setExecutor(new CE_Whitelist(this));  //$NON-NLS-1$
		this.getCommand("whitelist")  //$NON-NLS-1$
				.setUsage(ChatColor.RED + Messages.getString(Messages.getString("BlockOwn.2")));  //$NON-NLS-1$
		this.getCommand("whitelist")  //$NON-NLS-1$
				.setDescription(Messages.getString(Messages.getString("BlockOwn.4"))); //$NON-NLS-1$
		this.getCommand("unwhitelist").setExecutor(new CE_Unwhitelist(this));  //$NON-NLS-1$
		this.getCommand("unwhitelist")  //$NON-NLS-1$
				.setUsage(ChatColor.RED + Messages.getString(Messages.getString("BlockOwn.9")));  //$NON-NLS-1$
		this.getCommand("unwhitelist")  //$NON-NLS-1$
				.setDescription(Messages.getString(Messages.getString("BlockOwn.12")));  //$NON-NLS-1$
		this.getCommand("protection").setExecutor(new CE_Protection(this)); //$NON-NLS-1$
		this.getCommand("protection").setUsage( //$NON-NLS-1$
				Messages.getString("BlockOwn.17")); //$NON-NLS-1$
		this.getCommand("protection") //$NON-NLS-1$
				.setDescription(
						Messages.getString("BlockOwn.20")); //$NON-NLS-1$
		this.getServer().getPluginManager()
				.registerEvents(new L_BlockClick(this), this);
		this.getServer().getPluginManager()
				.registerEvents(new L_BlockPlace(this), this);
		this.getServer().getPluginManager()
				.registerEvents(new L_BlockBreak(this), this);
		this.getServer().getPluginManager()
				.registerEvents(new L_StructureGrow(this), this);
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
		owning = new Owning(this);
		playerSettings = new PlayerSettings(this);
		if (!this.getConfig().getBoolean("ServerSettings.enable")) { //$NON-NLS-1$
			this.con(ChatColor.YELLOW, Messages.getString("BlockOwn.40")); //$NON-NLS-1$
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		} else {
			this.con(Messages.getString("BlockOwn.41")); //$NON-NLS-1$
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		boolean isPlayer = sender instanceof Player;
		if (isPlayer && !((Player) sender).hasPermission("blockown.admin")) { //$NON-NLS-1$
			this.tell(
					sender,
					ChatColor.RED,
					Messages.getString("BlockOwn.44") + String.valueOf(((Player) sender).hasPermission("blockown.admin"))); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		if (args[0].equalsIgnoreCase("save")) { //$NON-NLS-1$
			if (this.owning.save()) {
				this.tell(sender, ChatColor.GREEN,
						Messages.getString("BlockOwn.47")); //$NON-NLS-1$
				return true;
			} else {
				this.tell(sender, ChatColor.RED,
						Messages.getString("BlockOwn.48")); //$NON-NLS-1$
				return false;
			}
		}
		if (args[0].equalsIgnoreCase("reload")) { //$NON-NLS-1$
			this.reloadConfig();
			playerSettings.save();
			this.saveConfig();
			this.tell(sender, ChatColor.GREEN,
					Messages.getString("BlockOwn.50")); //$NON-NLS-1$
			return true;
		}
		return false;
	}

	public void con(ChatColor cc, String s) {
		console.sendMessage(cc + inBrackets(this.getName()) + s);
	}

	public void con(String s) {
		console.sendMessage(inBrackets(this.getName()) + s);
	}

	public void tell(CommandSender sender, ChatColor cc, String s) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage(cc + serverNameInBrackets() + s);
		} else {
			con(cc, s);
		}
	}

	public void tell(CommandSender sender, String s) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage(serverNameInBrackets() + s);
		} else {
			con(s);
		}
	}

	public void say(Player player, ChatColor cc, String s) {
		player.sendMessage(cc + serverNameInBrackets() + s);
	}

	public void say(Player player, String s) {
		player.sendMessage(serverNameInBrackets() + s);
	}

	public void say(ChatColor cc, String s) {
		this.getServer().broadcastMessage(cc + serverNameInBrackets() + s);
	}

	public void say(String s) {
		this.getServer().broadcastMessage(serverNameInBrackets() + s);
	}

	public File getPluginDir() {
		return pluginDir;
	}

	public File getSettingsFile() {
		return settingsFile;
	}

	public File getBlockOwnerFile() {
		return blockOwnerFile;
	}

	public String inBrackets(String s) {
		return "[" + s + "] "; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String serverNameInBrackets() {
		return this.inBrackets(this.getServer().getServerName());
	}
}
