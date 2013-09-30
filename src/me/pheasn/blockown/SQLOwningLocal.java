package me.pheasn.blockown;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import me.pheasn.mysql.MySqlLocal;
import me.pheasn.mysql.TableDefinition;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public class SQLOwningLocal extends SQLOwning {
public SQLOwningLocal(BlockOwn plugin) throws ClassNotFoundException, MySQLNotConnectingException{
	msql=new MySqlLocal();
	this.plugin=plugin;
	if(!this.load()){
		throw new MySQLNotConnectingException();
	}
}
	@Override
	public boolean load() {
	return	(msql.connect("./plugins/BlockOwn/data.db", plugin.getName(), "pw4242")&&createTablesIfNotExist());
	}
	private boolean createTablesIfNotExist(){
		TableDefinition[] tables = new TableDefinition[2];
		tables[0] = new TableDefinition("player",new String[]{"playerid INTEGER PRIMARY KEY "+msql.getParameter("AUTO_INCREMENT"),"playername VARCHAR(50) UNIQUE"});
		tables[1]= new TableDefinition("block", new String[] {"world VARCHAR(50)","x INTEGER", "y INTEGER", "z INTEGER", "ownerid INTEGER","PRIMARY KEY(world, x, y, z)", "FOREIGN KEY(ownerid) REFERENCES player(playerid)"});
	return	msql.createTables(tables);
	}
	@Override
	public OfflinePlayer getOwner(Block block) {
		String world = block.getWorld().getName();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		ResultSet rs = msql.doQuery("SELECT playername FROM block INNER JOIN player ON block.ownerid=player.playerid WHERE x="+x+" AND y="+y +" AND z="+z + " AND world='"+world+"';");
		try {
			if(	rs.next()){
			return plugin.getServer().getOfflinePlayer(rs.getString("playername"));
			}else{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void setOwner(Block block, OfflinePlayer offlinePlayer) {
		setOwner(block, offlinePlayer.getName());
	}

	@Override
	public void setOwner(Block block, String player) {
		if(!playerExists(player)){
			msql.doUpdate("INSERT INTO player(playername) VALUES('"+player+"');");
		}
		String world = block.getWorld().getName();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		ResultSet rs =  msql.doQuery("SELECT playerid FROM player WHERE playername='"+player+"';");
		Integer playerid=null;
		try {
			rs.next();
			playerid= rs.getInt("playerid");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		msql.doUpdate("INSERT OR IGNORE INTO block(world, x, y, z, ownerid) VALUES('"+world+"', '"+x+"', '"+y+"', '"+z+"', '"+playerid+"');");
	}

	@Override
	public void removeOwner(Block block) {
		String world = block.getWorld().getName();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		msql.doUpdate("DELETE FROM block WHERE x="+x+" AND y="+y+" AND z="+z+" AND world='"+world+"';");
	}

	@Override
	public boolean save() {
		return msql.close();
	}
	@Override
	boolean playerExists(String player) {
		player = plugin.getServer().getOfflinePlayer(player).getName();
		ResultSet rs = msql.doQuery("SELECT playername FROM player;");
		ArrayList<String> players = new ArrayList<String>();
		try {
			while(rs.next()){
				players.add(rs.getString("playername"));
			}
			if(players.contains(player)){
				return true;
			}else{
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public void deleteOwningsOf(String player) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteOwningsOf(OfflinePlayer offlinePlayer) {
		deleteOwningsOf(offlinePlayer.getName());
	}
	
}
