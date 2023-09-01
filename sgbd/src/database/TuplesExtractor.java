package database;

import java.util.*;

import controller.ConstantController;

import entities.Column;

import sgbd.prototype.BData;
import sgbd.prototype.ComplexRowData;
import sgbd.prototype.query.Tuple;
import sgbd.query.Operator;

import utils.Utils;

public class TuplesExtractor {

    public static List<Map<String, String>> getAllRows(Operator operator, boolean sourceAndName) {
        operator.open();

        List<Map<String, String>> rows = new ArrayList<>();

        Map<String, String> row;

        row = getRow(operator, sourceAndName);

        while (row != null) {
            rows.add(row);
            row = getRow(operator, sourceAndName);
        }

        operator.close();

        return rows;
    }

    public static Map<String, String> getRow(Operator operator, boolean sourceAndName) {
		if (operator == null) {
			return null;
		}

        Set<String> possibleKeys = new HashSet<>();
        Map<String, String> row = new TreeMap<>();

		for (Map.Entry<String, List<String>> content : operator.getContentInfo().entrySet()) {
			possibleKeys.addAll(content
				.getValue()
				.stream()
				.map(x -> sourceAndName ? entities.Column.composeSourceAndName(content.getKey(), x) : x)
				.toList());
		}

        if (operator.hasNext()) {
            Tuple tuple = operator.next();

			for (Map.Entry<String, ComplexRowData> line : tuple) {
				for (Map.Entry<String, BData> data : line.getValue()) {
					String columnName = sourceAndName ? Column.composeSourceAndName(line.getKey(), data.getKey()) : data.getKey();

					switch (Utils.getColumnDataType(tuple, line.getKey(), data.getKey())) {
						case INTEGER -> row.put(columnName, Objects.toString(line.getValue().getInt(data.getKey()), ConstantController.NULL));
						case LONG -> row.put(columnName, Objects.toString(line.getValue().getLong(data.getKey()), ConstantController.NULL));
						case DOUBLE -> row.put(columnName, Objects.toString(line.getValue().getDouble(data.getKey()), ConstantController.NULL));
						case FLOAT -> row.put(columnName, Objects.toString(line.getValue().getFloat(data.getKey()), ConstantController.NULL));
						default -> row.put(columnName, Objects.toString(line.getValue().getString(data.getKey()), ConstantController.NULL));
					}
				}
			}
        } else {
            return null;
        }

		for (String key : possibleKeys) {
			if (!row.containsKey(key)) {
				row.put(key, ConstantController.NULL);
			}
		}

        return row;
    }
}