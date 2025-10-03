package com.marketplaceProject.db;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.marketplaceProject.managers.CartUpdaterThread;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {
	private CartUpdaterThread updaterThread;

	// run when starting the app
	@Override
	public void contextInitialized(ServletContextEvent sce) {

		// initiate DBs
		System.out.println("Initializing database...");
		try {
			DataBaseInit.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Create an Carts updater thread for the DB
		if (updaterThread == null || !updaterThread.isAlive()) {
			System.out.println("Starting CartUpdaterThread...");
			updaterThread = new CartUpdaterThread();
			updaterThread.start();
		} else {
			System.out.println("CartUpdaterThread already running.");
		}

	}

	// destroyer - running when the server shut down
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("Shutting down cleanup thread...");

		// shutting down the Cart Updater
		if (updaterThread != null) {
			updaterThread.stopUpdater();
			updaterThread.interrupt();
		}

		// closing the JDBC driver to avoid memory leak
		try {
			Enumeration<Driver> drivers = DriverManager.getDrivers();
			while (drivers.hasMoreElements()) {
				Driver driver = drivers.nextElement();
				DriverManager.deregisterDriver(driver);
				System.out.println("Deregistered JDBC driver: " + driver);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// closing open connections to DB
		AbandonedConnectionCleanupThread.checkedShutdown();
		System.out.println("Cleanup thread shut down successfully.");
	}

}
