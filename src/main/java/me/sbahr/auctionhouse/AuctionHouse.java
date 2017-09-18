package me.sbahr.auctionhouse;

import org.bukkit.plugin.java.JavaPlugin;

public class AuctionHouse extends JavaPlugin {

	/** The instance of this plugin */
	private static AuctionHouse instance;
	
	@Override
	public void onEnable(){
		
		// reference to plugin
		instance = this;
	}
	
	@Override
	public void onDisable(){
		
	}
}
