/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree.generic;

import ibd.index.btree.BPlusTreeFile;
import ibd.index.btree.Key;
import ibd.index.btree.TreeIndexHeader;
import ibd.index.btree.Value;
import ibd.persistent.PageFile;

public class BPlusTreeFileGeneric extends BPlusTreeFile {

    //the schema of the indexed keys
    RowSchema keySchema;
    
    //the schema of the indexed values
    protected RowSchema valueSchema;

    
    /**
     * Constructor
     *
     * @param pagefile: the file storing the entries of this index.
     * @param keySchema: the schema of the keys.
     * @param valueSchema: the schema of the indexed values.
     */
    public BPlusTreeFileGeneric(PageFile pagefile,  RowSchema keySchema, RowSchema valueSchema) throws InstantiationException, IllegalAccessException {
        super(pagefile);
        this.valueSchema = valueSchema;
        this.keySchema = keySchema;
    }
    
    

    @Override
    public Key createKey() {
        return new PrimitiveKey(keySchema);
    }
    
    @Override
    public Value createValue() {
        return new PrimitiveValue(valueSchema);
    }

    /**
     * Creates a TreeIndexHeaderGeneric.
     * This header contains specialized information for this B+tree implementation, such as the key and value schemas.
     * the schemas are basically lists of pritimive data types
     * @return a new header for this index structure
     */
    @Override
    protected TreeIndexHeader loadHeader() {
        return new TreeIndexHeaderGeneric(file.getPageSize(), m, leafM, rootID, firstLeafID, keySchema, valueSchema);
    }
    
    /**
     * extracts the key and value schemas from the file header. 
     * @param header: the file header from where the properties are read from 
     */
    @Override
    protected void readFromHeader(TreeIndexHeader header){
        TreeIndexHeaderGeneric headerTable = (TreeIndexHeaderGeneric)header;
        keySchema = headerTable.getKeySchema();
        valueSchema = headerTable.getValueSchema();
    }

    public RowSchema getKeySchema() {
        return keySchema;
    }

}
