package me.sbahr.auctionhouse.util;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.ChatMessage;
import net.minecraft.server.v1_9_R2.ContainerAnvil;
import net.minecraft.server.v1_9_R2.EntityHuman;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.PacketPlayOutOpenWindow;

/**
 * From: https://bukkit.org/threads/anvilgui-use-the-anvil-gui-to-retrieve-strings.211849/
 */
public class AnvilGUI {

	/** Extend base ContainerAnvil. */
	private class AnvilContainer extends ContainerAnvil {
		public AnvilContainer(EntityHuman entity) {
			super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
		}

		@Override
		public boolean a(EntityHuman entityhuman) {
			return true;
		}
	}

	public enum AnvilSlot {
		INPUT_LEFT(0),
		INPUT_RIGHT(1),
		OUTPUT(2);

		/** Item slot */
		private int slot;

		/**
		 * Anvil slot enum.
		 * 
		 * @param slot - the slot
		 */
		AnvilSlot(int slot) {
			this.slot = slot;
		}

		/**
		 * Get the slot.
		 * 
		 * @return The slot.
		 */
		public int getSlot() {
			return slot;
		}

		/**
		 * Get the enum type based off slot.
		 * 
		 * @param slot - the slot
		 * @return The AnvilSlot enum corresponding to the slot.
		 */
		public static AnvilSlot bySlot(int slot) {
			for (AnvilSlot anvilSlot : values()) {
				if (anvilSlot.getSlot() == slot) {
					return anvilSlot;
				}
			}

			return null;
		}
	}

	public class AnvilClickEvent {

		/** The inventory */
		private Inventory inventory;
		/** The anvil slot */
		private AnvilSlot slot;
		/** The input bar contents */
		private String input;
		/** Should we close the inventory */
		private boolean close = true;
		/** Should we destroy the inventory */
		private boolean destroy = true;

		/**
		 * Construct a new Anvil Click Event.
		 * 
		 * @param inventory - the inventory clicked
		 * @param slot - the slot clicked
		 * @param input - the input
		 */
		public AnvilClickEvent(Inventory inventory, AnvilSlot slot, String input) {
			this.inventory = inventory;
			this.slot = slot;
			this.input = input;
		}

		/**
		 * Get the inventory that was clicked.
		 * 
		 * @return The inventory that was clicked.
		 */
		public Inventory getInventory() {
			return inventory;
		}

		/**
		 * Get the anvil slot that was clicked.
		 * 
		 * @return The anvil slot that was clicked.
		 */
		public AnvilSlot getSlot() {
			return slot;
		}

		/**
		 * Get the input of the anvil.
		 * 
		 * @return The input of the anvil.
		 */
		public String getInput() {
			return input;
		}

		/**
		 * Get whether or not the inventory closes.
		 * 
		 * @return {@code true} if the inventory closes, {@code false}
		 *         otherwise.
		 */
		public boolean getWillClose() {
			return close;
		}

		/**
		 * Set whether or not the inventory closes.
		 * 
		 * @param close {@code true} to close, {@code false} otherwise.
		 */
		public void setWillClose(boolean close) {
			this.close = close;
		}

		/**
		 * Get whether or not the inventory is destroyed.
		 * 
		 * @return {@code true} if the inventory is destroyed, {@code false}
		 *         otherwise.
		 */
		public boolean getWillDestroy() {
			return destroy;
		}

		/**
		 * Set whether or not the inventory is destroyed.
		 * 
		 * @param destroy {@code true} to destroy, {@code false} otherwise.
		 */
		public void setWillDestroy(boolean destroy) {
			this.destroy = destroy;
		}
	}

	public interface AnvilClickEventHandler {
		void onAnvilClick(AnvilClickEvent event);
	}

	/** The player that has this GUI */
	private Player player;
	/** Handler object */
	private AnvilClickEventHandler handler;
	/** Mapping of inventory contents */
	private HashMap<AnvilSlot, ItemStack> items = new HashMap<>();
	/** The inventory of the GUI */
	private Inventory inventory;
	/** Listener of the GUI */
	private Listener listener;

	/**
	 * Construct a new AnvilGUI.
	 * 
	 * @param plugin - the owning plugin
	 * @param player - the player involved
	 * @param handler - the handler
	 */
	public AnvilGUI(Plugin plugin, Player player, final AnvilClickEventHandler handler) {
		this.player = player;
		this.handler = handler;

		this.listener = new Listener() {

			@EventHandler
			public void onInventoryClick(InventoryClickEvent event) {
				if (event.getWhoClicked() instanceof Player) {
					Player clicker = (Player) event.getWhoClicked();

					if (event.getInventory().equals(inventory)) {
						event.setCancelled(true);

						ItemStack item = event.getCurrentItem();
						int slot = event.getRawSlot();
						String name = "";

						if (item != null) {
							if (item.hasItemMeta()) {
								ItemMeta meta = item.getItemMeta();

								if (meta.hasDisplayName()) {
									name = meta.getDisplayName();
								}
							}
						}

						AnvilClickEvent clickEvent = new AnvilClickEvent(inventory, AnvilSlot.bySlot(slot), name);

						handler.onAnvilClick(clickEvent);

						if (clickEvent.getWillClose()) {
							event.getWhoClicked().closeInventory();
						}

						if (clickEvent.getWillDestroy()) {
							destroy();
						}
					}
				}
			}

			@EventHandler
			public void onInventoryClose(InventoryCloseEvent event) {
				if (event.getPlayer() instanceof Player) {
					Player player = (Player) event.getPlayer();
					Inventory inv = event.getInventory();

					if (inv.equals(AnvilGUI.this.inventory)) {
						inv.clear();
						destroy();
					}
				}
			}

			@EventHandler
			public void onPlayerQuit(PlayerQuitEvent event) {
				if (event.getPlayer().equals(getPlayer())) {
					destroy();
				}
			}
		};

		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	/**
	 * Get the player from this AnvilGUI.
	 * 
	 * @return The player bound to this AnvilGUI.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Set the slot item in the GUI.
	 * 
	 * @param slot - the slot
	 * @param item - the item
	 */
	public void setSlot(AnvilSlot slot, ItemStack item) {
		items.put(slot, item);
	}

	/**
	 * Open the GUI.
	 */
	public void open() {
		EntityPlayer p = ((CraftPlayer) player).getHandle();

		AnvilContainer container = new AnvilContainer(p);

		// Set the items to the items from the inventory given
		inventory = container.getBukkitView().getTopInventory();

		for (AnvilSlot slot : items.keySet()) {
			inventory.setItem(slot.getSlot(), items.get(slot));
		}

		// Counter stuff that the game uses to keep track of inventories
		int c = p.nextContainerCounter();

		// Send the packet
		p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing"), 0));
		// Set their active container to the container
		p.activeContainer = container;

		// Set their active container window id to that counter stuff
		p.activeContainer.windowId = c;

		// Add the slot listener
		p.activeContainer.addSlotListener(p);
	}

	/**
	 * Destroy the GUI.
	 */
	public void destroy() {
		player = null;
		handler = null;
		items = null;

		HandlerList.unregisterAll(listener);

		listener = null;
	}
}
