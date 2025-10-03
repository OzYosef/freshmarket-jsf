package com.marketplaceProject.managers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import models.Cart;

// mapping all carts for DB updates
public class CartManager {
    private static CartManager instance = new CartManager();
    private Map<Integer, Cart> carts;

    // constructor
    private CartManager() {
        carts = new ConcurrentHashMap<>();
    }

    // getters
    public static CartManager getInstance() 		{ return instance; }
    public Map<Integer, Cart> getAllCarts() 		{ return carts; }
    public Cart getCart(int cartId) 				{ return carts.get(cartId); }

    // register new cart to management
    public void registerCart(Cart cart) {
        carts.put(cart.getCartId(), cart);
    }

    // remove cart from management
    public void removeCart(int cartId) {
        carts.remove(cartId);
    }

}