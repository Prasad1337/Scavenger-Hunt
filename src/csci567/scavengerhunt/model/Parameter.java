package csci567.scavengerhunt.model;

import java.io.Serializable;

/**
 * @author ccubukcu
 * */
public class Parameter extends PersistentEntity implements Serializable {
	private static final long serialVersionUID = 2336777779246322310L;

	private String key;
	private String value;

	public static final String KEY_KEY = "USERNAME";
	public static final String VALUE_KEY = "PASSWORD";

	public static final String TABLE_NAME = "PARAMETERS";

	public Parameter() {
		tableName = TABLE_NAME;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
		putValueToMap(KEY_KEY, key);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		putValueToMap(VALUE_KEY, value);
	}

}
