/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.memory;

import ibd.index.btree.Key;
import ibd.index.btree.Value;
import ibd.index.btree.table.BinaryKey;
import ibd.index.btree.table.BinaryValue;
import ibd.query.lookup.LookupFilter;
import ibd.table.ComparisonTypes;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.Header;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class IndexedMemoryTable extends Table {

    String name;
    Prototype prototype;
    TreeMap<Key, Value> rows = new TreeMap();

    public IndexedMemoryTable(Header header) throws Exception {

        super(header);
        this.name = header.get(Header.TABLE_NAME);
        create(header.getPrototype(), 0);
        prototype.validateColumns();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHeaderName() {
        return header.getFileName();
    }

    @Override
    public final void create(Prototype prototype, int pageSize) throws Exception {
        this.prototype = prototype;
    }

    @Override
    public void open() throws Exception {

    }

    public Key createKey() {
        return new BinaryKey(prototype);
    }

    public Value createValue() {
        return new BinaryValue(prototype);
    }

    /**
     * Closes the table
     */
    @Override
    public void close() {
    }

    /**
     * Flushes the table's content to disk
     *
     * @throws Exception
     */
    @Override
    public void flushDB() throws Exception {
    }

    /**
     * Adds a row to the table
     *
     * @param dataRow: the row to be added
     * @param unique
     * @return the added row or null if no row was added
     */
    @Override
    public LinkedDataRow addRecord(BasicDataRow dataRow, boolean unique) {
        LinkedDataRow linkedDataRow = dataRow.getLinkedDataRow(prototype);

        DataRow pkRow = prototype.createPKRow(linkedDataRow);
        Key key = createKey();
        key.setKeys(new DataRow[]{pkRow});

        //sets the value to be a byte array corresponding to the row's content
        Value value = createValue();
        byte bytes[] = prototype.convertToArray(linkedDataRow);
        value.set(0, bytes);
        ((BinaryValue) value).rowData = linkedDataRow;

        rows.put(key, value);
        return linkedDataRow;
    }

    /**
     * Updates a row from the table
     *
     * @param dataRow: the row to be updated
     * @return the updated row or null if no row was updated
     */
    @Override
    public LinkedDataRow updateRecord(BasicDataRow dataRow) {

        LinkedDataRow linkedDataRow = dataRow.getLinkedDataRow(prototype);
        return updateRecord(linkedDataRow);
    }

    /**
     * Updates a row in the table
     *
     * @param linkedDataRow: the row to be updated
     * @return the updated row or null if no row was updated
     */
    @Override
    public LinkedDataRow updateRecord(LinkedDataRow linkedDataRow) {

        DataRow pkRow = prototype.createPKRow(linkedDataRow);
        Key key = createKey();
        key.setKeys(new DataRow[]{pkRow});

        //sets the value to be a byte array corresponding to the row's content
        Value value = createValue();
        byte bytes[] = prototype.convertToArray(linkedDataRow);
        value.set(0, bytes);
        ((BinaryValue) value).rowData = linkedDataRow;

        rows.put(key, value);

        return linkedDataRow;
    }

    /**
     * Removes a row from the table
     *
     * @param dataRow the row to be removed
     * @return the removed row or null if no row was removed
     */
    @Override
    public LinkedDataRow removeRecord(BasicDataRow dataRow) {
        LinkedDataRow linkedDataRow = dataRow.getLinkedDataRow(prototype);

        DataRow pkRow = prototype.createPKRow(linkedDataRow);
        Key key = createKey();
        key.setKeys(new DataRow[]{pkRow});

        //tries to remove the row from the b-tree
        Value value = rows.remove(key);
        if (value == null) {
            return null;
        }

        //converts the rows's byte array stored in the b-tree back to a row format
        byte bytes_[] = (byte[]) value.get(0);
        LinkedDataRow dataRow1 = prototype.convertBinaryToRowData(bytes_);

        //this.tree.flush();
        return dataRow1;
    }

    private List<LinkedDataRow> partialSearch(Key key) {
        List<LinkedDataRow> matchedRows = new ArrayList();
        Collection<Entry<Key, Value>> entries = rows.entrySet();
        for (Entry<Key, Value> entry : entries) {
            if (key.partialMatch(entry.getKey())) {
                BinaryValue v = (BinaryValue) entry.getValue();
                matchedRows.add(v.rowData);
            }

        }
        return matchedRows;
    }

    /**
     * Returns all rows from the table use getAllRecordsIterator if you want the
     * memory friendly version
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<LinkedDataRow> getAllRecords() throws Exception {
        //returns all b-trees leaf entries
        List<LinkedDataRow> allRows = new ArrayList();
        Collection<Value> values = rows.values();
        for (Value value : values) {
            BinaryValue v = (BinaryValue) value;
            LinkedDataRow rowData = v.rowData;
            allRows.add(rowData);
        }
        return allRows;
    }

    /**
     * Returns an iterator to access all rows from the table
     *
     * @return
     * @throws Exception
     */
    @Override
    public Iterator getAllRecordsIterator() throws Exception {
        return getAllRecords().iterator();
    }

    /**
     * Returns a row that satisfies a primary key search condition.
     *
     * @param dataRow the unlinked row whose primary key is used to do the
     * search
     * @return the row that satisfy the search condition or null if no row
     * satisfies the condition.
     */
    @Override
    public LinkedDataRow getRecord(BasicDataRow dataRow) {
        //converts the row to one whose columns are linked to a prototype
        LinkedDataRow linkedDataRow = dataRow.getLinkedDataRow(prototype);

        //creates a simplified row containing only the primary key columns
        LinkedDataRow pkRow = prototype.createPKRow(linkedDataRow);

        //return the stored row that contains the given primary key columns
        return getRecord(pkRow);

    }

    /**
     * Returns a row that satisfies a primary key search condition.
     *
     * @param pkRow the linked row whose primary key is used to do the search
     * @return the row that satisfy the search condition or null if no row
     * satisfies the condition.
     */
    @Override
    public LinkedDataRow getRecord(LinkedDataRow pkRow) {

        Key key = createKey();
        key.setKeys(new DataRow[]{pkRow});

        //performs the sarch over the b-tree
        Value v = rows.get(key);
        if (v == null) {
            return null;
        }

        //gets the stored row
        LinkedDataRow dataRow = ((BinaryValue) v).rowData;

        return dataRow;
    }

    /**
     * Returns a list of rows that satisfies a primary key search condition.
     * This function can return more than one row if the search uses a prefix of
     * the primary key instead of the whole primary key
     *
     * @param dataRow the unlinked row whose primary key is used to do the
     * search
     * @return the list of rows that satisfy the search condition
     */
    @Override
    public List<LinkedDataRow> getRecords(BasicDataRow dataRow) {

        //converts the row to one whose columns are linked to a prototype
        LinkedDataRow linkedDataRow = dataRow.getLinkedDataRow(prototype);

        //creates a simplified row containing only the primary key columns
        LinkedDataRow pkRow = prototype.createPKRow(linkedDataRow);

        //return the stored row that contains the given primary key columns
        return getRecords(pkRow);

    }

    /**
     * Returns a list of rows that satisfies a primary key search condition.
     * This function can return more than one row if the search uses a prefix of
     * the primary key instead of the whole primary key
     *
     * @param pkRow the linked row whose primary key is used to do the search
     * @return the list of rows that satisfy the search condition
     */
    @Override
    public List<LinkedDataRow> getRecords(LinkedDataRow pkRow) {

        Key key = createKey();
        key.setKeys(new DataRow[]{pkRow});

        return partialSearch(key);

    }

    /**
     * Returns a list of rows that satisfies a primary key search condition and
     * other arbitrary search conditions. This function can return more than one
     * row if the search uses a prefix of the primary key instead of the whole
     * primary key
     *
     * @param pkRow the linked row whose primary key is used to define the pk
     * search condition
     * @param rowFilter the arbitrary search conditions
     * @return the list of rows that satisfy all search conditions
     */
    @Override
    public List<LinkedDataRow> getRecords(LinkedDataRow pkRow, LookupFilter rowFilter) {

        Key key = createKey();
        key.setKeys(new DataRow[]{pkRow});

        //performs the sarch over the b-tree
        List<LinkedDataRow> matchedRows = partialSearch(key);
        List<LinkedDataRow> filteredMatchedRows = new ArrayList();
        for (LinkedDataRow r : matchedRows) {
            if (rowFilter.match(r)) {
                filteredMatchedRows.add(r);
            }
        }
        return filteredMatchedRows;

    }

    /**
     * Checks if there is at least one row that satisfies a primary key search
     * condition. This is cheaper than retrieving the rows, since no data needs
     * to be transfered
     *
     * @param pkRow the linked row whose primary key is used to do the search
     * @return
     */
    @Override
    public boolean contains(LinkedDataRow pkRow) {

        Key key = createKey();
        key.setKeys(new DataRow[]{pkRow});

        return (rows.get(key) != null);

    }

    /**
     * Returns all rows that satisfy a single column comparison. This function
     * only exists for backward compatibility. Uue the more generic
     * getFilteredRecords() or getFilteredRecordsIterator() functions.
     *
     * @param col: the column name
     * @param comparable: the comparable value to be compared against
     * @param comparisonType: the comparison type (<,>,...)
     * @return all rows that satisfy a single column comparison
     * @throws Exception
     */
    @Override
    public List<LinkedDataRow> getRecords(String col, Comparable comparable, int comparisonType) throws Exception {
        List<LinkedDataRow> rows = new ArrayList();
        List<LinkedDataRow> allRows = getAllRecords();
        for (LinkedDataRow row : allRows) {
            if (ComparisonTypes.match(row.getValue(col), comparable, comparisonType)) {
                rows.add(row);
            }
        }
        return rows;

    }

    /**
     * Returns all rows from the table that satisfy a filter. Use
     * getFilteredRecordsIterator() if you want the memory friendly version
     *
     * @param filter the lookup filter to be satisfied
     * @return
     * @throws Exception
     */
    @Override
    public List<LinkedDataRow> getFilteredRecords(LookupFilter filter) throws Exception {
        //returns all b-trees leaf entries
        List<LinkedDataRow> allRows = getAllRecords();
        List<LinkedDataRow> filteredRows = new ArrayList();
        //traverses all entries
        for (LinkedDataRow r : allRows) {
            if (filter.match(r)) {
                filteredRows.add(r);
            }
        }
        return filteredRows;
    }

    /**
     * Return an iterator to access the rows that satisfy a filter
     *
     * @param filter the lookup filter to be satisfied
     * @return
     * @throws Exception
     */
    @Override
    public Iterator getFilteredRecordsIterator(LookupFilter filter) throws Exception {
        List<LinkedDataRow> list = getFilteredRecords(filter);
        return list.iterator();
    }

    /**
     * Return an iterator to access the rows that satisfy a filter
     *
     * @param filter the lookup filter to be satisfied
     * @return
     * @throws Exception
     */
    @Override
    public Iterator getPKFilteredRecordsIterator(LinkedDataRow pkRow, LookupFilter rowFilter, int compType) throws Exception {
        Key key = createKey();
        key.setKeys(new DataRow[]{pkRow});

        List<LinkedDataRow> matchedRows = new ArrayList();
        Collection<Entry<Key, Value>> entries = rows.entrySet();
        boolean canAdd = false;
        for (Entry<Key, Value> entry : entries) {
            LinkedDataRow row = ((BinaryValue) entry.getValue()).rowData;
            if (compType == ComparisonTypes.EQUAL) {
                if (key.partialMatch(entry.getKey()) && rowFilter.match(row)) {
                    matchedRows.add(row);
                }
            } else if (compType == ComparisonTypes.GREATER_EQUAL_THAN) {
                if (key.partialMatch(entry.getKey()) || canAdd) {
                    canAdd = true;
                    if (rowFilter.match(row)) {
                        matchedRows.add(row);
                    }

                }
            } else if (compType == ComparisonTypes.GREATER_THAN) {
                if (key.partialMatch(entry.getKey())) {
                    canAdd = true;

                } else if (canAdd) {
                    if (rowFilter.match(row)) {
                        matchedRows.add(row);
                    }

                }
            }
        }

        return matchedRows.iterator();

    }

    /**
     * Prints statistics concerning the table's usage
     *
     * @throws Exception
     */
    @Override
    public void printStats() throws Exception {
        //System.out.println("largest used page id:"+p.getNextPageID());
    }

    /**
     * Returns the number of rows stored in the table
     *
     * @return the number of rows stored in the table
     * @throws Exception
     */
    @Override
    public int getRecordsAmount() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Prototype getPrototype() {
        return prototype;
    }

}
