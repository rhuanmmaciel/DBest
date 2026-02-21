package ibd.table.btree;

import ibd.index.btree.table.BPlusTreeFileTable;
import ibd.index.btree.DictionaryPair;
import ibd.index.btree.Key;
import ibd.index.btree.Value;
import ibd.index.btree.table.BinaryValue;
import ibd.persistent.PersistentPageFile;
import ibd.persistent.cache.Cache;
import ibd.query.lookup.LookupFilter;
import ibd.table.ComparisonTypes;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.Header;
import ibd.table.prototype.LinkedDataRow;

import java.util.ArrayList;
import java.util.List;
import ibd.table.prototype.Prototype;

public class BTreeTable extends Table {

    //the b-tree that stores the table's content
    BPlusTreeFileTable tree = null;

    //the optional cache for the tree's pages
    protected Cache cache = null;

    //the location of the table
    String folder;

    //the file name uses to store the table's contents
    String name;

    //the size of the cache used to store table pages
    int cacheSize;

    //prevents the file from being reopened
    boolean loaded = false;

    /**
     *
     * @param folder: the location of the table
     * @param name: the file name uses to store the table's contents
     * @param cacheSize: the size of the cache used to store table pages.
     * @throws Exception
     */
    public BTreeTable(Header header, String folder, String fileName, int cacheSize) throws Exception {

        super(header);
        if (header != null) {
            String path = header.getTablePath();

            Path filePath = Paths.get(path);
            // Get the directory path (parent)
            Path folder_ = filePath.getParent();
            this.folder = ".";

            if (folder_ != null) {
                this.folder = folder_.toString();
            }

            // Get the file name
            this.name = filePath.getFileName().toString();
            //header.set(Header.FILE_PATH, path);
            //header.set(Header.TABLE_TYPE,"SimpleTable");
        } else {
            this.folder = folder;
            this.name = fileName;
        }

        this.cacheSize = cacheSize;

    }

