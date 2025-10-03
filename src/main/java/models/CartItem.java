package models;

import java.sql.SQLException;

import com.marketplaceProject.db.ProductsDB;

public class CartItem {
	private int id;
	private int prodId;
	private int quantity;
	private Product product;

	// constructors
	public CartItem(int prodId, int quantity) {
		this.prodId = prodId;
		this.quantity = quantity;
	}

	public CartItem(int id, int prodId, int quantity) {
		this.id = id;
		this.prodId = prodId;
		this.quantity = quantity;
	}

	// getters and setters
	public Product getProduct() {
		if (product == null) {
			try {
				setProduct(ProductsDB.getProductById(prodId));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return product;
	}

	public void setProduct(Product product) 		{ this.product = product; }

	public int getId() 								{ return id; }
	public void setId(int id) 						{ this.id = id; }

	public int getProdId() 							{ return prodId; }
	public void setProdId(int prodId) 				{ this.prodId = prodId; }

	public int getQuantity() 						{ return quantity; }
	public void setQuantity(int quantity) {
		if (quantity >= 0) {
			this.quantity = quantity;
		}
	}

	// ===== Methods =====
	
	// increase quantity of an item
	public void increaseQuantity(int amount) {
		this.quantity += amount;
	}

	// decrease quantity of an item
	public void decreaseQuantity(int amount) {
		if (this.quantity - amount >= 0) {
			this.quantity -= amount;
		}
	}

}
