package csci567.scavengerhunt.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import csci567.scavengerhunt.model.Parameter;
import csci567.scavengerhunt.model.User;
import csci567.scavengerhunt.services.QueryService;

/**
 * @author ccubukcu
 * */
public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "android_api";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		createUserTable(db);
		createParameterTable(db);
	}
	
	public void createUserTable(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE IF NOT EXISTS " + User.TABLE_NAME + "("
				+ User.ID_KEY + " INTEGER AUTO_INCREMENT PRIMARY KEY," 
				+ User.USERNAME_KEY + " TEXT,"
				+ User.EMAIL_KEY + " TEXT,"
				+ User.CREATION_TIME_KEY + " TEXT" + ")";
		db.execSQL(CREATE_LOGIN_TABLE);
	}
	
	public void createParameterTable(SQLiteDatabase db) {
		String CREATE_PARAM_TABLE = "CREATE TABLE IF NOT EXISTS " + Parameter.TABLE_NAME + "("
				+ Parameter.ID_KEY + " INTEGER AUTO_INCREMENT PRIMARY KEY ," 
				+ Parameter.KEY_KEY + " TEXT,"
				+ Parameter.VALUE_KEY + " TEXT" + ")";
		db.execSQL(CREATE_PARAM_TABLE);
	}
	
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Parameter.TABLE_NAME);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String name, String email, String id, String created_at) {
		SQLiteDatabase db = this.getWritableDatabase();
		createUserTable(db);

		ContentValues values = new ContentValues();
		values.put(User.USERNAME_KEY, name); // Name
		values.put(User.EMAIL_KEY, email); // Email
		values.put(User.ID_KEY, id); // Email
		values.put(User.CREATION_TIME_KEY, created_at); // Created At

		// Inserting Row
		db.insert(User.TABLE_NAME, null, values);
		db.close(); // Closing database connection
	}
	
	/**
	 * Getting user data from database
	 * */
	public User getUserDetails(){
		User user = new User();
		String selectQuery = "SELECT  * FROM " + User.TABLE_NAME;
		 
		SQLiteDatabase db = this.getWritableDatabase();
		createUserTable(db);
		
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
        	user.setUsername(cursor.getString(cursor.getColumnIndex(User.USERNAME_KEY)));
        	user.setEmail(cursor.getString(cursor.getColumnIndex(User.EMAIL_KEY)));
        	user.setId(cursor.getInt(cursor.getColumnIndex(User.ID_KEY)));
        	user.setCreationTime(DataHelpers.parseDateFromString(cursor.getString(cursor.getColumnIndex(User.CREATION_TIME_KEY))));
        }
        cursor.close();
        db.close();
		// return user
		return user;
	}
	
	public void setParameter(Parameter parameter) {
		String query = "";
		if (checkIfParamExists(parameter.getKey())) {
			query = QueryService.createUpdateQuery(parameter);
		} else {
			query = QueryService.createInsertQuery(parameter);
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		createParameterTable(db);
        if(query != null) db.execSQL(query);
	}
	
	public Parameter getParameter(String key) {
		Parameter param = new Parameter();
		String selectQuery = "SELECT  * FROM " + Parameter.TABLE_NAME + " WHERE " + Parameter.KEY_KEY + " = '" + key + "'";
		 
        SQLiteDatabase db = this.getWritableDatabase();
		createParameterTable(db);
		
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
        	param.setKey(cursor.getString(cursor.getColumnIndex(Parameter.KEY_KEY)));
        	param.setValue(cursor.getString(cursor.getColumnIndex(Parameter.VALUE_KEY)));
        	param.setId(cursor.getInt(cursor.getColumnIndex(Parameter.ID_KEY)));
        }
        cursor.close();
        db.close();
		// return user
		return param;
	}
	
	public boolean checkIfParamExists(String key) {
		String countQuery = "SELECT  * FROM " + Parameter.TABLE_NAME + " WHERE " + Parameter.KEY_KEY + " = '" + key + "'";
		SQLiteDatabase db = this.getWritableDatabase();
		createParameterTable(db);
		
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		
		db.close();
		cursor.close();
		return rowCount > 0;
	}
	
	/**
	 * Getting user login status
	 * return true if rows are there in table
	 * */
	public int getUserCount() {
		String countQuery = "SELECT  * FROM " + User.TABLE_NAME;
		SQLiteDatabase db = this.getWritableDatabase();
		createUserTable(db);
		
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();
		
		// return row count
		return rowCount;
	}
	
	/**
	 * Re crate database
	 * Delete all tables and create them again
	 * */
	public void resetUserTable(){
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(User.TABLE_NAME, null, null);
		db.close();
	}
	
	
}
