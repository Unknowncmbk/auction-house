package me.sbahr.auctionhouse.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.sbahr.auctionhouse.AuctionConfiguration;
import me.sbahr.auctionhouse.AuctionHouse;
import me.sbahr.auctionhouse.database.AuctionItemDAO;
import me.sbahr.auctionhouse.event.GUIType;
import me.sbahr.auctionhouse.event.OpenGUIEvent;
import me.sbahr.auctionhouse.house.AuctionManager;
import me.sbahr.auctionhouse.item.AuctionItem;
import me.sbahr.auctionhouse.item.AuctionType;
import me.sbahr.auctionhouse.item.PendingOffer;
import me.sbahr.auctionhouse.player.BasePlayer;
import me.sbahr.auctionhouse.player.PlayerContainer;
import me.sbahr.auctionhouse.util.AnvilGUI;
import me.sbahr.auctionhouse.util.AnvilGUI.AnvilClickEvent;
import me.sbahr.auctionhouse.util.AnvilGUI.AnvilClickEventHandler;
import me.sbahr.auctionhouse.util.AnvilGUI.AnvilSlot;
import me.sbahr.auctionhouse.util.MenuItems;

public class OfferBuilderMenu implements Listener {

	/** The title of this menu */
	private static final String MENU_TITLE = ChatColor.BOLD + "Create Offer";
	/** The owning plugin */
	private Plugin plugin;

