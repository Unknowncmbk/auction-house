package me.sbahr.auctionhouse.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.sbahr.auctionhouse.event.GUIType;
import me.sbahr.auctionhouse.event.OpenGUIEvent;
import me.sbahr.auctionhouse.item.AuctionType;
import me.sbahr.auctionhouse.item.PendingOffer;
import me.sbahr.auctionhouse.player.BasePlayer;
import me.sbahr.auctionhouse.player.PlayerContainer;
import me.sbahr.auctionhouse.util.MenuItems;

public class AuctionTypeMenu implements Listener {

	/** The title of this menu */
	private static final String MENU_TITLE = ChatColor.BOLD + "Select Auction Type";
	/** The owning plugin */
	private Plugin plugin;
	/** Inventory representation for this */
	private Inventory inventory;
	
	/**
	 * Create a new AuctionTypeMenu.
	 * <p>
	 * This is a menu that allows players to choose their auction type.
	 * 
	 * @param plugin - the owning plugin
	 */
	public AuctionTypeMenu(Plugin plugin) {
		this.plugin = plugin;
		this.inventory = createInventory();

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	/**
	 * Creates the inventory for this menu.
	 * 
	 * @return The inventory for this menu.
	 */
	protected Inventory createInventory(){
		Inventory gui = Bukkit.createInventory(null, 9 * 3, MENU_TITLE);
		
		gui.setItem(11, MenuItems.getBuyAuctionTypeItem());
		gui.setItem(15, MenuItems.getSellAuctionTypeItem());
		
		return gui;
	}

	/**
	 * Listens in on gui open events.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onOpenGUI(OpenGUIEvent event) {
		
		if (event.getGUIType() == GUIType.CHOOSE_AUCTION_TYPE){
			event.getPlayer().openInventory(inventory);
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

		if (inven.getTitle().equalsIgnoreCase(MENU_TITLE)) {
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
			
			BasePlayer bp = PlayerContainer.getInstance().getPlayer(p.getUniqueId()).orElse(null);
			if (bp != null){
				
				// grab player's pending offer
				PendingOffer po = bp.getPendingOffer();
				
				if (is.isSimilar(MenuItems.getBuyAuctionTypeItem())) {
					po.setType(AuctionType.BUY);
					
					// open the build offer menu
					Bukkit.getPluginManager().callEvent(new OpenGUIEvent(p, GUIType.BUILD_OFFER));
				}
				else if (is.isSimilar(MenuItems.getSellAuctionTypeItem())) {
					po.setType(AuctionType.SELL);
					
					// open the build offer menu
					Bukkit.getPluginManager().callEvent(new OpenGUIEvent(p, GUIType.BUILD_OFFER));
				}
			}
		}

		return false;
	}
}

