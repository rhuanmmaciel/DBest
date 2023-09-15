package database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import controllers.MainController;
import entities.Column;
import entities.cells.TableCell;
import enums.ColumnDataType;

/**
 * TODO: Criar classe superior genérica (abstrata)
 */
public class MySQLConnection {

    private Connection connection;

    private final String database;

    public MySQLConnection(String host, String port, String database, String user, String password) {
        this.database = database;

        try {
            String connectionURL = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
            this.connection = DriverManager.getConnection(connectionURL, user, password);
        } catch (SQLException exception) {
            this.connection = null;
        }
    }

    public boolean isValid() {
        return this.connection != null;
    }

    public boolean saveTables(List<String> tables) throws SQLException {
        List<TableCell> tableCells = this.getTableCells(tables);

        for (TableCell tableCell : tableCells) {
            MainController.saveTable(tableCell);
        }

        return false;
    }

    public List<String> getTableNames() throws SQLException {
        ArrayList<String> tableNames = new ArrayList<>();

        ResultSet tablesCollection = this.connection
            .getMetaData()
            .getTables(this.database, null, null, new String[]{"TABLE"});

        while (tablesCollection.next()) {
            tableNames.add(tablesCollection.getString("TABLE_NAME"));
        }

        return tableNames;
    }

    protected List<TableCell> getTableCells(List<String> tableNames) throws SQLException {
        List<TableCell> tableCells = new ArrayList<>();

        for (String tableName : tableNames) {
            // FIXME: Vulnerável a SQL Injection, mas talvez nesse caso não tenha risco? A conexão é do próprio usuário.
            String sql = String.format("SELECT * FROM \"%s\"", tableName);

            try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                List<Column> columns = this.getTableColumns(resultSet, tableName);
                Map<Integer, Map<String, String>> rows = this.getTableRows(resultSet, columns);

                // TableCreator tableCreator = new TableCreator(tableName, columns, rows, false);
                // tableCells.add(tableCreator.getTableCell());
            }
        }

        return tableCells;
    }

    protected List<Column> getTableColumns(ResultSet resultSet, String tableName) throws SQLException {
        List<Column> columns = new ArrayList<>();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        ArrayList<String> primaryKeyColumns = this.getTablePrimaryKeyColumns(tableName);

        for (int i = 1; i <= columnCount; i++) {
            ColumnDataType columnType = this.parseColumnTypeName(resultSetMetaData.getColumnTypeName(i));
            String columnName = resultSetMetaData.getColumnName(i);
            boolean isPrimaryKey = primaryKeyColumns.contains(columnName);
            columns.add(new Column(columnName, tableName, columnType, isPrimaryKey));
        }

        return columns;
    }

    private ArrayList<String> getTablePrimaryKeyColumns(String tableName) throws SQLException {
        ArrayList<String> primaryKeyColumns = new ArrayList<>();
        ResultSet resultSet = this.connection.getMetaData().getPrimaryKeys(null, null, tableName);

        while (resultSet.next()) {
            primaryKeyColumns.add(resultSet.getString("COLUMN_NAME"));
        }

        return primaryKeyColumns;
    }

    private ColumnDataType parseColumnTypeName(String columnTypeName) {
        return switch (columnTypeName) {
            case "INT", "INTEGER" -> ColumnDataType.INTEGER;
            case "BIGINT" -> ColumnDataType.LONG;
            case "FLOAT" -> ColumnDataType.FLOAT;
            case "DOUBLE" -> ColumnDataType.DOUBLE;
            case "CHAR", "VARCHAR", "TEXT" -> ColumnDataType.STRING;
            default -> ColumnDataType.NONE;
        };
    }

    protected Map<Integer, Map<String, String>> getTableRows(ResultSet resultSet, List<Column> columns) throws SQLException {
        Map<Integer, Map<String, String>> rows = new LinkedHashMap<>();

        int rowIndex = 0;

        while (resultSet.next()) {
            Map<String, String> rowData = new HashMap<>();

            for (Column column : columns) {
                String value = resultSet.getString(column.getName());

                if (resultSet.wasNull()) {
                    value = "null";
                }

                rowData.put(column.getName(), value);
            }

            rows.put(rowIndex, rowData);
            rowIndex++;
        }

        return rows;
    }
}
