package com.github.pheasn.blockown.commands;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.pheasn.Material;
import com.github.pheasn.OfflineUser;
import com.github.pheasn.User;
import com.github.pheasn.blockown.BlockOwn;
import com.github.pheasn.blockown.Messages;
import com.github.pheasn.blockown.BlockOwn.Permission;
import com.github.pheasn.blockown.BlockOwn.Setting;

public class CE_Protect implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Protect(BlockOwn plugin) {
		this.plugin = plugin;
	}

	public enum Arg {
		MATERIAL("material"), //$NON-NLS-1$
		PLAYER("against"); //$NON-NLS-1$
		
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
							plugin.say(player, ChatColor.RED, Messages.getString("invalidMaterial")); //$NON-NLS-1$
							return false;
						}
					}

					if(Arg.PLAYER.equalsIgnoreCase(arg)){
						playerName = argIterator.next();
						continue;
					}

				}

			}catch(NoSuchElementException e){
				plugin.say(player, ChatColor.RED, Messages.getString("syntax")); //$NON-NLS-1$
				return false;
			}

			// Analyze arguments
			if(material == null){
				Block target = User.getInstance(player).getTargetBlock();
				if(target == null){
					plugin.say(player, ChatColor.RED, Messages.getString("noTargetBlock")); //$NON-NLS-1$
					return false;
				}
				material = Material.getMaterial(target.getType());
			}

			OfflineUser against = null;
			if(playerName != null){
				against = OfflineUser.getInstance(playerName);
				if(against == null){
					plugin.say(player, ChatColor.RED, Messages.getString("invalidPlayer")); //$NON-NLS-1$
					return false;
				}
			}else{
				plugin.say(player, ChatColor.RED, Messages.getString("playerNameNeeded")); //$NON-NLS-1$
				return false;
			}


			// Analyze whether attempt makes sense
			if (plugin.getPlayerSettings().getRawBlacklists(user).get(material) != null && plugin.getPlayerSettings().getRawBlacklists(user).get(material).contains(against)){
				plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_Protect.unneccessary", against.getName())); //$NON-NLS-1$
				return true;
			}


			// Economy
			if (Setting.ECONOMY_ENABLE.getBoolean(plugin) && plugin.getEconomy() != null && Setting.ECONOMY_PRICE_PROTECT.getDouble(plugin) > 0.0) {
				if (plugin.getEconomy().getBalance(player.getName()) < Setting.ECONOMY_PRICE_PROTECT.getDouble(plugin)) {
					plugin.say(player, ChatColor.RED, Messages.getString("CE_Protect.noMoney", Setting.ECONOMY_PRICE_PROTECT.getDouble(plugin), plugin.getEconomy().currencyNamePlural())); //$NON-NLS-1$
					return true;
				} else {
					plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_Protect.howMuch", Setting.ECONOMY_PRICE_PROTECT.getDouble(plugin), plugin.getEconomy().currencyNamePlural())); //$NON-NLS-1$
					plugin.getEconomy().withdrawPlayer(player.getName(), Setting.ECONOMY_PRICE_PROTECT.getDouble(plugin));
				}
			}


			// Perform actual command
			plugin.getPlayerSettings().addBlacklisted(material, against, user);
			String mName = (material.equals(Material.ALL_BLOCKS)) ? "" : material.name(); //$NON-NLS-1$
			String pName = (against.equals(OfflineUser.ALL_PLAYERS)) ? Messages.getString("CE_Protect.allPlayers") : against.getName(); //$NON-NLS-1$
			this.sendSuccessMessage(player, mName, pName);
			return true;
		} else {
			plugin.con(Messages.getString("justForPlayers")); //$NON-NLS-1$
			return false;
		}
	}

	private void sendSuccessMessage(Player player, String materialName, String playerName) {
		plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Protect.success", materialName, playerName)); //$NON-NLS-1$ 
	}
}
