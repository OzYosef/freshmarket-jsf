package com.marketplaceProject.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.marketplaceProject.db.ProductsDB;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import models.Product;
import java.io.Serializable;

@Named
@ViewScoped
public class ProductBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<Product> products;
	private String statusFilter = "all";
	private int selectedCategoryId;
	
    @Inject
    private CategoryBean categoryBean;
	
	// load all product on page load
    @PostConstruct
    public void init() {
    	loadProducts();
    }
    
    // getters and setters
    public String getStatusFilter() 							{ return statusFilter; }
    public void setStatusFilter(String statusFilter) 			{ this.statusFilter = statusFilter; }
    
    public int getSelectedCategoryId() 							{ return selectedCategoryId; }
	public void setSelectedCategoryId(int selectedCategoryId) 	{ this.selectedCategoryId = selectedCategoryId; }

	// ===== Methods =====
	
	// get all product
    public List<Product> getProducts() {
    	
    	// getting all the products
        if (statusFilter == null || statusFilter.equals("all"))
            return products;
        
        // getting only the active products 
        else if (statusFilter.equals("active")) {
        	List<Product> filteredProducts = new ArrayList<Product>();
        	
        	for (Product prod: products) 
        		if (prod.isDeleted() == false) { filteredProducts.add(prod); }
            
        	return filteredProducts;
        } 
        
        // getting all inactive products
        else {
        	List<Product> filteredProducts = new ArrayList<Product>();
        	for (Product prod: products)
        		if (prod.isDeleted() == true) { filteredProducts.add(prod); }
        	
            return filteredProducts;
        }
    }
    
    // loading the products from DB
    public void loadProducts() {
        try {
			products = ProductsDB.getAllProducts(categoryBean);
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "שגיאה בטיענת המוצרים, נסה שנית", ""));
		}
    }
    
    // get products filtered by category
    public List<Product> getFilteredProducts(){
    	if( selectedCategoryId == 0 ) { return products; }
    	return products.stream().filter(p -> p.getCategory().getId() == selectedCategoryId).collect(Collectors.toList());
    }
    
    

    
}
