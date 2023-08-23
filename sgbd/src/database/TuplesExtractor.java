package database;

import java.util.*;

import controller.ConstantController;
import entities.Column;
import sgbd.prototype.RowData;
import sgbd.prototype.query.Tuple;
import sgbd.prototype.query.fields.*;
import sgbd.query.Operator;

public class TuplesExtractor {

	public static List<Map<String, String>> getAllRows(Operator operator, boolean sourceAndName){

		operator.open();

		List<Map<String, String>> rows = new ArrayList<>();

		Map<String, String> row;
		row = getRow(operator, sourceAndName);

		while(row != null){

			rows.add(row);
			row = getRow(operator, sourceAndName);

		}

		operator.close();

		return rows;

	}

	public static Map<String, String> getRow(Operator operator, boolean sourceAndName) {

		if(operator == null) return null;

		Set<String> possibleKeys = new HashSet<>();
	    Map<String, String> row = new TreeMap<>();

        for(Map.Entry<String, List<String>> content: operator.getContentInfo().entrySet())
			possibleKeys.addAll(content.getValue().stream()
					.map(x-> sourceAndName ? entities.Column.putSource(x, content.getKey()) : x)
					.toList());

	    if (operator.hasNext()) {

	    	Tuple t = operator.next();

	        for (Map.Entry<String, RowData> line : t)
				for (Map.Entry<String, ? extends Field> data : line.getValue()) {

					String columnName = sourceAndName ? Column.putSource(data.getKey(), line.getKey()) : data.getKey();

					if(data.getValue() instanceof IntegerField) row.put(columnName, Objects.toString(line.getValue().getInt(data.getKey()), ConstantController.NULL));
					else if(data.getValue() instanceof LongField) row.put(columnName, Objects.toString(line.getValue().getLong(data.getKey()), ConstantController.NULL));
					else if(data.getValue() instanceof DoubleField) row.put(columnName, Objects.toString(line.getValue().getDouble(data.getKey()), ConstantController.NULL));
					else if(data.getValue() instanceof FloatField) row.put(columnName, Objects.toString(line.getValue().getFloat(data.getKey()), ConstantController.NULL));
					else row.put(columnName, Objects.toString(line.getValue().getString(data.getKey()), ConstantController.NULL));
				}
	    }else{

			return null;

		}

		for (String key : possibleKeys)
			if (!row.containsKey(key))
				row.put(key, ConstantController.NULL);

		return row;
	
	}

}