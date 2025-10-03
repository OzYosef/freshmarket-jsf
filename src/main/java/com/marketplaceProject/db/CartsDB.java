package com.marketplaceProject.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.CartItem;

public class CartsDB {
	private static final String URL = DataBaseInit.URL;
	private static final String USER = DataBaseInit.USER;
	private static final String PASS = DataBaseInit.PASS;

	// ========== Carts DB methods ==========//

	// creating new cart on DB
	public static int createNewCart() throws SQLException {

		String sql = "INSERT INTO carts (createdAt) VALUES (CURRENT_TIMESTAMP)";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
				PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

			stmt.executeUpdate();

			// returning cart ID if no errors
			try (ResultSet rs = stmt.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1);
				} else {
					throw new SQLException("Creating cart failed: no ID returned.");
				}
			}
		}
	}

	// ========== Cart Items DB methods ==========//

	// add or update a cart item
	public static void addOrUpdateItem(int cartId, CartItem item) throws SQLException {
		String selectSql = "SELECT id, quantity FROM cartItems WHERE cartId = ? AND prodId = ?";
		String updateSql = "UPDATE cartItems SET quantity = ? WHERE id = ?";
		String insertSql = "INSERT INTO cartItems (cartId, prodId, quantity) VALUES (?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
			try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
				selectStmt.setInt(1, cartId);
				selectStmt.setInt(2, item.getProdId());

				// getting the cart item
				try (ResultSet rs = selectStmt.executeQuery()) {
					// if the item exist in the cart
					if (rs.next()) {
						int existingId = rs.getInt("id");

						try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
							updateStmt.setInt(1, item.getQuantity());
							updateStmt.setInt(2, existingId);
							updateStmt.executeUpdate();
						}
					} else { // new item in cart
						try (PreparedStatement insertStmt = conn.prepareStatement(insertSql,
								Statement.RETURN_GENERATED_KEYS)) {
							insertStmt.setInt(1, cartId);
							insertStmt.setInt(2, item.getProdId());
							insertStmt.setInt(3, item.getQuantity());
							insertStmt.executeUpdate();

							// extract the key(id) of the new item row in DB
							try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
								if (generatedKeys.next()) {
									item.setId(generatedKeys.getInt(1));
								}
							}
						}
					}
				}
			}
		}
	}

	// remove empties
	public static void removeEmpty(int cartId) throws SQLException {
		String sql = "DELETE FROM cartItems WHERE cartId = ? AND quantity < 1";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, cartId);
				stmt.executeUpdate();
			}
		}
	}

	// empty the carts in DB
	public static void emptyCart(int cartId) throws SQLException {
		String sql = "DELETE FROM cartItems WHERE cartId = ?";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, cartId);
				stmt.executeUpdate();
			}
		}
	}

	// remove an item by cart and product ids
	public static void removeItem(int cartId, int prodID) throws SQLException {
		String sql = "DELETE FROM cartItems WHERE cartId = ? AND prodId = ?";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, cartId);
				stmt.setInt(2, prodID);
				stmt.executeUpdate();
			}
		}
	}

	// add or update list of items
	public static void addOrUpdateItemList(int cartId, List<CartItem> items) throws SQLException {
		for (CartItem item : items) {
			addOrUpdateItem(cartId, item);
			removeEmpty(cartId);
		}
	}

	// get all items of a cart
	public static List<CartItem> getItemsByCartId(int cartId) throws SQLException {
		List<CartItem> items = new ArrayList<>();
		String sql = "SELECT id, prodId, quantity FROM cartItems WHERE cartId = ?";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, cartId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int id = rs.getInt("id");
					int prodId = rs.getInt("prodId");
					int quantity = rs.getInt("quantity");
					items.add(new CartItem(id, prodId, quantity));
				}
			}
		}

		return items;
	}
}
