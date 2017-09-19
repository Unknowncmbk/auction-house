package me.sbahr.auctionhouse.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.inventory.ItemStack;

import me.sbahr.auctionhouse.item.AuctionItem;
import me.sbahr.auctionhouse.item.AuctionType;
import me.sbahr.auctionhouse.util.ItemStackUtil;

public class AuctionItemDAO {
	
	/**
	 * A {@link Pattern} used to identify and/or split full UUIDs
	 */
	public static final Pattern PATTERN_UUID = Pattern.compile("^[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}$", Pattern.CASE_INSENSITIVE);
	/**
	 * A {@link Pattern} used to identify and/or split trimmed UUIDs
	 */
	public static final Pattern PATTERN_TRIMMED_UUID = Pattern.compile("^([a-z0-9]{8})([a-z0-9]{4})([a-z0-9]{4})([a-z0-9]{4})([a-z0-9]{12})$", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Create the auction item in the database.
	 * 
	 * @param conn - the database connection
	 * @param item - the item to save
	 * 
	 * @return The id of the auction item that was created.
	 */
	public static int createAuctionItem(Connection conn, AuctionItem item){
		
		String query = "INSERT IGNORE INTO auction_item (uuid, auction_type, value, amount, price, expired) VALUES (UNHEX(?), ?, ?, ?, ?, ?);";
		
		try (PreparedStatement ps = conn.prepareStatement(query)){
			ps.setString(1, item.getOwner().toString().replaceAll("-", ""));
			ps.setString(2, item.getType().name());
			ps.setString(3, ItemStackUtil.itemStackToBase64(item.getItemStack()));
			ps.setInt(4, item.getAmount());
			ps.setInt(5, item.getPrice());
			ps.setTimestamp(6, new Timestamp(item.getExpiration()));
			
			ps.executeUpdate();
			
			try (PreparedStatement ps2 = conn.prepareStatement("SELECT LAST_INSERT_ID();")){
				try (ResultSet result = ps2.executeQuery()){
					if (result.next()){
						return result.getInt(1);
					}
				}
			}
		}
		catch(Exception e){
			System.out.println("Error occurred for createAuctionItem() for id=" + item.getID());
			e.printStackTrace();
		}
		
		return -1;
	}
	
	/**
	 * Remove the specified auction item from the database.
	 * 
	 * @param conn - the database connection thread
	 * @param itemID - the id of the item
	 * 
	 * @return {@code true} if the auction item was removed, {@code false} otherwise.
	 */
	public static boolean removeAuctionItem(Connection conn, int itemID){
		
		String query = "DELETE FROM auction_item WHERE id=?;";
		
		try (PreparedStatement ps = conn.prepareStatement(query)){
			ps.setInt(1, itemID);
			
			ps.executeUpdate();
			return true;
		}
		catch(Exception e){
			System.out.println("Error occurred for removeAuctionItem() for id=" + itemID);
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Get all the auction items in the database.
	 * 
	 * @param conn - the database connection thread
	 * 
	 * @return A list of auction items.
	 */
	public static List<AuctionItem> getAllAuctionItems(Connection conn){
		
		List<AuctionItem> items = new ArrayList<>();
		
		String query = "SELECT id, HEX(uuid), auction_type, value, amount, price, expired FROM auction_item;";
		
		try (PreparedStatement ps = conn.prepareStatement(query)){
			try (ResultSet result = ps.executeQuery()){
				while (result.next()){
					
					int id = result.getInt("id");
					UUID owner = createUUID(result.getString("uuid")).orElse(null);
					AuctionType type = AuctionType.valueOf(result.getString("auction_type"));
					ItemStack itemStack = ItemStackUtil.itemStackFromBase64(result.getString("value"));
					int amount = result.getInt("amount");
					int price = result.getInt("price");
					Timestamp expired = result.getTimestamp("expired");
					
					items.add(new AuctionItem(id, owner, type, itemStack, amount, price, expired.getTime()));
				}
			}
		}
		catch(Exception e){
			System.out.println("Error occurred for getAllAuctionitems()");
			e.printStackTrace();
		}
		
		return items;
	}
	
	/**
	 * Create a UUID safely from a {@link String}.
	 *
	 * @param string The {@link String} to deserialize into an {@link UUID} object.
	 * @return {@link Optional#empty()} if the provided {@link String} is illegal, otherwise an {@link Optional}
	 * containing the deserialized {@link UUID} object.
	 */
	protected static Optional<UUID> createUUID(String string) {
		if (string == null) {
			return Optional.empty();
		}

		UUID result = null;

		try {
			// Is it a valid UUID?
			if (!PATTERN_UUID.matcher(string).matches()) {
				// Un-trim UUID if it is trimmed
				Matcher matcher = PATTERN_TRIMMED_UUID.matcher(string);
				if (matcher.matches()) {
					StringBuilder sb = new StringBuilder();

					for (int i = 1; i <= matcher.groupCount(); i++) {
						if (i != 1) {
							sb.append("-");
						}

						sb.append(matcher.group(i));
					}

					string = sb.toString();
				}
				else {
					// Invalid UUID
					string = null;
				}
			}

			if (string != null) {
				result = UUID.fromString(string);
			}
		}
		catch (IllegalArgumentException ignored) {
			// Useless data passed
		}

		return Optional.ofNullable(result);
	}
}
