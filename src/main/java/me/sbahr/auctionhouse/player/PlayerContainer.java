package me.sbahr.auctionhouse.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerContainer {

	/** Singleton reference for this class */
	private static PlayerContainer instance;
	/** Maps uuid of the player to the player obj */
	private Map<UUID, BasePlayer> players;

	/**
	 * Construct a new PlayerContainer.
	 * <p>
	 * This holds all the BasePlayer objects.
	 */
	private PlayerContainer() {
		this.players = new HashMap<>();
	}

	/**
	 * Get the singleton instance of this class.
	 * 
	 * @return The instance of the PlayerContainer class.
	 */
	public static PlayerContainer getInstance() {
		if (instance == null) {
			instance = new PlayerContainer();
		}

		return instance;
	}

	/**
	 * Adds the specified base player object reference to this container.
	 * 
	 * @param basePlayer - the base player to add
	 * 
	 * @return {@code true} if the player was added to the contaner,
	 *         {@code false} otherwise.
	 */
	public boolean addPlayer(BasePlayer basePlayer) {

		if (!players.containsKey(basePlayer.getUUID())){
			players.put(basePlayer.getUUID(), basePlayer);
			return true;
		}
		
		return false;
	}

	/**
	 * Removes the player from this container with the specified uuid.
	 * 
	 * @param uuid - the uuid of the player to remove
	 * 
	 * @return The player that was removed from this container, if found;
	 *         otherwise empty.
	 */
	public Optional<BasePlayer> removePlayer(UUID uuid) {
		if (players.containsKey(uuid)){
			return Optional.of(players.remove(uuid));
		}
		
		return Optional.empty();
	}

	/**
	 * Get the player from the container, if they exist.
	 * 
	 * @param uuid - the uuid of the player to lookup
	 * 
	 * @return The player that was retrieved, if one exists; otherwise empty.
	 */
	public Optional<BasePlayer> getPlayer(UUID uuid) {
		if (players.containsKey(uuid)) {
			return Optional.of(players.get(uuid));
		}

		return Optional.empty();
	}
}
