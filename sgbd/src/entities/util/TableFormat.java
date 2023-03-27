package entities.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.util.Util;

public class TableFormat {

	public static Map<Integer, Map<String, String>> getRows(Operator operator) {
	    Operator aux = operator;
	    aux.open();

	    Set<String> possibleKeys = new HashSet<>(); 
	    Map<Integer, Map<String, String>> rows = new HashMap<>();

	    Tuple tuple = aux.hasNext() ? aux.next() : null;
	    int i = 0;
	    while (aux.hasNext() || tuple != null) {
	        Tuple t = tuple == null ? aux.next() : tuple;

	        Map<String, String> row = new HashMap<>();

	        for (Map.Entry<String, ComplexRowData> line : t) {
	            for (Map.Entry<String, byte[]> data : line.getValue()) {
	            	possibleKeys.add(data.getKey());
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
	        tuple = null;
	        i++;
	    }

	    aux.close();

	    for (Map<String, String> row : rows.values()) {
	        for (String key : possibleKeys) {
	            if (!row.containsKey(key)) {
	                row.put(key, "");
	            }
	        }
	    }
	    
	    return rows;
	}

}