	/**
	 * Create a new OfferBuilderMenu.
	 * <p>
	 * This is menu that displays the current offer types.
	 * 
	 * @param plugin - the owning plugin
	 */
	public OfferBuilderMenu(Plugin plugin) {
		this.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Create the inventory for the requested player.
	 * 
	 * @param p - the player requesting the inventory
	 * 
	 * @return The inventory that was created.
	 */
	protected Inventory createInventory(Player p) {

		Inventory inven = Bukkit.createInventory(null, 9 * 5, MENU_TITLE);

		// add offer item
		inven.setItem(10, getOfferItem(p));
		
		// add offer type item
		inven.setItem(12, getOfferTypeItem(p));

		// add quantity item
		inven.setItem(14, getQuantityItem(p));

		// add price item
		inven.setItem(16, getPriceItem(p));

		// add create offer item
		inven.setItem(31, getCreateOfferItem(p));

		return inven;
	}

	/**
	 * Listens in on gui open events.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onOpenGUI(OpenGUIEvent event) {

		if (event.getGUIType() == GUIType.BUILD_OFFER) {

			// grab event variables
			Player p = event.getPlayer();

			// create inventory
			Inventory inven = createInventory(p);
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
				
				PendingOffer po = bp.getPendingOffer();
				
				if (is.isSimilar(getOfferItem(p))) {
					
					// open choose item menu
					Bukkit.getPluginManager().callEvent(new OpenGUIEvent(p, GUIType.CHOOSE_ITEM));
				}
				else if (is.isSimilar(getOfferTypeItem(p))) {
					
					// open choose type menu
					Bukkit.getPluginManager().callEvent(new OpenGUIEvent(p, GUIType.CHOOSE_AUCTION_TYPE));
				}
				else if (is.isSimilar(getQuantityItem(p))) {
					
					AnvilGUI gui = new AnvilGUI(plugin, p, new AnvilClickEventHandler(){

						@Override
						public void onAnvilClick(AnvilClickEvent event) {

							String input = event.getInput().trim();
							try{
								int amount = Integer.parseInt(input);
								if (amount <= 0){
									p.sendMessage(ChatColor.RED + "Please enter a number greater than 0!");
									return;
								}
								
								po.setQuantity(amount);
								
								Bukkit.getScheduler().runTaskLater(plugin, () -> {
									// reopen menu
									Bukkit.getPluginManager().callEvent(new OpenGUIEvent(p, GUIType.BUILD_OFFER));
								}, 3L);
							}
							catch(Exception e){
								p.sendMessage(ChatColor.RED + "Please enter a valid number!");
							}
						}
					});
					gui.setSlot(AnvilSlot.INPUT_LEFT, MenuItems.modifyItemStack(new ItemStack(Material.NAME_TAG), "Enter amount", null));
					gui.open();
				}
				else if (is.isSimilar(getPriceItem(p))) {
					
					AnvilGUI gui = new AnvilGUI(plugin, p, new AnvilClickEventHandler(){

						@Override
						public void onAnvilClick(AnvilClickEvent event) {

							String input = event.getInput().trim();
							try{
								int amount = Integer.parseInt(input);
								if (amount <= 0){
									p.sendMessage(ChatColor.RED + "Please enter a number greater than 0!");
									return;
								}
								
								po.setPrice(amount);
								
								Bukkit.getScheduler().runTaskLater(plugin, () -> {
									// reopen menu
									Bukkit.getPluginManager().callEvent(new OpenGUIEvent(p, GUIType.BUILD_OFFER));
								}, 3L);
							}
							catch(Exception e){
								p.sendMessage(ChatColor.RED + "Please enter a valid number!");
							}
						}
					});
					gui.setSlot(AnvilSlot.INPUT_LEFT, MenuItems.modifyItemStack(new ItemStack(Material.NAME_TAG), "Enter price", null));
					gui.open();
				}
				else if (is.isSimilar(getCreateOfferItem(p))) {
					
					if (po.getItemStack() == null){
						p.sendMessage(ChatColor.RED + "Please choose an ItemStack!");
						return false;
					}
					
					if (po.getType() == null){
						p.sendMessage(ChatColor.RED + "Please choose an auction type!");
						return false;
					}
					
					if (po.getQuantity() <= 0){
						p.sendMessage(ChatColor.RED + "Please choose an amount to buy/sell!");
						return false;
					}
					
					if (po.getPrice() <= 0){
						p.sendMessage(ChatColor.RED + "Please choose a price!");
						return false;
					}
					
					if (po.getType() == AuctionType.SELL){
						if (!p.getInventory().containsAtLeast(po.getItemStack(), po.getQuantity())){
							p.sendMessage(ChatColor.RED + "You do not have " + po.getQuantity() + " " + po.getItemStack().getType().toString().toLowerCase() + " to sell!");
							return false;
						}
					}
					
					if (po.getType() == AuctionType.BUY){
						if (bp.getMoney() < po.getPrice()){
							p.sendMessage(ChatColor.RED + "You do not have enough money to create that offer!");
							return false;
						}
					}
					
					// create the offer
					AuctionItem ai = new AuctionItem(0, p.getUniqueId(), po.getType(), po.getItemStack(), po.getQuantity(), po.getPrice(), System.currentTimeMillis() + AuctionConfiguration.AUCTION_ITEM_DURATION);
					
					// async create in db
					Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
						
						int itemID = AuctionItemDAO.createAuctionItem(AuctionHouse.getInstance().getDB().getConnection(), ai);
						
						if (itemID > 0){
							Bukkit.getScheduler().runTask(plugin, () -> {
								ai.setID(itemID);
								
								// rebuild pages
								AuctionManager.getInstance().addItem(ai);
								AuctionManager.getInstance().createPages();
								
								if (ai.getType() == AuctionType.SELL){
									
									// remove the items
									ItemStack toRemove = ai.getItemStack().clone();
									toRemove.setAmount(1);
									
									for (int i = 0; i < ai.getAmount(); i++){
										p.getInventory().removeItem(toRemove);
									}
									p.updateInventory();
								}
								else if (ai.getType() == AuctionType.BUY){
									
									// subtract money
									bp.setMoney(bp.getMoney() - ai.getPrice());
								}
								
								if (p.isOnline()){
									p.sendMessage(ChatColor.GREEN + "Successfully created offer!");
								}
							});
						}
					});

					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * Get the offer type item for the inventory.
	 * 
	 * @param p - the player requesting the item
	 * 
	 * @return The ItemStack representation for this GUI item.
	 */
	private ItemStack getOfferItem(Player p) {

		BasePlayer bp = PlayerContainer.getInstance().getPlayer(p.getUniqueId()).orElse(null);
		if (bp != null && bp.getPendingOffer() != null && bp.getPendingOffer().getItemStack() != null) {
			return MenuItems.modifyItemStack(new ItemStack(bp.getPendingOffer().getItemStack().getType()), ChatColor.RED + "Change Item", Arrays.asList(ChatColor.WHITE + "Item: " + bp.getPendingOffer().getItemStack().getType()));
		}

		return MenuItems.modifyItemStack(new ItemStack(Material.REDSTONE_BLOCK), ChatColor.RED + "Change Item", Arrays.asList(ChatColor.GRAY + "Choose Item for this Auction"));
	}

	/**
	 * Get the offer type item for the inventory.
	 * 
	 * @param p - the player requesting the item
	 * 
	 * @return The ItemStack representation for this GUI item.
	 */
	private ItemStack getOfferTypeItem(Player p) {

		BasePlayer bp = PlayerContainer.getInstance().getPlayer(p.getUniqueId()).orElse(null);
		if (bp != null && bp.getPendingOffer() != null && bp.getPendingOffer().getType() != null) {
			return MenuItems.modifyItemStack(new ItemStack(Material.BANNER), ChatColor.RED + "Change Offer Type", Arrays.asList(ChatColor.WHITE + "Offer Type: " + bp.getPendingOffer().getType()));
		}

		return MenuItems.modifyItemStack(new ItemStack(Material.REDSTONE_BLOCK), ChatColor.RED + "Change Offer Type", Arrays.asList(ChatColor.GRAY + "Types can be either BUY or SELL"));
	}

	/**
	 * Get the quantity item for the inventory.
	 * 
	 * @param p - the player requesting the item
	 * 
	 * @return The ItemStack representation for this GUI item.
	 */
	private ItemStack getQuantityItem(Player p) {

		BasePlayer bp = PlayerContainer.getInstance().getPlayer(p.getUniqueId()).orElse(null);
		if (bp != null && bp.getPendingOffer() != null && bp.getPendingOffer().getQuantity() > 0) {
			return MenuItems.modifyItemStack(new ItemStack(Material.CHORUS_FLOWER), ChatColor.AQUA + "Change Amount", Arrays.asList(ChatColor.WHITE + "Amount to trade: " + bp.getPendingOffer().getQuantity()));
		}

		return MenuItems.modifyItemStack(new ItemStack(Material.REDSTONE_BLOCK), ChatColor.AQUA + "Change Amount", Arrays.asList(ChatColor.GRAY + "Click to set the quantity."));
	}

	/**
	 * Get the price item for the inventory.
	 * 
	 * @param p - the player requesting the item
	 * 
	 * @return The ItemStack representation for this GUI item.
	 */
	private ItemStack getPriceItem(Player p) {

		BasePlayer bp = PlayerContainer.getInstance().getPlayer(p.getUniqueId()).orElse(null);
		if (bp != null && bp.getPendingOffer() != null && bp.getPendingOffer().getPrice() > 0) {
			return MenuItems.modifyItemStack(new ItemStack(Material.GOLD_INGOT), ChatColor.YELLOW + "Change Price", Arrays.asList(ChatColor.WHITE + "Price: " + bp.getPendingOffer().getPrice()));
		}

		return MenuItems.modifyItemStack(new ItemStack(Material.REDSTONE_BLOCK), ChatColor.YELLOW + "Change Price", Arrays.asList(ChatColor.GRAY + "Click to set the price."));
	}

	/**
	 * Get the create offer item for the inventory.
	 * 
	 * @param p - the player requesting the item
	 * 
	 * @return The ItemStack representation for this GUI item.
	 */
	private ItemStack getCreateOfferItem(Player p) {
		return MenuItems.modifyItemStack(new ItemStack(Material.WORKBENCH), "" + ChatColor.GREEN + ChatColor.BOLD + "Create Offer!", Arrays.asList(ChatColor.GRAY + "Create your auction offer!"));
	}
}
