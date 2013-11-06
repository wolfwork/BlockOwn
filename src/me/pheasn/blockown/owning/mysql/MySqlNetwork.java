package me.pheasn.blockown.owning.mysql;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlNetwork extends MySql {
	public MySqlNetwork() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		parameters.put("AUTO_INCREMENT", "AUTO_INCREMENT");
		parameters.put("INTEGER", "INTEGER");
	}

	@Override
	public boolean connect(String path, String user, String password) {
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + path + "?user="
					+ user + "&password=" + password + "&autoReconnect=true");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public boolean close() {
		try {
			if (con != null && (!(con.isClosed()))) {
				con.close();
			}
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public boolean createTables(TableDefinition[] tables) {
		for (TableDefinition table : tables) {
			try {
				StringBuffer updateString = new StringBuffer();
				updateString.append("CREATE TABLE IF NOT EXISTS `"
						+ table.getName() + "` (");
				for (String entry : table.getEntries()) {
					updateString.append(entry + ", ");
				}
				updateString.delete(updateString.length() - 2,
						updateString.length());
				updateString.append(")");
				con.createStatement().executeUpdate(updateString.toString());
			} catch (SQLException e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean createTable(TableDefinition table) {
		try {
			StringBuffer updateString = new StringBuffer();
			updateString.append("CREATE TABLE IF NOT EXISTS `"
					+ table.getName() + "` (");
			for (String entry : table.getEntries()) {
				updateString.append(entry + ", ");
			}
			updateString.delete(updateString.length() - 2,
					updateString.length());
			updateString.append(")");
			con.createStatement().executeUpdate(updateString.toString());
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public void doUpdate(String sql) {
		try {
			con.createStatement().execute(sql);
		} catch (SQLException e) {
		}
	}

	@Override
	public ResultSet doQuery(String sql) {
		try {
			return con.createStatement().executeQuery(sql);
		} catch (SQLException e) {
			return null;
		}
	}

}
