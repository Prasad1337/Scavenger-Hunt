package csci567.scavengerhunt.services;

import android.content.Context;
import csci567.scavengerhunt.model.Parameter;
import csci567.scavengerhunt.tools.DatabaseHandler;

/**
 * @author ccubukcu
 * */
public class ParameterService {

	public static boolean saveParameter(Parameter param, Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.setParameter(param);
		return true;
	}

	public static Parameter getParameter(String key, Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		return db.getParameter(key);
	}
}
