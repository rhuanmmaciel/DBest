package ibd.table.jdbc;

import database.jdbc.DynamicDriverManager;
import engine.exceptions.DataBaseException;
import enums.DatabaseType;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ibd.query.lookup.LookupFilter;
import ibd.table.ComparisonTypes;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.column.*;
import ibd.table.prototype.Header;
import ibd.table.Table;

public class JDBCTable extends Table {
    public Connection connection;
    private String name;

    public JDBCTable(Header header) {
        super(header);
        this.validateHeaderConnection(header);
        this.name = header.get("tablename");
    }
    
    public JDBCTable(Header header, String connectionUrl, String connectionUser, String connectionPassword) {
        super(header);
        this.header.set("connection-url", connectionUrl);
        this.header.set("connection-user", connectionUser);
        this.header.set("connection-password", connectionPassword);
        this.name = header.get("tablename");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getHeaderName() {
        return header.getFileName();
    }    @Override
    public void open() throws Exception {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                String connectionUrl = this.header.get("connection-url");
                String connectionUser = this.header.get("connection-user");
                String connectionPassword = this.header.get("connection-password");
                
                DatabaseType databaseType = detectDatabaseTypeFromUrl(connectionUrl);

                if (databaseType != null) {
                    if (!DynamicDriverManager.isDriverAvailable(databaseType)) {
                        throw new DataBaseException("JDBCTable", 
                            "Driver not available for " + databaseType.getDisplayName() + 
                            ". Please download the driver first.");
                    }
                }
                
                if (connectionUser != null && connectionPassword != null && 
                    !connectionUser.trim().isEmpty() && !connectionPassword.trim().isEmpty()) {
                    this.connection = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
                } else {
                    this.connection = DriverManager.getConnection(connectionUrl);
                }
            }
            this.setPrototype();
        } catch (SQLException e) {
            throw new DataBaseException("JDBCTable", e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DataBaseException("JDBCTable", e.getMessage());
        }
    }

    @Override
    public LinkedDataRow getRecord(BasicDataRow dataRow) throws Exception {
        LinkedDataRow linkedDataRow = dataRow.getLinkedDataRow(prototype);
        LinkedDataRow pkRow = prototype.createPKRow(linkedDataRow);
        return getRecord(pkRow);
    }

