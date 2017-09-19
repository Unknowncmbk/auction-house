package me.sbahr.auctionhouse.command;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.sbahr.auctionhouse.player.BasePlayer;
import me.sbahr.auctionhouse.player.PlayerContainer;

public class MoneyCommand extends BukkitCommand {

	/**
	 * Create a new MoneyCommand which is used to display a user's balance.
	 */
	public MoneyCommand() {
		super("money", "Displays the player's money", "/money", Arrays.asList("bal", "m"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;

			BasePlayer bp = PlayerContainer.getInstance().getPlayer(player.getUniqueId()).orElse(null);
			if (bp != null) {
				player.sendMessage(ChatColor.WHITE + "Your money balance is: " + ChatColor.YELLOW + "$" + bp.getMoney());
			}
		}

		return true;
	}
}
