package ibd.table.prototype.query.fields;

import ibd.table.prototype.BData;
import ibd.table.prototype.UnlinkedBData;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.metadata.BooleanMetadata;
import ibd.table.prototype.metadata.Metadata;

public class BooleanField extends Field<Boolean>{
    public BooleanField(Metadata metadata, BData data) {
        super(metadata, data);
    }

    public BooleanField(boolean value) {
        super(BooleanMetadata.generic,value);
    }
    
    public BooleanField(Metadata metadata, boolean value) {
        super(BooleanMetadata.generic,value);
    }
    
    @Override
    public String getType(){
        return Column.BOOLEAN_TYPE;
    }

    @Override
    public Boolean getValue() {
        if (value!=null)
            return value;
        else value = data.getBoolean();
        return value;
    }
    
    @Override
    public BData getBData(){
        return new UnlinkedBData(new byte[]{(byte) (getValue() ? 1 : 0)},0,1);
        //return data;
    }

    @Override
    public int compareTo(Field f) {
        if(f == null)return NULL_COMPARE;
        if(!f.metadata.isBoolean())return NOT_DEFINED;
        return getValue().compareTo(f.getBoolean());
    }
}
