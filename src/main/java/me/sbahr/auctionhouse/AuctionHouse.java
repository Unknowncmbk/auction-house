package me.sbahr.auctionhouse;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

import me.sbahr.auctionhouse.command.AuctionHouseCommand;
import me.sbahr.auctionhouse.command.MoneyCommand;
import me.sbahr.auctionhouse.database.AuctionItemDAO;
import me.sbahr.auctionhouse.database.BaseDatabase;
import me.sbahr.auctionhouse.database.Credentials;
import me.sbahr.auctionhouse.database.UserDAO;
import me.sbahr.auctionhouse.house.AuctionManager;
import me.sbahr.auctionhouse.item.AuctionItem;
import me.sbahr.auctionhouse.item.AuctionType;
import me.sbahr.auctionhouse.menu.AuctionMenu;
import me.sbahr.auctionhouse.menu.AuctionTypeMenu;
import me.sbahr.auctionhouse.menu.OfferBuilderMenu;
import me.sbahr.auctionhouse.menu.PickItemStackMenu;
import me.sbahr.auctionhouse.player.PlayerListener;
import me.sbahr.auctionhouse.util.ItemStackUtil;
import me.sbahr.auctionhouse.util.JSONBuilder;

public class AuctionHouse extends JavaPlugin {

	/** The instance of this plugin */
	private static AuctionHouse instance;
	/** Database instance */
	private BaseDatabase database;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEnable() {

		// reference to plugin
		instance = this;

		initDatabase();

		// register player listener
		new PlayerListener(this);

		// register menus
		new AuctionMenu(this);
		new AuctionTypeMenu(this);
		new OfferBuilderMenu(this);
		new PickItemStackMenu(this);

		// register commands
		registerCommand(new AuctionHouseCommand(), "auctionhouse");
		registerCommand(new MoneyCommand(), "money");

		// ExampleItems.populateExampleItems();

		// load auction items
		List<AuctionItem> items = AuctionItemDAO.getAllAuctionItems(database.getConnection());
		for (AuctionItem ai : items) {
			AuctionManager.getInstance().addItem(ai);
		}
		
		// create auction pages
		AuctionManager.getInstance().createPages();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDisable() {

		// remove expired items
		AuctionManager.getInstance().getItems().forEach(ai -> {
			
			// if expired
			if (ai.hasExpired()){
				
				// remove from auction house
				AuctionItemDAO.removeAuctionItem(database.getConnection(), ai.getID());
				
				// give back to players
				if (ai.getType() == AuctionType.BUY){
					
					// create money json payload in db
					UserDAO.createTransaction(AuctionHouse.getInstance().getDB().getConnection(), ai.getOwner(), new JSONBuilder().set("type", "money").set("amount", ai.getPrice()).create().toJSONString());
				}
				else if (ai.getType() == AuctionType.SELL){
					// create itemstack json payload in db
					UserDAO.createTransaction(AuctionHouse.getInstance().getDB().getConnection(), ai.getOwner(), new JSONBuilder().set("type", "itemstack").set("item_data", ItemStackUtil.itemStackToBase64(ai.getItemStack())).set("amount", ai.getAmount()).create().toJSONString());
				}
				
			}
		});
	}

	/**
	 * Initialize the database.
	 */
	public void initDatabase() {

		saveDefaultConfig();

		String host = getConfig().getString("database.host");
		String name = getConfig().getString("database.name");
		String user = getConfig().getString("database.user");
		String pass = getConfig().getString("database.password");
		Credentials creds = new Credentials(host, name, user, pass);
		database = new BaseDatabase(creds);
	}

	/**
	 * Registers the command, skipping plugin.yml.
	 * 
	 * @param command - the command to register
	 * @param usage - the usage for that command
	 */
	public void registerCommand(BukkitCommand command, String usage) {
		try {
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

			bukkitCommandMap.setAccessible(true);
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

			commandMap.register(usage, command);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the instance of this plugin.
	 * 
	 * @return The instance of this plugin.
	 */
	public static AuctionHouse getInstance() {
		return instance;
	}

	/**
	 * Get the database.
	 * 
	 * @return The database instance.
	 */
	public BaseDatabase getDB() {
		return database;
	}
}
