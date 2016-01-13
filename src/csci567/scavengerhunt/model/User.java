package csci567.scavengerhunt.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ccubukcu
 * */
public class User extends PersistentEntity implements Serializable{
	private static final long serialVersionUID = -7411815485987653075L;
	
	private String username;
	private String email;
	private String password;
	private Date creationTime;
	
	private Progress userProgres;
	
	public static final String USERNAME_KEY = "USERNAME";
	public static final String PASSWORD_KEY = "PASSWORD";
	public static final String EMAIL_KEY = "EMAIL";
	public static final String CREATION_TIME_KEY = "CREATION_TIME";
	
	public static final String TABLE_NAME = "USERS";
	
	public User() {
		tableName = TABLE_NAME;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
		putValueToMap(USERNAME_KEY, username);
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
		putValueToMap(EMAIL_KEY, email);
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
		putValueToMap(CREATION_TIME_KEY, creationTime);
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
		putValueToMap(PASSWORD_KEY, password);
	}

	public Progress getUserProgres() {
		return userProgres;
	}

	public void setUserProgres(Progress userProgres) {
		this.userProgres = userProgres;
	}
	
}
