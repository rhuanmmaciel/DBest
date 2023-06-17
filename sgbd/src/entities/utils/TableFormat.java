package entities.utils;

import java.util.*;

import entities.Column;
import enums.ColumnDataType;
import sgbd.prototype.BData;
import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.util.statitcs.Util;

public class TableFormat {

	public static Row getRow(Operator operator) {

		if(operator == null) return null;

		Set<String> possibleKeys = new LinkedHashSet<>();
	    Map<String, String> row = new LinkedHashMap<>();
		Map<String, ColumnDataType> types = new LinkedHashMap<>();

        for(Map.Entry<String, List<String>> content: operator.getContentInfo().entrySet())
			possibleKeys.addAll(content.getValue().stream()
					.map(x-> entities.Column.putSource(x, content.getKey()))
					.toList());

	    if (operator.hasNext()) {

	    	Tuple t = operator.next();

	        for (Map.Entry<String, ComplexRowData> line : t)
				for (Map.Entry<String, BData> data : line.getValue()) {

					String columnName = Column.putSource(data.getKey(), line.getKey());

					switch (Util.typeOfColumn(line.getValue().getMeta(data.getKey()))) {
						case "int" -> {
							row.put(columnName, line.getValue().getInt(data.getKey()).toString());
							types.put(columnName, ColumnDataType.INTEGER);
						}
						case "float" -> {
							row.put(columnName, line.getValue().getFloat(data.getKey()).toString());
							types.put(columnName, ColumnDataType.FLOAT);
						}
						case "boolean" -> {
							row.put(columnName, line.getValue().getString(data.getKey()));
							types.put(columnName, ColumnDataType.BOOLEAN);
						}
						default -> {
							row.put(Column.putSource(data.getKey(), line.getKey()), line.getValue().getString(data.getKey()));
							types.put(columnName, ColumnDataType.STRING);
						}
					}
				}
	    }else{

			return null;

		}

		for (String key : possibleKeys)
			if (!row.containsKey(key))
				row.put(key, "null");

		return new Row(row, types);
	
	}

	public record Row(Map<String, String> row, Map<String, ColumnDataType> types){}

}