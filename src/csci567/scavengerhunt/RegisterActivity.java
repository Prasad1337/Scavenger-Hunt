package csci567.scavengerhunt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import csci567.scavengerhunt.enums.ProjectConstants;
import csci567.scavengerhunt.model.User;
import csci567.scavengerhunt.services.UserService;

/**
 * @author ccubukcu
 * */
public class RegisterActivity extends Activity {
	static RegisterActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		activity = this;

		Button btnLogin = (Button) findViewById(R.id.btnLoginScreen);
		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(activity, LoginActivity.class);
				startActivity(i);
			}
		});

		Button btnRegister = (Button) findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				ProgressDialog pd = ProgressDialog.show(RegisterActivity.this,
						"",
						getApplicationContext().getString(R.string.loading),
						true, false);
				String username = ((EditText) findViewById(R.id.registerUsername))
						.getText().toString();
				String password = ((EditText) findViewById(R.id.registerPassword))
						.getText().toString();
				String email = ((EditText) findViewById(R.id.registerEmail))
						.getText().toString();

				TextView errorView = (TextView) findViewById(R.id.registerError);
				errorView.setText("");
				Boolean validated = true;

				if (!isUsernameValid(username)) {
					errorView.setVisibility(View.VISIBLE);
					validated = false;
					errorView.setText(R.string.registerUsernameError);
				} else if (!isEmailValid(email)) {
					errorView.setVisibility(View.VISIBLE);
					validated = false;
					errorView.setText(R.string.registerEmailError);
				} else if (!isPasswordValid(password)) {
					errorView.setVisibility(View.VISIBLE);
					validated = false;
					errorView.setText(R.string.registerPasswordError);
				}

				if (validated) {
					User user = new User();
					user.setEmail(email);
					user.setUsername(username);
					user.setPassword(password);

					User newUser = UserService.registerUser(user, activity);
					pd.dismiss();
					if (newUser != null) {
						Intent mapActivity = new Intent(activity,
								MapActivity.class);
						mapActivity.putExtra(ProjectConstants.USER_EXTRA_KEY,
								user);

						// Close all views before launching Dashboard
						mapActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(mapActivity);

						LoginActivity.finishActivity();
						finish();
					}
				}
			}
		});
	}

	public static void finishActivity() {
		if (activity != null)
			activity.finish();
	}

	boolean isEmailValid(CharSequence email) {
		return Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	boolean isUsernameValid(CharSequence username) {
		return username.length() > 4;
	}

	boolean isPasswordValid(CharSequence password) {
		return password.length() >= 8;
	}
}
