package csci567.scavengerhunt.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import csci567.scavengerhunt.LoginActivity;
import csci567.scavengerhunt.MapActivity;
import csci567.scavengerhunt.ProfileActivity;
import csci567.scavengerhunt.enums.CollectibleType;
import csci567.scavengerhunt.enums.Level;
import csci567.scavengerhunt.enums.ProjectConstants;
import csci567.scavengerhunt.model.Collectible;
import csci567.scavengerhunt.model.Progress;
import csci567.scavengerhunt.model.User;
import csci567.scavengerhunt.tools.DataHelpers;
import csci567.scavengerhunt.tools.DatabaseHandler;
import csci567.scavengerhunt.tools.JSONParser;

/**
 * @author ccubukcu
 * */
public class UserService {
	private static String login_tag = "login";
	private static String register_tag = "register";

	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ID = "id";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATION_TIME = "creation_time";
	
	// constructor
	public UserService() {
	}
	
	public static User loginUser(String username, String password, Context ctx) {
		// Building Parameters
		JSONObject json = null;
		User u = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		try {
			json = new JSONParser(JSONService.handlerURL, params).execute()
					.get();

			if (json.getString(KEY_SUCCESS) != null) {
				String res = json.getString(KEY_SUCCESS);
				if (Integer.parseInt(res) == 1) {
					DatabaseHandler db = new DatabaseHandler(ctx);
					JSONObject json_user = json.getJSONObject("user");

					clearUserTables(ctx);
					db.addUser(json_user.getString(KEY_NAME),
							json_user.getString(KEY_EMAIL),
							json_user.getString(KEY_ID),
							json_user.getString(KEY_CREATION_TIME));

					u = new User();
					u.setEmail(json_user.getString(KEY_EMAIL));
					u.setUsername(json_user.getString(KEY_NAME));
					u.setId(json_user.getInt(KEY_ID));
					u.setCreationTime(DataHelpers.parseDateFromString(json_user.getString(KEY_CREATION_TIME)));
					
					Progress p = retrieveUserProgress(u);
					u.setUserProgres(p);
				} else {
					LoginActivity.throwError(json.getString(KEY_ERROR));
				}
			} else {
				// TODO Data retrieval error
				LoginActivity.throwError("");
			}
		} catch (Exception e) {
			// TODO exception
			LoginActivity.throwError("");
			e.printStackTrace();
		}
		return u;
	}

	public static User registerUser(User user, Context ctx) {
		// Building Parameters
		JSONObject json = null;
		User u = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("username", user.getUsername()));
		params.add(new BasicNameValuePair("email", user.getEmail()));
		params.add(new BasicNameValuePair("password", user.getPassword()));
		
