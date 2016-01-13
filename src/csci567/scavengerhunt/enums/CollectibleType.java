package csci567.scavengerhunt.enums;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * @author ccubukcu
 * */
public enum CollectibleType {
	TINY_BOX(0, 50, BitmapDescriptorFactory.HUE_RED, 1),
	MEDIUM_BOX(1, 150,BitmapDescriptorFactory.HUE_BLUE, 6), 
	LARGE_BOX(2, 400, BitmapDescriptorFactory.HUE_GREEN, 24);

	private int index;
	private int value;
	private int cooldown;
	private float color;

	private CollectibleType(int index, int value, float color, int cooldown) {
		this.index = index;
		this.value = value;
		this.color = color;
		this.cooldown = cooldown;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public float getColor() {
		return color;
	}

	public void setColor(float color) {
		this.color = color;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public static int getValueByIndex(int type) {
		for (CollectibleType c : values()) {
			if(c.getIndex() == type)
				return c.getValue();
		}
		return 0;
	}

	public static CollectibleType getTypeByIndex(int type) {
		for (CollectibleType c : values()) {
			if(c.getIndex() == type)
				return c;
		}
		return null;
	}
	
	public String getStringKey() {
		return "items." + name();
	}
}
