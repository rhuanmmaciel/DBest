package ibd.table.prototype.query.fields;

import ibd.table.prototype.BData;
import ibd.table.prototype.UnlinkedBData;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.metadata.LongMetadata;
import ibd.table.prototype.metadata.Metadata;
import ibd.table.util.UtilConversor;

public class LongField extends Field<Long>{
    public LongField(Metadata metadata, BData data) {
        super(metadata, data);
    }

    public LongField(long value) {
        super(LongMetadata.generic, value);
    }
    
    public LongField(Metadata metadata, long value) {
        super(metadata, value);
    }
    
    @Override
    public String getType(){
        return Column.LONG_TYPE;
    }
    
    @Override
    public Long getValue() {
        if (value!=null)
            return value;
        else value = data.getLong();
        return value;
    }
    
    @Override
    public BData getBData(){
        return new UnlinkedBData(UtilConversor.longToByteArray(getValue()),0,8);
        //return data;
    }

    @Override
    public int compareTo(Field f) {
        if(f == null)return NULL_COMPARE;
        if(f.metadata.isString() || f.metadata.isFloat())return f.compareTo(this);
        if(!f.metadata.isInt())return NOT_DEFINED;
        Long val;
        if(f.metadata.getSize()==8){
            val = f.getLong();
        } else {
            val = f.getInt().longValue();
        }
        return getValue().compareTo(val);
    }
}
