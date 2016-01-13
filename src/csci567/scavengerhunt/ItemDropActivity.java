package csci567.scavengerhunt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import csci567.scavengerhunt.enums.CollectibleType;
import csci567.scavengerhunt.enums.ProjectConstants;
import csci567.scavengerhunt.model.User;
import csci567.scavengerhunt.services.CollectibleService;

public class ItemDropActivity extends ListActivity implements OnItemClickListener {

	User user;
	static CollectibleType selectedType;
	static boolean itemSelected;
	Map<Integer, Long> itemAvailibilityMap;

	private static final String TITLE = "title";
	private static final String SUBTEXT = "subtext";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_drop);
		itemSelected = false;
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		user = (User) getIntent().getExtras().getSerializable(
				ProjectConstants.USER_EXTRA_KEY);

        itemAvailibilityMap = CollectibleService.getCollectibleCountdowns(user.getUsername());
        
		final CollectibleType[] states = CollectibleType.values();
        final ListAdapter listAdapter = createListAdapter(states);
        setListAdapter(listAdapter);
        
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		boolean cooldown = false;
		
		if(itemAvailibilityMap != null && itemAvailibilityMap.size() > 0) {
			Long timeDif = itemAvailibilityMap.get(position);
			
			if(timeDif != null && timeDif > 0) {
				cooldown = true;
			}
		}
		
		if(!cooldown) {
			selectedType = CollectibleType.getTypeByIndex(position);
			itemSelected = true;
		
			finish();
		} else {
			Toast.makeText(this, "Selected item is on cooldown. Please pick another item.",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	private ListAdapter createListAdapter(final CollectibleType[] states) {
	    final String[] fromMapKey = new String[] {TITLE, SUBTEXT};
	    final int[] toLayoutId = new int[] {android.R.id.text1, android.R.id.text2};
	    final List<Map<String, String>> list = convertToListItems(states);
	    
	    SimpleAdapter sa = new SimpleAdapter(this, list,
	                             android.R.layout.simple_list_item_2,
	                             fromMapKey, toLayoutId);
	    
	    return sa;
	}
	
	private List<Map<String, String>> convertToListItems(final CollectibleType[] collectibles) {
	    final List<Map<String, String>> listItem =
	      new ArrayList<Map<String, String>>(collectibles.length);

	    for (final CollectibleType cl: collectibles) {
	        final Map<String, String> listItemMap = new HashMap<String, String>();
	        int resId = getResources().getIdentifier(cl.getStringKey(), "string", getPackageName());
	        listItemMap.put(TITLE, getString(resId));
	        
	        String subtext = Integer.toString(cl.getValue()) + " points. Next Available: ";
	        
	        if(itemAvailibilityMap != null && itemAvailibilityMap.size() > 0) {
	        	Long timeDif = itemAvailibilityMap.get(cl.getIndex());
	        	
	        	if(timeDif != null && timeDif > 0) {
		        	Long hours = TimeUnit.MILLISECONDS.toHours(timeDif);
		        	
		        	Long minuteDif = timeDif - TimeUnit.HOURS.toMillis(hours);
		        	Long minutes = TimeUnit.MILLISECONDS.toMinutes(minuteDif);
		        	subtext += hours + ":" + minutes;
	        	} else {
	        		subtext += "Now!";
	        	}
	        } else {
        		subtext += "Now!";
        	}
	        
	        listItemMap.put(SUBTEXT, subtext);
	        listItem.add(Collections.unmodifiableMap(listItemMap));
	    }

	    return Collections.unmodifiableList(listItem);
	}

	public static CollectibleType getSelectedType() {
		return selectedType;
	}

	public static void setSelectedType(CollectibleType selectedType) {
		ItemDropActivity.selectedType = selectedType;
	}

	public static boolean isItemSelected() {
		return itemSelected;
	}

	public static void setItemSelected(boolean itemSelected) {
		ItemDropActivity.itemSelected = itemSelected;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}
}
