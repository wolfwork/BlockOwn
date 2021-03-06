package com.github.pheasn.blockown.owning;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.WorldCreator;
import org.bukkit.block.Block;

import com.github.pheasn.OfflineUser;
import com.github.pheasn.blockown.BlockOwn;
import com.github.pheasn.blockown.mysql.MySqlNetwork;
import com.github.pheasn.blockown.mysql.TableDefinition;

public class SQLOwningNetwork extends SQLOwning {
	public SQLOwningNetwork(BlockOwn plugin) throws ClassNotFoundException,
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
		return (msql.connect(plugin.getConfig().getString(
				Setting.MYSQL_HOST.toString()) + ":" + plugin.getConfig().getInt(Setting.MYSQL_PORT.toString()) + "/" + plugin.getConfig().getString(Setting.MYSQL_DATABASE.toString()), plugin.getConfig().getString(Setting.MYSQL_USER.toString()), plugin.getConfig().getString(Setting.MYSQL_PASSWORD.toString())) && createTablesIfNotExist()); //$NON-NLS-1$ //$NON-NLS-2$
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
	public OfflineUser getOwner(Block block) {
		String world = block.getWorld().getName();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		ResultSet rs = msql.doQuery("SELECT playername FROM block INNER JOIN player ON block.ownerid=player.playerid WHERE x=" + x + " AND y=" + y + " AND z=" + z + " AND world='" + world + "';"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		try {
			if (rs.next()) {
				rs.getStatement().close();
				return OfflineUser.getInstance(rs.getString("playername")); //$NON-NLS-1$
			} else {
				rs.getStatement().close();
				return null;
			}
		} catch (SQLException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}

	}

	@Override
	public void setOwner(Block block, OfflineUser player) {
		if (!playerExists(player.getName())) {
			msql.doUpdate("INSERT INTO player(playername) VALUES('" + player.getName() + "');"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		String world = block.getWorld().getName();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		ResultSet rs = msql.doQuery("SELECT playerid FROM player WHERE playername='" + player.getName() + "';"); //$NON-NLS-1$ //$NON-NLS-2$
		int playerid = 0;
		try {
			if (rs.next()) {
				playerid = rs.getInt("playerid"); //$NON-NLS-1$
			}
			rs.getStatement().close();
		} catch (SQLException e) {
			plugin.error(e);
		} catch (NullPointerException e) {
		}
		msql.doUpdate("INSERT INTO block(world, x, y, z, ownerid) VALUES('" + world + "', '" + x + "', '" + y + "', '" + z + "', '" + playerid + "') ON DUPLICATE KEY UPDATE world=VALUES(world), x=VALUES(x), y=VALUES(y), z=VALUES(z), ownerid=VALUES(ownerid);"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
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
	protected boolean playerExists(String playerName) {
		playerName = OfflineUser.getInstance(playerName).getName();
		ResultSet rs = msql.doQuery("SELECT playername FROM player WHERE playername = '" + playerName + "';"); //$NON-NLS-1$
		try {
			if (rs.next()) {
				rs.getStatement().close();
				return true;
			}else{
				rs.getStatement().close();
				return false;
			}
		} catch (SQLException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
	}

	@Override
	public void deleteOwningsOf(OfflineUser player) {
		ResultSet rs = msql.doQuery("SELECT playerid FROM player WHERE playername='" + player.getName() + "';"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			if(rs.next()){
			int playerid = rs.getInt("playerid"); //$NON-NLS-1$
			rs.getStatement().close();
			msql.doUpdate("DELETE FROM block WHERE ownerid=" + playerid + ";"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (SQLException e) {
		}
	}

	@Override
	public HashMap<Block, OfflineUser> getOwnings() {
		HashMap<Block, OfflineUser> result = new HashMap<Block, OfflineUser>();
		ResultSet rs = msql.doQuery("SELECT * FROM block INNER JOIN player ON block.ownerid=player.playerid;"); //$NON-NLS-1$
		try {
			while (rs.next()) {
				String world = rs.getString("world"); //$NON-NLS-1$
				int x = rs.getInt("x"); //$NON-NLS-1$
				int y = rs.getInt("y"); //$NON-NLS-1$
				int z = rs.getInt("z"); //$NON-NLS-1$
				if (plugin.getServer().getWorld(world) == null) {
					plugin.getServer().createWorld(new WorldCreator(world));
				}
				result.put(plugin.getServer().getWorld(world).getBlockAt(x, y, z), OfflineUser.getInstance(rs.getString("playername"))); //$NON-NLS-1$
			}
			rs.getStatement().close();
			return result;
		} catch (SQLException e) {
			return new HashMap<Block, OfflineUser>();
		}
	}
}
