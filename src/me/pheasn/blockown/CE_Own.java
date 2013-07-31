package me.pheasn.blockown;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			if (plugin.owning.getOwner(player.getTargetBlock(null, 200)) == null
					|| plugin.owning.getOwner(player.getTargetBlock(null, 200))
							.getName().equalsIgnoreCase(player.getName()))
				plugin.owning
						.setOwner(player.getTargetBlock(null, 200), player);
			return true;
		}
		return false;
	}

}