		try {
			json = new JSONParser(JSONService.handlerURL, params).execute()
					.get();

			if (json.getString(KEY_SUCCESS) != null) {
				String res = json.getString(KEY_SUCCESS);
				if (Integer.parseInt(res) == 1) {
					
					DatabaseHandler db = new DatabaseHandler(ctx);
					JSONObject json_user = json.getJSONObject("user");

					clearUserTables(ctx);
					db.addUser(json_user.getString(KEY_NAME),
							json_user.getString(KEY_EMAIL),
							json_user.getString(KEY_ID),
							json_user.getString(KEY_CREATION_TIME));

					u = new User();
					u.setEmail(json_user.getString(KEY_EMAIL));
					u.setUsername(json_user.getString(KEY_NAME));
					u.setId(json_user.getInt(KEY_ID));
					u.setCreationTime(DataHelpers.parseDateFromString(json_user.getString(KEY_CREATION_TIME)));
					
					Progress p = new Progress();
					p.setUsername(u.getUsername());
					p.setLevel(0);
					p.setTotalItemsCollected(0);
					p.setTotalItemsDropped(0);
					p.setTotalPoints(0);
					
					String query = QueryService.createInsertQuery(p);
					int id = JSONService.sendInsertQuery(query);
					if(id > 0) {
						p.setId(id);
					}
					u.setUserProgres(p);
				} else {
					LoginActivity.throwError(json.getString(KEY_ERROR));
				}
			} else {
				// TODO Data retrieval error
				LoginActivity.throwError("");
			}
		} catch (Exception e) {
			// TODO exception
			LoginActivity.throwError("");
			e.printStackTrace();
		}
		return u;
	}

	public static List<User> fetchAllUsers() {
		List<User> userList = new ArrayList<User>();

		List<Map<String, String>> keyValuePairs = JSONService.selectItems(
				User.TABLE_NAME, "");

		for (Map<String, String> map : keyValuePairs) {
			User u = new User();

			u.setId(Integer.parseInt(map.get(User.ID_KEY)));
			u.setUsername(map.get(User.USERNAME_KEY));
			u.setPassword(map.get(User.PASSWORD_KEY));
			u.setEmail(map.get(User.EMAIL_KEY));
			u.setCreationTime(DataHelpers.parseDateFromString(map.get(User.CREATION_TIME_KEY)));

			userList.add(u);
		}

		return userList;
	}

	public static User getUserByUsername(String username) {
		List<Map<String, String>> keyValuePairs = JSONService.selectItems(
				User.TABLE_NAME, User.USERNAME_KEY + " = '" + username + "'");

		User u = new User();
		
		for (Map<String, String> map : keyValuePairs) {

			u.setId(Integer.parseInt(map.get(User.ID_KEY)));
			u.setUsername(map.get(User.USERNAME_KEY));
			u.setPassword(map.get(User.PASSWORD_KEY));
			u.setEmail(map.get(User.EMAIL_KEY));
			u.setCreationTime(DataHelpers.parseDateFromString(map.get(User.CREATION_TIME_KEY)));
		}

		return u;
	}

	public static Boolean itemCollected(User u, Collectible c) {
		Progress p = u.getUserProgres();
		int itemValue = CollectibleType.getValueByIndex(c.getType());
		int curLevel = p.getLevel();
		int xpToNextLevel = Level.getTotalXpForLevel(curLevel + 1);

		if (itemValue > xpToNextLevel) {
			p.setLevel(curLevel + 1);
		}

		p.setTotalPoints(p.getTotalPoints() + itemValue);
		p.setTotalItemsCollected(p.getTotalItemsCollected() + 1);
		
		c.setCollectedBy(u.getUsername());
		String collectibleUpdateQuery = QueryService.createUpdateQuery(c);
		JSONService.sendUpdateQuery(collectibleUpdateQuery);
		
		String progressUpdateQuery = QueryService.createUpdateQuery(p);
		return JSONService.sendUpdateQuery(progressUpdateQuery) == -1 ? Boolean.FALSE : Boolean.TRUE;
	}
	
	public static Boolean itemDropped(User u, Collectible c) {
		Progress p = u.getUserProgres();
		int itemValue = CollectibleType.getValueByIndex(c.getType());
		int curLevel = p.getLevel();
		int xpToNextLevel = Level.getTotalXpForLevel(curLevel + 1);
		int droppedItemValue = (int) Math.round(itemValue * ProjectConstants.droppedItemValueMultipler);
		
		if (droppedItemValue > xpToNextLevel) {
			p.setLevel(curLevel + 1);
		}

		p.setTotalPoints(p.getTotalPoints() + droppedItemValue);
		p.setTotalItemsDropped(p.getTotalItemsDropped() + 1);
		
		String updateQuery = QueryService.createUpdateQuery(p);
		return JSONService.sendUpdateQuery(updateQuery) == -1 ? Boolean.FALSE : Boolean.TRUE;
	}

	public static boolean isUserLoggedIn(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getUserCount();
		if (count > 0) {
			// user logged in
			return true;
		}
		return false;
	}

	public static User getUserDetails(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		return db.getUserDetails();
	}
	
	public static Progress retrieveUserProgress(User u) {
		List<Map<String, String>> keyValuePairs = JSONService.selectItems(
				Progress.TABLE_NAME, " USERNAME = '" + u.getUsername() + "'");

		Progress p = new Progress();
		for (Map<String, String> map : keyValuePairs) {
			p.setId(Integer.parseInt(map.get(Progress.ID_KEY)));
			p.setUsername(map.get(Progress.USERNAME_KEY));
			p.setTotalItemsCollected(Integer.parseInt(map.get(Progress.TOTAL_COLLECTED_KEY)));
			p.setTotalItemsDropped(Integer.parseInt(map.get(Progress.TOTAL_DROPPED_KEY)));
			p.setTotalPoints(Integer.parseInt(map.get(Progress.TOTAL_POINTS_KEY)));
			p.setLevel(Integer.parseInt(map.get(Progress.LEVEL_KEY)));
		}

		return p;
	}
	
	public static List<Progress> getTopProgress(String orderColumn) {
		List<Map<String, String>> keyValuePairs = JSONService.selectItems(
				Progress.TABLE_NAME, " ID > 0 ORDER BY " + orderColumn + " DESC LIMIT 0 , 10 ");

		List<Progress> progressList = new ArrayList<Progress>();
		for (Map<String, String> map : keyValuePairs) {
			Progress p = new Progress();
			p.setId(Integer.parseInt(map.get(Progress.ID_KEY)));
			p.setUsername(map.get(Progress.USERNAME_KEY));
			p.setTotalItemsCollected(Integer.parseInt(map.get(Progress.TOTAL_COLLECTED_KEY)));
			p.setTotalItemsDropped(Integer.parseInt(map.get(Progress.TOTAL_DROPPED_KEY)));
			p.setTotalPoints(Integer.parseInt(map.get(Progress.TOTAL_POINTS_KEY)));
			p.setLevel(Integer.parseInt(map.get(Progress.LEVEL_KEY)));
			
			progressList.add(p);
		}

		return progressList;
	}
	
	public static boolean clearUserTables(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetUserTable();
		return true;
	}

	public static void logoutUser(Context ctx) {
		clearUserTables(ctx);
		MapActivity.finishActivity();
		ProfileActivity.finishActivity();
		Intent loginIntent = new Intent(ctx, LoginActivity.class);
		loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(loginIntent);
	}
}
