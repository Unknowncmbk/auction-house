package me.sbahr.auctionhouse.item;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.sbahr.auctionhouse.house.AuctionManager;

public class ExampleItems {
	
	/**
	 * Test method to populate example items into the manager.
	 */
	public static void populateExampleItems(){
		
		AuctionItem ai1 = new AuctionItem(1, UUID.randomUUID(), AuctionType.BUY, new ItemStack(Material.DIRT, 1), 64, 10, System.currentTimeMillis() + (30 * 1000));
		AuctionItem ai2 = new AuctionItem(2, UUID.randomUUID(), AuctionType.SELL, new ItemStack(Material.GRASS, 1), 64, 10, System.currentTimeMillis() + (30 * 1000));
		AuctionItem ai3 = new AuctionItem(3, UUID.randomUUID(), AuctionType.SELL, new ItemStack(Material.DIRT, 1), 64, 45, System.currentTimeMillis() + (30 * 1000));
		AuctionItem ai4 = new AuctionItem(4, UUID.randomUUID(), AuctionType.SELL, new ItemStack(Material.DIRT, 1), 64, 45, System.currentTimeMillis() + (30 * 1000));
		AuctionItem ai5 = new AuctionItem(5, UUID.randomUUID(), AuctionType.BUY, new ItemStack(Material.IRON_AXE, 1), 10, 55, System.currentTimeMillis() - (30 * 1000));
		AuctionItem ai6 = new AuctionItem(6, UUID.randomUUID(), AuctionType.BUY, new ItemStack(Material.DIRT, 1), 64, 10, System.currentTimeMillis() + (30 * 1000));
		AuctionItem ai7 = new AuctionItem(7, UUID.randomUUID(), AuctionType.SELL, new ItemStack(Material.DIRT, 1), 64, 56, System.currentTimeMillis() + (30 * 1000));
		AuctionItem ai8 = new AuctionItem(8, UUID.randomUUID(), AuctionType.BUY, new ItemStack(Material.GRASS, 1), 64, 1, System.currentTimeMillis() + (30 * 1000));
		AuctionItem ai9 = new AuctionItem(9, UUID.randomUUID(), AuctionType.SELL, new ItemStack(Material.DIRT, 1), 64, 55, System.currentTimeMillis() + (30 * 1000));
		AuctionItem ai10 = new AuctionItem(10, UUID.randomUUID(), AuctionType.BUY, new ItemStack(Material.DIAMOND, 1), 10, 100, System.currentTimeMillis() + (30 * 1000));
		
		AuctionManager.getInstance().addItem(ai1);
		AuctionManager.getInstance().addItem(ai2);
		AuctionManager.getInstance().addItem(ai3);
		AuctionManager.getInstance().addItem(ai4);
		AuctionManager.getInstance().addItem(ai5);
		AuctionManager.getInstance().addItem(ai6);
		AuctionManager.getInstance().addItem(ai7);
		AuctionManager.getInstance().addItem(ai8);
		AuctionManager.getInstance().addItem(ai9);
		AuctionManager.getInstance().addItem(ai10);
	}

}
