package me.pheasn.blockown.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import me.pheasn.Material;
import me.pheasn.OfflineUser;
import me.pheasn.Region;
import me.pheasn.User;
import me.pheasn.blockown.BlockOwn;
import me.pheasn.blockown.Messages;
import me.pheasn.blockown.BlockOwn.Permission;
import me.pheasn.blockown.BlockOwn.Setting;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
		SELECTION("selection"),
		MATERIAL("material"),
		PLAYER("player");
		
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
				plugin.say(player, ChatColor.RED, "Owning is disabled in this world");
				return true;
			}
			if (Setting.PERMISSION_NEEDED_OWN_COMMAND.getBoolean(plugin)
					&& !player.hasPermission(Permission.OWN_COMMAND.toString())) {
				if(!(player.getGameMode()==GameMode.CREATIVE&&player.hasPermission(Permission.OWN_COMMAND_CREATIVE.toString()))){
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
						if(next.equalsIgnoreCase("not")){
							not = true;
							continue;
						}else{
							Material material = Material.getMaterial(next);
							if(material != null){
								materials.add(material);
							}
						}
						while(argIterator.hasNext()){
							next = argIterator.next();
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
				plugin.say(player, ChatColor.RED, "You made some syntax mistake!");
				return false;
			}

			// Getting specified player
			OfflineUser newOwner = (playerName == null) ? user : OfflineUser.getInstance(playerName);
			if(newOwner == null) {
				plugin.say(player, ChatColor.RED, "You specified an invalid player");
				return false;
			}

			// Determining amount of blocks
			List<Block> targets = new ArrayList<Block>();
			int others = 0;
			int unneccessary = 0;
			if(selection){
				if(plugin.getWorldEdit() == null) {
					plugin.tell(sender, ChatColor.RED, Messages.getString("CE_Own.selection.noWorldedit"));
					return true;
				}
				Selection s = plugin.getWorldEdit().getSelection((Player)sender);
				List<Block> candidateTargets = new Region(s.getWorld(), s.getMinimumPoint().getBlockX(), s.getMinimumPoint().getBlockY(), s.getMinimumPoint().getBlockZ(), s.getHeight(), s.getLength(), s.getWidth()).getBlocks();
				for(Block block : candidateTargets){
					if(block.getType().equals(org.bukkit.Material.AIR)) continue;
					if(materials != null){
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
				targets.add(User.getInstance(player).getTargetBlock());
			}

			// Economy
			if(selection && plugin.getEconomy() != null && Setting.ECONOMY_ENABLE.getBoolean(plugin) && Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin) != 0.0){
				if (plugin.getEconomy().getBalance(player.getName()) < (targets.size() * Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin))) {
					plugin.say(player, ChatColor.RED, Messages.getString("CE_Own.selection.noMoney",
									(targets.size() * Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin)), plugin.getEconomy().currencyNamePlural()));
					return true;
				} else {
					plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_Own.selection.howMuch",
									(targets.size() * Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin)), plugin.getEconomy().currencyNamePlural()));
					plugin.getEconomy().withdrawPlayer(player.getName(), (targets.size() * Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin)));
				}
			}

			//Actually own blocks
			for(Block block : targets){
				plugin.getOwning().setOwner(block, newOwner);
			}

			// Tell player about the result
			if(selection){
				plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Own.selection.success"));
				if(others > 0 || unneccessary > 0){
					plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_Own.selection.except", others, unneccessary));
				}
				return true;
			}else if(playerName == null){
				plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Own.success.own"));
				return true;
			}else{
				plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Own.success.other", newOwner.getName()));
				return true;
			}

		} else {
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return false;
		}

	}

}
