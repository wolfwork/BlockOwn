package me.pheasn.blockown;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_Owner implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Owner(BlockOwn plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Block target = BOPlayer.getInstance(player).getTargetBlock();
			if (target != null) {
				if (plugin.owning.getOwner(target) != null) {
					plugin.say(player, ChatColor.GREEN,
							Messages.getString("CE_Owner.0") //$NON-NLS-1$
									+ plugin.owning.getOwner(target).getName());
					return true;
				} else {
					plugin.say(player, ChatColor.GREEN,
							Messages.getString("CE_Owner.1")); //$NON-NLS-1$
					return true;
				}
			} else {
				plugin.say(player, ChatColor.RED,
						Messages.getString("CE_Owner.2")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("CE_Owner.3")); //$NON-NLS-1$
			return false;
		}
	}

}
