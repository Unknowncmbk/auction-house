package me.sbahr.auctionhouse.item;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import me.sbahr.auctionhouse.house.AuctionManager;

public class AuctionItemTest {
	
	@Test
	public void hasExpired(){
		long expired = System.currentTimeMillis() - 1000;
		
		AuctionItem ai = new AuctionItem(1, null, null, null, 0, 0, expired);
		assertEquals(true, ai.hasExpired());
	}
	
	@Test
	public void getFromManager(){
		
		AuctionItem ai = new AuctionItem(1, null, null, null, 0, 0, 0);
		AuctionManager.getInstance().addItem(ai);
		
		assertEquals(true, AuctionManager.getInstance().getItem(ai).isPresent());
	}

}
