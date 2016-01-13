package csci567.scavengerhunt;

import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import csci567.scavengerhunt.enums.Level;
import csci567.scavengerhunt.enums.ProjectConstants;
import csci567.scavengerhunt.model.Progress;
import csci567.scavengerhunt.model.User;
import csci567.scavengerhunt.services.UserService;

/**
 * @author ccubukcu
 * */
public class ProfileActivity extends ActionBarActivity implements
		OnItemSelectedListener {
	private User user;
	private static ProfileActivity activity;

	List<Progress> progressItems;
	int selectedLeaderboard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		activity = this;

		getActionBar().setDisplayHomeAsUpEnabled(true);

		user = (User) getIntent().getExtras().getSerializable(
				ProjectConstants.USER_EXTRA_KEY);

		this.setTitle(user.getUsername());
		
		selectedLeaderboard = 0;

		Spinner spinner = (Spinner) findViewById(R.id.leaderboardsSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.leaderboard_options,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(selectedLeaderboard);
		spinner.setOnItemSelectedListener(this);

		((TextView) findViewById(R.id.levelData)).setText(user.getUserProgres()
				.getLevel().toString());
		((TextView) findViewById(R.id.xpToNextData)).setText(Integer
				.toString(Level.getXpForNextLevel(user.getUserProgres()
						.getTotalPoints())));
		((TextView) findViewById(R.id.totalPointsData)).setText(user
				.getUserProgres().getTotalPoints().toString());
		((TextView) findViewById(R.id.totalCollData)).setText(user
				.getUserProgres().getTotalItemsCollected().toString());
		((TextView) findViewById(R.id.totalDropData)).setText(user
				.getUserProgres().getTotalItemsDropped().toString());

		createLeaderBoardsTable();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.logout:
			UserService.logoutUser(getApplicationContext());
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		selectedLeaderboard = position;
		createLeaderBoardsTable();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		progressItems = new ArrayList<Progress>();
	}

	public void loadProgressItems() {
		String orderColumn = Progress.TOTAL_POINTS_KEY;
		if (selectedLeaderboard == 1) {
			orderColumn = Progress.TOTAL_COLLECTED_KEY;
		} else if (selectedLeaderboard == 2) {
			orderColumn = Progress.TOTAL_DROPPED_KEY;
		}

		progressItems = UserService.getTopProgress(orderColumn);
	}

	public void createLeaderBoardsTable() {
		loadProgressItems();

		final float scale = getResources().getDisplayMetrics().density;
		int pad = (int) (1.0f * scale + 0.5f);
		TableLayout usernameTable = (TableLayout) findViewById(R.id.leaderboardsUsernameTable);
		TableLayout dataTable = (TableLayout) findViewById(R.id.leaderboardsDataTable);

		usernameTable.removeAllViews();
		dataTable.removeAllViews();

		TableRow usernameHeader = new TableRow(this);
		TableRow dataHeader = new TableRow(this);

		usernameHeader.setGravity(Gravity.CENTER);
		usernameHeader.setBackgroundColor(color.white);
		usernameHeader.setPadding(0, pad, 0, pad);
		dataHeader.setGravity(Gravity.CENTER);
		dataHeader.setBackgroundColor(color.white);
		dataHeader.setPadding(0, pad, 0, pad);

		TextView usernameLabel = new TextView(this);
		TextView dataLabel = new TextView(this);

		usernameLabel.setGravity(Gravity.CENTER);
		usernameLabel.setBackgroundColor(color.white);
		dataLabel.setGravity(Gravity.CENTER);
		dataLabel.setBackgroundColor(color.white);

		usernameLabel.setText(getApplicationContext().getString(
				R.string.username));
		String dataLabelText = getApplicationContext().getString(
				R.string.totalPoints);
		if (selectedLeaderboard == 1) {
			dataLabelText = getApplicationContext().getString(
					R.string.totalCollectedBoards);
		} else if (selectedLeaderboard == 2) {
			dataLabelText = getApplicationContext().getString(
					R.string.totalDroppedBoards);
		}
		dataLabel.setText(dataLabelText);

		usernameHeader.addView(usernameLabel);
		usernameTable.addView(usernameHeader);

		dataHeader.addView(dataLabel);
		dataTable.addView(dataHeader);

		if (progressItems != null && progressItems.size() > 0) {
			for (Progress p : progressItems) {
				TableRow username = new TableRow(this);
				TableRow data = new TableRow(this);

				username.setGravity(Gravity.CENTER);
				username.setBackgroundColor(color.white);
				username.setPadding(0, pad, 0, pad);
				data.setGravity(Gravity.CENTER);
				data.setBackgroundColor(color.white);
				data.setPadding(0, pad, 0, pad);

				TextView usernameLbl = new TextView(this);
				TextView dataLbl = new TextView(this);

				usernameLbl.setGravity(Gravity.CENTER);
				usernameLbl.setBackgroundColor(color.white);
				dataLbl.setGravity(Gravity.CENTER);
				dataLbl.setBackgroundColor(color.white);

				usernameLbl.setText(p.getUsername());
				dataLbl.setText(p.getTotalPoints().toString());

				if (selectedLeaderboard == 1) {
					dataLbl.setText(p.getTotalItemsCollected().toString());
				} else if (selectedLeaderboard == 2) {
					dataLbl.setText(p.getTotalItemsDropped().toString());
				}

				username.addView(usernameLbl);
				data.addView(dataLbl);

				usernameTable.addView(username);
				dataTable.addView(data);
			}
		}
	}

	public static void finishActivity() {
		if (activity != null) {
			activity.finish();
		}
	}
}
