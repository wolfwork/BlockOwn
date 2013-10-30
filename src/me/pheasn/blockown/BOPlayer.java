package me.pheasn.blockown;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BOPlayer {
	private Player player;

	private BOPlayer(Player player) {
		this.player = player;
	}

	public static BOPlayer getInstance(Player player) {
		return new BOPlayer(player);
	}

	public Player getPlayer() {
		return player;
	}

	public Block getTargetBlock() {
		Vector direction = player.getEyeLocation().getDirection();
		int distance = 0;
		Location view = player.getEyeLocation();
		while (distance <= 300) {
			int x, y, z;
			view = view.add(direction);
			x = view.getBlockX();
			y = view.getBlockY();
			z = view.getBlockZ();
			distance++;
			Block viewedBlock = player.getWorld().getBlockAt(x, y, z);
			if (!viewedBlock.getType().equals(Material.AIR)) {
				return viewedBlock;
			}
		}
		return null;
	}
}
