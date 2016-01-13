package csci567.scavengerhunt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import csci567.scavengerhunt.enums.ProjectConstants;
import csci567.scavengerhunt.model.Progress;
import csci567.scavengerhunt.model.User;
import csci567.scavengerhunt.services.UserService;

/**
 * @author ccubukcu
 * */
public class LoginActivity extends Activity {
	static LoginActivity activity;
	static Context appContext;

	// Static to allow access from other activities
	public static User user = null;
	public static Boolean doubleBackToExitPressedOnce = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = getApplicationContext();
		setContentView(R.layout.activity_login);
		setTitle(R.string.title_activity_login);
		activity = this;
		
		if(UserService.isUserLoggedIn(appContext)) {
			ProgressDialog pd = ProgressDialog.show(LoginActivity.this, "", 
					getApplicationContext().getString(R.string.logging), true,
		                false);
			
			String username = UserService.getUserDetails(appContext).getUsername();
			user = UserService.getUserByUsername(username);
			
			if (user != null) {
				Progress p = UserService.retrieveUserProgress(user);
				user.setUserProgres(p);
				pd.dismiss();
				Intent mapActivity = new Intent(appContext, MapActivity.class);
				mapActivity.putExtra(ProjectConstants.USER_EXTRA_KEY, user);
				
				mapActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mapActivity);
				
				RegisterActivity.finishActivity();
				finish();
			} else {
				UserService.logoutUser(appContext);
			}
		}
		
		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		
		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				ProgressDialog pd = ProgressDialog.show(LoginActivity.this, "", 
						getApplicationContext().getString(R.string.logging), true,
			                false);
				String username = ((EditText) findViewById(R.id.loginUsername))
						.getText().toString();
				String password = ((EditText) findViewById(R.id.loginPassword))
						.getText().toString();
				user = UserService.loginUser(username, password, appContext);
				
				if (user != null) {
					pd.dismiss();
					Intent mapActivity = new Intent(appContext, MapActivity.class);
					mapActivity.putExtra(ProjectConstants.USER_EXTRA_KEY, user);
					
					mapActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mapActivity);
					
					RegisterActivity.finishActivity();
					finish();
				} else {
					// TODO error during login error
					throwError("");
				}
			}
		});

		Button btnRegister = (Button) findViewById(R.id.btnRegisterScreen);
		btnRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(appContext, RegisterActivity.class);
				startActivity(i);
			}
		});
	}

	public static void throwError(String errorMessage) {
		// TODO set error message and make that part visible
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			moveTaskToBack(true);
			return;
		}

		doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit",
				Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

	public static void finishActivity() {
		if (activity != null)
			activity.finish();
	}
}
