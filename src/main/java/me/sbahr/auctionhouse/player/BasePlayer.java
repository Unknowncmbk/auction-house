package me.sbahr.auctionhouse.player;

import java.util.UUID;

import me.sbahr.auctionhouse.item.PendingOffer;

public class BasePlayer {
	
	/** The uuid of the owning player */
	private final UUID uuid;
	/** The currency for the player */
	private int money;
	/** The pending offer for this player */
	private PendingOffer pendingOffer;
	
	/**
	 * Create a new BasePlayer reference.
	 * <p>
	 * This holds custom attributes and fields for our definition of a player.
	 * 
	 * @param uuid - the uuid of the player
	 */
	public BasePlayer(UUID uuid){
		this.uuid = uuid;
		this.money = 0;
	}

	/**
	 * Get the uuid of this player object.
	 * 
	 * @return The uuid of this player.
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * Get the amount of money this player has.
	 * 
	 * @return The amount of money.
	 */
	public int getMoney() {
		return money;
	}

	/**
	 * Set the amount of money this player has.
	 * 
	 * @param money - the new amount of money.
	 */
	public void setMoney(int money) {
		this.money = money;
	}

	/**
	 * Get the pending offer for this player.
	 * <p>
	 * This is used as a builder so the player can build their offer up.
	 * 
	 * @return The pending offer for this player.
	 */
	public PendingOffer getPendingOffer() {
		return pendingOffer;
	}

	/**
	 * Sets the pending offer object for this player.
	 * 
	 * @param pendingOffer - the pending offer
	 */
	public void setPendingOffer(PendingOffer pendingOffer) {
		this.pendingOffer = pendingOffer;
	}
}
