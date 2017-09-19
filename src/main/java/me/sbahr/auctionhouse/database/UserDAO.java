package me.sbahr.auctionhouse.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UserDAO {
	
	/**
	 * Get user state transactions that need to be credited to the user's account.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the suer
	 * 
	 * @return A list of json objects where each json needs to be handled seperately.
	 */
	public static List<JSONObject> getStateTransaction(Connection conn, UUID uuid){
		
		List<JSONObject> trans = new ArrayList<>();
		
		String query = "SELECT id, payload FROM user_state_transaction WHERE uuid=UNHEX(?);";
		
		try (PreparedStatement ps = conn.prepareStatement(query)){
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			
			try (ResultSet result = ps.executeQuery()){
				while (result.next()){
					
					JSONObject payload;
					try {
						payload = (JSONObject) new JSONParser().parse(result.getString("payload"));
					}
					catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					
					if (payload != null){
						trans.add(payload);
					}
					
					int transID = result.getInt("id");
					
					// delete the transaction
					deleteTransaction(conn, transID);
				}
			}
		}
		catch(Exception e){
			System.out.println("Error occurred for processStateTransaction() for uuid=" + uuid.toString());
			e.printStackTrace();
		}
		
		return trans;
	}
	
	/**
	 * Deletes the specified transaction from the database.
	 * 
	 * @param conn - the database thread
	 * @param transID - the transaction id to delete
	 * 
	 * @return {@code true} if the transaction was deleted, {@code false} otherwise.
	 */
	public static boolean deleteTransaction(Connection conn, int transID){
		
		String query = "DELETE FROM user_state_transaction WHERE id=?;";
		
		try (PreparedStatement ps = conn.prepareStatement(query)){
			ps.setInt(1, transID);
			ps.executeUpdate();
			return true;
		}
		catch(Exception e){
			System.out.println("Error occurred for deleteTransaction() for id=" + transID);
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Create transaction for the specified player and value.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid for the player
	 * @param value - the value for the transaction
	 * 
	 * @return {@code true} if the transaction was created, {@code false} otherwise.
	 */
	public static boolean createTransaction(Connection conn, UUID uuid, String value){
		
		String query = "INSERT IGNORE INTO user_state_transaction (uuid, payload) VALUES (UNHEX(?), ?);";
		
		try (PreparedStatement ps = conn.prepareStatement(query)){
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			ps.setString(2, value);
			
			ps.executeUpdate();
			return true;
		}
		catch(Exception e){
			System.out.println("Error occurred for createTransaction() for uuid=" + uuid + ", value=" + value);
			e.printStackTrace();
		}
		
		return false;
	}
}
