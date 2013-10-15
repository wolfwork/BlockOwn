package me.pheasn.owning;

import java.util.HashMap;



import me.pheasn.PheasnPlugin;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public abstract class Owning {
	protected PheasnPlugin plugin;
	protected DatabaseType type;
	public enum DatabaseType {
		SQL_LOCAL("local"), SQL_NETWORK("network"), CLASSIC("classic"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		private String s;

		private DatabaseType(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return s;
		}
	}

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
