package me.sbahr.auctionhouse.player;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;

import me.sbahr.auctionhouse.AuctionHouse;
import me.sbahr.auctionhouse.database.UserDAO;
import me.sbahr.auctionhouse.util.ItemStackUtil;
import me.sbahr.auctionhouse.util.JSONParser;

public class PlayerListener implements Listener {
	
	/** The owning plugin */
	private Plugin plugin;
	
	/**
	 * Create a new player listener.
	 * 
	 * @param plugin - the owning plugin
	 */
	public PlayerListener(Plugin plugin){
		this.plugin = plugin;
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Listens in on player join events.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		
		// grab the player
		Player p = event.getPlayer();
		
		// create a new base player with $100
		BasePlayer bp = new BasePlayer(p.getUniqueId());
		bp.setMoney(100);
		
		// add to player container
		PlayerContainer.getInstance().addPlayer(bp);
		
		// check for state transactions
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
			
			List<JSONObject> trans = UserDAO.getStateTransaction(AuctionHouse.getInstance().getDB().getConnection(), bp.getUUID());
			if (trans != null && trans.size() > 0){
				trans.forEach(j -> {
					
					JSONParser parser = new JSONParser(j);
					if (parser.hasKey("type")){
						String type = parser.getString("type");
						
						if (type.equalsIgnoreCase("money")){
							int amount = parser.getInt("amount");
							
							bp.setMoney(bp.getMoney() + amount);
						}
						else if (type.equalsIgnoreCase("itemstack")){
							
							// convert from base64 to itemstack
							ItemStack is = null;
							try {
								is = ItemStackUtil.itemStackFromBase64(parser.getString("item_data"));
							}
							catch (Exception e) {
								e.printStackTrace();
							}
							
							if (is != null){
								is.setAmount(1);
								
								// get the amount
								int amount = parser.getInt("amount");
								
								// credit to user
								for (int i = 0; i < amount; i++){
									p.getInventory().addItem(is.clone());
								}
								p.updateInventory();
							}
						}
					}
				});
			}
		}, 20L);
	}
	
	/**
	 * Listens in on player quit events.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		
		PlayerContainer.getInstance().removePlayer(event.getPlayer().getUniqueId());
	}
}
