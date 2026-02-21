/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree.generic;

import ibd.index.btree.Value;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Sergio
 */
public class PrimitiveValue extends Value {

    //the datatype of the objects that compose a value are define in the schema
    private RowSchema schema;

    public PrimitiveValue(RowSchema schema) {
        objects = new Object[schema.getSize()];
        this.schema = schema;
    }

    /*
    * the size refers to the amount of bytes taken by each object of the value.
     */
    @Override
    public int getSizeInBytes() {
        int size = 0;
        for (int i = 0; i < objects.length; i++) {
            size += schema.getDataSizeInBytes(i, objects[i]);
        }

        return size;
    }

    @Override
    public void writeExternal(DataOutput out) throws IOException {
        for (int i = 0; i < objects.length; i++) {
            switch (schema.get(i)) {
                case 'I':
                    out.writeInt((Integer) objects[i]);
                    continue;
                case 'L':
                    out.writeLong((Long) objects[i]);
                    continue;
                case 'S':
                    out.writeUTF((String) objects[i]);
                    continue;
                case 'R':
                    byte target[] = new byte[schema.getSize(i)];
                    byte source[] = (byte[])objects[i];//((GenericRecord)objects[i]).getData();
                    System.arraycopy(source, 0, target, 0, source.length);
                    out.write(target);
            }
        }
    }

    @Override
    public void readExternal(DataInput in) throws IOException {
        for (int i = 0; i < objects.length; i++) {
            switch (schema.get(i)) {
                case 'I':
                    objects[i] = in.readInt();
                    continue;
                case 'L':
                    objects[i] = in.readLong();
                    continue;
                case 'S':
                    objects[i] = in.readUTF();
                    continue;
                case 'R':
                    byte b[] = new byte[schema.getSize(i)];
                    in.readFully(b);
                    objects[i] = b;//new GenericRecord(b);
            }
        }
    }
}
