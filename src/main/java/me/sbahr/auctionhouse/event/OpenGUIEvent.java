package me.sbahr.auctionhouse.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class OpenGUIEvent extends PlayerEvent {
	
	/** List of handlers for this event. */
	private static final HandlerList HANDLERS = new HandlerList();
	/** The type of GUI to open */
	private final GUIType guiType;
	/** Any extra data bound to the open event */
	private Object data;

	/**
	 * Create a new OpenGUIEvent.
	 * <p>
	 * This should be called when we are trying to open a menu.
	 * 
	 * @param who - the player requesting to open
	 * @param guiType - the gui type to open
	 * @param data - extra data bound to the event
	 */
	public OpenGUIEvent(Player who, GUIType guiType, Object data) {
		super(who);
		this.guiType = guiType;
		this.data = data;
	}
	
	/**
	 * Create a new OpenGUIEvent.
	 * <p>
	 * This should be called when we are trying to open a menu.
	 * 
	 * @param who - the player requesting to open
	 * @param guiType - the gui type to open
	 */
	public OpenGUIEvent(Player who, GUIType guiType) {
		this(who, guiType, null);
	}
	
	/**
	 * Get the GUIType that is attempting to be opened.
	 * 
	 * @return The GUIType attempting to be opened.
	 */
	public GUIType getGUIType() {
		return guiType;
	}

	/**
	 * Get the extra data bound to this event.
	 * 
	 * @return The extra data bound to this event.
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Set the extra data bound to this event.
	 * 
	 * @param data - the extra data
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * Get the handlers of this event.
	 * 
	 * @return The handlers for this event.
	 */
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

}
