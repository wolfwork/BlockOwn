package me.pheasn.blockown.owning;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import me.pheasn.OfflineUser;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.mysql.MySqlLocal;
import me.pheasn.blockown.mysql.TableDefinition;

import org.bukkit.WorldCreator;
import org.bukkit.block.Block;

public class SQLOwningLocal extends SQLOwning {
	public SQLOwningLocal(BlockOwn plugin) throws ClassNotFoundException,
			MySQLNotConnectingException {
		this.type = DatabaseType.SQL_LOCAL;
		msql = new MySqlLocal();
		this.plugin = plugin;
		if (!this.load()) {
			throw new MySQLNotConnectingException();
		}
	}

	@Override
	public boolean load() {
		return (msql.connect(plugin.getPluginDirectory().getPath() + "/data.db", plugin.getName().toLowerCase(), "pw4242") && createTablesIfNotExist()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	private boolean createTablesIfNotExist() {
		TableDefinition[] tables = new TableDefinition[2];
		tables[0] = new TableDefinition(
				"player", new String[] { "playerid INTEGER PRIMARY KEY " + msql.getParameter("AUTO_INCREMENT"), "playername VARCHAR(50) UNIQUE" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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
				OfflineUser user = OfflineUser.getInstance(rs.getString("playername")); //$NON-NLS-1$
				rs.getStatement().close();
				return user;
			} else {
				rs.getStatement().close();
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
		Integer playerid = null;
		try {
			rs.next();
			playerid = rs.getInt("playerid"); //$NON-NLS-1$
			rs.getStatement().close();
		} catch (SQLException e) {
			plugin.error(e);
		} catch (NullPointerException e) {

		}
		msql.doUpdate("INSERT OR REPLACE INTO block(world, x, y, z, ownerid) VALUES('" + world + "', '" + x + "', '" + y + "', '" + z + "', '" + playerid + "');"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
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
		playerName = plugin.getServer().getOfflinePlayer(playerName).getName();
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
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			return false;
		}
	}

	@Override
	public void deleteOwningsOf(OfflineUser player) {
		ResultSet rs = msql.doQuery("SELECT playerid FROM player WHERE playername='" + player.getName() + "';"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			rs.next();
			int playerid = rs.getInt("playerid"); //$NON-NLS-1$
			rs.getStatement().close();
			msql.doUpdate("DELETE FROM block WHERE ownerid=" + playerid + ";"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (SQLException e) {
			e.printStackTrace();
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
				result.put(plugin.getServer().getWorld(world).getBlockAt(x, y, z),
						OfflineUser.getInstance(rs.getString("playername"))); //$NON-NLS-1$
			}
			rs.getStatement().close();
			return result;
		} catch (SQLException e) {
			return new HashMap<Block, OfflineUser>();
		}
	}
}
