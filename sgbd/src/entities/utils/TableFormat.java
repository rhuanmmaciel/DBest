package entities.utils;

import java.util.*;

import entities.Column;
import sgbd.prototype.BData;
import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.util.statitcs.Util;

public class TableFormat {

	public static Map<String, String> getRow(Operator operator) {

		if(operator == null) return null;

		Set<String> possibleKeys = new LinkedHashSet<>();
	    Map<String, String> row = new LinkedHashMap<>();
	    
        for(Map.Entry<String, List<String>> content: operator.getContentInfo().entrySet())
			possibleKeys.addAll(content.getValue().stream().map(x-> entities.Column.putSource(x, content.getKey()))
					.toList());

	    if (operator.hasNext()) {

	    	Tuple t = operator.next();

	        for (Map.Entry<String, ComplexRowData> line : t)
				for (Map.Entry<String, BData> data : line.getValue())
					switch (Util.typeOfColumn(line.getValue().getMeta(data.getKey()))) {
						case "int" -> row.put(Column.putSource(data.getKey(), line.getKey()), line.getValue().getInt(data.getKey()).toString());
						case "float" -> row.put(Column.putSource(data.getKey(), line.getKey()), line.getValue().getFloat(data.getKey()).toString());
						default -> row.put(Column.putSource(data.getKey(), line.getKey()), line.getValue().getString(data.getKey()));
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