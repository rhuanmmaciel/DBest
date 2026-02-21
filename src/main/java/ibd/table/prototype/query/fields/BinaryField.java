package ibd.table.prototype.query.fields;

import ibd.table.prototype.BData;
import ibd.table.prototype.UnlinkedBData;
import ibd.table.prototype.metadata.Metadata;

import ibd.table.prototype.column.Column;
import ibd.table.prototype.metadata.IntegerMetadata;

public class BinaryField extends Field<byte[]>{
    
    public BinaryField(Metadata metadata, BData data) {
        super(metadata, data);
    }
    
    public BinaryField(byte[] value) {
        super(IntegerMetadata.generic, value);
    }
    
    public BinaryField(Metadata metadata, byte[] value) {
        super(metadata, value);
    }
    
    @Override
    public String getType(){
        return Column.BINARY_TYPE;
    }

    @Override
    public byte[] getValue() {
        return value;
    }
    
    @Override
    public BData getBData(){
        return new UnlinkedBData(value,0,0);
        //return data;
    }

    @Override
    public int compareTo(Field f) {
        if(f==null)return 0;
        return 0;
        //ERRO
        //return Arrays.compare(getBufferedData(),f.getBData());
    }
}
