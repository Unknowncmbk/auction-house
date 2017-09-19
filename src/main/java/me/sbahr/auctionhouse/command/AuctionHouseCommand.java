package me.sbahr.auctionhouse.command;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.sbahr.auctionhouse.event.GUIType;
import me.sbahr.auctionhouse.event.OpenGUIEvent;

public class AuctionHouseCommand extends BukkitCommand {

	/**
	 * Create a new AuctionHouseCommand which is used to open the auction house menu.
	 */
	public AuctionHouseCommand() {
		super("auctionhouse", "Opens the auction house", "/auctionhouse", Arrays.asList("ah"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		
		if (sender instanceof Player){
			Player player = (Player) sender;
			Bukkit.getPluginManager().callEvent(new OpenGUIEvent(player, GUIType.AUCTION_HOUSE));
		}
		
		return true;
	}
}
