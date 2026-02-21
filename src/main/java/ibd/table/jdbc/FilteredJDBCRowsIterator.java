package ibd.table.jdbc;

import ibd.query.lookup.LookupFilter;
import ibd.table.prototype.LinkedDataRow;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import engine.exceptions.DataBaseException;

class FilteredJDBCRowsIterator implements Iterator<LinkedDataRow> {
    private final JDBCTable table;
    private final LookupFilter filter;
    private final Iterator<LinkedDataRow> baseIterator;
    private LinkedDataRow nextRow;

    public FilteredJDBCRowsIterator(JDBCTable table, LookupFilter filter) {
        this.table = table;
        this.filter = filter;
        this.baseIterator = new JDBCRowsIterator(table);
        findNext();
    }

    private void findNext() {
        nextRow = null;
        while (baseIterator.hasNext()) {
            LinkedDataRow row = baseIterator.next();
            if (filter.match(row)) {
                nextRow = row;
                break;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return nextRow != null;
    }

    @Override
    public LinkedDataRow next() {
        if (nextRow == null) {
            throw new DataBaseException("FilteredJDBCRowsIterator", "No more elements");
        }

        LinkedDataRow current = nextRow;
        findNext();
        return current;
    }
}
