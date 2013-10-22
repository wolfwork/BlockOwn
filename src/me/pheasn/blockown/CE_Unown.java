package me.pheasn.blockown;

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

public class CE_Unown implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Unown(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length == 0) {
					Block target = player.getTargetBlock(null, 200);

					if (target != null) {
						OfflinePlayer owner = plugin.owning.getOwner(target);
						if (owner != null
								&& owner.getName().equalsIgnoreCase(
										player.getName())) {
							plugin.owning.removeOwner(target);
							plugin.say(player, ChatColor.GREEN,
									Messages.getString("CE_Unown.0")); //$NON-NLS-1$
							return true;
						} else {
							plugin.say(player, ChatColor.RED,
									Messages.getString("CE_Unown.1")); //$NON-NLS-1$
						}
					}
				} else if (args.length == 1
						&& args[0].equalsIgnoreCase("selection")) { //$NON-NLS-1$
					if(plugin.getWorldEdit()==null){
						plugin.tell(sender, ChatColor.RED,Messages.getString("CE_Unown.3")); //$NON-NLS-1$
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
						for (int x = xMin; x <= xMax; x++) {
							for (int y = yMin; y <= yMax; y++) {
								for (int z = zMin; z <= zMax; z++) {
									Block block = w.getBlockAt(x, y, z);
									OfflinePlayer owner = plugin.owning
											.getOwner(block);
									if (owner.getName().equalsIgnoreCase(
											player.getName())) {
										plugin.owning.removeOwner(w.getBlockAt(
												x, y, z));
									}
								}
							}
						}
						plugin.tell(sender, ChatColor.GREEN,
								Messages.getString("CE_Unown.4")); //$NON-NLS-1$
						return true;
					} else {
						plugin.tell(sender, ChatColor.RED,
								Messages.getString("CE_Unown.5")); //$NON-NLS-1$
						return true;
					}
				} else {
					plugin.tell(sender, ChatColor.RED,
							Messages.getString("CE_Unown.6")); //$NON-NLS-1$
					return false;
				}
			} else {
				plugin.con(ChatColor.RED, Messages.getString("CE_Unown.2")); //$NON-NLS-1$
			}
		} catch (Exception ex) {

		}
		return false;
	}

}
