package ibd.table.prototype.query.fields;

import ibd.table.prototype.BData;
import ibd.table.prototype.UnlinkedBData;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.metadata.DoubleMetadata;
import ibd.table.prototype.metadata.Metadata;
import ibd.table.util.UtilConversor;

public class DoubleField extends Field<Double>{
    public DoubleField(Metadata metadata, BData data) {
        super(metadata, data);
    }
    public DoubleField(double value) {
        super(DoubleMetadata.generic,value);
    }
    
    public DoubleField(Metadata metadata, double value) {
        super(metadata, value);
    }
    
    @Override
    public String getType(){
        return Column.DOUBLE_TYPE;
    }

    @Override
    public Double getValue() {
        if (value!=null)
            return value;
        else value = data.getDouble();
        return value;
    }
    
    @Override
    public BData getBData(){
        return new UnlinkedBData(UtilConversor.doubleToByteArray(getValue()),0,8);
        //return data;
    }

    @Override
    public int compareTo(Field f) {
        if(f == null)return NULL_COMPARE;
        if(f.metadata.isString())return f.compareTo(this);
        if(!f.metadata.isFloat() && !f.metadata.isInt())return NOT_DEFINED;
        Double val;
        if(f.metadata.isInt()){
            if(f.metadata.getSize()==8){
                val = f.getLong().doubleValue();
            } else {
                val = f.getInt().doubleValue();
            }
        }else{
            if(f.metadata.getSize() == 4)
                val = f.getFloat().doubleValue();
            else
                val = f.getDouble();
        }
        return getValue().compareTo(val);
    }
}
