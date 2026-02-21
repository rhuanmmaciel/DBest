/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree.table;

import ibd.index.btree.BPlusTreeFile;
import ibd.index.btree.Key;
import ibd.index.btree.TreeIndexHeader;
import ibd.index.btree.Value;
import ibd.persistent.PageFile;
import java.util.List;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.Column;

/**
 * This class stores information as table rows. 
 * The rows schema is defined by a prototype
 *
 * @author Sergio
 */
public class BPlusTreeFileTable extends BPlusTreeFile {

    public Prototype prototype;

    /**
     * Constructor
     *
     * @param pagefile: the file storing the entries of this index.
     * @param prototype: the schema of the table
     */
    public BPlusTreeFileTable(PageFile pagefile, Prototype prototype) throws InstantiationException, IllegalAccessException {
        super(pagefile);
        this.prototype = prototype;
    }

    /**
     * Creates a specialized key to be used by this b-tree class
     * @return the created key
     */
    @Override
    public Key createKey() {
        return new BinaryKey(prototype);
    }
    
    /**
     * Creates a specialized value to be used by this b-tree class
     * @return the created value
     */
    @Override
    public Value createValue() {
        return new BinaryValue(prototype);
    }

    /**
     * Creates a TreeIndexHeaderTable.
     * This header contains specialized information for this B+tree implementation, such as the table schema.
     * @return a new header for this index structure
     */
    @Override
    protected TreeIndexHeader loadHeader() {
        return new TreeIndexHeaderTable(file.getPageSize(),  rootID, firstLeafID, prototype);
    }
    
    /**
     * extracts the table schema from the file header. 
     * @param header: the file header from where the properties are read from 
     */
    @Override
    protected void readFromHeader(TreeIndexHeader header){
        TreeIndexHeaderTable headerTable = (TreeIndexHeaderTable)header;
        prototype = headerTable.prototype;
        prototype.validateColumns();
    }
    

}
