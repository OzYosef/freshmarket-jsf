package com.marketplaceProject.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import models.User;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;

import com.marketplaceProject.db.UsersDB;
import com.marketplaceProject.db.UsersDB.PasswordUtils;
import com.marketplaceProject.managers.CartManager;

@Named
@SessionScoped
public class LoginBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	private String confirmPassword; 
    private String firstName;

    
    // Getters & Setters
    public String getUsername() 							{ return username; }
    public void setUsername(String username) 				{ this.username = username; }

    public String getPassword() 							{ return password; }
    public void setPassword(String password) 				{ this.password = password; }
    
    public String getFirstName() 							{ return firstName; }
	public void setFirstName(String firstName) 				{ this.firstName = firstName; }
	
	public String getConfirmPassword() 						{ return confirmPassword; }
	public void setConfirmPassword(String confirmPassword) 	{ this.confirmPassword = confirmPassword; }
	
	// ====== User methods =======

	// getting the user
	public User getUser() {
		return (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user");
	}
	
	// check if the user logged in
    public boolean isLoggedIn() { 
    	return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user") != null;
    }

    // logging in function
    public String login() {

    	// validate the name field
    	if (username == null || username.length() < 1) {
    		addErrorMessage("login-form:username", "שגיאה בשם המשתמש", "שם משתמש לא יכול להיות ריק");
    	}
    	
    	// validate password field
    	if (password == null || password.length() < 1) {
    		addErrorMessage("login-form:password", "שגיאה בסיסמה", "סיסמה לא יכולה להיות ריקה");
    	}
    	
    	// error checks
        if (FacesContext.getCurrentInstance().getMessageList().size() > 0) {
        	return null;
        }
    	
        // checking if username exist
    	User user = UsersDB.getUserByUsername(username);
    	if(user == null) {
    		addErrorMessage(null, "פרטי התחברות שגויים, נסה שוב", "פרטי התחברות שגויים, נסה שוב");
            return null;
    	}
    	
    	// validate the password
    	if (PasswordUtils.checkPassword(password, UsersDB.getHashPassword(username))) {
    		
    		// setting the cart
    		user.getCurCart().getItemsFromDB();
    		
    		// adding the user to session
    		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", user);
    		
    		// success message and redirect
    		addSuccessMessage(null, "התחברות הצליחה!", "התחברות הצליחה!");
    		
    		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    		return "index?faces-redirect=true";
    		
    	} else {
    		// login error: sending general message
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "פרטי התחברות שגויים, נסה שוב", "פרטי התחברות שגויים, נסה שוב"));
             return null;
    	}
    }
    	
    // log out function
    public String logout() {
    	int cartId = getUser().getCurCart().getCartId();
    	
    	// updating the cart items in DB
    	getUser().getCurCart().updateItemsInDB();
    	
    	// clear the session data
    	CartManager.getInstance().removeCart(cartId);
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        return "index?faces-redirect=true";
    }
    
    
    // Register new user
    public String register() {
    	
    	// validate userName
    	if (username == null || username.length() < 1) {
    		addErrorMessage("register-form:username", "שגיאה בשם המשתמש", "שם משתמש לא יכול להיות ריק");
    	}
    	
    	if (username.length() > 50) {
    		addErrorMessage("register-form:username", "שגיאה בשם המשתמש", "שם משתמש ארוך מדי (מקסימום 50 תווים)");
    	}
    	
    	// validate name
        if (firstName == null || firstName.length() < 1) {
            addErrorMessage("register-form:name", "שגיאה בשם פרטי", "שם פרטי לא יכול להיות ריק");
        }
    	
    	// validate password
        if (password == null || password.isEmpty()) {
            addErrorMessage("register-form:password", "שגיאה בסיסמה", "סיסמה לא יכולה להיות ריקה");
        }
    	
        else if ( password.length() < 8 ) {
    		addErrorMessage("register-form:password", "שגיאה בסיסמה", "סיסמה חייבת להכיל לפחות 8 תווים");
    	}
    	
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            addErrorMessage("register-form:confirmPassword", "שגיאה באימות סיסמה", "אימות סיסמה לא יכול להיות ריק");
        }
        
        
        else if (!password.equals(confirmPassword)) {
            addErrorMessage("register-form:confirmPassword", "שגיאה באימות סיסמה", "הסיסמאות אינן תואמות");
        }
    	
        // having errors
        if (FacesContext.getCurrentInstance().getMessageList().size() > 0) {
        	return null;
        }
        
    	// check if user exist
    	User user = UsersDB.getUserByUsername(username);
    	if (user != null) {
    		addErrorMessage("register-form:username", "שם מתמש תפוס", "שם המשתמש תפוס, נסה שם אחר");
    		return null;
    	}
    	
    	try {
    		// adding user to DB
			UsersDB.addUser(new User(username, firstName), password);
			addSuccessMessage(null, "הרשמה בוצעה בהצלחה!", "הרשמה בוצעה בהצלחה!");
			FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
			return "login?faces-redirect=true";
			
		} catch (SQLException e) {
			// DataBase error
			addErrorMessage(null, "הרשמה לא הצליחה, נסה שוב.", "הרשמה לא הצליחה, נסה שוב.");
			return null;
		}
    }
    
    // ====== admin user methods ====
    
    // login as admin form
    public void adminLoggin() {
    	if("admin".equals(username) && "password".equals(password))
    		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("admin", "admin");
    }
    
    // checking admin access
    public boolean isAdmin() {
    	return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("admin") != null;
    }
    
    // redirect unauthorized users
    public void redirectIfNotLoggedIn() {
    	if (!isLoggedIn()) {
    		try {
    			FacesContext.getCurrentInstance().getExternalContext()
    			.redirect("login.xhtml");
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    // ===== easy access methods ====
    
    // easy access to error messages
    private void addErrorMessage(String elementStr, String globlMsg, String message) {
        FacesContext.getCurrentInstance().addMessage(elementStr, new FacesMessage(FacesMessage.SEVERITY_ERROR, globlMsg, message));
    }

    // easy access to success messages
    private void addSuccessMessage(String elementStr, String globlMsg, String message) {
        FacesContext.getCurrentInstance().addMessage(elementStr, new FacesMessage(FacesMessage.SEVERITY_INFO, globlMsg, message));
    }
    
}
