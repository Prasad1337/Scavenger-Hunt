package csci567.scavengerhunt.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ccubukcu
 * */
public class PersistentEntity {
	public static final String ID_KEY = "ID";
	
	public Integer id;
	
	public String tableName;
	
	/**Object only handles Strings and Integers at this moment*/
	public Map<String, Object> columnValueMap;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
		putValueToMap("ID", id);
	}
	
	public PersistentEntity() {
		columnValueMap = new HashMap<String, Object>();
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, Object> getColumnValueMap() {
		return columnValueMap;
	}

	public void setColumnValueMap(Map<String, Object> columnValueMap) {
		this.columnValueMap = columnValueMap;
	}
	
	public void putValueToMap(String columnName, Object value) {
		columnValueMap.put(columnName, value);
	}
}
