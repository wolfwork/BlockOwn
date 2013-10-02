package me.pheasn.blockown;

import java.util.HashMap;

import me.pheasn.blockown.BlockOwn.DatabaseType;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public abstract class Owning {
	BlockOwn plugin;
	DatabaseType type;

	public abstract boolean load();

	public abstract boolean save();

	public abstract OfflinePlayer getOwner(Block block);

	public abstract void setOwner(Block block, OfflinePlayer offlinePlayer);

	public abstract void setOwner(Block block, String player);

	public abstract void removeOwner(Block block);

	public abstract void deleteOwningsOf(String player);

	public abstract void deleteOwningsOf(OfflinePlayer offlinePlayer);

	public DatabaseType getType() {
		return type;
	}

	public abstract HashMap<Block, String> getOwnings();
}
