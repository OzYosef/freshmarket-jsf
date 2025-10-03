package com.marketplaceProject.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.marketplaceProject.beans.CategoryBean;

import models.Product;

public class ProductsDB {
	private static final String URL = DataBaseInit.URL;
	private static final String USER = DataBaseInit.USER;
	private static final String PASS = DataBaseInit.PASS;

	// add a product to DB
	public static void addProduct(String name, String description, String imagePath, int category, double price)
			throws Exception {
		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
				PreparedStatement stmt = conn.prepareStatement(
						"INSERT INTO products (prodName, description, image, category, price) VALUES (?, ?, ?, ?, ?)")) {

			// Add the barcode
			stmt.setString(1, name);
			stmt.setString(2, description);
			stmt.setString(3, imagePath);
			stmt.setInt(4, category);
			stmt.setDouble(5, price);

			stmt.executeUpdate();
		}
	}

	// get all products from DB
	public static List<Product> getAllProducts(CategoryBean catBean) throws Exception {
		List<Product> productList = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products");
				ResultSet rs = stmt.executeQuery()) {

			// creating the product object
			while (rs.next()) {
				Product product = new Product();
				product.setBarcode(rs.getInt("barcode"));
				product.setName(rs.getString("prodName"));
				product.setDescription(rs.getString("description"));
				product.setPrice(rs.getDouble("price"));
				product.setImage(rs.getString("image"));
				product.setDeleted(rs.getBoolean("deleted"));
				product.setCategory(catBean.getCategory(rs.getInt("category")));
				productList.add(product);
			}
		}

		return productList;
	}

	// get a product by id
	public static Product getProductById(int prodId) throws SQLException {
		String sql = "SELECT * FROM products WHERE barcode = ? AND deleted = FALSE";
		Product product = new Product();

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, prodId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					product.setBarcode(rs.getInt("barcode"));
					product.setName(rs.getString("prodName"));
					product.setDescription(rs.getString("description"));
					product.setPrice(rs.getDouble("price"));
					product.setImage(rs.getString("image"));
				}
			}
		}

		return product;
	}

	// set a product as deleted in DB
	public static void setDeleted(int barcode, boolean deleted) throws SQLException {
		String sql = "UPDATE products SET deleted = ? WHERE barcode = ?";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setBoolean(1, deleted);
				stmt.setInt(2, barcode);
				stmt.executeUpdate();
			}
		}
	}

	// update product details in DB
	public static void updateProduct(Product product) throws SQLException {
		String updateSql = "UPDATE products SET prodName = ?, description = ?,  price = ?, category = ? WHERE barcode = ?";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
			try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
				updateStmt.setString(1, product.getName());
				updateStmt.setString(2, product.getDescription());
				updateStmt.setDouble(3, product.getPrice());
				updateStmt.setInt(4, product.getCategory().getId());
				updateStmt.setInt(5, product.getBarcode());
				updateStmt.executeUpdate();
			}
		}
	}

}
