/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree.table;

import ibd.index.btree.Key;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;

/**
 * The key is a single data row.
 * Only the primary key of the data row is stored/retrieved from disk
 * A translator that knows the table schema is used to extract the primary key from the row.
 * @author Sergio
 */
public class BinaryKey extends Key {

    //used to extract the primary key from the row.
    Prototype prototype;

    public BinaryKey(Prototype prototype) {
        this.prototype = prototype;
        //defines the single entry of the key as a data row
        keys = new LinkedDataRow[1];
    }
    
    /** 
     * return the size in bytes of the primary key contents extracted from the data row.
     * Primary keys have a fixed lenght, so the size can be determined without examining the data row.
     * @return the size in bytes of the primary key
     */
    @Override
    public int getSizeInBytes() {
        return prototype.getPrimaryKeySize();
    }
    
    /**
     * returns true if the first set levels of this key are equal to the first levels of other key.
     * For binary keys, only the first level is set (i=0, always), and it contains a rowData.
     * The rowData comparison needs to consider that this is a partialMatch.
     * It means that only the overlapping attributes from the two rowData need to be compared. 
     * @param otherKey the key to be compared with
     * @return
     */
    @Override
    public boolean partialMatch(Key otherKey) {
        DataRow otherRow = (DataRow)otherKey.get(0);
        DataRow thisRow = (DataRow)keys[0];
        return (thisRow.partialMatch(otherRow)==0);
    }
    
    

    @Override
    public void writeExternal(DataOutput out) throws IOException {
        //uses the translator to extract the byte array that represent the primary key of the data row
        byte bytes[] = prototype.convertPrimaryKeyToByteArray((LinkedDataRow) keys[0]);
        out.write(bytes);
    }

    @Override
    public void readExternal(DataInput in) throws IOException {
        //uses the translator to read a data row from a byte array.
        //only the primary key contents are read. 
        byte b[] = new byte[prototype.getPrimaryKeySize()];
        in.readFully(b);
        DataRow rowData = prototype.convertBinaryToPKRowData(b, null, false, true);
        //DataRow rowData = prototype.convertBinaryToPKRowData(b);
        keys[0] = rowData;
    }

}
