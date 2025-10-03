package com.marketplaceProject.beans;

import java.io.Serializable;
import java.util.List;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import models.Cart;
import models.CartItem;
import models.User;

@Named
@SessionScoped
public class CartBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private String paymentMethod = "cash";

	// getters and setters
	public String getPaymentMethod() 					{ return paymentMethod; }
	public void setPaymentMethod(String paymentMethod) 	{ this.paymentMethod = paymentMethod; }

	// getting the user cart
	private Cart getCart() {
		User user = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user");
		return user.getCurCart();
	}

	// increase quantity of an item
	public void increaseQuantity(int prodId) {
		Cart cart = getCart();
		cart.addItem(prodId, 1);
	}

	// overloading of increase quantity
	public void increaseQuantity(CartItem item) {
		item.increaseQuantity(1);
	}

	// decrease quantity of an item
	public void decreaseQuantity(int prodId) {
		Cart cart = getCart();
		cart.decriceQty(prodId, 1);
	}

	// overloading of decrease quantity
	public void decreaseQuantity(CartItem item) {
		item.decreaseQuantity(1);
	}

	// get quantity
	public int getQuantityForProduct(int prodId) {
		CartItem item = getCart().getItem(prodId);
		if (item != null) {
			return item.getQuantity();
		} else
			return 0;
	}

	// get all items of a cart
	public List<CartItem> getCartItems() {
		Cart cart = getCart();
		return cart.getItems();
	}

	// remove an item
	public void remove(CartItem item) {
		Cart cart = getCart();
		cart.removeItem(item.getProdId());
	}

	// get the total price
	public String getTotalPrice() {
		double total = getCart().getItems().stream()
				.mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice()).sum();
		return String.format("%.2f", total);
	}

	// demo checkout: success messaga and clean the cart
	public String checkout() {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "התשלום עבר בהצלחה!", "התשלום עבר בהצלחה!"));
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

		getCart().clearCart();
		return "index?faces-redirect=true";
	}

}
