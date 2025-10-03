package com.marketplaceProject.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import com.marketplaceProject.managers.CartManager;

import models.Cart;
import models.User;

public class UsersDB {
    private static final String URL = DataBaseInit.URL;
    private static final String USER = DataBaseInit.USER;
    private static final String PASS = DataBaseInit.PASS;
    
    
    // add a User to DB
    public static void addUser(User user, String password) throws SQLException {
    	String hashedPassword = PasswordUtils.hashPassword(password);
    	try (Connection conn = DriverManager.getConnection(URL, USER, PASS); 
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, currentCart, firstname) VALUES (?, ?, ?, ?)")){
    		
    		stmt.setString(1, user.getUserName());
    		stmt.setString(2, hashedPassword);
    		stmt.setInt(3, user.getCurCart().getCartId());
    		stmt.setString(4, user.getFirstName());
    		stmt.executeUpdate();
    		
    		System.out.println("user [" + user.getUserName() + "] succesfully added to DB");
    	}
    }
    
    // get user by user name
    public static User getUserByUsername(String username) {

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS); 
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("userId"));
                    user.setUserName(rs.getString("username"));
                    user.setFirstName(rs.getString("firstname"));
                    
                    Cart cart = new Cart(rs.getInt("currentCart"));
                    CartManager.getInstance().registerCart(cart);
                    user.setCurCart(cart);
                    
                    return user;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; 
    }
    
    // returning the hashed password
    public static String getHashPassword(String username) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS); 
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
               stmt.setString(1, username);

               try (ResultSet rs = stmt.executeQuery()) {
                   if (rs.next()) {
                       return rs.getString("password");
                   }
               }

           } catch (SQLException e) {
               e.printStackTrace();
           }

           return null; 
       }
    	
    // sub class for hash and confirm a password.
	public class PasswordUtils {

		// hashing the password for better security
	    public static String hashPassword(String plainTextPassword) {
	        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
	    }

	    // validate the hashed password with original
	    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
	        return BCrypt.checkpw(plainTextPassword, hashedPassword);
	    }
	}
	
	

    
}
