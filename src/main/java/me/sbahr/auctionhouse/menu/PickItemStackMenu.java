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
import me.sbahr.auctionhouse.item.PendingOffer;
import me.sbahr.auctionhouse.player.BasePlayer;
import me.sbahr.auctionhouse.player.PlayerContainer;

public class PickItemStackMenu implements Listener {

	/** The title of this menu */
	private static final String MENU_TITLE = ChatColor.BOLD + "Choose ItemStack";
	/** The owning plugin */
	private Plugin plugin;

	/**
	 * Create a new PickItemStackMenu..
	 * <p>
	 * This is menu that displays items that can be chosen.
	 * 
	 * @param plugin - the owning plugin
	 */
	public PickItemStackMenu(Plugin plugin) {
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

		Inventory inven = Bukkit.createInventory(null, 9 * 4, MENU_TITLE);

		for (int i = 0; i < p.getInventory().getSize(); i++) {
			ItemStack is = p.getInventory().getItem(i);
			
			if (is != null){
				is = is.clone();
				is.setAmount(1);
				inven.setItem(i, is);
			}
		}

		return inven;
	}

	/**
	 * Listens in on gui open events.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onOpenGUI(OpenGUIEvent event) {

		if (event.getGUIType() == GUIType.CHOOSE_ITEM) {

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

		if (is != null) {

			BasePlayer bp = PlayerContainer.getInstance().getPlayer(p.getUniqueId()).orElse(null);
			if (bp != null) {

				PendingOffer po = bp.getPendingOffer();
				po.setItemStack(is.clone());
				
				// open choose type menu
				Bukkit.getPluginManager().callEvent(new OpenGUIEvent(p, GUIType.BUILD_OFFER));
			}
		}

		return false;
	}
}
