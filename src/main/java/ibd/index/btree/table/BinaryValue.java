/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree.table;

import ibd.index.btree.Value;
import ibd.table.prototype.LinkedDataRow;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import ibd.table.prototype.Prototype;

/**
 *
 * @author Sergio
 */
public class BinaryValue extends Value {

    Prototype prototype;
    public LinkedDataRow rowData;

    public BinaryValue(Prototype prototype) {
        this.objects = new Object[1];
        this.prototype = prototype;
    }

    /*
    * the size refers to the amount of bytes taken by each object of the value.
     */
    @Override
    public int getSizeInBytes() {
        return prototype.maxRecordSize();
    }

    @Override
    public void writeExternal(DataOutput out) throws IOException {

        byte target[] = new byte[prototype.maxRecordSize()];
        byte source[] = (byte[]) objects[0];//((GenericRecord)objects[i]).getData();
        System.arraycopy(source, 0, target, 0, source.length);
        out.write(target);
    }

    @Override
    public void readExternal(DataInput in) throws IOException {

        byte b[] = new byte[prototype.maxRecordSize()];
        in.readFully(b);
        objects[0] = b;
        
        //uncomment to do the conversion earlier
        rowData = prototype.convertBinaryToRowData(b); 
    }
}
