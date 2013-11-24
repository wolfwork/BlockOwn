package me.pheasn.blockown.owning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import me.pheasn.OfflineUser;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;

public class ClassicOwning extends Owning {
	private HashMap<Block, OfflineUser> ownings;

	public ClassicOwning(BlockOwn plugin) {
		this.type = DatabaseType.CLASSIC;
		this.plugin = plugin;
		ownings = new HashMap<Block, OfflineUser>();
		this.load();
	}

	@Override
	public boolean load() {
		if (plugin.getBlockOwnerFile().exists()) {
			try {
				FileReader reader = new FileReader(plugin.getBlockOwnerFile());
				BufferedReader bufferedReader = new BufferedReader(reader);
				String owning;
				while ((owning = bufferedReader.readLine()) != null) {
					String[] owningDiv = owning.split(":"); //$NON-NLS-1$
					String worldName = owningDiv[0];
					String[] BlockCoordinates = owningDiv[1].split("#"); //$NON-NLS-1$
					String playerName = owningDiv[2];
					OfflineUser player = OfflineUser.getInstance(playerName);
					if (plugin.getServer().getWorld(worldName) == null) {
						plugin.getServer().createWorld(
								new WorldCreator(worldName));
					}
					if (plugin.getServer().getWorld(worldName) != null && player != null) {
						Block block = plugin.getServer().getWorld(worldName).getBlockAt(
								Integer.valueOf(BlockCoordinates[0]),
								Integer.valueOf(BlockCoordinates[1]),
								Integer.valueOf(BlockCoordinates[2]));
						if (!block.getType().equals(Material.AIR)) {
							ownings.put(block, player);
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
			plugin.con(ChatColor.RED, Messages.getString("ClassicOwning.FileNotFound.error")); //$NON-NLS-1$
			return false;
		}
	}

	@Override
	public boolean save() {
		if (!plugin.getBlockOwnerFile().exists()) {
			plugin.con(ChatColor.YELLOW, Messages.getString("ClassicOwning.FileNotFound.recreate")); //$NON-NLS-1$
			try {
				plugin.getBlockOwnerFile().createNewFile();
			} catch (IOException e) {
			}
		}
		try {
			FileWriter fileWriter = new FileWriter(plugin.getBlockOwnerFile(),
					false);
			@SuppressWarnings("unchecked")
			final HashMap<Block, OfflineUser> curOwnings = (HashMap<Block, OfflineUser>) ownings
					.clone();
			for (Entry<Block, OfflineUser> entry : curOwnings.entrySet()) {
				Block block = entry.getKey();
				fileWriter.write(block.getWorld().getName()
								+ ":" //$NON-NLS-1$
								+ block.getX()
								+ "#" //$NON-NLS-1$
								+ block.getY()
								+ "#" //$NON-NLS-1$
								+ block.getZ()
								+ ":" //$NON-NLS-1$
								+ entry.getValue().getName() + System.getProperty("line.separator")); //$NON-NLS-1$
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
	public OfflineUser getOwner(Block block) {
		try {
			return ownings.get(block);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public synchronized void setOwner(Block block, OfflineUser offlineUser) {
		ownings.put(block, offlineUser);
	}

	@Override
	public void removeOwner(Block block) {
		if (ownings.get(block) != null) {
			ownings.remove(block);
		}
	}

	@Override
	public void deleteOwningsOf(OfflineUser player) {
		for (Entry<Block, OfflineUser> entry : ownings.entrySet()) {
			if (entry.getValue().equals(player)) {
				this.removeOwner(entry.getKey());
			}
		}
	}

	@Override
	public HashMap<Block, OfflineUser> getOwnings() {
		return ownings;
	}
}
