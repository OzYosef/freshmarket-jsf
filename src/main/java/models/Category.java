package models;

public class Category {
	private int id;
	private String name;
	private boolean deleted;
	private String uptName; // use for update the name 
	
	// constructors
	public Category () {};
	
	public Category(int id, String name, boolean deleted) {
		this.id = id;
		this.name = name;
		this.deleted = deleted;
	}
	
	// getters and setters
	public int getId() 						{ return id; }
	public void setId(int id) 				{ this.id = id; }

	public boolean isDeleted() 				{ return deleted; }
	public void setDeleted(boolean deleted) { this.deleted = deleted; }

	public String getUptName() 				{ return uptName != null ? uptName : name; }
	public void setUptName(String uptName) 	{ this.uptName = uptName; }
	
	public String getName() 				{ return name; }
	public void setName(String name) 		{
		if (name != null && name.length() > 0)
			this.name = name;
	}
	
}
