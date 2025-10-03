package models;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

public class Product {
    private int barcode;
    private String name;
    private String description;
    private String image;
    private Category category;
    private double price;
    private boolean deleted;
    private int catID;	// for updating the category
    
    
    // constructors
    public Product() {}
    
    // getters & setters
	public boolean isDeleted() 						{ return deleted; }
	public void setDeleted(boolean deleted) 		{ this.deleted = deleted; }
	
	public int getBarcode() 						{ return barcode; }
	public void setBarcode(int barcode) 			{ this.barcode = barcode; }
	
	public String getName() 						{ return name; }
	public void setName(String name) 				{ this.name = name; }
	
	public String getDescription() 					{ return description; }
	public void setDescription(String description) 	{ this.description = description; }
	
	public String getImage() 						{ return image; }
	public void setImage(String image) 				{ this.image = image; }
	
	public Category getCategory() 					{ return category; }
	public void setCategory(Category category) 		{
		this.category = category;
		this.catID = category.getId();
	}
	
	
	public int getCatID() 							{ return catID; }
	public void setCatID(int uptCat) 				{
		this.category = new Category();
		this.category.setId(uptCat);
		this.catID = uptCat;
	}
	
	public double getPrice() 						{ return price; }
	public void setPrice(double price) {
		if (price > 0 && price < 1000000)
			this.price = price;
		else {
			FacesContext.getCurrentInstance().addMessage(null,
    	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "מחיר חייב להיות חיובי וקטן ממליון", ""));
		}
	}
	
	// overload to catch not a double issues
	public void setPrice(String price) {
		System.out.println("test");
		try {
		    setPrice(Double.parseDouble(price)); 
		} catch (NumberFormatException e) {
    	    FacesContext.getCurrentInstance().addMessage(null,
    	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "מחיר חייב להיות מספר!", "מחיר חייב להיות מספר!"));
		}
	}
	
	// toString function
	@Override
	public String toString() {
		return "Product [barcode=" + barcode + ", name=" + name + ", description=" + description + ", image=" + image
				+ ", category=" + category.getName() + ", price=" + price + "]";
	}
    
	
    
}

