/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree.generic;

/**
 * Defines the schema of a row, i.e., the datatype for each field of the row
 * @author Sergio
 */
public class RowSchema {

    Character types[];
    Integer sizes[];
    
    int current = 0;

    public RowSchema(int size) {
        types = new Character[size];
        sizes = new Integer[size];
    }

    public void addDataType(char c) {
        switch (c) {
            case 'I':
                addIntDataType();
                return;
            case 'L':
                addLongDataType();
                return;
            case 'S':
                addStringDataType();
        }
    }

    public void addIntDataType() {
        types[current] = 'I';
        sizes[current] = Integer.BYTES;
        current++;
    }

    public void addLongDataType() {
        types[current] = 'L';
        sizes[current] = Long.BYTES;
        current++;
    }

    //The string type is fixed as char(100)
    public void addStringDataType() {
        types[current] = 'S';
        sizes[current] = 100;//ibd.table.record.Record.RECORD_SIZE;
        current++;
    }
    
    public void addRecordDataType(int size) {
        types[current] = 'R';
        sizes[current] = size;
        current++;
    }
    
    public void addBigKeyDataType(int size) {
        types[current] = 'K';
        sizes[current] = size;
        current++;
    }

    public int getSize() {
        return types.length;
    }

    public char get(int index) {
        return types[index];
    }
    
    public int getSize(int index) {
        return sizes[index];
    }

    /*
    * Returns how many bytes each object takes, given its type
    * The current function assumes the string type is fixed as char(100)
    */
    public int getDataSizeInBytes(int type, Object obj) {
        return sizes[type];
        }

}
