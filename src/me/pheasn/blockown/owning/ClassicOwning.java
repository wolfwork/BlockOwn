package me.pheasn.blockown.owning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import me.pheasn.blockown.BlockOwn;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;

public class ClassicOwning extends Owning {
	private HashMap<Block, String> ownings;

	public ClassicOwning(BlockOwn plugin) {
		this.type = DatabaseType.CLASSIC;
		this.plugin = plugin;
		ownings = new HashMap<Block, String>();
		this.load();
	}

	@Override
	public boolean load() {
		if (plugin.getBlockOwnerFile().exists()) {
			try {
				FileReader reader = new FileReader(plugin.getBlockOwnerFile());
				BufferedReader bufferedReader = new BufferedReader(reader);
				String owning;
				ArrayList<String> players = new ArrayList<String>();
				for (OfflinePlayer offlinePlayer : plugin.getServer()
						.getOfflinePlayers()) {
					players.add(offlinePlayer.getName());
				}
				while ((owning = bufferedReader.readLine()) != null) {
					String[] owningDiv = owning.split(":"); //$NON-NLS-1$
					String worldName = owningDiv[0];
					String[] BlockCoordinates = owningDiv[1].split("#"); //$NON-NLS-1$
					String playerName = owningDiv[2];
					if (plugin.getServer().getWorld(worldName) == null) {
						plugin.getServer().createWorld(
								new WorldCreator(worldName));
					}
					if (plugin.getServer().getWorld(worldName) != null
							&& players.contains(playerName)) {
						Block block = plugin
								.getServer()
								.getWorld(worldName)
								.getBlockAt(
										Integer.valueOf(BlockCoordinates[0]),
										Integer.valueOf(BlockCoordinates[1]),
										Integer.valueOf(BlockCoordinates[2]));
						if (!block.getType().equals(Material.AIR)) {
							ownings.put(block, playerName);
						}
					}
				}
				bufferedReader.close();
				reader.close();
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("ClassicOwning.1")); //$NON-NLS-1$
			return false;
		}
	}

	@Override
	public boolean save() {
		if (!plugin.getBlockOwnerFile().exists()) {
			plugin.con(ChatColor.YELLOW, Messages.getString("ClassicOwning.0")); //$NON-NLS-1$
			try {
				plugin.getBlockOwnerFile().createNewFile();
			} catch (IOException e) {
			}
		}
		try {
			FileWriter fileWriter = new FileWriter(plugin.getBlockOwnerFile(),
					false);
			@SuppressWarnings("unchecked")
			final HashMap<Block, String> curOwnings = (HashMap<Block, String>) ownings
					.clone();
			for (Entry<Block, String> entry : curOwnings.entrySet()) {
				Block block = entry.getKey();
				fileWriter
						.write(block.getWorld().getName()
								+ ":" //$NON-NLS-1$
								+ block.getX()
								+ "#" + block.getY() + "#" //$NON-NLS-1$ //$NON-NLS-2$
								+ block.getZ()
								+ ":" + entry.getValue() + System.getProperty("line.separator")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			fileWriter.flush();
			fileWriter.close();
			return true;
		} catch (Exception ex) {
			plugin.con(ChatColor.RED, ex.toString());
		}
		return false;
	}

	@Override
	public OfflinePlayer getOwner(Block block) {
		try {
			return plugin.getServer().getOfflinePlayer(ownings.get(block));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public synchronized void setOwner(Block block, OfflinePlayer offlinePlayer) {
		ownings.put(block, offlinePlayer.getName());
	}

	@Override
	public synchronized void setOwner(Block block, String player) {
		ownings.put(block, player);
	}

	@Override
	public void removeOwner(Block block) {
		if (ownings.containsKey(block)) {
			ownings.remove(block);
		}
	}

	@Override
	public void deleteOwningsOf(OfflinePlayer offlinePlayer) {
		deleteOwningsOf(offlinePlayer.getName());
	}

	@Override
	public void deleteOwningsOf(String player) {
		for (Entry<Block, String> entry : ownings.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(player)) {
				this.removeOwner(entry.getKey());
			}
		}
	}

	@Override
	public HashMap<Block, String> getOwnings() {
		return ownings;
	}
}
