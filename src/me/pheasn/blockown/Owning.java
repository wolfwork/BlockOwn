package me.pheasn.blockown;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public abstract class Owning {
BlockOwn plugin;

public abstract boolean load();

public abstract boolean save();

public abstract OfflinePlayer getOwner(Block block);

public abstract void setOwner(Block block, OfflinePlayer offlinePlayer);

public abstract void setOwner(Block block, String player);

public abstract void removeOwner(Block block);

public abstract void deleteOwningsOf(String player);

public abstract void deleteOwningsOf(OfflinePlayer offlinePlayer);

}