    @Override
    public LinkedDataRow getRecord(LinkedDataRow pkRow) throws Exception {
        StringBuilder query = new StringBuilder("SELECT * FROM " + header.get("tablename") + " WHERE ");
        List<String> pkColumns = prototype.getPKColumns();

        for (int i = 0; i < pkColumns.size(); i++) {
            Column col = prototype.getColumn(pkColumns.get(i));
            if (i > 0) query.append(" AND ");
            query.append(col).append(" = ?");
        }

        try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
            int paramIndex = 1;
            for (String colName : pkColumns) {
                Column col = prototype.getColumn(colName);
                setStatementParameter(ps, paramIndex++, col, pkRow.getValue(col.getName()));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return resultSetToLinkedDataRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<LinkedDataRow> getRecords(BasicDataRow rowData) throws Exception {
        LinkedDataRow linkedDataRow = rowData.getLinkedDataRow(prototype);
        LinkedDataRow pkRow = prototype.createPKRow(linkedDataRow);
        return getRecords(pkRow);
    }

    @Override
    public List<LinkedDataRow> getRecords(LinkedDataRow pkRow) throws Exception {
        List<LinkedDataRow> results = new ArrayList<>();
        Iterator<LinkedDataRow> it = getAllRecordsIterator();
        while (it.hasNext()) {
            LinkedDataRow row = it.next();
            if (row.compareTo(pkRow) == 0) {
                results.add(row);
            }
        }
        return results;
    }

    @Override
    public List<LinkedDataRow> getRecords(LinkedDataRow pkRow, LookupFilter rowFilter) throws Exception {
        List<LinkedDataRow> results = new ArrayList<>();
        for (LinkedDataRow row : getRecords(pkRow)) {
            if (rowFilter.match(row)) {
                results.add(row);
            }
        }
        return results;
    }

    @Override
    public List<LinkedDataRow> getRecords(String col, Comparable comparable, int comparisonType) throws Exception {
        StringBuilder query = new StringBuilder("SELECT * FROM " + header.get("tablename") + " WHERE " + col);

        switch (comparisonType) {
            case ComparisonTypes.EQUAL -> query.append(" = ?");
            case ComparisonTypes.GREATER_THAN -> query.append(" > ?");
            case ComparisonTypes.GREATER_EQUAL_THAN -> query.append(" >= ?");
            case ComparisonTypes.LOWER_THAN -> query.append(" < ?");
            case ComparisonTypes.LOWER_EQUAL_THAN-> query.append(" <= ?");
            case ComparisonTypes.DIFF -> query.append(" != ?");
            default -> throw new DataBaseException("JDBCTable", "Invalid comparison type");
        }

        List<LinkedDataRow> results = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
            Column column = prototype.getColumn(col);
            setStatementParameter(ps, 1, column, comparable);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(resultSetToLinkedDataRow(rs));
                }
            }
        }
        return results;
    }

    @Override
    public boolean contains(LinkedDataRow pkRow) {
        try {
            return getRecord(pkRow) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<LinkedDataRow> getAllRecords() throws Exception {
        List<LinkedDataRow> results = new ArrayList<>();
        String query = "SELECT * FROM " + header.get("tablename");

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(resultSetToLinkedDataRow(rs));
            }
        }
        return results;
    }

    @Override
    public Iterator getAllRecordsIterator() throws Exception {
        return new JDBCRowsIterator(this);
    }

    @Override
    public List<LinkedDataRow> getFilteredRecords(LookupFilter filter) throws Exception {
        List<LinkedDataRow> results = new ArrayList<>();
        Iterator<LinkedDataRow> it = getAllRecordsIterator();
        while (it.hasNext()) {
            LinkedDataRow row = it.next();
            if (filter.match(row)) {
                results.add(row);
            }
        }
        return results;
    }

    @Override
    public Iterator getFilteredRecordsIterator(LookupFilter filter) throws Exception {
        return new FilteredJDBCRowsIterator(this, filter);
    }

    @Override
    public Iterator getPKFilteredRecordsIterator(LinkedDataRow pkRow, LookupFilter rowFilter, int compType) throws Exception {
        return new FilteredJDBCRowsIterator(this, rowFilter);
        /**
         * TODO: Implement this iterator
         * return new PKFilteredJDBCRowsIterator(this, pkRow, rowFilter, compType);
         * */
    }

    @Override
    public void create(Prototype prototype, int pageSize) throws Exception {
        throw new DataBaseException("JDBCTable", "This type of table (JDBCTable) cannot be created programmatically");
    }

    @Override
    public void flushDB() throws Exception {
        // No implementation needed for JDBC table
    }

    @Override
    public int getRecordsAmount() throws Exception {
        String query = "SELECT COUNT(*) FROM " + header.get("tablename");
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    @Override
    public void printStats() throws Exception {
        System.out.println("Table: " + getName());
        System.out.println("Total records: " + getRecordsAmount());
    }

    @Override
    public Prototype getPrototype() {
        return this.prototype;
    }

    @Override
    public LinkedDataRow addRecord(BasicDataRow rowdata, boolean unique) {
        throw new DataBaseException("JDBCTable", "This type of table (JDBCTable) is not writable");
    }

    @Override
    public LinkedDataRow updateRecord(BasicDataRow rowdata) {
        throw new DataBaseException("JDBCTable", "This type of table (JDBCTable) is not writable");
    }

    @Override
    public LinkedDataRow updateRecord(LinkedDataRow rowdata) {
        throw new DataBaseException("JDBCTable", "This type of table (JDBCTable) is not writable");
    }

    @Override
    public LinkedDataRow removeRecord(BasicDataRow rowdata) {
        throw new DataBaseException("JDBCTable", "This type of table (JDBCTable) is not writable");
    }    private void validateHeaderConnection(Header header) {
        ArrayList<String> missingConnectionFields = new ArrayList<>();
        if (header.get("connection-url") == null) {
            missingConnectionFields.add("connection-url");
        }
        if (!missingConnectionFields.isEmpty()) {
            throw new DataBaseException("JDBCTable", "Header must contain: " + String.join(", ", missingConnectionFields));
        }
    } 

    private static DatabaseType detectDatabaseTypeFromUrl(String connectionUrl) {
        if (connectionUrl == null) {
            return null;
        }
        
        String url = connectionUrl.toLowerCase();
        
        if (url.startsWith("jdbc:mysql:")) {
            return DatabaseType.MYSQL;
        } else if (url.startsWith("jdbc:postgresql:")) {
            return DatabaseType.POSTGRESQL;
        } else if (url.startsWith("jdbc:oracle:")) {
            return DatabaseType.ORACLE;
        } else if (url.startsWith("jdbc:sqlite:")) {
            return DatabaseType.SQLITE;
        } else if (url.startsWith("jdbc:mariadb:")) {
            return DatabaseType.MARIADB;
        }
        
        return null;
    }

    protected void setPrototype() {
        Prototype pt = new Prototype();
        try {
            DatabaseMetaData metaData = this.connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, this.header.get("tablename"), null);
            ResultSet pkColumns = metaData.getPrimaryKeys(null, null, this.header.get("tablename"));
            ArrayList<String> pkColumnsNames = new ArrayList<>();
            ArrayList<String> addedColumnNames = new ArrayList<>();

            while (pkColumns.next()) {
                pkColumnsNames.add(pkColumns.getString("COLUMN_NAME"));
            }

            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                
                if (addedColumnNames.contains(columnName)) {
                    continue;
                }

                addedColumnNames.add(columnName);
                
                int columnType = Integer.parseInt(columns.getString("DATA_TYPE"));
                boolean isPk = pkColumnsNames.contains(columnName);

                Column newColumn = switch (columnType) {
                    case java.sql.Types.BIGINT -> new LongColumn(columnName, isPk);
                    case java.sql.Types.INTEGER, java.sql.Types.SMALLINT, java.sql.Types.TINYINT -> new IntegerColumn(columnName, isPk);
                    case java.sql.Types.REAL -> new FloatColumn(columnName);
                    case java.sql.Types.DOUBLE, java.sql.Types.FLOAT, java.sql.Types.NUMERIC, java.sql.Types.DECIMAL -> new DoubleColumn(columnName);
                    case java.sql.Types.BOOLEAN, java.sql.Types.BIT -> new BooleanColumn(columnName);
                    default -> new StringColumn(columnName); // Default to string for VARCHAR, CHAR, TEXT, etc.
                };

                pt.addColumn(newColumn);
            }

            this.prototype = pt;
        } catch (SQLException e) {
            throw new DataBaseException("JDBCTable", e.getMessage());
        }
    }

    private LinkedDataRow resultSetToLinkedDataRow(ResultSet rs) throws SQLException {
        LinkedDataRow row = new LinkedDataRow(prototype, false);
        for (Column col : prototype.getColumns()) {
            String val = rs.getString(col.getName());
            boolean isNull = (val == null || val.compareToIgnoreCase("null") == 0 || val.isEmpty() || val.strip().isEmpty());

            if (isNull) {
                continue;
            }

            switch (col.getType()) {
                case Column.STRING_TYPE -> row.setString(col.getName(), val);
                case Column.INTEGER_TYPE -> row.setInt(col.getName(), rs.getInt(col.getName()));
                case Column.LONG_TYPE -> row.setLong(col.getName(), rs.getLong(col.getName()));
                case Column.DOUBLE_TYPE -> row.setDouble(col.getName(), rs.getDouble(col.getName()));
                case Column.FLOAT_TYPE -> row.setFloat(col.getName(), rs.getFloat(col.getName()));
                case Column.BOOLEAN_TYPE -> row.setBoolean(col.getName(), rs.getBoolean(col.getName()));
            }
        }
        return row;
    }

    private void setStatementParameter(PreparedStatement ps, int index, Column col, Object value) throws SQLException {
        if (value == null) {
            ps.setNull(index, java.sql.Types.NULL);
            return;
        }

        switch (col.getType()) {
            case Column.STRING_TYPE -> ps.setString(index, value.toString());
            case Column.INTEGER_TYPE -> ps.setInt(index, (Integer) value);
            case Column.LONG_TYPE -> ps.setLong(index, (Long) value);
            case Column.DOUBLE_TYPE -> ps.setDouble(index, (Double) value);
            case Column.FLOAT_TYPE -> ps.setFloat(index, (Float) value);
            case Column.BOOLEAN_TYPE -> ps.setBoolean(index, (Boolean) value);
        }
    }
}
