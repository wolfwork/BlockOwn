package me.pheasn.blockown.commands;

import me.pheasn.User;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;
import me.pheasn.blockown.Messages;

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
				if(Setting.PERMISSION_NEEDED_UNOWN_COMMAND.getBoolean(plugin)&&!player.hasPermission(Permission.UNOWN.toString())){
					plugin.say(player, ChatColor.RED, Messages.getString("noPermission"));
					return true;
				}
				if (args.length == 0) {
					Block target = User.getInstance(player)
							.getTargetBlock();

					if (target != null) {
						OfflinePlayer owner = plugin.getOwning().getOwner(
								target);
						if (owner != null
								&& owner.getName().equalsIgnoreCase(
										player.getName())) {
							plugin.getOwning().removeOwner(target);
							plugin.say(player, ChatColor.GREEN,
									Messages.getString("CE_Unown.success")); //$NON-NLS-1$
							return true;
						} else {
							plugin.say(player, ChatColor.RED,
									Messages.getString("CE_Unown.unneccessary")); //$NON-NLS-1$
						}
					}
				} else if (args.length == 1
						&& args[0].equalsIgnoreCase("selection")) { //$NON-NLS-1$
					if (plugin.getWorldEdit() == null) {
						plugin.tell(sender, ChatColor.RED, 
								Messages.getString("CE_Unown.selection.noWorldedit")); //$NON-NLS-1$
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
									OfflinePlayer owner = plugin.getOwning().getOwner(block);
									if (owner.getName().equalsIgnoreCase(player.getName())) {
										plugin.getOwning().removeOwner(w.getBlockAt(x, y, z));
									}
								}
							}
						}
						plugin.tell(sender, ChatColor.GREEN, Messages
								.getString("CE_Unown.selection.success")); //$NON-NLS-1$
						return true;
					} else {
						plugin.tell(sender, ChatColor.RED,
								Messages.getString("CE_Unown.selection.noArea")); //$NON-NLS-1$
						return true;
					}
				} else {
					plugin.tell(sender, ChatColor.RED,
							Messages.getString("countArgs")); //$NON-NLS-1$
					return false;
				}
			} else {
				plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			}
		} catch (Exception ex) {

		}
		return false;
	}

}
