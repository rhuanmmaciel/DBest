package entities.utils;

import java.util.*;

import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.util.statics.Util;

public class TableFormat {

	public static Map<String, String> getRow(Operator operator) {

	    Set<String> possibleKeys = new LinkedHashSet<>();
	    Map<String, String> row = new LinkedHashMap<>();
	    
        for(Map.Entry<String, List<String>> content: operator.getContentInfo().entrySet())
			possibleKeys.addAll(content.getValue());

	    if (operator.hasNext()) {

	    	Tuple t = operator.next();

	        for (Map.Entry<String, ComplexRowData> line : t)
	            for (Map.Entry<String, byte[]> data : line.getValue())
					switch (Util.typeOfColumn(line.getValue().getMeta(data.getKey()))) {
						case "int" -> row.put(data.getKey(), line.getValue().getInt(data.getKey()).toString());
						case "float" -> row.put(data.getKey(), line.getValue().getFloat(data.getKey()).toString());
						default -> row.put(data.getKey(), line.getValue().getString(data.getKey()));
					}

	    }else{

			return null;

		}

		for (String key : possibleKeys)
			if (!row.containsKey(key))
				row.put(key, "null");

	    return row;
	
	}

}