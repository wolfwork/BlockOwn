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
		pluginDir = new File("./plugins/" + this.getName() + "/");
		blockOwnerFile = new File(pluginDir.getPath() + "/blocks.dat");
		settingsFile = new File(pluginDir.getPath() + "/config.yml");
		this.getCommand("blockown").setExecutor(this);
		this.getCommand("blockown").setUsage(
				ChatColor.RED + "Appropriate usage: /<command> <reload / save>");
		this.getCommand("blockown")
				.setDescription(
						"Perform administrator commands.");
		this.getCommand("own").setExecutor(new CE_Own(this));
		this.getCommand("own").setUsage(
				ChatColor.RED + "Appropriate usage: /<command>");
		this.getCommand("own")
				.setDescription(
						"Gives you the ownership of the targeted block without having to replace it.");
		this.getCommand("unown").setExecutor(new CE_Unown(this));
		this.getCommand("unown").setUsage(
				ChatColor.RED + "Appropriate usage: /<command>");
		this.getCommand("unown").setDescription(
				"Deletes your ownership from a block.");
		this.getCommand("owner").setExecutor(new CE_Owner(this));
		this.getCommand("owner").setUsage(
				ChatColor.RED + "Appropriate usage: /<command>");
		this.getCommand("owner").setDescription(
				"Shows you who owns the block you are targeting.");
		this.getCommand("protect").setExecutor(new CE_Protect(this));
		this.getCommand("protect")
				.setUsage(
						ChatColor.RED
								+ "Appropriate usage: /<command> <player> <(Optional) blockType>");
		this.getCommand("protect")
				.setDescription(
						"Protects the targeted (or specified) BlockType against specified Player.");
		this.getCommand("unprotect").setExecutor(new CE_Unprotect(this));
		this.getCommand("unprotect")
				.setUsage(
						ChatColor.RED
								+ "Appropriate usage: /<command> <player> <(Optional) blockType>");
		this.getCommand("unprotect")
				.setDescription(
						"Reverts protection of the targeted (or specified) BlockType against specified Player.");
		this.getServer().getPluginManager()
				.registerEvents(new L_BlockClick(this), this);
		if (!pluginDir.exists()) {
			try {
				this.con(ChatColor.YELLOW,
						"Plugin environment is being prepared for first usage...");
				pluginDir.mkdir();
				blockOwnerFile.createNewFile();
				this.saveDefaultConfig();
			} catch (IOException ex) {
				this.con(ChatColor.RED,
						"Error while setting up plugin environment!");
			}
		}
		if (!blockOwnerFile.exists()) {
			try {
				this.con(ChatColor.YELLOW,
						"BlockOwners file not found. Creating a new one...");
				blockOwnerFile.createNewFile();
			} catch (IOException ex) {
				this.con(ChatColor.RED,
						"Error while setting up plugin environment!");
			}
		}
		if (!settingsFile.exists()) {
			this.con(ChatColor.YELLOW,
					"config.yml not found. Creating a new one...");
			this.saveDefaultConfig();
		}
		owning = new Owning(this);
		playerSettings = new PlayerSettings(this);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmd_label, String[] args){
		boolean isPlayer = sender instanceof Player;
		if(isPlayer&&!((Player)sender).hasPermission("blockown.admin")){
			return false;
		}
		if(args[0].equalsIgnoreCase("save")){
			return this.owning.save();
		}
		if(args[0].equalsIgnoreCase("reload")){
			this.reloadConfig();
			playerSettings.save();
			this.saveConfig();
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
		return "[" + s + "]";
	}

	public String serverNameInBrackets() {
		return this.inBrackets(this.getServer().getServerName());
	}
}
