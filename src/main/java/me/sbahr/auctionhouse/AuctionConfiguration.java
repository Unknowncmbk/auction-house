package me.sbahr.auctionhouse;

import org.bukkit.ChatColor;

public class AuctionConfiguration {
	
	/**
	 * The title of the menu for the auction page
	 */
	public static final String AUCTION_MENU_TITLE = ChatColor.BOLD + "Auction House";
	
	/**
	 * The number of auction items to display per page.
	 * <p>
	 * Note: Do not set this above 45.
	 */
	public static final int AUCTION_ITEMS_PER_PAGE = 2;
	
	/**
	 * The duration of an auction item,
	 */
	public static final long AUCTION_ITEM_DURATION = 30 * 1000;

}
