package com.marketplaceProject.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import java.io.*;
import java.util.Map;
import java.util.UUID;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.marketplaceProject.db.ProductsDB;

@Named("addProduct")
@RequestScoped
public class AddProduct implements Serializable {
	private static final long serialVersionUID = 1L;

	private String productName;
	private String description;
	private Double price;
	private String barcode;
	private int category;
	private Part image;
	private String imagePath;

	// Default constructor
	public AddProduct() {
	}

	// ===== Getters and Setters ======
	public String getProductName() 						{ return productName; }
	public void setProductName(String productName) 		{ this.productName = productName; }

	public String getDescription() 						{ return description; }
	public void setDescription(String description) 		{ this.description = description; }

	public Double getPrice() 							{ return price; }
	public void setPrice(Double price) 					{ this.price = price; }

	public String getBarcode() 							{ return barcode; }
	public void setBarcode(String barcode) 				{ this.barcode = barcode; }

	public int getCategory() 							{ return category; }
	public void setCategory(int category) 				{ this.category = category; }

	public String getImagePath() 						{ return imagePath; }
	public void setImagePath(String imagePath) 			{ this.imagePath = imagePath; }

	public Part getImage() 								{ return image; }
	
	public void setImage(Part image) {
		if (image != null) {
			this.image = image;
		}
		if (image != null) {
			String fileName = getFileName(image);
			try {
				saveImageCloud(image, fileName);
			} catch (IOException e) {
				FacesContext.getCurrentInstance().addMessage("productName",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "שגיאה", "שגיאה בהעלאת התמונה, נסה שוב"));
				e.printStackTrace();
			}
		}
	}


	// ===== Methods =====
	
	// Create product form function
	public String createProduct() {
		try {

			// checking name length
			if (productName.length() < 1 || productName.length() > 255) {
				FacesContext.getCurrentInstance().addMessage("productForm:productName", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "שם המוצר לא תקין", "השם חייב להיות בין 1 ל-255 תווים"));
			}

			// Handling empty Description
			if (description == null) {
				setDescription("");
			}

			// long description
			else if (description.length() > 255) {
				FacesContext.getCurrentInstance().addMessage("productForm:description", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "תיאור ארוך מדי", "תיאור מוצר יכול להכיל עד 255 תווים"));
			}

			// empty category
			if (category == 0) {
				FacesContext.getCurrentInstance().addMessage("productForm:category-select",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "אין קטגוריות זמינות, אנא צור קטגוריה תחילה",
								"אנא בחר קטגוריה עבור המוצר"));
			}

			// invalid number
			if (price == null || price <= 0) {
				FacesContext.getCurrentInstance().addMessage("productForm:price",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "מחיר לא חוקי", "המחיר חייב להיות מספר חיובי"));
			}

			// big price
			else if (price > 999999) {
				FacesContext.getCurrentInstance().addMessage("productForm:price",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "מחיר לא חוקי", "מחיר מקסימלי הוא: 999,999"));
			}

			// returning all errors
			if (FacesContext.getCurrentInstance().getMessageList().size() > 0) {
				return null;
			}

			// handling empty image
			if (image == null) {
				setImagePath("https://placehold.co/600x400");
			}

			// adding to DB
			try {
				ProductsDB.addProduct(productName, description, imagePath, category, price);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"מוצר נוצר בהצלחה!", "מוצר נוצר בהצלחה ונוסף לבסיס הנתונים"));
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"שגיאה בהעלאת המוצר", "מוצר לא נוצר, נסה שוב במועד מאוחר יותר"));
				return null;
			}

			// Returning page
			FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
			return "admin?faces-redirect=true";

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "שגיאה", "שגיאת שרת, נסה שוב במועד מאוחר יותר"));
			return null;
		}
	}


	// get the file name from the uploaded file
	private String getFileName(Part part) {
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}


	// upload the image to cloud 
	private void saveImageCloud(Part filePart, String fileName) throws IOException {
		final long MAX_FILE_SIZE = 5 * 1024 * 1024;

		// Handling file too big issue
		String contentType = filePart.getContentType();
		if (filePart.getSize() > MAX_FILE_SIZE) {
			FacesContext.getCurrentInstance().addMessage("image",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "תמונה גדולה מדי", "גודל התמונה המקסימלי הוא MB5"));
			throw new IOException("File too large.");
		}

		// Handling uploading files that are not images
		if (contentType == null || !contentType.startsWith("image/")) {
			FacesContext.getCurrentInstance().addMessage("image",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "פורמט לא נתמך", "רק קבצי תמונה מותרים"));
			throw new IOException("Only image files are allowed.");
		}

		// converting the stream to filebyte
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try (InputStream inputStream = filePart.getInputStream()) {
			byte[] temp = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(temp)) != -1) {
				buffer.write(temp, 0, bytesRead);
			}
		}
		byte[] fileBytes = buffer.toByteArray();

		// Connecting to cloud user
		Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "doiifevu4", "api_key",
				"292716929339587", "api_secret", "dvQPkYwr0TonaKqD15Dz8evPknI"));

		// uploading the image file to cloud
		@SuppressWarnings("unchecked")
		Map<String, Object> uploadResult = cloudinary.uploader().upload(fileBytes,
				ObjectUtils.asMap("public_id", UUID.randomUUID().toString() + "_" + fileName, // create unique file name
						"folder", "productImages"));

		// setting the image path of the image
		setImagePath((String) uploadResult.get("secure_url"));
	}

}