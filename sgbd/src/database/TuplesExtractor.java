package database;

import java.util.*;

import entities.Column;
import sgbd.prototype.BData;
import sgbd.prototype.ComplexRowData;
import sgbd.prototype.query.Tuple;
import sgbd.query.Operator;
import util.Utils;

public class TuplesExtractor {

	public static Map<String, String> getRow(Operator operator, boolean sourceAndName) {

		if(operator == null) return null;

		Set<String> possibleKeys = new LinkedHashSet<>();
	    Map<String, String> row = new LinkedHashMap<>();

        for(Map.Entry<String, List<String>> content: operator.getContentInfo().entrySet())
			possibleKeys.addAll(content.getValue().stream()
					.map(x-> sourceAndName ? entities.Column.putSource(x, content.getKey()) : x)
					.toList());

	    if (operator.hasNext()) {

	    	Tuple t = operator.next();

	        for (Map.Entry<String, ComplexRowData> line : t)
				for (Map.Entry<String, BData> data : line.getValue()) {

					String columnName = sourceAndName ? Column.putSource(data.getKey(), line.getKey()) : data.getKey();
					switch (Utils.getType(t, line.getKey(), data.getKey())) {
						case INTEGER -> row.put(columnName, line.getValue().getInt(data.getKey()).toString());
						case LONG -> row.put(columnName, line.getValue().getLong(data.getKey()).toString());
						case DOUBLE -> row.put(columnName, line.getValue().getDouble(data.getKey()).toString());
						case FLOAT -> row.put(columnName, line.getValue().getFloat(data.getKey()).toString());
						default -> row.put(columnName, line.getValue().getString(data.getKey()));
					}
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