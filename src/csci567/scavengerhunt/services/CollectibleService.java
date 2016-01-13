package csci567.scavengerhunt.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import csci567.scavengerhunt.enums.CollectibleType;
import csci567.scavengerhunt.model.Collectible;
import csci567.scavengerhunt.tools.DataHelpers;

/**
 * @author ccubukcu
 * */
public class CollectibleService {
	public static int storeCollectible(Collectible marker) {
		String query = QueryService.createInsertQuery(marker);

		return JSONService.sendInsertQuery(query);
	}

	public static Map<Integer, Long> getCollectibleCountdowns(String username) {
		Map<Integer, Long> itemAvailabiltyMap = new HashMap<Integer, Long>();

		for (CollectibleType ct : CollectibleType.values()) {
			List<Map<String, String>> itemCountdowns = JSONService.selectItems(
					Collectible.TABLE_NAME, "DROPPED_BY = '" + username
							+ "' AND type = " + ct.getIndex()
							+ " ORDER BY CREATION_TIME DESC LIMIT 0,1");

			if (itemCountdowns != null && itemCountdowns.size() > 0) {
				Calendar now = Calendar.getInstance();
				Calendar itemCal = Calendar.getInstance();

				for (Map<String, String> item : itemCountdowns) {
					itemCal.setTime(DataHelpers.parseDateFromString(item
							.get(Collectible.CREATION_TIME_KEY)));

					long timeDif = now.getTimeInMillis()
							- itemCal.getTimeInMillis();
					CollectibleType t = CollectibleType.getTypeByIndex(Integer
							.parseInt(item.get(Collectible.TYPE_KEY)));

					timeDif = TimeUnit.HOURS.toMillis(t.getCooldown())
							- timeDif;

					itemAvailabiltyMap.put(t.getIndex(), timeDif);
				}
			}
		}
		return itemAvailabiltyMap;
	}
	
	public static List<Collectible> getNearbyCollectibles(String username, LatLngBounds llb) {
		LatLng a=llb.northeast;
		LatLng b=llb.southwest;
		
		double x=a.latitude;
		double y=a.longitude;
		
		double z=b.latitude;
		double w=b.longitude;
		
		double x1=x;
		double y1=w;
		
		double z1=z;
		double w1=y;
		
		String whereClause = Collectible.DROPPED_BY_KEY + " != '" + username + "' AND " + Collectible.COLLECTED_KEY + " = 0 ";

//		whereClause += " AND " + Collectible.LAT_KEY + " > " + x1 + "  AND " + Collectible.LNG_KEY + " < " + y1;
//		whereClause += " AND " + Collectible.LAT_KEY + " < " + x + "  AND " + Collectible.LNG_KEY + " < " + y;
//		whereClause += " AND " + Collectible.LAT_KEY + " > " + z + "  AND " + Collectible.LNG_KEY + " > " + w;
//		whereClause += " AND " + Collectible.LAT_KEY + " < " + z1 + "  AND " + Collectible.LNG_KEY + " > " + w1;

		System.out.println(whereClause);
		
		List<Collectible> markers = new ArrayList<Collectible>();
		
		List<Map<String, String>> getMarkerMap = JSONService.selectItems(Collectible.TABLE_NAME, whereClause);
		
		for (Map<String, String> map : getMarkerMap) {
			Collectible cl = new Collectible();
			cl.setId(Integer.parseInt(map.get(Collectible.ID_KEY)));
			cl.setType(Integer.parseInt(map.get(Collectible.TYPE_KEY)));
			
			cl.setDroppedBy(map.get(Collectible.DROPPED_BY_KEY));
			cl.setCollectedBy(map.get(Collectible.COLLECTED_BY_KEY));
			cl.setDropped(getBooleanFromString(map.get(Collectible.DROPPED_KEY)));
			cl.setCollected(getBooleanFromString(map.get(Collectible.COLLECTED_KEY)));

			cl.setLatitude(Double.parseDouble(map.get(Collectible.LAT_KEY)));
			cl.setLongitude(Double.parseDouble(map.get(Collectible.LNG_KEY)));
			
			cl.setCreationTime(DataHelpers.parseDateFromString(map.get(Collectible.CREATION_TIME_KEY)));
			
			markers.add(cl);
		}
		
		return markers;
	}
	
	private static Boolean getBooleanFromString(String str) {
		return DataHelpers.getBooleanFromString(str);
	}
}
