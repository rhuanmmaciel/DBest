package entities.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.util.Util;

public class TableFormat {

	public static Map<Integer, Map<String, String>> getRows(Operator operator) {
		
	    Operator aux = operator;
	    aux.close();
	    aux.open();

	    Set<String> possibleKeys = new HashSet<>(); 
	    Map<Integer, Map<String, String>> rows = new HashMap<>();
	    
        for(Map.Entry<String, List<String>> content: aux.getContentInfo().entrySet()){
            for(String col:content.getValue()){
            	
            	possibleKeys.add(col);
            	
            }
        }

	    int i = 0;

	    while (aux.hasNext()) {

	    	Tuple t = aux.next();
	    	
	        Map<String, String> row = new HashMap<>();

	        for (Map.Entry<String, ComplexRowData> line : t) {
	            for (Map.Entry<String, byte[]> data : line.getValue()) {

	            	switch(Util.typeOfColumn(line.getValue().getMeta(data.getKey()))) {
	                    case "int":
	                        row.put(data.getKey(), line.getValue().getInt(data.getKey()).toString());
	                        break;
	                    case "float":
	                        row.put(data.getKey(), line.getValue().getFloat(data.getKey()).toString());
	                        break;
	                    case "string":
	                    default:
	                        row.put(data.getKey(), line.getValue().getString(data.getKey()));
            	
	            	}
	            	
	            }
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

	    aux.close();
	    
	    return rows;
	
	}

}