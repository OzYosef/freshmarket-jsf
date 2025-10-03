package com.marketplaceProject.beans;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.marketplaceProject.db.CategoryDB;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import models.Category;

@Named
@ApplicationScoped
public class CategoryBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<Category> categories;

	// Constructor
	public CategoryBean() {
		try {
			this.categories = CategoryDB.getAllCategories();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// get all categories
	public List<Category> getCategories() { return categories; }

	// get a single category by ID
	public Category getCategory(int id) {
		for (Category cat : categories) {
			if (cat.getId() == id) {
				return cat;
			}
		}

		return null;
	}

	// get active categories
	public List<Category> getAllActives() {
		List<Category> newCats = new ArrayList<Category>();

		for (Category cat : categories) {
			if (!cat.isDeleted()) {
				newCats.add(cat);
			}
		}
		return newCats;
	}

	// get inactive categories
	public List<Category> getAllNonActives() {
		List<Category> newCats = new ArrayList<Category>();

		for (Category cat : categories) {
			if (cat.isDeleted()) {
				newCats.add(cat);
			}
		}
		return newCats;
	}

	// add new category
	public void addCategory(String name) throws SQLException {
		int id = CategoryDB.addCategory(name);
		Category cat = new Category(id, name, false);
		categories.add(cat);
	}

}
