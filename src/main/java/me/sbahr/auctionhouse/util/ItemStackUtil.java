package me.sbahr.auctionhouse.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemStackUtil {
	
	/**
	 * A method to serialize an {@link ItemStack} to a Base64 String
	 *
	 * Based off of {@link #toBase64(Inventory)}
	 *
	 * @param item Item being turned to Base64
	 *
	 * @return Base64 string of the item
	 *
	 * @throws IllegalStateException Illegal input state
	 */
	public static String itemStackToBase64(ItemStack item) throws IllegalStateException {
		if (isEmpty(item)) {
			item = new ItemStack(Material.AIR);
		}

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Write item
			dataOutput.writeObject(item);

			// Serialize
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		}
		catch (Exception e) {
			throw new IllegalStateException("Unable to save item stack", e);
		}
	}
	
	/**
	 * Gets an ItemStack from a Base64 string
	 * <p>
	 * Based off of {@link #fromBase64(String)}
	 *
	 * @param str Base64 string to convert to ItemStack
	 *
	 * @return ItemStack created from the Base64 string
	 *
	 * @throws IOException No data found
	 */
	public static ItemStack itemStackFromBase64(String str) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(str));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

			// Read the serialized item
			ItemStack itm = (ItemStack) dataInput.readObject();

			dataInput.close();
			return itm;
		}
		catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type", e);
		}
	}
	
	/**
	 * Get whether an ItemStack is empty in any capacity
	 *
	 * @param itm The ItemStack
	 *
	 * @return {@code true} if the ItemStack is empty, {@code false} otherwise
	 */
	public static boolean isEmpty(ItemStack itm) {
		return itm == null || itm.getType() == Material.AIR || itm.getAmount() <= 0;
	}
}