    /**
     *
     * @param folder: the location of the table
     * @param name: the file name uses to store the table's contents
     * @param cacheSize: the size of the cache used to store table pages.
     * @throws Exception
     */
    public BTreeTable(String path, int cacheSize) throws Exception {

        
        super(null);
        
        this.header = new Header(path);

        Path filePath = Paths.get(path);
        // Get the directory path (parent)
        Path folder_ = filePath.getParent();
        this.folder = ".";

        if (folder_ != null) {
            this.folder = folder_.toString();
        }

        // Get the file name
        this.name = filePath.getFileName().toString();
        //header.set(Header.FILE_PATH, path);
        //header.set(Header.TABLE_TYPE,"SimpleTable");

        this.cacheSize = cacheSize;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHeaderName() {
        return header.getFileName();
    }

    /**
     * Creates a table
     *
     * @param prototype: the schema of the table
     * @param pageSize: the size of the file pages
     * @throws Exception
     */
    @Override
    public void create(Prototype prototype, int pageSize) throws Exception {
        if (loaded) {
            return;
        }
        //defines the paged file that the BTree will use
        PersistentPageFile p = new PersistentPageFile(pageSize, Paths.get(folder + File.separator + name), true);
        open(p, prototype);
    }

    /**
     * Open an existing table
     *
     * @throws Exception if the file does not exists or if the provided file is
     * not a valid table
     */
    @Override
    public void open() throws Exception {
        if (loaded) {
            //resources mey be implicititly closed by jvm when a thread is cancelled, so we need to assure that file handles remain valid
            tree.reopen();
            return;
        }
        

        Path fileName = Paths.get(folder + File.separator + name);
        boolean exists = Files.exists(fileName);
        if (!exists) {
            throw new Exception("The file " + fileName + " does not exists");
        }
        //defines the paged file thatthe BTree will use
        PersistentPageFile p = new PersistentPageFile(-1, fileName, false);
        open(p, null);
    }

    private void open(PersistentPageFile p, Prototype prototype) throws Exception {

        if (cacheSize > 0) {
            //LRUCache lru = new LRUCache(5000000, p);
            //defines the buffer management to be used, if any.
            cache = new ibd.persistent.cache.LRUCache(cacheSize);
            //cache = new MidPointCache(5000000);
            cache.setPageFile(p);
        }

        //cache = null;
        //creates a B+ Tree instance using the defined buffer manager, if any
        if (cache != null) {
            tree = new BPlusTreeFileTable(cache, prototype);
        } else {
            tree = new BPlusTreeFileTable(p, prototype);
        }
        tree.open();
        
        loaded = true;
    }

    /**
     * Closes the table
     */
    @Override
    public void close() {
        tree.flush();
        tree.close();
    }

    /**
     * Flushes the table's content to disk
     *
     * @throws Exception
     */
    @Override
    public void flushDB() throws Exception {
        tree.flush();
        if (cache != null) {
            cache.flush();
        }
    }

    /**
     * Adds a row to the table
     *
     * @param dataRow: the row to be added
     * @param unique: rows with duplicate primary keys are not allowed
     * @return the added row or null if no row was added
     */
    @Override
    public LinkedDataRow addRecord(BasicDataRow dataRow, boolean unique) {
        LinkedDataRow linkedDataRow = dataRow.getLinkedDataRow(tree.prototype);

        DataRow pkRow = tree.prototype.createPKRow(linkedDataRow);
        Key key = tree.createKey();
        key.setKeys(new DataRow[]{pkRow});

        //sets the value to be a byte array corresponding to the row's content
        Value value = tree.createValue();
        byte bytes[] = tree.prototype.convertToArray(linkedDataRow);
        value.set(0, bytes);
        ((BinaryValue) value).rowData = linkedDataRow;

        //tries to insert the row into the b-tree
        boolean ok = tree.insert(key, value, unique);
        if (!ok) {
            return null;
        }

        //this.tree.flush();
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

        LinkedDataRow linkedDataRow = dataRow.getLinkedDataRow(tree.prototype);
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

        DataRow pkRow = tree.prototype.createPKRow(linkedDataRow);
        Key key = tree.createKey();
        key.setKeys(new DataRow[]{pkRow});

        //sets the value to be a byte array corresponding to the row's content
        Value value = tree.createValue();
        byte bytes[] = tree.prototype.convertToArray(linkedDataRow);
        value.set(0, bytes);
        ((BinaryValue) value).rowData = linkedDataRow;

        //tries to update the row in the b-tree
        Value v = tree.update(key, value);
        if (v == null) {
            return null;
        }

        //this.tree.flush();
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
        LinkedDataRow linkedDataRow = dataRow.getLinkedDataRow(tree.prototype);

        DataRow pkRow = tree.prototype.createPKRow(linkedDataRow);
        Key key = tree.createKey();
        key.setKeys(new DataRow[]{pkRow});

        //tries to remove the row from the b-tree
        Value value = tree.delete(key);
        if (value == null) {
            return null;
        }

        //converts the rows's byte array stored in the b-tree back to a row format
        byte bytes_[] = (byte[]) value.get(0);
        LinkedDataRow dataRow1 = tree.prototype.convertBinaryToRowData(bytes_);

        //this.tree.flush();
        return dataRow1;
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
        List<DictionaryPair> values = tree.searchAll();
        List<LinkedDataRow> rows = new ArrayList();
        //traverses all entries
        for (DictionaryPair value : values) {
            //for each entry, converts the rows's byte array stored in the b-tree back to a row format
//            Value v = value.getValue();
//            byte bytes_[] = (byte[]) v.get(0);
//            LinkedDataRow rowData = tree.prototype.convertBinaryToRowData(bytes_);
            BinaryValue v = (BinaryValue) value.getValue();
            LinkedDataRow rowData = v.rowData;
            rows.add(rowData);
        }
        return rows;
    }

    /**
     * Returns an iterator to access all rows from the table
     *
     * @return
     * @throws Exception
     */
    @Override
    public AllRowsIterator getAllRecordsIterator() throws Exception {
        return new AllRowsIterator(tree);
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
        LinkedDataRow linkedDataRow = dataRow.getLinkedDataRow(tree.prototype);

        //creates a simplified row containing only the primary key columns
        LinkedDataRow pkRow = tree.prototype.createPKRow(linkedDataRow);

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

        Key key = tree.createKey();
        key.setKeys(new DataRow[]{pkRow});

        //performs the sarch over the b-tree
        Value v = tree.search(key);
        if (v == null) {
            return null;
        }

        //converts the rows's byte array stored in the b-tree back to a row format
        //byte bytes_[] = (byte[]) v.get(0);
        //LinkedDataRow dataRow = tree.prototype.convertBinaryToRowData(bytes_);
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
        LinkedDataRow linkedDataRow = dataRow.getLinkedDataRow(tree.prototype);

        //creates a simplified row containing only the primary key columns
        LinkedDataRow pkRow = tree.prototype.createPKRow(linkedDataRow);

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

        Key key = tree.createKey();
        key.setKeys(new DataRow[]{pkRow});

        //performs the sarch over the b-tree
        List<Value> values = tree.partialSearch(key);
        List<LinkedDataRow> rows = new ArrayList();
        for (Value value : values) {
            //converts the rows's byte array stored in the b-tree back to a row format
            //byte bytes_[] = (byte[]) value.get(0);
            //LinkedDataRow dataRow = tree.prototype.convertBinaryToRowData(bytes_);
            LinkedDataRow dataRow = ((BinaryValue) value).rowData;
            rows.add(dataRow);
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
    public List<LinkedDataRow> getRecords(LinkedDataRow pkRow, LookupFilter rowFilter) {

        Key key = tree.createKey();
        key.setKeys(new DataRow[]{pkRow});

        //performs the sarch over the b-tree
        List<Value> values = tree.partialSearch(key);
        List<LinkedDataRow> rows = new ArrayList();
        for (Value value : values) {
            //converts the rows's byte array stored in the b-tree back to a row format
            //byte bytes_[] = (byte[]) value.get(0);
            //LinkedDataRow dataRow = tree.prototype.convertBinaryToRowData(bytes_);
            LinkedDataRow dataRow = ((BinaryValue) value).rowData;
            if (rowFilter.match(dataRow)) {
                rows.add(dataRow);
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

        Key key = tree.createKey();
        key.setKeys(new DataRow[]{pkRow});

        return tree.contains(key);

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
        List<DictionaryPair> values = tree.searchAll();
        List<LinkedDataRow> rows = new ArrayList();
        //traverses all entries
        for (DictionaryPair value : values) {
            //for each entry, converts the rows's byte array stored in the b-tree back to a row format
//            Value v = value.getValue();
//            byte bytes_[] = (byte[]) v.get(0);
//            LinkedDataRow rowData = tree.prototype.convertBinaryToRowData(bytes_);
            BinaryValue v = (BinaryValue) value.getValue();
            LinkedDataRow dataRow = v.rowData;
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
    public FilteredRowsIterator getFilteredRecordsIterator(LookupFilter filter) throws Exception {
        return new FilteredRowsIterator(tree, filter);
    }

    /**
     * Return an iterator to access the rows that satisfy a filter
     *
     * @param filter the lookup filter to be satisfied
     * @return
     * @throws Exception
     */
    @Override
    public RowsIterator getPKFilteredRecordsIterator(LinkedDataRow pkRow, LookupFilter rowFilter, int compType) throws Exception {
        Key key = tree.createKey();
        key.setKeys(new DataRow[]{pkRow});
        return new KeyFilteredRowsIterator(tree, key, rowFilter, compType);
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
        return tree.prototype;
    }

}
