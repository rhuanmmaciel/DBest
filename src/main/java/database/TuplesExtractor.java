package database;

import controllers.ConstantController;
import entities.Column;
import sgbd.prototype.RowData;
import sgbd.prototype.query.Tuple;
import sgbd.query.Operator;
import utils.Utils;

import java.util.*;

public class TuplesExtractor {

    private TuplesExtractor() {

    }

    public static Map<Integer, Map<String, String>>  getAllRowsMap(Operator operator, boolean sourceAndName) {
        operator.open();

        Map<Integer, Map<String, String>> rows = new HashMap<>();

        Map<String, String> row;

        row = getRow(operator, sourceAndName);
        int i = 0;
        while (row != null) {
            rows.put(i++, row);
            row = getRow(operator, sourceAndName);
        }

        operator.close();

        return rows;
    }

    public static List<Map<String, String>> getRows(Operator operator, int amount, boolean sourceAndName) {
        operator.open();

        List<Map<String, String>> rows = new ArrayList<>();

        Map<String, String> row;

        row = getRow(operator, sourceAndName);
        int i = 1;
        while (row != null && i++ <= amount) {
            rows.add(row);
            row = getRow(operator, sourceAndName);
        }

        operator.close();

        return rows;
    }

    public static List<Map<String, String>> getAllRowsList(Operator operator, boolean sourceAndName) {
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

    public static List<Map<String, String>> getRows(Operator operator, boolean sourceAndName, int amount){

        if(amount <= 0)
            throw new IllegalArgumentException();

        List<Map<String, String>> rows = new ArrayList<>();

        for(int i = 0; i < amount; i++) {
            Map<String, String> row = getRow(operator, sourceAndName);
            if(row == null) return rows;

            rows.add(row);
        }

        return rows;

    }


    public static Map<String, String> getRow(Operator operator, boolean sourceAndName) {
        if (operator == null) return null;

        Set<String> possibleKeys = new HashSet<>();

        Map<String, String> row = new TreeMap<>();

        for (Map.Entry<String, List<String>> content : operator.getContentInfo().entrySet()) {
            possibleKeys.addAll(content
                .getValue()
                .stream()
                .map(columnName -> sourceAndName ? entities.Column.composeSourceAndName(content.getKey(), columnName) : columnName)
                .toList()
            );
        }

        if (operator.hasNext()) {
            Tuple tuple = operator.next();

            for (Map.Entry<String, List<String>> content : operator.getContentInfo().entrySet()) {
                for (String columnName : content.getValue()) {
                    RowData rowData = tuple.getContent(content.getKey());
                    String sourceAndColumn = sourceAndName ? Column.composeSourceAndName(content.getKey(), columnName) : columnName;

                    switch (Utils.getColumnDataType(tuple, content.getKey(), columnName)) {
                        case INTEGER ->
                            row.put(sourceAndColumn, Objects.toString(rowData.getInt(columnName), ConstantController.NULL));
                        case LONG ->
                            row.put(sourceAndColumn, Objects.toString(rowData.getLong(columnName), ConstantController.NULL));
                        case FLOAT ->
                            row.put(sourceAndColumn, Objects.toString(rowData.getFloat(columnName), ConstantController.NULL));
                        case DOUBLE ->
                            row.put(sourceAndColumn, Objects.toString(rowData.getDouble(columnName), ConstantController.NULL));
                        case BOOLEAN ->
                            row.put(sourceAndColumn, Objects.toString(rowData.getBoolean(columnName), ConstantController.NULL));
                        case STRING, NONE, CHARACTER ->
                            row.put(sourceAndColumn, Objects.toString(rowData.getString(columnName), ConstantController.NULL));
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
