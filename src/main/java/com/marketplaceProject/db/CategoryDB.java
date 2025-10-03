package com.marketplaceProject.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Category;

public class CategoryDB {
	private static final String URL = DataBaseInit.URL;
	private static final String USER = DataBaseInit.USER;
	private static final String PASS = DataBaseInit.PASS;

	// get all categories from DB
	public static List<Category> getAllCategories() throws SQLException {
		List<Category> categoriesList = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM categories");
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Category cat = new Category(rs.getInt("id"), rs.getString("name"), rs.getBoolean("deleted"));
				categoriesList.add(cat);
			}
		}

		return categoriesList;
	}

	// adding new category to DB - returning ID
	public static int addCategory(String name) throws SQLException {
		String sql = "INSERT INTO categories (name) VALUES (?)";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
				PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, name);
			stmt.executeUpdate();

			// returning the ID if no errors
			try (ResultSet rs = stmt.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1);
				} else {
					throw new SQLException();
				}
			}
		}
	}

	// set the deleted status
	public static void setDeleted(int id, boolean del) throws SQLException {
		String sql = "UPDATE categories SET deleted = ? WHERE id = ?";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setBoolean(1, del);
				stmt.setInt(2, id);
				stmt.executeUpdate();
			}
		}
	}

	// update category details in DB
	public static void updateCategory(Category cat) throws SQLException {
		String sql = "UPDATE categories SET name = ? WHERE id = ?";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, cat.getUptName());
				stmt.setInt(2, cat.getId());
				stmt.executeUpdate();
			}
		}
	}

}
