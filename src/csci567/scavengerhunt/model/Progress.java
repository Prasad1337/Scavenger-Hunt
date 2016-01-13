package csci567.scavengerhunt.model;

import java.io.Serializable;

/**
 * @author ccubukcu
 * */
public class Progress extends PersistentEntity implements Serializable{
	private static final long serialVersionUID = -597557326838210609L;

	public static final String TABLE_NAME = "USER_PROGRESS";
	
	private String username;
	private Integer totalPoints;
	private Integer totalItemsCollected;
	private Integer totalItemsDropped;
	private Integer level;
	
	public static final String USERNAME_KEY = "USERNAME";
	public static final String TOTAL_POINTS_KEY = "TOTAL_POINTS";
	public static final String TOTAL_COLLECTED_KEY = "TOTAL_COLLECTED";
	public static final String TOTAL_DROPPED_KEY = "TOTAL_DROPPED";
	public static final String LEVEL_KEY = "LEVEL";
	
	public Progress() {
		tableName = TABLE_NAME;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		putValueToMap(USERNAME_KEY, username);
	}

	public Integer getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(Integer totalPoints) {
		this.totalPoints = totalPoints;
		putValueToMap(TOTAL_POINTS_KEY, totalPoints);
	}

	public Integer getTotalItemsCollected() {
		return totalItemsCollected;
	}

	public void setTotalItemsCollected(Integer totalItemsCollected) {
		this.totalItemsCollected = totalItemsCollected;
		putValueToMap(TOTAL_COLLECTED_KEY, totalItemsCollected);
	}

	public Integer getTotalItemsDropped() {
		return totalItemsDropped;
	}

	public void setTotalItemsDropped(Integer totalItemsDropped) {
		this.totalItemsDropped = totalItemsDropped;
		putValueToMap(TOTAL_DROPPED_KEY, totalItemsDropped);
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
		putValueToMap(LEVEL_KEY, level);
	}
}
