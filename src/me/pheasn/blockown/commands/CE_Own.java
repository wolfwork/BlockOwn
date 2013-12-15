package me.pheasn.blockown.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import me.pheasn.OfflineUser;
import me.pheasn.User;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
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

	public enum Arg {
		SELECTION("selection"), //$NON-NLS-1$
		MATERIAL("material"), //$NON-NLS-1$
		PLAYER("player"); //$NON-NLS-1$
		
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
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] argArray) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if(Setting.DISABLE_OWNING_IN_WORLDS.getStringList(plugin).contains(player.getWorld().getName())){
				plugin.say(player, ChatColor.RED, Messages.getString("CE_Own.disabledInWorld")); //$NON-NLS-1$
				return true;
			}
			if (Setting.PERMISSION_NEEDED_OWN_COMMAND.getBoolean(plugin) && !player.hasPermission(Permission.OWN_COMMAND.toString())) {
				if(!(player.getGameMode() == GameMode.CREATIVE && player.hasPermission(Permission.OWN_COMMAND_CREATIVE.toString()))){
				plugin.say(player, ChatColor.RED, Messages.getString("noPermission")); //$NON-NLS-1$
				return true;
				}
			}
			OfflineUser user = OfflineUser.getInstance(player);

			//Getting arguments
			List<String> args = Arrays.asList(argArray);
			Iterator<String> argIterator = args.iterator();
			String arg;
			boolean selection = false, not = false;
			ArrayList<Material> materials = null;
			String playerName = null;
			try{
				while(argIterator.hasNext()){
					arg = argIterator.next();

					if(Arg.MATERIAL.equalsIgnoreCase(arg)){
						materials = new ArrayList<Material>();
						String next = argIterator.next();
						if(next.equalsIgnoreCase("not")){ //$NON-NLS-1$
							not = true;
						}else{
							Material material = Material.getMaterial(next.toUpperCase());
							if(material != null){
								materials.add(material);
							}else{
								plugin.say(player, ChatColor.RED, Messages.getString("invalidMaterial")); //$NON-NLS-1$
								return false;
							}
						}
						while(argIterator.hasNext()){
							next = argIterator.next().toUpperCase();
							Material material = Material.getMaterial(next);
							if(material != null){
								materials.add(material);
							}else{
								break;
							}
						}
					}

					if(Arg.SELECTION.equalsIgnoreCase(arg)){
						selection = true;
						continue;
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

			// Checking for permission to own for other players
			if(playerName != null && !player.hasPermission(Permission.OWN_COMMAND_OTHERS.toString())){
				plugin.say(player, ChatColor.RED, Messages.getString("CE_Own.noPermissionOthers")); //$NON-NLS-1$
				return true;
			}
			// Getting specified player
			OfflineUser newOwner = (playerName == null) ? user : OfflineUser.getInstance(playerName);
			if(newOwner == null) {
				plugin.say(player, ChatColor.RED, Messages.getString("invalidPlayer")); //$NON-NLS-1$
				return false;
			}

			// Determining amount of blocks
			List<Block> targets = new ArrayList<Block>();
			int others = 0;
			int unneccessary = 0;
			if(selection){
				if(plugin.getWorldEdit() == null) {
					plugin.tell(sender, ChatColor.RED, Messages.getString("CE_Own.selection.noWorldedit")); //$NON-NLS-1$
					return true;
				}
				Selection s = plugin.getWorldEdit().getSelection((Player)sender);
				if(s == null){
					plugin.say(player, ChatColor.RED, Messages.getString("CE_Own.selection.noArea")); //$NON-NLS-1$
					return true;
				}
				List<Block> candidateTargets = new me.pheasn.Region(s.getMinimumPoint(), s.getMaximumPoint()).getBlocks();
				for(Block block : candidateTargets){
					if(block.getType().equals(org.bukkit.Material.AIR)) continue;
					if(materials != null){
						if(materials.isEmpty()){
							plugin.say(player, ChatColor.RED, Messages.getString("CE_Own.noValidMaterials")); //$NON-NLS-1$
							return false;
						}
						if(not){
							if(materials.contains(block.getType())) continue;
						}else{
							if(!materials.contains(block.getType())) continue;
						}
					}
					OfflineUser owner = plugin.getOwning().getOwner(block);
					if(owner != null){
						if(owner.getName().equals(newOwner.getName())){
							unneccessary++;
						}else{
							others++;
						}
					}else{
						targets.add(block);
					}
				}
			}else{
				Block target = User.getInstance(player).getTargetBlock();
				OfflineUser owner = plugin.getOwning().getOwner(target);
				if(owner == null){
					targets.add(target);
				}else if(owner.equals(user)){
					plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_Own.unneccessary")); //$NON-NLS-1$
					return true;
				}else{
					plugin.say(player, ChatColor.RED, Messages.getString("CE_Own.ownedBy", owner.getName())); //$NON-NLS-1$
					return true;
				}
			}

			// Economy
			if(selection && plugin.getEconomy() != null && Setting.ECONOMY_ENABLE.getBoolean(plugin) && Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin) != 0.0){
				if (plugin.getEconomy().getBalance(player.getName()) < (targets.size() * Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin))) {
					plugin.say(player, ChatColor.RED, Messages.getString("CE_Own.selection.noMoney", //$NON-NLS-1$
									(targets.size() * Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin)), plugin.getEconomy().currencyNamePlural()));
					return true;
				} else {
					plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_Own.selection.howMuch", //$NON-NLS-1$
									(targets.size() * Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin)), plugin.getEconomy().currencyNamePlural()));
					plugin.getEconomy().withdrawPlayer(player.getName(), (targets.size() * Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin)));
				}
			}

			//Actually own blocks
			for(Block block : targets){
				plugin.getOwning().setOwner(block, newOwner);
			}

			// Tell player about the result
			if(selection && playerName == null){
				plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Own.selection.success.own")); //$NON-NLS-1$
				if(others > 0 || unneccessary > 0){
					plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_Own.selection.except", others, unneccessary)); //$NON-NLS-1$
				}
				return true;
			} else if(selection && playerName != null){
				plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Own.selection.success.other", playerName)); //$NON-NLS-1$
				if(others > 0 || unneccessary > 0){
					plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_Own.selection.except", others, unneccessary)); //$NON-NLS-1$
				}
				return true;
			
			}else if(playerName == null){
				plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Own.success.own")); //$NON-NLS-1$
				return true;
			}else{
				plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Own.success.other", newOwner.getName())); //$NON-NLS-1$
				return true;
			}

		} else {
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return false;
		}

	}

}
