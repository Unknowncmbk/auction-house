package me.sbahr.auctionhouse.player;

import java.util.UUID;

public class BasePlayer {
	
	/** The uuid of the owning player */
	private final UUID uuid;
	
	/**
	 * Create a new BasePlayer reference.
	 * <p>
	 * This holds custom attributes and fields for our definition of a player.
	 * 
	 * @param uuid - the uuid of the player
	 */
	public BasePlayer(UUID uuid){
		this.uuid = uuid;
	}

	/**
	 * Get the uuid of this player object.
	 * 
	 * @return The uuid of this player.
	 */
	public UUID getUUID() {
		return uuid;
	}
}
