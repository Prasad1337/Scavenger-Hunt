package csci567.scavengerhunt.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;
import csci567.scavengerhunt.LoginActivity;
import csci567.scavengerhunt.tools.JSONParser;

/**
 * @author ccubukcu
 * */
public class JSONService {

	public static String handlerURL = "http://www.favornova.com/db_connector/";

	private static String INSERT_TAG = "insert";
	private static String UPDATE_TAG = "update";
	private static String SELECT_TAG = "select";

	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ID = "id";
	private static String ERROR_MESSAGE = "error_message";
	private static String SQL_MESSAGE = "mysql_error";
	private static String KEY_OBJECT = "objects";

	public static Integer sendInsertQuery(String query) {
		return sendExecutableQuery(query, 1);
	}
	
	public static Integer sendUpdateQuery(String query) {
		return sendExecutableQuery(query, 0);
	}
	
	private static Integer sendExecutableQuery(String query, int type) {
		JSONObject json = null;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", type == 1 ? INSERT_TAG : UPDATE_TAG));
		params.add(new BasicNameValuePair("query", query));

		try {
			json = new JSONParser(JSONService.handlerURL, params).execute()
					.get();

			if (json.getString(KEY_SUCCESS) != null) {
				String res = json.getString(KEY_SUCCESS);
				if (Integer.parseInt(res) == 1) {
					return json.getInt(KEY_ID);
				} else {
					Log.e("JSONService Query Execute",
							json.getString(KEY_ERROR));
					return -1;
				}
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static List<Map<String, String>> selectItems(String tableName,
			String whereClause) {
		List<Map<String, String>> items = new ArrayList<Map<String, String>>();

		JSONObject json = null;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", SELECT_TAG));
		params.add(new BasicNameValuePair("tableName", tableName));
		params.add(new BasicNameValuePair("whereClause", whereClause));

		try {
			json = new JSONParser(JSONService.handlerURL, params).execute()
					.get();

			if (json.getString(KEY_SUCCESS) != null) {
				String res = json.getString(KEY_SUCCESS);

				if (Integer.parseInt(res) == 1) {
					JSONArray objects = json.getJSONArray(KEY_OBJECT);

					if (objects != null) {
						for (int i = 0; i < objects.length(); i++) {
							JSONObject element = objects.getJSONObject(i);
							Iterator<String> keys = element.keys();
							Map<String, String> keyValuePairs = new HashMap<String, String>();

							while (keys.hasNext()) {
								String key = keys.next();
								keyValuePairs.put(key, element.getString(key));
							}

							items.add(keyValuePairs);
						}
					} else {
						LoginActivity.throwError(json.getString(KEY_ERROR));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}
}
