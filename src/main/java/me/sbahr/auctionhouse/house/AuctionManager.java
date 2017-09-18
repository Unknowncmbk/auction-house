package me.sbahr.auctionhouse.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import me.sbahr.auctionhouse.item.AuctionItem;

public class AuctionManager {

	/** Singleton reference for this class */
	private static AuctionManager instance;
	/** All the auction items held by this manager */
	private List<AuctionItem> items;
	/** Maps the id of the auction item to the auction item */
	private Map<Integer, AuctionItem> idToItem;

	/**
	 * Construct a new AuctionManager.
	 * <p>
	 * This holds all the auction items.
	 */
	private AuctionManager() {
		this.items = new ArrayList<>();
		this.idToItem = new HashMap<>();
	}

	/**
	 * Get the singleton instance of this class.
	 * 
	 * @return The instance of the AuctionManager class.
	 */
	public static AuctionManager getInstance() {
		if (instance == null) {
			instance = new AuctionManager();
		}

		return instance;
	}
	
	/**
	 * Adds the item to the auction manager.
	 * 
	 * @param item - the item to add
	 * 
	 * @return {@code true} if the item was successfully added, {@code false} otherwise.
	 */
	public boolean addItem(AuctionItem item){
		
		if (!idToItem.containsKey(item.getID())){
			idToItem.put(item.getID(), item);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes the specified auction item from this manager.
	 * 
	 * @param item - the item to remove
	 * 
	 * @return The auction item that was removed, if one was found.
	 */
	public Optional<AuctionItem> removeItem(AuctionItem item){
		if (idToItem.containsKey(item.getID())){
			return Optional.of(idToItem.remove(item.getID()));
		}
		
		return Optional.empty();
	}
	
	/**
	 * Get the specified auction item from this manager.
	 * 
	 * @param item - the item to get
	 * 
	 * @return The auction item that was retrieved, if one was found.
	 */
	public Optional<AuctionItem> getItem(AuctionItem item){
		if (idToItem.containsKey(item.getID())){
			return Optional.of(idToItem.get(item.getID()));
		}
		
		return Optional.empty();
	}
}
