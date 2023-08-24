package database;

import java.util.*;

import controller.ConstantController;
import entities.Column;
import sgbd.prototype.RowData;
import sgbd.prototype.query.Tuple;
import sgbd.query.Operator;
import util.Utils;

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

			for(Map.Entry<String, List<String>> content: operator.getContentInfo().entrySet())
				for(String col : content.getValue()){

					RowData rowData = t.getContent(content.getKey());
					String columnName = sourceAndName ? Column.putSource(col, content.getKey()) : col;

					switch (Utils.getType(t, content.getKey(), col)){

						case INTEGER -> row.put(columnName, Objects.toString(rowData.getInt(col), ConstantController.NULL));
						case LONG -> row.put(columnName, Objects.toString(rowData.getLong(col), ConstantController.NULL));
						case FLOAT -> row.put(columnName, Objects.toString(rowData.getFloat(col), ConstantController.NULL));
						case DOUBLE -> row.put(columnName, Objects.toString(rowData.getDouble(col), ConstantController.NULL));
						case BOOLEAN -> row.put(columnName, Objects.toString(rowData.getBoolean(col), ConstantController.NULL));
						case STRING, NONE, CHARACTER -> row.put(columnName, Objects.toString(rowData.getString(col), ConstantController.NULL));

					}

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