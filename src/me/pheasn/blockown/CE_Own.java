package me.pheasn.blockown;

import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.Selection;

public class CE_Own implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Own(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (Setting.PERMISSION_NEEDED_FOR_OWN_COMMAND.getBoolean(plugin)
					&& !player.hasPermission(Permission.OWN.toString())) {
				plugin.say(player, ChatColor.RED,
						Messages.getString("CE_Own.10")); //$NON-NLS-1$
				return true;
			}
			if (args.length == 0) {
				OfflinePlayer owner = plugin.owning.getOwner(player
						.getTargetBlock(null, 200));
				if (owner == null) {
					plugin.owning.setOwner(player.getTargetBlock(null, 200),
							player);
					plugin.say(player, ChatColor.GREEN,
							Messages.getString("CE_Own.0")); //$NON-NLS-1$
					return true;
				}
				if (owner.getName().equalsIgnoreCase(player.getName())) {
					plugin.say(player, ChatColor.YELLOW,
							Messages.getString("CE_Own.1")); //$NON-NLS-1$
					return true;
				} else {
					plugin.say(player, ChatColor.RED,
							Messages.getString("CE_Own.2") //$NON-NLS-1$
									+ owner.getName() + "."); //$NON-NLS-1$
					return false;
				}
			} else if (args.length == 1
					&& args[0].equalsIgnoreCase("selection")) { //$NON-NLS-1$
				if (plugin.getWorldEdit() == null) {
					plugin.tell(sender, ChatColor.RED,
							Messages.getString("CE_Own.3")); //$NON-NLS-1$
					return false;
				}
				Selection selection;
				if ((selection = plugin.getWorldEdit().getSelection(player)) != null) {
					Location min = selection.getMinimumPoint();
					int xMin = min.getBlockX();
					int yMin = min.getBlockY();
					int zMin = min.getBlockZ();
					Location max = selection.getMaximumPoint();
					int xMax = max.getBlockX();
					int yMax = max.getBlockY();
					int zMax = max.getBlockZ();
					World w = min.getWorld();
					int failed = 0;
					for (int x = xMin; x <= xMax; x++) {
						for (int y = yMin; y <= yMax; y++) {
							for (int z = zMin; z <= zMax; z++) {
								Block block = w.getBlockAt(x, y, z);
								OfflinePlayer owner = plugin.owning
										.getOwner(block);
								if (owner == null) {
									plugin.owning.setOwner(
											w.getBlockAt(x, y, z), player);
								} else if (!owner.getName().equalsIgnoreCase(
										player.getName())) {
									failed += 1;
								}
							}
						}
					}
					plugin.tell(sender, ChatColor.GREEN,
							Messages.getString("CE_Own.5")); //$NON-NLS-1$
					if (failed > 0) {
						plugin.tell(
								sender,
								ChatColor.YELLOW,
								Messages.getString("CE_Own.6") //$NON-NLS-1$
										+ failed
										+ Messages.getString("CE_Own.7")); //$NON-NLS-1$
					}
					return true;
				} else {
					plugin.tell(sender, ChatColor.RED,
							Messages.getString("CE_Own.8")); //$NON-NLS-1$
					return true;
				}
			} else {
				plugin.tell(sender, ChatColor.RED,
						Messages.getString("CE_Own.9")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("CE_Own.4")); //$NON-NLS-1$
			return false;
		}

	}

}
