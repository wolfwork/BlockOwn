package me.pheasn.blockown.owning;

import me.pheasn.blockown.mysql.MySql;

public abstract class SQLOwning extends Owning {
	protected MySql msql;

	public enum Setting {
		MYSQL_ENABLE("ServerSettings.MySQL.enable"), //$NON-NLS-1$
		MYSQL_TYPE("ServerSettings.MySQL.type"), //$NON-NLS-1$
		MYSQL_HOST("ServerSettings.MySQL.host"), //$NON-NLS-1$
		MYSQL_PORT("ServerSettings.MySQL.port"), //$NON-NLS-1$
		MYSQL_DATABASE("ServerSettings.MySQL.database"), //$NON-NLS-1$
		MYSQL_USER("ServerSettings.MySQL.user"), //$NON-NLS-1$
		MYSQL_PASSWORD("ServerSettings.MySQL.password"); //$NON-NLS-1$
		private String s;

		private Setting(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return s;
		}
	}
	
	/**
	 * Checks whether player is already in the database
	 */
	protected abstract boolean playerExists(String player);

}
