package csci567.scavengerhunt.services;

import java.util.Map;
import java.util.Map.Entry;

import csci567.scavengerhunt.model.PersistentEntity;

/**
 * @author ccubukcu
 * */
public class QueryService {
	
	public static String createInsertQuery(PersistentEntity entity) {
		String query = null;
		Map<String, Object> colValueMap = entity.getColumnValueMap();
		if(entity.getTableName() != null && !entity.getTableName().equals("")
				&& colValueMap != null && colValueMap.size() > 0) {
			
			query = "INSERT INTO " + entity.getTableName() + "(";
			String values = " VALUES(";
			int i = 0;
			for (Entry<String, Object> mapEntry : colValueMap.entrySet()) {
				if(mapEntry.getKey().equals(PersistentEntity.ID_KEY))
					continue;
				
				Object val = mapEntry.getValue();
				
				if(val == null) {
					i++;
					continue;
				}
				
				if(val instanceof Integer || val instanceof Double) {
					values += val.toString();
				} else if (val instanceof String){
					values += "'" + val.toString() + "'";
				} else if (val instanceof Boolean) {
					values += ((Boolean)val == true ? "TRUE" : "FALSE");
				} else {
					i++;
					continue;
				}
				query += mapEntry.getKey();
				
				if(i < colValueMap.size() - 1) {
					query += ", ";
					values += ", ";
				}
				i++;
			}
			
			query = query + ") " +  values + ")";
		}
		return query;
	}
	
	public static String createUpdateQuery(PersistentEntity entity) {
		String query = null;
		Map<String, Object> colValueMap = entity.getColumnValueMap();
		if(entity.getTableName() != null && !entity.getTableName().equals("")
				&& colValueMap != null && colValueMap.size() > 0 
				&& entity.getId() != null && entity.getId() > 0) {
			
			query = "UPDATE " + entity.getTableName() + " SET ";
			int i = 0;
			for (Entry<String, Object> mapEntry : colValueMap.entrySet()) {
				if(mapEntry.getKey().equals(PersistentEntity.ID_KEY))
					continue;
				
				Object val = mapEntry.getValue();
				
				if(val == null) {
					i++;
					continue;
				}

				query += mapEntry.getKey() + "=";
				
				if(val instanceof Integer || val instanceof Double) {
					query += val.toString();
				} else if (val instanceof String){
					query += "'" + val.toString() + "'";
				} else if (val instanceof Boolean) {
					query += ((Boolean)val == true ? "1" : "0");
				} else {
					continue;
				}
				
				if(i < colValueMap.size() - 1) {
					query += ", ";
				}
				i++;
			}
			String str = query.substring(query.length()-2);
			if(str.equals(", ")) {
				query = query.substring(0, query.length() -2);
			}
			query = query + " WHERE ID = " + entity.getId();
		}
		return query;
	}
}
