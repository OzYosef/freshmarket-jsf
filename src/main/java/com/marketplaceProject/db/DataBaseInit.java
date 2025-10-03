package com.marketplaceProject.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


public class DataBaseInit {
	public static final String DBCON = "jdbc:mysql://localhost:3306/";
	public static final String DBNAME = "marketplace";
    public static final String URL = DBCON + DBNAME;
    public static final String USER = "Oz";
    public static final String PASS = "Oz1234";
	
	public static void initialize() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // create the connection
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            Statement stmt = conn.createStatement();
            
            // create new DB if not exist
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DBNAME);
            
            // initiate categories table            
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS categories (
            		    id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50) NOT NULL UNIQUE,
            			deleted BOOLEAN NOT NULL DEFAULT FALSE
                    )
                """);

            // initiate product table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS products (
                    barcode INT AUTO_INCREMENT PRIMARY KEY,
                    prodName VARCHAR(100) NOT NULL,
                    description TEXT,
                    image VARCHAR(255) NOT NULL,
                    category INT,
                    price DECIMAL(10,2) NOT NULL,
                    deleted BOOLEAN NOT NULL DEFAULT FALSE,
                    FOREIGN KEY (category) REFERENCES categories(id)
                )
            """);
            
            
            // initiate carts table
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS carts (
                        cartId INT AUTO_INCREMENT PRIMARY KEY,
                        createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);
            
            // initiate carts table
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS cartItems (
            		    id INT AUTO_INCREMENT PRIMARY KEY,
                        cartId INT NOT NULL,
                        prodId INT NOT NULL,
                        quantity int NOT NULL,
                        FOREIGN KEY (cartId) REFERENCES carts(cartId),
            			FOREIGN KEY (prodId) REFERENCES products(barcode)
                    )
                """);
            
            
            //TODO: add more parameters for user for example name, email etc.
            //initiate users table
            stmt.executeUpdate("""
                CREATE TABLE  IF NOT EXISTS users (
            		userId INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    firstname VARCHAR(50),
                    password VARCHAR(255) NOT NULL,
                    currentCart INT,
                    FOREIGN KEY (currentCart) REFERENCES carts(cartId)
                )
            """);
            
            
            

            stmt.close();
            conn.close();
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
        	System.out.println("Database initialized Failed.");
            e.printStackTrace();
        }
    }
	
}
