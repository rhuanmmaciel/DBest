package entities.utils;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.util.statics.Util;

public class TableFormat {

	public static Map<Integer, Map<String, String>> getRows(Operator operator) {

		operator.open();

	    Set<String> possibleKeys = new LinkedHashSet<>(); 
	    Map<Integer, Map<String, String>> rows = new LinkedHashMap<>();
	    
        for(Map.Entry<String, List<String>> content: operator.getContentInfo().entrySet())
			possibleKeys.addAll(content.getValue());

	    int i = 0;
	    
	    while (operator.hasNext()) {

	    	Tuple t = operator.next();
	    	
	        Map<String, String> row = new LinkedHashMap<>();

	        for (Map.Entry<String, ComplexRowData> line : t)
	            for (Map.Entry<String, byte[]> data : line.getValue())
					switch (Util.typeOfColumn(line.getValue().getMeta(data.getKey()))) {
						case "int" -> row.put(data.getKey(), line.getValue().getInt(data.getKey()).toString());
						case "float" -> row.put(data.getKey(), line.getValue().getFloat(data.getKey()).toString());
						default -> row.put(data.getKey(), line.getValue().getString(data.getKey()));
					}

	        rows.put(i, row);
	        i++;
	    }

	    for (Map<String, String> row : rows.values()) {
	        for (String key : possibleKeys) {
	            if (!row.containsKey(key)) {
	                row.put(key, "null");
	            }
	        }
	    }

	    Map<Integer, Map<String, String>> rowsInOrder = new LinkedHashMap<>();
	    
	    for(Map.Entry<Integer, Map<String, String>> row : rows.entrySet()) {
	    	
	    	Map<String, String> newRow = new LinkedHashMap<>();
	    	for(String key : possibleKeys) {
	    		
	    		if(row.getValue().containsKey(key)) {
	    			
	    			newRow.put(key, row.getValue().get(key));
	    			
	    		}
	    		
	    	}
	    	rowsInOrder.put(row.getKey(), newRow);
	    	
	    }
	    
	    operator.close();
	    	
	    return rowsInOrder;
	
	}

}