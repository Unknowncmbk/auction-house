package me.sbahr.auctionhouse.item;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

public class AuctionItem {
	
	/** The id of the auction item */
	private int id;
	/** The uuid of the owner of this auction item */
	private final UUID owner;
	/** The type of auction */
	private final AuctionType type;
	/** The ItemStack representation for this auction */
	private final ItemStack itemStack;
	/** The amount of this item for trade */
	private final int amount;
	/** The price of the item for trade */
	private final int price;
	/** The timestamp, in millis, for when this item expires */
	private final long expiration;
	
	/**
	 * Construct a new AuctionItem.
	 * <p>
	 * This is a data object that holds relative information about an item that is on sale in the auction house.
	 * 
	 * @param id - the integer id of this item
	 * @param owner - the uuid of the owner of the auction item
	 * @param type - the type of sale it is
	 * @param itemStack - the itemstack representation of the item on sale
	 * @param amount - the amount of the item that is on sale
	 * @param price - the price for the item
	 * @param expiration - the expiration for this sale
	 */
	public AuctionItem(int id, UUID owner, AuctionType type, ItemStack itemStack, int amount, int price, long expiration){
		this.id = id;
		this.owner = owner;
		this.type = type;
		this.itemStack = itemStack;
		this.amount = amount;
		this.price = price;
		this.expiration = expiration;
	}

	/**
	 * Get the ID of this auction item.
	 * 
	 * @return The id of this auction item.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Set the ID for this auction item.
	 * 
	 * @param id - the new id
	 */
	public void setID(int id){
		this.id = id;
	}

	/**
	 * Get the UUID of the owner of the auction item.
	 * 
	 * @return The uuid of the owner of the auction item.
	 */
	public UUID getOwner() {
		return owner;
	}

	/**
	 * Get the type of the auction item.
	 * 
	 * @return The type of the auction item.
	 */
	public AuctionType getType() {
		return type;
	}

	/**
	 * Get the ItemStack representation for this item.
	 * 
	 * @return The ItemStack representation for this item.
	 */
	public ItemStack getItemStack() {
		return itemStack;
	}

	/**
	 * Get the quantity of this item that is being sold.
	 * 
	 * @return The quantity of this item that is being sold.
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Get the price of the item that is being sold.
	 * 
	 * @return The price of the item that is being sold.
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * Get the expiration, in milliseconds, for when this auction item is expired.
	 * 
	 * @return The expiration time, in milliseconds, for this auction item has expired.
	 */
	public long getExpiration() {
		return expiration;
	}
	
	/**
	 * Get whether or not this auction item has expired.
	 * 
	 * @return {@code true} if the auction item has expired.
	 */
	public boolean hasExpired(){
		return System.currentTimeMillis() > expiration;
	}
}
