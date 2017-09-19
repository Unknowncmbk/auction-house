package me.sbahr.auctionhouse.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.sbahr.auctionhouse.AuctionConfiguration;
import me.sbahr.auctionhouse.AuctionHouse;
import me.sbahr.auctionhouse.database.AuctionItemDAO;
import me.sbahr.auctionhouse.item.AuctionItem;
import me.sbahr.auctionhouse.util.MenuItems;

public class AuctionManager {

	/** Singleton reference for this class */
	private static AuctionManager instance;
	/** Maps the id of the auction item to the auction item */
	private Map<Integer, AuctionItem> idToItem;
	/** Maps pages to inventory */
	private Map<Integer, Inventory> pageNumToInventory;

	/**
	 * Construct a new AuctionManager.
	 * <p>
	 * This holds all the auction items.
	 */
	private AuctionManager() {
		this.idToItem = new HashMap<>();
		this.pageNumToInventory = new HashMap<>();
	}

	/**
	 * Get the singleton instance of this class.
	 * 
	 * @return The instance of the AuctionManager class.
	 */
	public static AuctionManager getInstance() {
		if (instance == null) {
			instance = new AuctionManager();
		}

		return instance;
	}

	/**
	 * Adds the item to the auction manager.
	 * 
	 * @param item - the item to add
	 * 
	 * @return {@code true} if the item was successfully added, {@code false}
	 *         otherwise.
	 */
	public boolean addItem(AuctionItem item) {

		if (!idToItem.containsKey(item.getID())) {
			idToItem.put(item.getID(), item);
			return true;
		}

		return false;
	}

	/**
	 * Removes the specified auction item from this manager.
	 * 
	 * @param item - the item to remove
	 * 
	 * @return The auction item that was removed, if one was found.
	 */
	public Optional<AuctionItem> removeItem(AuctionItem item) {
		if (idToItem.containsKey(item.getID())) {
			return Optional.of(idToItem.remove(item.getID()));
		}

		return Optional.empty();
	}

	/**
	 * Get the specified auction item from this manager.
	 * 
	 * @param auctionID - the id of the auction item
	 * 
	 * @return The auction item that was retrieved, if one was found.
	 */
	public Optional<AuctionItem> getItem(int auctionID) {
		if (idToItem.containsKey(auctionID)) {
			return Optional.of(idToItem.get(auctionID));
		}

		return Optional.empty();
	}

	/**
	 * Get the requested page of this paginated auction house.
	 * 
	 * @param pageNum - the number of the page to get
	 * 
	 * @return The inventory, if one exists, that represents the auction house
	 *         page.
	 */
	public Optional<Inventory> getPage(int pageNum) {
		if (pageNumToInventory.containsKey(pageNum)) {
			return Optional.of(pageNumToInventory.get(pageNum));
		}

		return Optional.empty();
	}

	/**
	 * Returns a copy of all the auction items available in this manager.
	 * 
	 * @return A list of auction items in this manager.
	 */
	public List<AuctionItem> getItems() {
		return new ArrayList<>(idToItem.values());
	}
	
	/**
	 * Check for expired auction items, and remove them if they are expired.
	 */
	public void checkForExpired(){
		
		List<Integer> toRemove = new ArrayList<>();

		for (AuctionItem ai : idToItem.values()){
			if (ai.hasExpired()){
				toRemove.add(ai.getID());
			}
		}
		
		toRemove.forEach(i -> {
			if (idToItem.containsKey(i)){
				idToItem.remove(i);
			}
			
			Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
				AuctionItemDAO.removeAuctionItem(AuctionHouse.getInstance().getDB().getConnection(), i);
			});
		});
	}
	
	/**
	 * Looks through the specified ItemStack's lore to determine the auction item number.
	 * 
	 * @param is - the itemstack
	 * 
	 * @return The id of the auction item.
	 */
	public int getAuctionNumber(ItemStack is){
		
		if (is.hasItemMeta()){
			List<String> lore = is.getItemMeta().getLore();
			for (String s : lore){
				if (s.contains("Item #")){
					String[] parts = s.split("Item #");
					if (parts.length == 2){
						return Integer.parseInt(parts[1]);
					}
				}
			}
		}
		
		return -1;
	}

	/**
	 * Create the pages for the auction house.
	 */
	public void createPages() {
		
		// clear previous pages
		pageNumToInventory.clear();

		// determine the number of pages
		int maxPages = idToItem.size() / AuctionConfiguration.AUCTION_ITEMS_PER_PAGE;
		
		// if not evenly divided, increment the page by 1
		if (idToItem.size() % AuctionConfiguration.AUCTION_ITEMS_PER_PAGE != 0){
			maxPages++;
		}
		
		if (maxPages == 0){
			maxPages = 1;
		}

		// get the auction items
		List<AuctionItem> items = getItems();

		int currentPage = 1;
		while (currentPage <= maxPages) {

			// create a sub page
			List<AuctionItem> subPage = new ArrayList<>();
			for (int i = 0; i < AuctionConfiguration.AUCTION_ITEMS_PER_PAGE; i++) {

				// if we have items
				if (items.size() > 0) {
					subPage.add(items.remove(0));
				}
				else {
					break;
				}
			}
			createPage(subPage, currentPage, maxPages);

			currentPage++;
		}
	}

	/**
	 * Create a specified page and populate it to the manager.
	 * 
	 * @param items - the items on this page
	 * @param pageNum - the number for this page
	 * @param maxPages - the max pages possible
	 */
	protected void createPage(List<AuctionItem> items, int pageNum, int maxPages) {
		
		Inventory menu = Bukkit.getServer().createInventory(null, 9 * 6, AuctionConfiguration.AUCTION_MENU_TITLE);

		int index = 0;
		for (AuctionItem ai : items) {

			// get the itemstack for this auction item
			ItemStack itemStack = createItemStack(ai);

			if (index < menu.getSize()) {
				menu.setItem(index, itemStack);
				index++;
			}
		}
		
		// create previous page
		if (pageNum > 1){
			menu.setItem(45, MenuItems.getPrevPageItem());
		}
		
		// create next page
		if (pageNum < maxPages){
			menu.setItem(53, MenuItems.getNextPageItem());
		}
		
		// create auction item
		menu.setItem(49, MenuItems.getAddAuctionItem());

		pageNumToInventory.put(pageNum, menu);
	}

	/**
	 * Create the ItemStack representation for this auction item.
	 * 
	 * @param ai - the auction item
	 * 
	 * @return The ItemStack that represents this auction item.
	 */
	protected ItemStack createItemStack(AuctionItem ai) {

		// clone the item stack
		ItemStack itemStack = ai.getItemStack().clone();
		
		String displayName = null;
		switch(ai.getType()){
			case BUY:
				displayName = ChatColor.GREEN + "BUYING " + ChatColor.WHITE + ai.getItemStack().getType().toString().toLowerCase();
				break;
			case SELL:
				displayName = ChatColor.RED + "SELLING " + ChatColor.WHITE + ai.getItemStack().getType().toString().toLowerCase();
				break;
		}
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Item #" + ai.getID());
		lore.add("");
		lore.add(ChatColor.GRAY + "Quantity: " + ChatColor.WHITE + ai.getAmount());
		lore.add(ChatColor.GRAY + "Price: " + ChatColor.WHITE + "$" + ai.getPrice());
		
		return MenuItems.modifyItemStack(itemStack, displayName, lore);
	}
}
