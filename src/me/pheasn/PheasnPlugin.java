package me.pheasn;

import java.io.File;

import me.pheasn.owning.Owning;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PheasnPlugin extends JavaPlugin {
	public boolean updatePending = false;
	public Owning owning = null;
	protected ConsoleCommandSender console;

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

	public String inBrackets(String s) {
		return "[" + s + "] "; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String serverNameInBrackets() {
		return this.inBrackets(this.getServer().getServerName());
	}

	public Owning getOwning() {
		return this.owning;
	}

	public abstract File getBlockOwnerFile();
}
