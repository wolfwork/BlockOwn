package me.pheasn.blockown;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
				Block target = player.getTargetBlock(null, 200);
				if (target != null
						&& plugin.owning.getOwner(target).getName()
								.equalsIgnoreCase(player.getName())) {
					plugin.owning.removeOwner(target);
					return true;
				}
			}
		} catch (Exception ex) {

		}
		return false;
	}

}
