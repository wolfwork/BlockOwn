package me.pheasn.blockown.commands;

import java.util.ArrayList;

import me.pheasn.OfflineUser;
import me.pheasn.Region;
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

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String cmd_label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if(Setting.DISABLE_OWNING_IN_WORLDS.getStringList(plugin).contains(player.getWorld().getName())){
				plugin.say(player, ChatColor.RED, "Owning is disabled in this world");
				return true;
			}
			OfflineUser user = OfflineUser.getInstance(player);
			if (Setting.PERMISSION_NEEDED_OWN_COMMAND.getBoolean(plugin)
					&& !player.hasPermission(Permission.OWN_COMMAND.toString())) {
				if(!(player.getGameMode()==GameMode.CREATIVE&&player.hasPermission(Permission.OWN_COMMAND_CREATIVE.toString()))){
				plugin.say(player, ChatColor.RED, Messages.getString("noPermission")); //$NON-NLS-1$
				return true;
				}
			}
			if (args.length == 0) {
				Block target = User.getInstance(player).getTargetBlock();
				OfflineUser owner = plugin.getOwning().getOwner(target);
				if (owner == null) {
					plugin.getOwning().setOwner(target, user);
					plugin.say(player, ChatColor.GREEN, Messages.getString("CE_Own.success")); //$NON-NLS-1$
					return true;
				}
				if (owner.equals(user)) {
					plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_Own.unneccessary")); //$NON-NLS-1$
					return true;
				} else {
					plugin.say(player, ChatColor.RED, Messages.getString("CE_Own.ownedBy", owner.getName())); //$NON-NLS-1$
					return false;
				}
			} else if (args.length == 1 && args[0].equalsIgnoreCase("selection")) { //$NON-NLS-1$
				if (plugin.getWorldEdit() == null) {
					plugin.tell(sender, ChatColor.RED, Messages.getString("CE_Own.selection.noWorldedit")); //$NON-NLS-1$
					return false;
				}
				Selection s;
				if ((s = plugin.getWorldEdit().getSelection(player)) != null) {
					Region region = new Region(s.getWorld(), s.getMinimumPoint().getBlockX(), s.getMinimumPoint().getBlockY(), s.getMinimumPoint().getBlockZ(), s.getHeight(), s.getLength(), s.getWidth());
					int failed = 0;
					ArrayList<Block> selectedBlocks = new ArrayList<Block>();
					for(Block block : region.getBlocks()){
						if(block.getType().equals(Material.AIR)) continue;
							OfflineUser owner = plugin.getOwning().getOwner(block);
							if (owner == null) {
								selectedBlocks.add(block);
							} else if (!owner.equals(user)) {
								failed += 1;
							}
					}
					if (Setting.ECONOMY_ENABLE.getBoolean(plugin)
							&& plugin.getEconomy() != null
							&& Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin) > 0.0) {
						if (plugin.getEconomy().getBalance(player.getName()) < (selectedBlocks.size() * Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin))) {
							plugin.say(player, ChatColor.RED, Messages.getString("CE_Own.selection.noMoney",
											(selectedBlocks.size() * Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin)), plugin.getEconomy().currencyNamePlural()));
							return true;
						} else {
							plugin.say(player, ChatColor.YELLOW, Messages.getString("CE_Own.selection.howMuch",
											(selectedBlocks.size() * Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin)), plugin.getEconomy().currencyNamePlural()));
							plugin.getEconomy().withdrawPlayer(player.getName(), (selectedBlocks.size() * Setting.ECONOMY_PRICE_OWN_SELECTION.getDouble(plugin)));
						}
					}
					for (Block selectedBlock : selectedBlocks) {
						plugin.getOwning().setOwner(selectedBlock, user);
					}
					plugin.tell(sender, ChatColor.GREEN, Messages.getString("CE_Own.selection.success")); //$NON-NLS-1$
					if (failed > 0) {
						plugin.tell(sender, ChatColor.YELLOW, Messages.getString("CE_Own.selection.except", failed)); //$NON-NLS-1$
					}
					return true;
				} else {
					plugin.tell(sender, ChatColor.RED, Messages.getString("CE_Own.selection.noArea")); //$NON-NLS-1$
					return true;
				}
			} else {
				plugin.tell(sender, ChatColor.RED, Messages.getString("countArgs")); //$NON-NLS-1$
				return false;
			}
		} else {
			plugin.con(ChatColor.RED, Messages.getString("justForPlayers")); //$NON-NLS-1$
			return false;
		}

	}

}
