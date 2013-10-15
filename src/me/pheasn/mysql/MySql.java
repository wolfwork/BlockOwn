package me.pheasn.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public abstract class MySql {
	Connection con;
	HashMap<String, String> parameters = new HashMap<String, String>();

	public abstract boolean connect(String path, String user, String password);

	public abstract boolean close();

	public abstract boolean createTables(TableDefinition[] tables);

	public abstract boolean createTable(TableDefinition table);

	public abstract void doUpdate(String sql);

	public abstract ResultSet doQuery(String sql);

	public Statement createStatement() throws SQLException {
		return con.createStatement();
	}

	public String getParameter(String parameter) {
		return parameters.get(parameter);
	}

}
