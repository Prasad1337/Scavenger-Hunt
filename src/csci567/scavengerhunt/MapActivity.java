package csci567.scavengerhunt;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import csci567.scavengerhunt.enums.CollectibleType;
import csci567.scavengerhunt.enums.ProjectConstants;
import csci567.scavengerhunt.model.Collectible;
import csci567.scavengerhunt.model.Progress;
import csci567.scavengerhunt.model.User;
import csci567.scavengerhunt.services.CollectibleService;
import csci567.scavengerhunt.services.UserService;

public class MapActivity extends ActionBarActivity implements LocationListener {

	static User user;
	static MapActivity activity;
	List<Marker> markerList;

	private GoogleMap map;
	Location location;
	LatLng myLocation;
	LatLng temp=null;
	public static Boolean doubleBackToExitPressedOnce = false;

	private void initMap() {
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		if (status != ConnectionResult.SUCCESS) {
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else {
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);

			map = fm.getMap();
			map.setMyLocationEnabled(true);

			Criteria criteria = new Criteria();
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			String provider = locationManager.getBestProvider(criteria, true);
			Location location = locationManager.getLastKnownLocation(provider);

			if (location != null) {
				onLocationChanged(location);
			}
			locationManager.requestLocationUpdates(provider, 20000, 0, this);
		}
	}

	private LatLng centerMapOnMyLocation() {
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();

		Location location = locationManager
				.getLastKnownLocation(locationManager.getBestProvider(criteria,
						false));

		final LatLng latlng1 = new LatLng(location.getLatitude(),
				location.getLongitude());

		if (location != null) {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng1, 15));
		}

		return latlng1;
	}
	
	public void onLocationChanged(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		LatLng latLng = new LatLng(latitude, longitude);

		map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		map.animateCamera(CameraUpdateFactory.zoomTo(15));
	}
	
	boolean isInProx(LatLng ll1,LatLng ll2) {
		double x=ll1.latitude-ll2.latitude;
		double y=ll1.longitude-ll2.longitude;
		
		if(x<0)
			x*=-1;
		
		if(y<0)
			y*=-1;
		
		if(x<0.0004 && y<0.0004) {
			return true;
		}
		
		return false;
	}
	
	public void dropCollect(boolean isReturning) {
		LatLng latlng1=centerMapOnMyLocation();
		
		boolean itemCollected = false;
		for (Marker m : markerList) {
			String snippet = m.getSnippet();
			
			String[] splitted = snippet.split(";");
			int id = Integer.parseInt(splitted[0]);
			double lat = Double.parseDouble(splitted[1]);
			double lng = Double.parseDouble(splitted[2]);
			LatLng latlng = new LatLng(lat, lng);
			
			if(isInProx(latlng1, latlng)) {
				Collectible cl = new Collectible();
				cl.setId(id);
				
				cl.setCollectedBy(user.getUsername());
				cl.setCollected(true);
				
				UserService.itemCollected(user, cl);
				itemCollected = true;
			}
		}
		
		if(itemCollected) {
			Toast.makeText(this, "Closeby items collectd. Click again to drop items.",
					Toast.LENGTH_SHORT).show();
		}
		
		createMarkersFromDatabase(map.getProjection().getVisibleRegion().latLngBounds);
		
		if(!itemCollected) {
			if(temp==null || !isInProx(temp,latlng1)) {
				if(isReturning) {
					Collectible cl = new Collectible();
					cl.setCollected(Boolean.FALSE);
					cl.setCollectedBy(null);
					cl.setLatitude(latlng1.latitude);
					cl.setLongitude(latlng1.longitude);
					
					CollectibleType t = ItemDropActivity.getSelectedType();
					
					map.addMarker(new MarkerOptions()
						.position(latlng1)
						.title(t.getValue()+" points")
						.icon(BitmapDescriptorFactory.defaultMarker(t.getColor())));
		
					cl.setType(t.getIndex());
					cl.setDroppedBy(user.getUsername());
					cl.setDropped(true);
		
					UserService.itemDropped(user, cl);
					CollectibleService.storeCollectible(cl);
				
					temp=latlng1;
					
					createMarkersFromDatabase(map.getProjection().getVisibleRegion().latLngBounds);

					Toast.makeText(this, "Item successfully dropped.",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent dropIntent = new Intent(MapActivity.this,
							ItemDropActivity.class);
					dropIntent.putExtra(ProjectConstants.USER_EXTRA_KEY, user);
	
					startActivity(dropIntent);
				}
			}
			else if(isInProx(temp,latlng1)) {
				Toast.makeText(this, "Cannot spam packet drops! Please relocate to new position.",
						Toast.LENGTH_SHORT).show();
			} 
		}
	}
	
	public void createMarkersFromDatabase(LatLngBounds bounds) {
		map.clear();
		markerList = new ArrayList<Marker>();

		List<Collectible> markers = CollectibleService.getNearbyCollectibles(user.getUsername(), bounds);
		
		for (Collectible marker : markers) {
			LatLng latlng = new LatLng(marker.getLatitude(), marker.getLongitude());
			CollectibleType t = CollectibleType.getTypeByIndex(marker.getType());
			String snippet = marker.getId() + ";" + marker.getLatitude() + ";" + marker.getLongitude();
			
			Marker m = map.addMarker(new MarkerOptions()
				.position(latlng)
				.title(t.getValue()+" points")
				.icon(BitmapDescriptorFactory.defaultMarker(t.getColor()))
				.snippet(snippet));
			
			markerList.add(m);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		user = (User) getIntent().getExtras().getSerializable(
				ProjectConstants.USER_EXTRA_KEY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		activity = this;
		markerList = null;

		ImageButton btn = (ImageButton) findViewById(R.id.redBtn);

		initMap();
		createMarkersFromDatabase(map.getProjection().getVisibleRegion().latLngBounds);

		TextView tvLocation = (TextView) findViewById(R.id.tv_location);
		tvLocation.setText(getString(R.string.info));
		tvLocation.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		tvLocation.setTypeface(null, Typeface.BOLD);
		
		centerMapOnMyLocation();

		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dropCollect(false);
			}
		});

		if (user.getUsername() == null || user.getId() == null || user.getUserProgres() == null) {
			user = UserService.getUserByUsername(UserService.getUserDetails(
					this).getUsername());
			Progress p = UserService.retrieveUserProgress(user);
			user.setUserProgres(p);
		}
	}

	@Override
	protected void onResume() {
		if (ItemDropActivity.isItemSelected()) {
			dropCollect(true);
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.profile:
			Intent profileIntent = new Intent(MapActivity.this,
					ProfileActivity.class);
			profileIntent.putExtra(ProjectConstants.USER_EXTRA_KEY, user);

			startActivity(profileIntent);
			return true;
		case R.id.logout:
			UserService.logoutUser(getApplicationContext());
		default:
			return super.onOptionsItemSelected(item);
		}
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

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public static void finishActivity() {
		if (activity != null) {
			activity.finish();
		}
	}
}
