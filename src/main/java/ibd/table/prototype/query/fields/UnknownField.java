package ibd.table.prototype.query.fields;

import ibd.table.prototype.BData;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.metadata.Metadata;

public class UnknownField extends Field<UnknownValue> {

    public UnknownField(Metadata col, BData data) {
        super(col, data);
    }
    
    @Override
    public String getType(){
        return Column.INTEGER_TYPE;
    }

    @Override
    public UnknownValue getValue() {
        return null;
    }
    
    @Override
    public BData getBData(){
        return null;
    }

    @Override
    public int compareTo(Field f) {
        throw new UnsupportedOperationException("Value with unknown data type"); 
    }


}
