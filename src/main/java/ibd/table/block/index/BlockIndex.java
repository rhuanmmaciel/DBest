/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.block.index;

import ibd.index.btree.DictionaryPair;
import ibd.index.btree.Key;
import ibd.index.btree.Value;
import ibd.index.btree.table.BPlusTreeFileTable;
import ibd.persistent.PersistentPageFile;
import ibd.persistent.cache.Cache;
import ibd.persistent.cache.LRUCache;
import ibd.table.block.Block;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.column.BooleanColumn;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.column.DoubleColumn;
import ibd.table.prototype.column.FloatColumn;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.LongColumn;
import ibd.table.prototype.column.StringColumn;

/**
 *
 * @author ferna
 */
public class BlockIndex {

    public Prototype indexPrototype = null;

    protected BPlusTreeFileTable tree = null;
    protected Cache<Block> cache = null;

    String folder;
    String name;
    int pageSize;
    boolean override;
    Prototype rowPrototype;
    boolean open = false;

    public BlockIndex(String folder, String name, int pageSize, boolean override, Prototype rowPrototype) {
        this.folder = folder;
        this.name = name;
        this.pageSize = pageSize;
        this.override = override;
        this.rowPrototype = rowPrototype;
    }

    private void createIndexPrototype() {
        indexPrototype = new Prototype();
//        indexPrototype.addColumn(new LongColumn("id", true));
//        indexPrototype.addColumn(new IntegerColumn("position"));

        List<Column> columns = rowPrototype.getColumns();
        for (Column column : columns) {
            if (column.isPrimaryKey()) {
                switch (column.getType()) {
                    case Column.STRING_TYPE:
                        indexPrototype.addColumn(new StringColumn(column.getName(), column.getSize(), column.getFlags()));
                        break;
                    case Column.INTEGER_TYPE:
                        indexPrototype.addColumn(new IntegerColumn(column.getName(), column.getSize(), column.getFlags()));
                        break;
                    case Column.LONG_TYPE:
                        indexPrototype.addColumn(new LongColumn(column.getName(), column.getSize(), column.getFlags()));
                        break;
                    case Column.FLOAT_TYPE:
                        indexPrototype.addColumn(new FloatColumn(column.getName(), column.getSize(), column.getFlags()));
                        break;
                    case Column.BOOLEAN_TYPE:
                        indexPrototype.addColumn(new BooleanColumn(column.getName(), column.getSize(), column.getFlags()));
                        break;
                    case Column.DOUBLE_TYPE:
                        indexPrototype.addColumn(new DoubleColumn(column.getName(), column.getSize(), column.getFlags()));
                        break;
                    default:
                        throw new AssertionError();
                }

            }
        }
        indexPrototype.addColumn(new IntegerColumn("position"));
        indexPrototype.validateColumns();

    }

    public void open() throws Exception {
        if (open) {
            return;
        }
        open = true;
        createIndexPrototype();
        PersistentPageFile p = new PersistentPageFile(pageSize, Paths.get(folder + File.separator + name + ".idx"), override);
        
        cache = new LRUCache(5000000);
        cache.setPageFile(p);
        
        if (cache != null) {
            tree = new BPlusTreeFileTable(cache, indexPrototype);
        } else {
            tree = new BPlusTreeFileTable(p, indexPrototype);
        }

        tree.open();
    }
    

    public Integer searchBlockID(DataRow keyData) throws Exception {

        Key key = tree.createKey();
        key.setKeys(new DataRow[]{keyData});

        Value value = tree.search(key);

        if (value == null) {
            return null;
        }

        byte bytes_[] = (byte[]) value.get(0);
        DataRow rowData = tree.prototype.convertBinaryToRowData(bytes_, null, true, false);

        //find the block that contains the record
        return rowData.getInt("position");
    }

    public void insert(DataRow record, int blockID) {
        DataRow keyData = indexPrototype.createPKRow(record);
        //keyData.setMetadata(indexPrototype);
        Key key = tree.createKey();
        key.setKeys(new DataRow[]{keyData});

        Value value = tree.createValue();
        LinkedDataRow rowData = indexPrototype.createPKRow(record);
        rowData.setInt("position", blockID);
        //rowData.setMetadata(indexPrototype);

        //translator.validateRowData(rowData);
        byte bytes[] = tree.prototype.convertToArray(rowData);
        value.set(0, bytes);

        tree.insert(key, value, true);
    }

    public void update(DataRow record, int blockID) {
        DataRow keyData = indexPrototype.createPKRow(record);
        //keyData.setMetadata(indexPrototype);
        Key key = tree.createKey();
        key.setKeys(new DataRow[]{keyData});

        Value value = tree.createValue();
        LinkedDataRow rowData = indexPrototype.createPKRow(record);
        //rowData.setLong("id", record.getLong("id"));
        rowData.setInt("position", blockID);
        //rowData.setMetadata(indexPrototype);

        byte bytes[] = tree.prototype.convertToArray(rowData);
        value.set(0, bytes);

        tree.update(key, value);
    }

    public void delete(DataRow record) {
        DataRow keyData = indexPrototype.createPKRow(record);
        //keyData.setLong("id", record.getLong("id"));
        //keyData.setMetadata(indexPrototype);
        Key key = tree.createKey();
        key.setKeys(new DataRow[]{keyData});

        //translator.validateRowData(rowData);
        //BigKey bigKey = translator.getPrimaryKey(rowData);
        tree.delete(key);
    }
    
    /**
     * traverses the numeric values from the primaryKey down until an existing
     * indexed record is found
     */
    public DataRow getLargestSmallerKey(DataRow primaryKey) {
        List<DictionaryPair> pairs = tree.searchAll();
        for (int i = pairs.size()-1; i >= 0; i--) {
            DictionaryPair pair = pairs.get(i);
            Key key = pair.getKey();
            DataRow keyData = (DataRow)key.get(0);
            Value value = pair.getValue();
            byte bytes_[] = (byte[]) value.get(0);
            DataRow rowData = tree.prototype.convertBinaryToRowData(bytes_, null, true, false);
            
            if (keyData.compareTo(rowData)<0) return rowData;
            
        }
        return null;
    }
    

    public void flush() {
        tree.flush();
    }

}
