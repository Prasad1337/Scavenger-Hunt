package csci567.scavengerhunt.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ccubukcu
 * */
public class Collectible extends PersistentEntity implements Serializable {
	private static final long serialVersionUID = 1700080004721469050L;

	private String droppedBy;
	private String collectedBy;
	private Boolean collected=false;
	private Boolean dropped=false;
	private double latitude;
	private double longitude;
	private int type;
	private Date creationTime;

	public static final String DROPPED_BY_KEY = "DROPPED_BY";
	public static final String COLLECTED_BY_KEY = "COLLECTED_BY";
	public static final String COLLECTED_KEY = "COLLECTED";
	public static final String DROPPED_KEY = "DROPPED";
	public static final String LAT_KEY = "LATITUDE";
	public static final String LNG_KEY = "LONGITUDE";
	public static final String TYPE_KEY = "TYPE";
	public static final String CREATION_TIME_KEY = "CREATION_TIME";

	public static final String TABLE_NAME = "MARKERS";

	public Collectible() {
		tableName = TABLE_NAME;
	}

	public String getDroppedBy() {
		return droppedBy;
	}

	public void setDroppedBy(String droppedBy) {
		this.droppedBy = droppedBy;
		putValueToMap(DROPPED_BY_KEY, droppedBy);
	}

	public String getCollectedBy() {
		return collectedBy;
	}

	public void setCollectedBy(String collectedBy) {
		this.collectedBy = collectedBy;
		putValueToMap(COLLECTED_BY_KEY, collectedBy);
	}

	public Boolean getCollected() {
		return collected;
	}

	public void setCollected(Boolean collected) {
		this.collected = collected;
		putValueToMap(COLLECTED_KEY, collected);
	}
	
	public Boolean getDropped() {
		return dropped;
	}

	public void setDropped(Boolean dropped) {
		this.dropped = dropped;
		putValueToMap(DROPPED_KEY, dropped);
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
		putValueToMap(LAT_KEY, latitude);
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
		putValueToMap(LNG_KEY, longitude);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
		putValueToMap(TYPE_KEY, type);
	}

	public Date getCreationTime() {
		return creationTime;
	}

	/*This is left out because it is filled automatically*/
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
}
