package com.marketplaceProject.managers;

// a server Thread that update carts items
public class CartUpdaterThread extends Thread {

    private boolean running = true;

    @Override
    public void run() {
        while (running) {
            try {
            	// sleep for 5 seconds
                Thread.sleep(5000);
                
                // iterate all carts
                CartManager.getInstance().getAllCarts().values().forEach(cart -> {
                    if (cart.isUpdated()) {
                        cart.updateItemsInDB();
                    }
                });
            } catch (InterruptedException e) {
            	running = false;
            	System.out.println("CartUpdater interrupted");
                break;
            }
        }
    }

    // stopping the thread
    public void stopUpdater() { running = false; }
    
}
