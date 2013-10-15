package me.pheasn.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.sqlite.JDBC;

public class MySqlLocal extends MySql {

	public MySqlLocal() throws ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		parameters.put("AUTO_INCREMENT", "AUTOINCREMENT");
		parameters.put("INTEGER", "INT");
	}

	@Override
	public boolean connect(String path, String user, String password) {
		Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", password);
		try {
			con = new JDBC().connect("jdbc:sqlite:" + path, props);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean close() {
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
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
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean createTables(TableDefinition[] tables) {
		try {
			for (TableDefinition table : tables) {
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
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void doUpdate(String sql) {
		try {
			con.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ResultSet doQuery(String sql) {
		try {
			return con.createStatement().executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
