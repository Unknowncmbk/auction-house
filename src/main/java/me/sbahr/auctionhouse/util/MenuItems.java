package me.sbahr.auctionhouse.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuItems {
	
	/**
	 * Get the add auction item.
	 * 
	 * @return The ItemStack GUI item for the auction item.
	 */
	public static ItemStack getAddAuctionItem() {
		String displayName = ChatColor.GREEN + "Add Item";
		return modifyItemStack(new ItemStack(Material.EMERALD, 1), displayName, Arrays.asList(ChatColor.GRAY + "Add an auction item!"));
	}

	/**
	 * Get the next page GUI item.
	 * 
	 * @return The ItemStack GUI item.
	 */
	public static ItemStack getNextPageItem() {
		String displayName = ChatColor.GREEN + "Next Page >>";

		ItemStack is = modifyItemStack(new ItemStack(Material.BANNER, 1), displayName, null);
		BannerMeta bm = (BannerMeta) is.getItemMeta();
		bm.setBaseColor(DyeColor.GREEN);
		is.setItemMeta(bm);

		return is;
	}

	/**
	 * Get the previous page GUI item.
	 *
	 * @return The ItemStack GUI item.
	 */
	public static ItemStack getPrevPageItem() {
		String displayName = ChatColor.RED + "<< Previous Page";

		ItemStack is = modifyItemStack(new ItemStack(Material.BANNER, 1), displayName, null);
		BannerMeta bm = (BannerMeta) is.getItemMeta();
		bm.setBaseColor(DyeColor.RED);
		is.setItemMeta(bm);

		return is;
	}
	
	/**
	 * Get the buy auction type item.
	 * 
	 * @return The ItemStack GUI item for the buy option.
	 */
	public static ItemStack getBuyAuctionTypeItem() {
		String displayName = "" + ChatColor.AQUA + ChatColor.BOLD + "BUY";
		return modifyItemStack(new ItemStack(Material.DIAMOND, 1), displayName, Arrays.asList(ChatColor.GRAY + "Click this is you want to buy!"));
	}
	
	/**
	 * Get the sell auction type item.
	 * 
	 * @return The ItemStack GUI item for the sell option.
	 */
	public static ItemStack getSellAuctionTypeItem() {
		String displayName = "" + ChatColor.RED + ChatColor.BOLD + "SELL";
		return modifyItemStack(new ItemStack(Material.REDSTONE, 1), displayName, Arrays.asList(ChatColor.GRAY + "Click this if you want to sell!"));
	}

	/**
	 * Modifies the ItemStack to include the display name and lore.
	 *
	 * @param is - the ItemStack to modify
	 * @param displayName - the display name of the new ItemStack
	 * @param lore - the lore of the new ItemStack
	 * @param itemFlags - the item flags of the new ItemStack
	 * 
	 * @return The modified ItemStack with the display name and lore.
	 */
	public static ItemStack modifyItemStack(ItemStack is, String displayName, List<String> lore, ItemFlag... itemFlags) {

		// get the meta of the item
		ItemMeta im = is.getItemMeta();

		if (displayName != null && !displayName.equalsIgnoreCase("")) {
			im.setDisplayName(displayName);
		}

		if (lore != null) {
			im.setLore(lore);
		}

		im.addItemFlags(itemFlags);

		is.setItemMeta(im);
		return is;
	}
}
