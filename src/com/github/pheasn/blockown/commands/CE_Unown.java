package com.github.pheasn.blockown.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.pheasn.OfflineUser;
import com.github.pheasn.User;
import com.github.pheasn.blockown.BlockOwn;
import com.github.pheasn.blockown.Messages;
import com.github.pheasn.blockown.BlockOwn.Permission;
import com.github.pheasn.blockown.BlockOwn.Setting;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class CE_Unown implements CommandExecutor {
	private BlockOwn plugin;

	public CE_Unown(BlockOwn plugin) {
		this.plugin = plugin;
	}

	public enum Arg {
		SELECTION("selection"), //$NON-NLS-1$
		MATERIAL("material"); //$NON-NLS-1$
		
		private String s;
		private Arg(String s){
			this.s = s;
		}

		/**
		 * This is not working the other way round (String.equalsIgnoreCase(Arg)), sorry Sun/Oracle!
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
			if (Setting.PERMISSION_NEEDED_UNOWN_COMMAND.getBoolean(plugin) && !player.hasPermission(Permission.UNOWN.toString())) {
				plugin.say(player, ChatColor.RED, Messages.getString("noPermission")); //$NON-NLS-1$
				return true;
			}
			OfflineUser user = OfflineUser.getInstance(player);

			//Getting arguments
			List<String> args = Arrays.asList(argArray);
			Iterator<String> argIterator = args.iterator();
			String arg;
			boolean selection = false, not = false;
			ArrayList<Material> materials = null;
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

				}

			}catch(NoSuchElementException e){
				plugin.say(player, ChatColor.RED, Messages.getString("syntax")); //$NON-NLS-1$
				return false;
			}

			// Determining amount of blocks
			List<Block> targets = new ArrayList<Block>();
			if(selection){
				if(plugin.getWorldEdit() == null) {
					plugin.tell(sender, ChatColor.RED, Messages.getString("CE_Unown.selection.noWorldedit")); //$NON-NLS-1$
					return true;
				}
				Selection s = plugin.getWorldEdit().getSelection((Player)sender);
				if(s == null){
					plugin.say(player, ChatColor.RED, Messages.getString("CE_Unown.selection.noArea")); //$NON-NLS-1$
					return true;
				}
				List<Block> candidateTargets = new com.github.pheasn.Region(s.getMinimumPoint(), s.getMaximumPoint()).getBlocks();
				for(Block block : candidateTargets){
					if(block.getType().equals(org.bukkit.Material.AIR)) continue;
					if(materials != null){
						if(materials.isEmpty()){
							plugin.say(player, ChatColor.RED, Messages.getString("CE_Unown.noValidMaterials")); //$NON-NLS-1$
							return false;
						}
						if(not){
							if(materials.contains(block.getType())) continue;
						}else{
							if(!materials.contains(block.getType())) continue;
						}
					}
					OfflineUser owner = plugin.getOwning().getOwner(block);
					if(owner != null && owner.equals(user)){
						targets.add(block);
					}
				}
			}else{
				Block target = User.getInstance(player).getTargetBlock();
				OfflineUser owner = plugin.getOwning().getOwner(target);
				if(owner != null && owner.equals(user)){
					targets.add(target);
				}else{
					plugin.say(player, ChatColor.RED, Messages.getString("CE_Unown.unneccessary")); //$NON-NLS-1$
					return true;
				}
			}

			//Actually own blocks
			for(Block block : targets){
				plugin.getOwning().removeOwner(block);
			}

			// Tell player about the result
			if(selection){
				plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Unown.selection.success")); //$NON-NLS-1$
				return true;
			}else{
				plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Unown.success")); //$NON-NLS-1$
				return true;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return false;
		}

	}

}
