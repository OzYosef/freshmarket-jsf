package models;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.marketplaceProject.db.CartsDB;

public class Cart {
	private int cartId;
	private Timestamp createdAt;
	private List<CartItem> items;
	private boolean updated;		// for manager thread

	// constructors
	public Cart(int cartId, Timestamp createdAt) {
		this.cartId = cartId;
		this.createdAt = createdAt;
		this.items = new ArrayList<>();
		this.updated = false;
	}

	public Cart(int cartId) {
		this(cartId, new Timestamp(System.currentTimeMillis()));
	}

	//  getters and setters
	public int getCartId() 					{ return cartId; }
	public Timestamp getCreatedAt() 		{ return createdAt; }
	public List<CartItem> getItems() 		{ return items; }

	public boolean isUpdated() 				{ return updated; }
	public void setUpdated(boolean updated) { this.updated = updated; }

	// add a new item or increasing qnt if exist
	public void addItem(int prodID, int qnt) {
		for (CartItem existing : items) {
			if (existing.getProdId() == prodID) {
				existing.increaseQuantity(qnt);
				this.updated = true;
				return;
			}
		}
		items.add(new CartItem(prodID, qnt));
		this.updated = true;
	}

	// decreasing the quantity
	public void decriceQty(int prodID, int qnt) {
		for (CartItem existing : items) {
			if (existing.getProdId() == prodID) {
				if (existing.getQuantity() + qnt < 1) {
					removeItem(prodID);
					this.updated = true;
					return;
				}
				existing.decreaseQuantity(qnt);
				this.updated = true;
				return;
			}
		}
	}

	// update all items of a cart to DB - for manager thread
	public synchronized void updateItemsInDB() {
		try {
			CartsDB.addOrUpdateItemList(cartId, items);
			setUpdated(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// get a single item by product id
	public CartItem getItem(int prodID) {
		for (CartItem existing : items) {
			if (existing.getProdId() == prodID) {
				return existing;
			}
		}
		return null;
	}

	// getting the items from DB
	public void getItemsFromDB() {
		try {
			this.items = CartsDB.getItemsByCartId(cartId);
		} catch (SQLException e) {
			System.out.println("[cartID: " + cartId + "] items not recived from DB");
		}
	}

	// remove an item from cart
	public void removeItem(int prodId) {
		// remove from list
		items.removeIf(item -> item.getProdId() == prodId);

		// remove from DB
		try {
			CartsDB.removeItem(this.getCartId(), prodId);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.updated = true;
	}

	// clear all cart items
	public void clearCart() {
		// empty memory cart
		items.clear();

		// empty from DB
		try {
			CartsDB.emptyCart(cartId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.updated = true;
	}
}
