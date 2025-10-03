package models;

import java.io.Serializable;
import java.sql.SQLException;

import com.marketplaceProject.db.CartsDB;
import com.marketplaceProject.managers.CartManager;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String userName;
	private String firstName;
	private Cart curCart;
	
	
	public User() {
		super();
	}

	public User(String userName, String firstName) {
		super();
		this.userName = userName;
		this.firstName = firstName;
		this.curCart = createNewCart();
	}
	
	// constructors
	public User(String userName, int userID, Cart cartID) {
		super();
		this.userName = userName;
		this.id = userID;
		this.curCart = cartID;
	}
	

	// getters and setters
	public int getId() 						{ return id; }
	public void setId(int id) 				{ this.id = id; }

	public Cart getCurCart() 				{ return curCart; }
	public void setCurCart(Cart curCart) 	{ this.curCart = curCart; }

	public String getUserName() 			{ return userName; }
	public void setUserName(String name) 	{ this.userName = name; }

	public String getFirstName() 			{ return firstName; }
	public void setFirstName(String name) 	{ this.firstName = name; }

	
	// creating new cart
	private Cart createNewCart() {
		try {
			Cart cart = new Cart(CartsDB.createNewCart());
			CartManager.getInstance().registerCart(cart);
			return cart;
			
		} catch (SQLException e) {
			System.out.println("cart can't be added to DB");
			return null;
		}		
	}

}
