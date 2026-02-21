package database;

import controllers.ConstantController;
import entities.Column;
import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;

import java.util.*;

public class TuplesExtractor {

    private TuplesExtractor() {

    }

    public static Map<Integer, Map<String, String>> getAllRowsMap(Operation operator, boolean sourceAndName) {
        Map<Integer, Map<String, String>> rows = new HashMap<>();

        try {
            operator.open();
            int i = 0;
            while (operator.hasNext()) {
                Tuple tuple = operator.next();
                Map<String, String> row = getRow_(tuple, operator, sourceAndName);
                rows.put(i++, row);
            }
            operator.close();

        } catch (Exception ex) {
        }
        return rows;
    }

    public static List<Map<String, String>> getRows(Operation operator, int amount, boolean sourceAndName) {
        List<Map<String, String>> rows = new ArrayList<>();
        try {
            operator.open();
            int i = 1;
            while (operator.hasNext() && i++ <= amount) {
                Tuple tuple = operator.next();
                Map<String, String> row = getRow_(tuple, operator, sourceAndName);
                rows.add(row);
            }

            operator.close();
        } catch (Exception ex) {
        }
        return rows;
    }

    public static List<Map<String, String>> getAllRowsList(Operation operator, boolean sourceAndName) {

        List<Map<String, String>> rows = new ArrayList<>();

        try {
            operator.open();
            while (operator.hasNext()) {
                Tuple tuple = operator.next();
                Map<String, String>  row = getRow_(tuple, operator, sourceAndName);
                rows.add(row);
            }

            operator.close();
        } catch (Exception ex) {
        }

        return rows;
    }

    public static List<Map<String, String>> getRows(Operation operator, boolean sourceAndName, int amount) {

        if (amount <= 0) {
            //throw new IllegalArgumentException();
            return new ArrayList<>();
        }

        List<Map<String, String>> rows = new ArrayList<>();
        int i = 0;
        while (i<amount && operator.hasNext()) {
            Tuple tuple = operator.next();
            
            Map<String, String> row = getRow_(tuple, operator, sourceAndName);
            rows.add(row);
            i++;
        }

        return rows;

    }

    public static Set<String> getPossibleKeys(Operation operator, boolean sourceAndName) {
        Set<String> possibleKeys = new HashSet<>();
        for (Map.Entry<String, List<String>> content : operator.getContentInfo().entrySet()) {
            possibleKeys.addAll(content
                    .getValue()
                    .stream()
                    .map(columnName -> sourceAndName ? entities.Column.composeSourceAndName(content.getKey(), columnName) : columnName)
                    .toList()
            );
        }
        return possibleKeys;
    }

    public static Map<String, String> getRow___(Operation operator, boolean sourceAndName) {
        if (operator == null) {
            return null;
        }

        Map<String, String> row = new TreeMap<>();

        if (operator.hasNext()) {
            Tuple tuple = operator.next();
            int rowIndex = 0;
            ReferedDataSource[] dataSources = null;
            try {
                dataSources = operator.getExposedDataSources();
            } catch (Exception ex) {
            }
            for (LinkedDataRow row1 : tuple.rows) {

                String alias = dataSources[rowIndex].alias;
                int colIndex = 0;
                for (ibd.table.prototype.column.Column column : row1.getPrototype().getColumns()) {
                    String columnName = column.getName();
                    String sourceAndColumn = sourceAndName ? Column.composeSourceAndName(alias, columnName) : columnName;
                    boolean hasValue = row1.hasValue(columnName);
                    if (hasValue) {
                        switch (column.getType()) {
                            case ibd.table.prototype.column.Column.INTEGER_TYPE ->
                                row.put(sourceAndColumn, Objects.toString(row1.getInt(columnName), ConstantController.NULL));
                            case ibd.table.prototype.column.Column.LONG_TYPE ->
                                row.put(sourceAndColumn, Objects.toString(row1.getLong(columnName), ConstantController.NULL));
                            case ibd.table.prototype.column.Column.FLOAT_TYPE ->
                                row.put(sourceAndColumn, Objects.toString(row1.getFloat(columnName), ConstantController.NULL));
                            case ibd.table.prototype.column.Column.DOUBLE_TYPE ->
                                row.put(sourceAndColumn, Objects.toString(row1.getDouble(columnName), ConstantController.NULL));
                            case ibd.table.prototype.column.Column.BOOLEAN_TYPE ->
                                row.put(sourceAndColumn, Objects.toString(row1.getBoolean(columnName), ConstantController.NULL));
                            case ibd.table.prototype.column.Column.STRING_TYPE ->
                                row.put(sourceAndColumn, Objects.toString(row1.getString(columnName), ConstantController.NULL));
                            default ->
                                row.put(sourceAndColumn, Objects.toString(row1.getString(columnName), ConstantController.NULL));

                        }
                    } else {
                        row.put(sourceAndColumn, ConstantController.NULL);
                    }
                    colIndex++;
                }
                rowIndex++;
            }

        } else {
            return null;
        }

//        for (String key : possibleKeys) {
//            if (!row.containsKey(key)) {
//                row.put(key, ConstantController.NULL);
//            }
//        }
        return row;
    }

    public static Map<String, String> getRow_(Tuple tuple, Operation operator, boolean sourceAndName) {

        Map<String, String> row = new TreeMap<>();

        int rowIndex = 0;
        ReferedDataSource[] dataSources = null;
        try {
            dataSources = operator.getExposedDataSources();
        } catch (Exception ex) {
        }
        for (LinkedDataRow row1 : tuple.rows) {

            String alias = dataSources[rowIndex].alias;
            int colIndex = 0;
            for (ibd.table.prototype.column.Column column : row1.getPrototype().getColumns()) {
                String columnName = column.getName();
                String sourceAndColumn = sourceAndName ? Column.composeSourceAndName(alias, columnName) : columnName;
                boolean hasValue = row1.hasValue(columnName);
                if (hasValue) {
                    switch (column.getType()) {
                        case ibd.table.prototype.column.Column.INTEGER_TYPE ->
                            row.put(sourceAndColumn, Objects.toString(row1.getInt(columnName), ConstantController.NULL));
                        case ibd.table.prototype.column.Column.LONG_TYPE ->
                            row.put(sourceAndColumn, Objects.toString(row1.getLong(columnName), ConstantController.NULL));
                        case ibd.table.prototype.column.Column.FLOAT_TYPE ->
                            row.put(sourceAndColumn, Objects.toString(row1.getFloat(columnName), ConstantController.NULL));
                        case ibd.table.prototype.column.Column.DOUBLE_TYPE ->
                            row.put(sourceAndColumn, Objects.toString(row1.getDouble(columnName), ConstantController.NULL));
                        case ibd.table.prototype.column.Column.BOOLEAN_TYPE ->
                            row.put(sourceAndColumn, Objects.toString(row1.getBoolean(columnName), ConstantController.NULL));
                        case ibd.table.prototype.column.Column.STRING_TYPE ->
                            row.put(sourceAndColumn, Objects.toString(row1.getString(columnName), ConstantController.NULL));
                        default ->
                            row.put(sourceAndColumn, Objects.toString(row1.getString(columnName), ConstantController.NULL));

                    }
                } else {
                    row.put(sourceAndColumn, ConstantController.NULL);
                }
                colIndex++;
            }
            rowIndex++;
        }

        return row;
    }
}
