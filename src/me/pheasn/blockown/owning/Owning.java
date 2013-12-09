package me.pheasn.blockown.owning;

import me.pheasn.blockown.BlockOwn;
import me.pheasn.interfaces.OwningDatabase;

public abstract class Owning implements OwningDatabase{
	protected BlockOwn plugin;
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

	public DatabaseType getType() {
		return type;
	}
}
