package ibd.table.csv;

import engine.exceptions.DataBaseException;
import ibd.query.lookup.LookupFilter;
import ibd.table.ComparisonTypes;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.Header;
import ibd.table.prototype.LinkedDataRow;

import java.util.ArrayList;
import java.util.List;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.Column;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import sources.csv.CSVRecognizer;
import sources.csv.InvalidCsvException;

public class CSVTable extends Table {

    public CSVRecognizer recognizer;
    public char separator, stringDelimiter;
    public int beginIndex;
    
    private String name;


    //private static final String pkName = "__IDX__";
    
    private static Header prepareStuff(Header header){
//        for (Column c:header.getPrototype()) {
//            if(c.getName().compareTo(pkName)==0)return header;
//        }
//        LongColumn newCol = new LongColumn(pkName, 8, (short)(Column.PRIMARY_KEY|Column.IGNORE_COLUMN));
//        header.getPrototype().addColumn(newCol);
        return header;
    }

public CSVTable(Header header) {
        super(prepareStuff(header));
        this.beginIndex = Integer.parseInt(header.get("beginIndex"));
        this.separator = header.get("separator").charAt(0);
        this.stringDelimiter = header.get("delimiter").charAt(0);
        
        String path = header.getTablePath();

            Path filePath = Paths.get(path);
            // Get the file name
            this.name = filePath.getFileName().toString();
    }
    public CSVTable(Header header,char separator, char stringDelimiter, int beginIndex) {
        super(prepareStuff(header));
        header.set(Header.TABLE_TYPE,"CSVTable");
        header.set("separator",separator+"");
        header.set("delimiter",stringDelimiter+"");
        header.set("beginIndex",beginIndex+"");
        this.beginIndex = beginIndex;
        this.separator = separator;
        this.stringDelimiter = stringDelimiter;
    }

    @Override
    public void create(Prototype prototype, int pageSize) throws Exception {
    }

    /**
     * Open an existing table
     *
     * @throws Exception if the file does not exists or if the provided file is
     * not a valid table
     */
    @Override
    public void open() throws Exception {
        try {
            this.recognizer = new CSVRecognizer(header.getTablePath(), separator, stringDelimiter, beginIndex);
        } catch (InvalidCsvException e) {
            throw new DataBaseException("CSVTable->Constructor", e.getMessage());
        }
    }

    /**
     * Closes the table
     */
    @Override
    public void close() {
        this.recognizer = null;
    }

    /**
     * Flushes the table's content to disk
     *
     * @throws Exception
     */
    @Override
    public void flushDB() throws Exception {
    }

    @Override
    public LinkedDataRow addRecord(BasicDataRow dataRow, boolean unique) {
        throw new DataBaseException("CSVTable", "This type of table (CSVTable) is not writable");
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
        List<LinkedDataRow> rows = new ArrayList();
        Iterator<String[]> csvLines = recognizer.iterator();
        long pk = 0L;
        while (csvLines.hasNext()) {
            String[] data = csvLines.next();
            if (data == null) {
                return null;
            }
            pk++;

            String[] columns = recognizer.getColumnNames();
            LinkedDataRow dataRow = convertRow(columns, data, pk);

            rows.add(dataRow);
        }

        return rows;
    }

    //defining a value for the null columns to prevent bugs.
    //this should be fixed to allow nulls
    public LinkedDataRow convertRow(String[] columns, String[] data, long pk) {
        LinkedDataRow dataRow = new LinkedDataRow(prototype, false);
        for (Column c
                : prototype.getColumns()) {
//            if (c.getName().compareTo(pkName) == 0) {
//                dataRow.setLong(c.getName(), pk);
//                continue;
//            }
            for (int x = 0; x < columns.length; x++) {
                String columnName = columns[x];
                if (c.getName().compareToIgnoreCase(columnName) != 0) {
                    continue;
                }
                String val = data[x];
                boolean isNull = (val == null || val.compareToIgnoreCase("null") == 0 || val.isEmpty() || val.strip().isEmpty());
                //dataRow.setField(c.getName(),new NullField(c),c);

                switch (c.getType()) {
                    case Column.STRING_TYPE -> {
                        if (!isNull) 
                        {
                            dataRow.setString(c.getName(), val);
                        }
                        else dataRow.setString(c.getName(), "");
                    }
                    case Column.INTEGER_TYPE -> {
                        if (!isNull) {
                            dataRow.setInt(c.getName(), Integer.parseInt(val));
                        }
                        else dataRow.setInt(c.getName(),Integer.MIN_VALUE);
                    }
                    case Column.LONG_TYPE -> {
                        if (!isNull) {
                            dataRow.setLong(c.getName(), Long.parseLong(val));
                        }
                        else dataRow.setLong(c.getName(),Long.MIN_VALUE);
                    }
                    case Column.DOUBLE_TYPE -> {
                        if (!isNull) {
                            dataRow.setDouble(c.getName(), Double.parseDouble(val));
                        }
                        else dataRow.setDouble(c.getName(),Double.MIN_VALUE);
                    }
                    case Column.FLOAT_TYPE -> {
                        if (!isNull) {
                            dataRow.setFloat(c.getName(), Float.parseFloat(val));
                        }
                        else dataRow.setFloat(c.getName(),Float.MIN_VALUE);
                    }
                    case Column.BOOLEAN_TYPE -> {
                        if (!isNull) {
                            dataRow.setBoolean(c.getName(), Boolean.parseBoolean(val));
                        }
                        else dataRow.setBoolean(c.getName(), false);
                    }
                }

            }
        }
        return dataRow;
    }

    /**
     * Returns an iterator to access all rows from the table
     *
     * @return
     * @throws Exception
     */
    @Override
    public Iterator getAllRecordsIterator() throws Exception {
        return new AllCSVRowsIterator(this);
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
        return new FilteredCSVRowsIterator(this, filter);
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

    public Iterator getPKFilteredRecordsIterator(LinkedDataRow pkRow, LookupFilter rowFilter) throws Exception {
        return new KeyFilteredCSVRowsIterator1(this, pkRow, rowFilter);
    }

    @Override
    public String getName() {
        return getName();
    }

    @Override
    public String getHeaderName() {
        return header.getFileName();
    }
    @Override
    public Iterator getPKFilteredRecordsIterator(LinkedDataRow pkRow, LookupFilter rowFilter, int compType) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
