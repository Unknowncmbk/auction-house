package me.sbahr.auctionhouse.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.sbahr.auctionhouse.AuctionConfiguration;
import me.sbahr.auctionhouse.AuctionHouse;
import me.sbahr.auctionhouse.database.AuctionItemDAO;
import me.sbahr.auctionhouse.database.UserDAO;
import me.sbahr.auctionhouse.event.GUIType;
import me.sbahr.auctionhouse.event.OpenGUIEvent;
import me.sbahr.auctionhouse.house.AuctionManager;
import me.sbahr.auctionhouse.item.AuctionItem;
import me.sbahr.auctionhouse.item.AuctionType;
import me.sbahr.auctionhouse.item.PendingOffer;
import me.sbahr.auctionhouse.player.BasePlayer;
import me.sbahr.auctionhouse.player.PlayerContainer;
import me.sbahr.auctionhouse.util.ItemStackUtil;
import me.sbahr.auctionhouse.util.JSONBuilder;
import me.sbahr.auctionhouse.util.MenuItems;

public class AuctionMenu implements Listener {

	/** The owning plugin */
	private Plugin plugin;
	/** Maps UUID to page number */
	private Map<UUID, Integer> uuidToPage;

	/**
	 * Create a new AuctionMenu.
	 * <p>
	 * This is a paginated menu that displays the contents of the auction house.
	 * 
	 * @param plugin - the owning plugin
	 */
	public AuctionMenu(Plugin plugin) {
		this.plugin = plugin;
		this.uuidToPage = new HashMap<>();

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Listens in on gui open events.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onOpenGUI(OpenGUIEvent event) {
		
		if (event.getGUIType() == GUIType.AUCTION_HOUSE){
			
			// grab event variables
			Player p = event.getPlayer();

			Inventory inven = AuctionManager.getInstance().getPage(getPageNumber(p)).orElse(null);
			if (inven != null) {
				p.openInventory(inven);
			}
		}
	}

	/**
	 * Listens in on inventory clicks.
	 *
	 * @param e - the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onInventoryInteract(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory inven = e.getInventory();

		if (inven.getTitle().equalsIgnoreCase(AuctionConfiguration.AUCTION_MENU_TITLE)) {
			e.setCancelled(true);

			if (e.isLeftClick() || e.isRightClick() || e.isShiftClick()) {
				if (e.getCurrentItem() != null && e.getRawSlot() < inven.getSize()) {
					boolean success = handleMenuClick(e, player);

					if (success) {
						player.closeInventory();
					}
				}
			}
		}
	}
	
	/**
	 * Listens in on the player quit event.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Player p = event.getPlayer();
		
		// clean up references
		if (uuidToPage.containsKey(p.getUniqueId())){
			uuidToPage.remove(p.getUniqueId());
		}
	}

	/**
	 * Handles the menu click for the player.
	 *
	 * @param e - the click event
	 * @param p - the player involved
	 * 
	 * @return {@code true} if they successfully can traverse through the menu,
	 *         {@code false} otherwise.
	 */
	private boolean handleMenuClick(InventoryClickEvent e, Player p) {
		ItemStack is = e.getCurrentItem();

		if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {

			if (is.isSimilar(MenuItems.getNextPageItem())) {

				int currentPage = getPageNumber(p);
				uuidToPage.put(p.getUniqueId(), currentPage + 1);

				// reopen menu
				Bukkit.getPluginManager().callEvent(new OpenGUIEvent(p, GUIType.AUCTION_HOUSE));
			}
			else if (is.isSimilar(MenuItems.getPrevPageItem())) {
				int currentPage = getPageNumber(p);
				uuidToPage.put(p.getUniqueId(), currentPage - 1);

				// reopen menu
				Bukkit.getPluginManager().callEvent(new OpenGUIEvent(p, GUIType.AUCTION_HOUSE));
			}
			else if (is.isSimilar(MenuItems.getAddAuctionItem())) {
				
				BasePlayer bp = PlayerContainer.getInstance().getPlayer(p.getUniqueId()).orElse(null);
				if (bp != null){
					bp.setPendingOffer(new PendingOffer());
					
					// open the build offer menu
					Bukkit.getPluginManager().callEvent(new OpenGUIEvent(p, GUIType.BUILD_OFFER));
				}
			}
			else {

				// determine the auction number of the item
				Integer auctionID = AuctionManager.getInstance().getAuctionNumber(is);

				// get that item from the manager
				AuctionItem ai = AuctionManager.getInstance().getItem(auctionID).orElse(null);
				if (ai != null) {

					if (ai.getType() == AuctionType.BUY) {
						return handleBuyItem(e, p, ai);
					}
					else if (ai.getType() == AuctionType.SELL) {
						return handleSellItem(e, p, ai);
					}
				}
			}
		}

		return false;
	}

	/**
	 * Handles the player buying the item from the auction house.
	 * 
	 * @param e - the inventory click event
	 * @param p - the player buying the item
	 * @param ai - the auction item being bought
	 * 
	 * @return {@code true} if the transaction went through correctly,
	 *         {@code false} otherwise.
	 */
	private boolean handleBuyItem(InventoryClickEvent e, Player p, AuctionItem ai) {

		if (ai.hasExpired()) {
			p.sendMessage(ChatColor.RED + "This item can no longer be bought!");
			return false;
		}

		// make sure this player has the amount to sell to this auction
		if (p.getInventory().containsAtLeast(ai.getItemStack(), ai.getAmount())) {

			ItemStack is = ai.getItemStack().clone();
			is.setAmount(1);

			// remove the items from the player
			for (int i = 0; i < ai.getAmount(); i++) {
				p.getInventory().removeItem(is);
			}
			p.updateInventory();

			// reward the player with cash
			BasePlayer bp = PlayerContainer.getInstance().getPlayer(p.getUniqueId()).orElse(null);
			if (bp != null) {
				bp.setMoney(bp.getMoney() + ai.getAmount());
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 100, 0.5f);
				p.sendMessage(ChatColor.GREEN + "Sold " + ai.getAmount() + " " + ai.getItemStack().getType().toString().toLowerCase() + " for " + ChatColor.YELLOW + "$" + ai.getPrice());
				
				// remove the auction item, refresh auction manager
				AuctionManager.getInstance().removeItem(ai);
				AuctionManager.getInstance().createPages();
				
				// see if other player is online
				Player buyer = Bukkit.getPlayer(ai.getOwner());
				if (buyer != null && buyer.isOnline()){
					
					// then give them items directly
					ItemStack toAdd = ai.getItemStack().clone();
					toAdd.setAmount(1);
					
					for (int a = 0; a < ai.getAmount(); a++){
						buyer.getInventory().addItem(toAdd);
					}
					
					buyer.sendMessage(ChatColor.GREEN + p.getDisplayName() + ChatColor.WHITE + " has sold you " + ai.getAmount() + " " + ai.getItemStack().getType().toString().toLowerCase() + " for " + ChatColor.YELLOW + "$" + ai.getPrice());
				}
				
				// async call
				Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
					AuctionItemDAO.removeAuctionItem(AuctionHouse.getInstance().getDB().getConnection(), ai.getID());
					
					// if buyer is not found
					if (buyer == null || !buyer.isOnline()){
						
						// create itemstack json payload in db
						UserDAO.createTransaction(AuctionHouse.getInstance().getDB().getConnection(), ai.getOwner(), new JSONBuilder().set("type", "itemstack").set("item_data", ItemStackUtil.itemStackToBase64(ai.getItemStack())).set("amount", ai.getAmount()).create().toJSONString());
					}
				});
				
				return true;
			}
		}
		else {
			p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 0.5f);
			p.sendMessage(ChatColor.RED + "You do not have enough " + ai.getItemStack().getType().toString().toLowerCase() + " to sell. They are asking for " + ai.getAmount());
		}

		return false;
	}

	/**
	 * Handles the selling of an item for the specified player.
	 * 
	 * @param e - the inventory click event
	 * @param p - the player selling the item
	 * @param ai - the auction item being sold
	 * 
	 * @return {@code true} if the transaction went through, {@code false}
	 *         otherwise.
	 */
	private boolean handleSellItem(InventoryClickEvent e, Player p, AuctionItem ai) {

		if (ai.hasExpired()) {
			p.sendMessage(ChatColor.RED + "This item is no longer on sale!");
			return false;
		}

		BasePlayer bp = PlayerContainer.getInstance().getPlayer(p.getUniqueId()).orElse(null);
		if (bp != null) {

			if (bp.getMoney() >= ai.getPrice()) {

				// subtract the money
				bp.setMoney(bp.getMoney() - ai.getPrice());

				ItemStack is = ai.getItemStack().clone();
				is.setAmount(1);

				// add item to the player
				for (int i = 0; i < ai.getAmount(); i++) {
					p.getInventory().addItem(is);
				}
				p.updateInventory();

				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 100, 0.5f);
				p.sendMessage(ChatColor.GREEN + "Bought " + ai.getAmount() + " " + ai.getItemStack().getType().toString().toLowerCase() + " for " + ChatColor.YELLOW + "$" + ai.getPrice());
				
				// remove the auction item
				AuctionManager.getInstance().removeItem(ai);
				AuctionManager.getInstance().createPages();
				
				// see if other player is online
				Player seller = Bukkit.getPlayer(ai.getOwner());
				if (seller != null && seller.isOnline()){
					BasePlayer sellerObj = PlayerContainer.getInstance().getPlayer(seller.getUniqueId()).orElse(null);
					if (sellerObj != null){
						sellerObj.setMoney(sellerObj.getMoney() + ai.getPrice());
						seller.sendMessage(ChatColor.GREEN + p.getDisplayName() + ChatColor.WHITE + " has just bought " + ai.getAmount() + " " + ai.getItemStack().getType().toString().toLowerCase() + " for " + ChatColor.YELLOW + "$" + ai.getPrice());
					}
				}
				
				// async remove
				Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
					AuctionItemDAO.removeAuctionItem(AuctionHouse.getInstance().getDB().getConnection(), ai.getID());
				
					if (seller == null || !seller.isOnline()){
						// create money json payload in db
						UserDAO.createTransaction(AuctionHouse.getInstance().getDB().getConnection(), ai.getOwner(), new JSONBuilder().set("type", "money").set("amount", ai.getPrice()).create().toJSONString());
					}
				});
				
				return true;
			}
			else {
				p.sendMessage(ChatColor.RED + "You do not have enough money to purchase this item!");
			}
		}

		return false;
	}

	/**
	 * Get the page number that the player is currently on.
	 * 
	 * @param p - the player in question
	 * 
	 * @return The number that the player is currently on.
	 */
	private int getPageNumber(Player p) {
		if (uuidToPage.containsKey(p.getUniqueId())) {
			int pageNum = uuidToPage.get(p.getUniqueId());

			// if that page exists
			if (AuctionManager.getInstance().getPage(pageNum).isPresent()) {
				return pageNum;
			}
		}

		// return page 1
		return 1;
	}
}
