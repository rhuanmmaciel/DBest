/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree.generic;

import ibd.index.btree.Key;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Sergio
 */
public class PrimitiveKey extends Key {

    //the datatype of the objects that compose keys are define in the schema
    protected RowSchema schema;
    
    public PrimitiveKey() {
    }
    
    public PrimitiveKey(RowSchema schema) {
        keys = new Comparable[schema.getSize()];
        this.schema = schema;
    }

    /*
    * the size refers to the amount of bytes taken by each object of the key.
     */
    @Override
    public int getSizeInBytes() {
        int size = 0;
        for (int i = 0; i < keys.length; i++) {
            size += schema.getDataSizeInBytes(i, keys[i]);
        }

        return size;
    }

    @Override
    public void writeExternal(DataOutput out) throws IOException {
        //out.writeInt(keys.length);
        for (int i = 0; i < keys.length; i++) {
            switch (schema.get(i)) {
                case 'I':
                    out.writeInt((Integer) keys[i]);
                    continue;
                case 'L':
                    out.writeLong((Long) keys[i]);
                    continue;
                case 'S':
                    out.writeUTF((String) keys[i]);
                    continue;
            }
        }
    }

    @Override
    public void readExternal(DataInput in) throws IOException {
        for (int i = 0; i < keys.length; i++) {
            switch (schema.get(i)) {
                case 'I':
                    keys[i] = in.readInt();
                    continue;
                case 'L':
                    keys[i] = in.readLong();
                    continue;
                case 'S':
                    keys[i] = in.readUTF();
                    continue;
            }
        }
    }

    
    

}
