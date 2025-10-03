package com.marketplaceProject.managers;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import models.Cart;
import models.User;

@WebListener
public class SessionCleanupListener implements HttpSessionListener {

	// Handling unpredictable session ends
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Object userObj = se.getSession().getAttribute("user");
        if (userObj instanceof User user) {
            Cart cart = user.getCurCart();
            if (cart != null) {
                CartManager.getInstance().removeCart(cart.getCartId());
                System.out.println("Session ended. Cart " + cart.getCartId() + " removed.");
            }
        }
    }
}
