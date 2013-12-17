package com.github.pheasn.blockown.owning;

import com.github.pheasn.blockown.BlockOwn;
import com.github.pheasn.interfaces.OwningDatabase;

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
