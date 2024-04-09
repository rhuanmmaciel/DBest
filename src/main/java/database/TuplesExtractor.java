package database;

import controllers.ConstantController;
import entities.Column;
import sgbd.prototype.RowData;
import sgbd.prototype.query.Tuple;
import sgbd.query.Operator;
import utils.Utils;

import java.util.*;

public class TuplesExtractor {

    public enum Type{
        ALL_ROWS_IN_A_LIST,
        ALL_ROWS_IN_A_MAP,
        ROWS_IN_A_LIST,
        ROWS_LEFT_WITHOUT_CLOSING,
        ROW
    }


    public final Type type;
    private final Operator operator;
    private final Boolean sourceAndName;
    private final int amount;

    public TuplesExtractor(Operator operator, boolean sourceAndName, int amount, Type type) {
        this.type = type;
        this.operator = operator;
        this.sourceAndName = sourceAndName;
        this.amount = amount;
    }

    public TuplesExtractor(Operator operator, boolean sourceAndName, Type type) {
        this.type = type;
        this.operator = operator;
        this.sourceAndName = sourceAndName;
        this.amount = -1;
    }

    public Map<Integer, Map<String, String>>  getAllRowsMap() {
        operator.open();

        Map<Integer, Map<String, String>> rows = new HashMap<>();

        Map<String, String> row;

        row = getRow();
        int i = 0;
        while (row != null) {
            rows.put(i++, row);
            row = getRow();
        }

        operator.close();

        return rows;
    }

    public List<Map<String, String>> getAllRowsList() {
        operator.open();

        List<Map<String, String>> rows = new ArrayList<>();

        Map<String, String> row;

        row = getRow();

        while (row != null) {
            rows.add(row);
            row = getRow();
        }

        operator.close();

        return rows;
    }

    public List<Map<String, String>> getAllRowsLeftWithoutClosingOperatorList() {

        List<Map<String, String>> rows = new ArrayList<>();

        Map<String, String> row;

        row = getRow();

        while (row != null) {
            rows.add(row);
            row = getRow();
        }

        return rows;
    }

    public List<Map<String, String>> getRows(){

        List<Map<String, String>> rows = new ArrayList<>();

        for(int i = 0; i < amount; i++) {
            Map<String, String> row = getRow();
            if(row == null) return rows;

            rows.add(row);
        }

        return rows;

    }


    public Map<String, String> getRow() {
        if (operator == null) return null;

        Set<String> possibleKeys = new HashSet<>();

        Map<String, String> row = new TreeMap<>();

        for (Map.Entry<String, List<String>> content : operator.getContentInfo().entrySet()) {
            possibleKeys.addAll(content
                .getValue()
                .stream()
                .map(columnName -> sourceAndName ? Column.composeSourceAndName(content.getKey(), columnName) : columnName)
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
