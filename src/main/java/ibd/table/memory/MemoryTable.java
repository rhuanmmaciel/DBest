/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.memory;

import engine.exceptions.DataBaseException;
import ibd.index.btree.Key;
import ibd.index.btree.Value;
import ibd.index.btree.table.BinaryKey;
import ibd.index.btree.table.BinaryValue;
import ibd.query.lookup.LookupFilter;
import ibd.table.ComparisonTypes;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.Header;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MemoryTable extends Table {

    String name;
    Prototype prototype;
    List<LinkedDataRow> rows = new ArrayList();

    public MemoryTable(Header header) throws Exception {

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

        rows.add(linkedDataRow);

        return linkedDataRow;
    }

    @Override
    public LinkedDataRow updateRecord(BasicDataRow dataRow) {
        throw new DataBaseException("CSVTable", "This type of table (CSVTable) is not writable");
    }

    @Override
    public LinkedDataRow updateRecord(LinkedDataRow linkedDataRow) {
        throw new DataBaseException("CSVTable", "This type of table (CSVTable) is not writable");
    }

    @Override
    public LinkedDataRow removeRecord(BasicDataRow dataRow) {
        throw new DataBaseException("CSVTable", "This type of table (CSVTable) is not writable");
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
        return rows;
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
    public LinkedDataRow getRecord(BasicDataRow dataRow) throws Exception {
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
    public LinkedDataRow getRecord(LinkedDataRow pkRow) throws Exception {

        Iterator<LinkedDataRow> it = getAllRecordsIterator();
        while (it.hasNext()){
            LinkedDataRow dataRow = it.next();
            if (dataRow.compareTo(pkRow)==0) return dataRow;
        }
        
        return null;
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
    public List<LinkedDataRow> getRecords(BasicDataRow dataRow) throws Exception{

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
    public List<LinkedDataRow> getRecords(LinkedDataRow pkRow) throws Exception{

        List<LinkedDataRow> rows = new ArrayList();
        Iterator<LinkedDataRow> it = getAllRecordsIterator();
        while (it.hasNext()){
            LinkedDataRow dataRow = it.next();
            if (dataRow.compareTo(pkRow)==0) rows.add(dataRow);
        }
            
        return rows;

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
    public List<LinkedDataRow> getRecords(LinkedDataRow pkRow, LookupFilter rowFilter) throws Exception{

        List<LinkedDataRow> rows = new ArrayList();
        Iterator<LinkedDataRow> it = getAllRecordsIterator();
        while (it.hasNext()){
            LinkedDataRow dataRow = it.next();
            if (dataRow.compareTo(pkRow)==0) {
                if (rowFilter.match(dataRow)) {
                rows.add(dataRow);
            }
            }
        }
            
        return rows;


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

        Iterator<LinkedDataRow> it;
        try {
            it = getAllRecordsIterator();
        } catch (Exception ex) {
            return false;
        }
        while (it.hasNext()){
            LinkedDataRow dataRow = it.next();
            if (dataRow.compareTo(pkRow)==0) return true;
        }
            
    return false;
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
        Iterator<LinkedDataRow> it = getAllRecordsIterator();
        while (it.hasNext()){
            LinkedDataRow dataRow = it.next();
            if (ComparisonTypes.match(dataRow.getValue(col), comparable, comparisonType)) {
                rows.add(dataRow);
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
        
        List<LinkedDataRow> rows = new ArrayList();
        Iterator<LinkedDataRow> it = getAllRecordsIterator();
        while (it.hasNext()){
            LinkedDataRow dataRow = it.next();
            if (filter.match(dataRow)) {
                rows.add(dataRow);
            }
        }
            
        return rows;
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
    
    @Override
    public Iterator getPKFilteredRecordsIterator(LinkedDataRow pkRow, LookupFilter rowFilter, int compType) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
