package me.pheasn.blockown;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_Unprotect implements CommandExecutor {
private BlockOwn plugin;
public CE_Unprotect(BlockOwn plugin){
	this.plugin=plugin;
}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmd_label,
			String[] args) {
		try{
		if(sender instanceof Player){
			Player player = (Player) sender;
			String blockType;
			if(args.length==2){
				blockType=args[1];
			}else{
				blockType=player.getTargetBlock(null, 200).getType().name();
			}
		plugin.playerSettings.blacklistRemove(player, blockType, args[0]);
		return true;
		}
		}catch(Exception ex){
			
		}
		return false;
	}

}
