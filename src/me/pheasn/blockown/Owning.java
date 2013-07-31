package me.pheasn.blockown;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Owning {
	private BlockOwn plugin;
	private HashMap<Block, String> ownings;

	public Owning(BlockOwn plugin) {
		this.plugin = plugin;
		ownings = new HashMap<Block, String>();
		this.load();
	}

	public boolean load() {
		if (plugin.getBlockOwnerFile().exists()) {
			try {
				FileReader reader = new FileReader(plugin.getBlockOwnerFile());
				BufferedReader bufferedReader = new BufferedReader(reader);
				String owning;
				ArrayList<String> worlds = new ArrayList<String>();
				ArrayList<String> players = new ArrayList<String>();
				for (World world : plugin.getServer().getWorlds()) {
					worlds.add(world.getName());
				}
				for (OfflinePlayer offlinePlayer : plugin.getServer()
						.getOfflinePlayers()) {
					players.add(offlinePlayer.getName());
				}
				while ((owning = bufferedReader.readLine()) != null) {
					String[] owningDiv = owning.split(":");
					String worldName = owningDiv[0];
					String[] BlockCoordinates = owningDiv[1].split("#");
					String playerName = owningDiv[2];
					if (worlds.contains(worldName)
							&& players.contains(playerName)) {
						ownings.put(
								plugin.getServer()
										.getWorld(worldName)
										.getBlockAt(
												Integer.valueOf(BlockCoordinates[0]),
												Integer.valueOf(BlockCoordinates[1]),
												Integer.valueOf(BlockCoordinates[2])),
								playerName);
					}
				}
				bufferedReader.close();
				reader.close();
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, "BlockOwners file not found.");
			return false;
		}
	}

	public boolean save() {
		if (!plugin.getBlockOwnerFile().exists()) {
			plugin.con(ChatColor.YELLOW,
					"BlockOwners file not found. Creating a new one...");
			try {
				plugin.getBlockOwnerFile().createNewFile();
			} catch (IOException e) {
			}
		}
		try {
			FileWriter fileWriter = new FileWriter(plugin.getBlockOwnerFile(),
					false);
			for (Entry<Block, String> entry : ownings.entrySet()) {
				Block block = entry.getKey();
				fileWriter.write(block.getWorld().getName() + ":"
						+ block.getX() + "#" + block.getY() + "#"
						+ block.getZ() + ":" + entry.getValue());
			}
			fileWriter.flush();
			fileWriter.close();
			return true;
		} catch (Exception ex) {
			plugin.con(ChatColor.RED,ex.toString());
		}
		return false;
	}

	public OfflinePlayer getOwner(Block block) {
		try {
			return plugin.getServer().getOfflinePlayer(ownings.get(block));
		} catch (Exception e) {
			return null;
		}
	}

	public void setOwner(Block block, OfflinePlayer offlinePlayer) {
		ownings.put(block, offlinePlayer.getName());
	}
	public void setOwner(Block block, String player) {
		ownings.put(block, player);
	}
	public void removeOwner(Block block){
		ownings.remove(block);
	}
}
