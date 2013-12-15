package me.pheasn.blockown.commands;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import me.pheasn.Material;
import me.pheasn.OfflineUser;
import me.pheasn.User;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CE_Unprotect implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Unprotect(BlockOwn plugin) {
		this.plugin = plugin;
	}


	public enum Arg {
		MATERIAL("material"),
		PLAYER("against");
		
		private String s;
		private Arg(String s){
			this.s = s;
		}

		/**
		 * This is not working the other way round (String.equalsIgnoreCase(Arg)), sorry Oracle!
		 * @param string
		 * @return
		 */
		public boolean equalsIgnoreCase(String string){
			if(string == null) return false;
			return string.equalsIgnoreCase(s);
		}

		@Override
		public String toString(){
			return this.s;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmd_label, String[] argArray) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			OfflineUser user = OfflineUser.getInstance(player);
			if (Setting.PERMISSION_NEEDED_PROTECT_AND_PRIVATIZE_COMMAND.getBoolean(plugin) && !player.hasPermission(Permission.PROTECT_AND_PRIVATIZE.toString())) {
				plugin.say(player, ChatColor.RED, Messages.getString("noPermission")); //$NON-NLS-1$
				return true;
			}

			//Getting arguments
			List<String> args = Arrays.asList(argArray);
			Iterator<String> argIterator = args.iterator();
			String arg;
			Material material = null;
			String playerName = null;
			try{
				while(argIterator.hasNext()){
					arg = argIterator.next();

					if(Arg.MATERIAL.equalsIgnoreCase(arg)){
						String next = argIterator.next();
						Material m = Material.getMaterial(next);
						if(m != null){
							material = m;
						}else{
							plugin.say(player, ChatColor.RED, Messages.getString("invalidMaterial"));
							return false;
						}
					}

					if(Arg.PLAYER.equalsIgnoreCase(arg)){
						playerName = argIterator.next();
						continue;
					}

				}

			}catch(NoSuchElementException e){
				plugin.say(player, ChatColor.RED, "You made some syntax mistake!");
				return false;
			}

			// Analyze arguments
			if(material == null){
				Block target = User.getInstance(player).getTargetBlock();
				if(target == null){
					plugin.say(player, ChatColor.RED, Messages.getString("noTargetBlock"));
					return false;
				}
				material = Material.getMaterial(target.getType());
			}

			OfflineUser against = null;
			if(playerName != null){
				against = OfflineUser.getInstance(playerName);
				if(against == null){
					plugin.say(player, ChatColor.RED, Messages.getString("invalidPlayer"));
					return false;
				}
			}else{
				plugin.say(player, ChatColor.RED, "You need to specify a playername!");
				return false;
			}


			// Analyze whether attempt makes sense
			if (plugin.getPlayerSettings().getRawBlacklists(user).get(material) == null || !plugin.getPlayerSettings().getRawBlacklists(user).get(material).contains(against)){
				plugin.say(player, ChatColor.RED, Messages.getString("CE_Unprotect.unneccessary", material.name(), against.getName())); //$NON-NLS-1$
				return true;
			}


			// Perform actual command
			plugin.getPlayerSettings().addBlacklisted(material, against, user);
			String mName = (material.equals(Material.ALL_BLOCKS)) ? "" : material.name();
			String pName = (against.equals(OfflineUser.ALL_PLAYERS)) ? "all players" : against.getName();
			this.sendSuccessMessage(player, mName, pName);
			return true;
		} else {
			plugin.con(Messages.getString("justForPlayers")); //$NON-NLS-1$
			return false;
		}
	}
//	@Override
//	public boolean onCommand(CommandSender sender, Command cmd,
//			String cmd_label, String[] args) {
//		if (sender instanceof Player) {
//			Player player = (Player) sender;
//			User user = User.getInstance(player);
//			String target = user.getTargetBlock().getType().name();
//			String protectName;
//			if (args.length == 2) {
//				target = args[0];
//			}
//			if (target != null) {
//				String blockName = target;
//				if (args.length == 1) {
//					protectName = args[0];
//					if (args[0].equalsIgnoreCase("all")) { //$NON-NLS-1$
//						args[0] = PlayerSettings.ALL_PLAYERS;
//						protectName = Messages.getString("CE_Unprotect.allPlayers"); //$NON-NLS-1$
//					}
//					OfflineUser against = OfflineUser.getInstance(args[0]);
//					plugin.getPlayerSettings().removeBlacklisted(Material.getMaterial(target), against, user.getOfflineUser());
//					sendSuccessMessage(player, blockName, protectName);
//					return true;
//				} else if (args.length == 2) {
//					blockName = args[0];
//					protectName = args[1];
//					if (args[0].equalsIgnoreCase("all")) { //$NON-NLS-1$
//						args[0] = PlayerSettings.ALL_BLOCKS;
//						blockName = "all"; //$NON-NLS-1$
//					}
//					Material material = Material.getMaterial(args[0]);
//					if (args[1].equalsIgnoreCase("all")) { //$NON-NLS-1$
//						args[1] = PlayerSettings.ALL_PLAYERS;
//						protectName = Messages.getString("CE_Unprotect.allPlayers"); //$NON-NLS-1$
//					}
//					OfflineUser against = OfflineUser.getInstance(args[1]);
//					plugin.getPlayerSettings().removeBlacklisted(material, against, user.getOfflineUser());
//					sendSuccessMessage(player, blockName, protectName);
//					return true;
//				} else {
//					plugin.say(player, ChatColor.RED, Messages.getString("countArgs")); //$NON-NLS-1$
//					return false;
//				}
//			} else {
//				plugin.say(player, ChatColor.RED, Messages.getString("noTargetBlock")); //$NON-NLS-1$
//				return false;
//			}
//		} else {
//			plugin.con(Messages.getString("justForPlayers")); //$NON-NLS-1$
//			return false;
//		}
//	}

	private void sendSuccessMessage(Player player, String materialName, String playerName) {
		plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Unprotect.success", materialName, playerName)); //$NON-NLS-1$
	}

}
