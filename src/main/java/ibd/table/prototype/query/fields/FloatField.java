package ibd.table.prototype.query.fields;

import ibd.table.prototype.BData;
import ibd.table.prototype.UnlinkedBData;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.metadata.FloatMetadata;
import ibd.table.prototype.metadata.Metadata;
import ibd.table.util.UtilConversor;

public class FloatField extends Field<Float>{
    public FloatField(Metadata metadata, BData data) {
        super(metadata, data);
    }

    public FloatField(float value) {
        super(FloatMetadata.generic, value);
    }
    
    public FloatField(Metadata metadata, float value) {
        super(metadata, value);
    }
    
    @Override
    public String getType(){
        return Column.FLOAT_TYPE;
    }
    
    @Override
    public Float getValue() {
        if (value!=null)
            return value;
        else value = data.getFloat();
        return value;
    }
    
    @Override
    public BData getBData(){
        return new UnlinkedBData(UtilConversor.floatToByteArray(getValue()),0,4);
        //return data;
    }

    @Override
    public int compareTo(Field f) {
        if(f == null)return NULL_COMPARE;
        if(f.metadata.isString())return f.compareTo(this);
        if(!f.metadata.isFloat() && !f.metadata.isInt())return NOT_DEFINED;
        if(f.metadata.isFloat() && f.metadata.getSize() == 8)return f.compareTo(this);
        Float val;
        if(f.metadata.isInt()){
            if(f.metadata.getSize()==8){
                val = f.getLong().floatValue();
            } else {
                val = f.getInt().floatValue();
            }
        }else{
            val = f.getFloat();
        }
        return getValue().compareTo(val);
    }
}
