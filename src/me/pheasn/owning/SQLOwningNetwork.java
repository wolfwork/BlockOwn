package me.pheasn.owning;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import me.pheasn.PheasnPlugin;
import me.pheasn.owning.mysql.MySqlNetwork;
import me.pheasn.owning.mysql.TableDefinition;

import org.bukkit.OfflinePlayer;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;

public class SQLOwningNetwork extends SQLOwning {
	public SQLOwningNetwork(PheasnPlugin plugin) throws ClassNotFoundException,
			MySQLNotConnectingException {
		this.type = DatabaseType.SQL_NETWORK;
		msql = new MySqlNetwork();
		this.plugin = plugin;
		if (!this.load()) {
			throw new MySQLNotConnectingException();
		}
	}

	@Override
	public boolean load() {
		return (msql
				.connect(
						plugin.getConfig().getString(
								Setting.MYSQL_HOST.toString())
								+ ":" + plugin.getConfig().getInt(Setting.MYSQL_PORT.toString()) + "/" + plugin.getConfig().getString(Setting.MYSQL_DATABASE.toString()), plugin.getConfig().getString(Setting.MYSQL_USER.toString()), plugin.getConfig().getString(Setting.MYSQL_PASSWORD.toString())) && createTablesIfNotExist()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private boolean createTablesIfNotExist() {
		TableDefinition[] tables = new TableDefinition[2];
		tables[0] = new TableDefinition(
				"player", new String[] { "playerid INTEGER PRIMARY KEY " + msql.getParameter("AUTO_INCREMENT"), "playername VARCHAR(50) UNIQUE NOT NULL" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		tables[1] = new TableDefinition(
				"block", new String[] { "world VARCHAR(50)", "x INTEGER", "y INTEGER", "z INTEGER", "ownerid INTEGER", "PRIMARY KEY(world, x, y, z)", "FOREIGN KEY(ownerid) REFERENCES player(playerid)" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
		return msql.createTables(tables);
	}

	@Override
	public OfflinePlayer getOwner(Block block) {
		String world = block.getWorld().getName();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		ResultSet rs = msql
				.doQuery("SELECT playername FROM block INNER JOIN player ON block.ownerid=player.playerid WHERE x=" + x + " AND y=" + y + " AND z=" + z + " AND world='" + world + "';"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		try {
			if (rs.next()) {
				return plugin.getServer().getOfflinePlayer(
						rs.getString("playername")); //$NON-NLS-1$
			} else {
				return null;
			}
		} catch (SQLException e) {
			return null;
		}

	}

	@Override
	public void setOwner(Block block, OfflinePlayer offlinePlayer) {
		setOwner(block, offlinePlayer.getName());
	}

	@Override
	public void setOwner(Block block, String player) {
		if (!playerExists(player)) {
			msql.doUpdate("INSERT INTO player(playername) VALUES('" + player + "');"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		String world = block.getWorld().getName();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		ResultSet rs = msql
				.doQuery("SELECT playerid FROM player WHERE playername='" + player + "';"); //$NON-NLS-1$ //$NON-NLS-2$
		int playerid = 0;
		try {
			if (rs.next()) {
				playerid = rs.getInt("playerid"); //$NON-NLS-1$
			}
		} catch (SQLException e) {
		}
		msql.doUpdate("INSERT IGNORE INTO block(world, x, y, z, ownerid) VALUES('" + world + "', '" + x + "', '" + y + "', '" + z + "', '" + playerid + "');"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}

	@Override
	public void removeOwner(Block block) {
		String world = block.getWorld().getName();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		msql.doUpdate("DELETE FROM block WHERE x=" + x + " AND y=" + y + " AND z=" + z + " AND world='" + world + "';"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	@Override
	public boolean save() {
		return msql.close();
	}

	@Override
	boolean playerExists(String player) {
		player = plugin.getServer().getOfflinePlayer(player).getName();
		ResultSet rs = msql.doQuery("SELECT playername FROM player;"); //$NON-NLS-1$
		ArrayList<String> players = new ArrayList<String>();
		try {
			while (rs.next()) {
				players.add(rs.getString("playername")); //$NON-NLS-1$
			}
			if (players.contains(player)) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public void deleteOwningsOf(String player) {
		ResultSet rs = msql
				.doQuery("SELECT playerid FROM player WHERE playername='" + player + "';"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			rs.next();
			int playerid = rs.getInt("playerid"); //$NON-NLS-1$
			msql.doUpdate("DELETE FROM block WHERE ownerid=" + playerid + ";"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (SQLException e) {
		}
	}

	@Override
	public void deleteOwningsOf(OfflinePlayer offlinePlayer) {
		deleteOwningsOf(offlinePlayer.getName());
	}

	@Override
	public HashMap<Block, String> getOwnings() {
		HashMap<Block, String> result = new HashMap<Block, String>();
		ResultSet rs = msql
				.doQuery("SELECT * FROM block INNER JOIN player ON block.ownerid=player.playerid;"); //$NON-NLS-1$
		try {
			while (rs.next()) {
				String world = rs.getString("world"); //$NON-NLS-1$
				int x = rs.getInt("x"); //$NON-NLS-1$
				int y = rs.getInt("y"); //$NON-NLS-1$
				int z = rs.getInt("z"); //$NON-NLS-1$
				if (plugin.getServer().getWorld(world) == null) {
					plugin.getServer().createWorld(new WorldCreator(world));
				}
				result.put(
						plugin.getServer().getWorld(world).getBlockAt(x, y, z),
						rs.getString("playername")); //$NON-NLS-1$
			}
			rs.close();
			return result;
		} catch (SQLException e) {
			return new HashMap<Block, String>();
		}
	}
}
