package com.marketplaceProject.beans;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.marketplaceProject.db.CategoryDB;
import com.marketplaceProject.db.ProductsDB;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import models.Category;
import models.Product;

@Named
@RequestScoped
public class AdminBean {
	private String categoryName; 			// for add new category
	private String statusFilter = "all"; 	// for filtering the return lists

	// give the ability to use CategoryBean methods
	@Inject
	private CategoryBean categoryBean;

	// getters and setters
	public String getCategoryName() 					{ return categoryName; }
	public void setCategoryName(String categoryName) 	{ this.categoryName = categoryName; }

	public String getStatusFilter() 					{ return statusFilter; }
	public void setStatusFilter(String statusFilter) 	{ this.statusFilter = statusFilter; }

	// ======= Product Methods ==========

	// set product as deleted
	public void removeProduct(Product product) {
		try {
			ProductsDB.setDeleted(product.getBarcode(), true);
		} catch (SQLException e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "מחיקת המוצר נכשלה, נסה שנית", ""));
		}
	}

	// set product as not deleted
	public void restoreProduct(Product product) {
		try {
			ProductsDB.setDeleted(product.getBarcode(), false);
		} catch (SQLException e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "שחזור המוצר נכשל, אנא נסה שנית", ""));
		}
	}

	// update a single product data
	public String updateProduct(Product product) {

		// check if the category was updated and load the original category by ID
		if (product.getCategory() != null && product.getCategory().getName() == null) {
			Category fullCategory = categoryBean.getCategory(product.getCategory().getId());
			if (fullCategory != null) {
				product.setCategory(fullCategory);
			}
		}

		// check for errors
		if (FacesContext.getCurrentInstance().getMessageList().size() > 0) {
			return null;
		}

		try {
			// update DB with new data
			ProductsDB.updateProduct(product);

			// add a success message
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "המוצר " + product.getName() + " שונה בהצלחה!", ""));

		} catch (SQLException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"שגיאה בעריכת המוצר, נסה שנית.", "שגיאה בעריכת המוצר, נסה שנית."));
		}

		return null;
	}

	// ===== Categories Methods ==========

	// add new category
	public void addCategory() {

		// validate the name field
		if (categoryName != null && categoryName.length() > 0) {
			try {
				// adding the category to the ram & DB
				categoryBean.addCategory(categoryName);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "קטגוריה נוספה בהצלחה!", ""));
				categoryName = null;

			} catch (SQLException e) {
				// issue uploading to DB - duplicated name
				FacesContext.getCurrentInstance().addMessage("categoryForm:catName",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "שגיאה בהוספת קטגוריה", "שם הקטגוריה כבר תפוס"));
			}
		} else {
			// Handling empty name
			FacesContext.getCurrentInstance().addMessage("categoryForm:catName", new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "שגיאה בהוספת קטגוריה", "שם הקטגוריה לא יכול להיות ריק"));
		}

	}

	// update Category in DB and ram
	public void updateCategory(Category cat) {
		try {
			CategoryDB.updateCategory(cat);
			cat.setName(cat.getUptName());
			cat.setUptName(null);

		} catch (SQLException e) {
			cat.setUptName(null);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "שם קטגוריה לא תקין", "שם קטגוריה לא תקין"));
			return;
		}
	}

	// set category as deleted
	public void removeCategory(Category cat) {
		try {
			CategoryDB.setDeleted(cat.getId(), true);
			cat.setDeleted(true);
		} catch (SQLException e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "שגיאת בסיס נתונים, נסה שנית", ""));
		}
	}

	// set category as not deleted
	public void restoreCategory(Category cat) {
		try {
			CategoryDB.setDeleted(cat.getId(), false);
			cat.setDeleted(false);
		} catch (SQLException e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "שגיאת בסיס נתונים, נסה שנית", ""));
		}
	}

	// get all/active/inactive categories list
	public List<Category> getCategories() {
		List<Category> catList = new ArrayList<Category>();

		if (statusFilter.equals("all")) {
			catList = categoryBean.getCategories();
		} else if (statusFilter.equals("active")) {
			catList = categoryBean.getAllActives();
		} else {
			catList = categoryBean.getAllNonActives();
		}
		return catList;
	}

}