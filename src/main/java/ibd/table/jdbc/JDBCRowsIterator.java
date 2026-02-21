package ibd.table.jdbc;

import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.query.fields.NullField;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import engine.exceptions.DataBaseException;

// Basic iterator for all records
class JDBCRowsIterator implements Iterator<LinkedDataRow> {
    private final JDBCTable table;
    private ResultSet resultSet;
    private PreparedStatement statement;
    private boolean hasNext;

    public JDBCRowsIterator(JDBCTable table) {
        this.table = table;
        try {
            String query = "SELECT * FROM " + table.getHeader().get("tablename");
            this.statement = table.connection.prepareStatement(query);
            this.resultSet = statement.executeQuery();
            this.hasNext = resultSet.next();
        } catch (SQLException e) {
            throw new DataBaseException("JDBCRowsIterator", e.getMessage());
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public LinkedDataRow next() {
        if (!hasNext) {
            throw new DataBaseException("JDBCRowsIterator", "No more elements");
        }

        try {
            // Convert current row to LinkedDataRow
            LinkedDataRow row = new LinkedDataRow(table.getPrototype(), false);
            for (var col : table.getPrototype().getColumns()) {
                String val = resultSet.getString(col.getName());
                boolean isNull = (val == null || val.compareToIgnoreCase("null") == 0 || val.isEmpty() || val.strip().isEmpty());

                if (isNull) {
                    continue;
                }

                switch (col.getType()) {
                    case Column.STRING_TYPE -> row.setString(col.getName(), val);
                    case Column.INTEGER_TYPE -> row.setInt(col.getName(), resultSet.getInt(col.getName()));
                    case Column.LONG_TYPE -> row.setLong(col.getName(), resultSet.getLong(col.getName()));
                    case Column.DOUBLE_TYPE -> row.setDouble(col.getName(), resultSet.getDouble(col.getName()));
                    case Column.FLOAT_TYPE -> row.setFloat(col.getName(), resultSet.getFloat(col.getName()));
                    case Column.BOOLEAN_TYPE -> row.setBoolean(col.getName(), resultSet.getBoolean(col.getName()));
                }
            }

            // Move to next row
            hasNext = resultSet.next();

            // Clean up if we've reached the end
            if (!hasNext) {
                try {
                    resultSet.close();
                    statement.close();
                } catch (SQLException e) {
                    // Ignore cleanup errors
                }
            }

            return row;

        } catch (SQLException e) {
            throw new DataBaseException("JDBCRowsIterator", e.getMessage());
        }
    }
}
