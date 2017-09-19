package me.sbahr.auctionhouse.item;

import org.bukkit.inventory.ItemStack;

public class PendingOffer {
	
	/** The type of auction */
	private AuctionType type;
	/** The ItemStack involved */
	private ItemStack itemStack;
	/** The quantity involved */
	private int quantity;
	/** The price involved */
	private int price;
	
	/**
	 * Create a new Pending offer with null values.
	 */
	public PendingOffer(){
		this.type = null;
		this.itemStack = null;
		this.quantity = -1;
		this.price = -1;
	}

	/**
	 * Get the type of the auction.
	 * 
	 * @return The type of the auction.
	 */
	public AuctionType getType() {
		return type;
	}

	/**
	 * Set the type of the auction.
	 * 
	 * @param type - the new type
	 */
	public void setType(AuctionType type) {
		this.type = type;
	}

	/**
	 * Get the ItemStack representation in this offer.
	 * 
	 * @return The ItemStack representation for this offer.
	 */
	public ItemStack getItemStack() {
		return itemStack;
	}

	/**
	 * Set the new ItemStack representation for this offer.
	 * 
	 * @param itemStack - the new ItemStack
	 */
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	/**
	 * Get the amount of items that are in this offer.
	 * 
	 * @return The quantity of items in this offer.
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Set the quantity of items in this offer.
	 * 
	 * @param quantity - the new amount
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Get the price of the offer.
	 * 
	 * @return The price of the offer.
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * Set the new price of the offer.
	 * 
	 * @param price - the new price
	 */
	public void setPrice(int price) {
		this.price = price;
	}
}
