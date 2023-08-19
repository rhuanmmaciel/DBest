package dbconnection;

import controller.MainController;
import database.TableCreator;
import entities.Column;
import entities.cells.TableCell;
import enums.ColumnDataType;

import java.sql.*;
import java.util.*;

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
            connection = DriverManager.getConnection(connectionURL, user, password);
        } catch (SQLException e) {
            connection = null;
        }
    }

    public boolean isValid()
    {
        return connection != null;
    }

    public boolean saveTables() throws SQLException {

        List<TableCell> tableCells = getTablesCell();

        for (TableCell tableCell: tableCells) {
            MainController.saveTable(tableCell);
        }

        return false;
    }

    protected List<TableCell> getTablesCell() throws SQLException {
        List<TableCell> tableCells = new ArrayList<>();
        ResultSet tablesCollection = connection.getMetaData().getTables(database, null, null, new String[] {"TABLE"});

        while(tablesCollection.next()) {
            String tableName = tablesCollection.getString("TABLE_NAME");

            // FIXME: Vulnerável a SQL Injection, mas talvez nesse caso não tenha risco? A conexão é do próprio usuário.
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `"+tableName+"`");
            ResultSet rs = stmt.executeQuery();
            List<Column> columns = getColumnsFromTable(rs, tableName);
            Map<Integer, Map<String, String>> rows = getRowsFromTable(rs, columns);

            TableCreator tableCreator = new TableCreator(tableName, columns, rows, false);
            tableCells.add(tableCreator.getTableCell());
        }

        return tableCells;
    }

    protected List<Column> getColumnsFromTable(ResultSet rs, String tableName) throws SQLException {
        List<Column> columns = new ArrayList<>();
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numColumns = rsMetaData.getColumnCount();
        ArrayList<String> pkColumns = getPkColumnsFromTable(tableName);
        for (int i = 1; i <= numColumns; i++) {
            ColumnDataType columnType = parseColumnTypeName(rsMetaData.getColumnTypeName(i));
            String columnName = rsMetaData.getColumnName(i);
            boolean isPk = pkColumns.contains(columnName);
            columns.add(new Column(columnName, tableName, columnType, isPk));
        }

        return columns;
    }


    private ArrayList<String> getPkColumnsFromTable(String tableName) throws SQLException {
        ArrayList<String> pkColumns = new ArrayList<>();
        ResultSet pkColumnRs = connection.getMetaData().getPrimaryKeys(null, null, tableName);
        while (pkColumnRs.next()) {
            pkColumns.add(pkColumnRs.getString("COLUMN_NAME"));
        }

        return pkColumns;
    }

    private ColumnDataType parseColumnTypeName(String columnTypeName)
    {
        return switch (columnTypeName) {
            case "INT", "INTEGER" -> ColumnDataType.INTEGER;
            case "BIGINT" -> ColumnDataType.LONG;
            case "FLOAT" -> ColumnDataType.FLOAT;
            case "DOUBLE" -> ColumnDataType.DOUBLE;
            case "CHAR", "VARCHAR", "TEXT" -> ColumnDataType.STRING;
            default -> ColumnDataType.NONE;
        };
    }

    protected Map<Integer, Map<String, String>> getRowsFromTable(ResultSet rs, List<Column> columns) throws SQLException {
        Map<Integer, Map<String, String>> rows = new LinkedHashMap<>();

        int rowIndex = 0;
        while (rs.next()) {
            Map<String, String> rowData = new HashMap<>();
            for (Column column : columns) {
                rowData.put(column.getName(), rs.getString(column.getName()));
            }
            rows.put(rowIndex, rowData);
            rowIndex++;
        }

        return rows;
    }

}
