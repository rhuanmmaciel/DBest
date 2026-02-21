package ibd.table.prototype.query.fields;

import ibd.table.prototype.BData;
import ibd.table.prototype.UnlinkedBData;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.metadata.Metadata;
import ibd.table.prototype.metadata.StringMetadata;
import ibd.table.util.Util;
import ibd.table.util.UtilConversor;

public class StringField extends Field<String>{
    public StringField(Metadata metadata, BData data) {
        super(metadata, data);
    }

    public StringField(String value) {
        super(new StringMetadata((short)value.length()), value);
    }
    
    public StringField(Metadata metadata, String value) {
        super(metadata, value);
    }
    
    @Override
    public String getType(){
        return Column.STRING_TYPE;
    }
    
    @Override
    public String getValue() {
        if (value!=null)
            return value;
        else if (data!=null)
            return data.getString();
        else 
        //return data.getInt();
            return value;
    }
    
    @Override
    public BData getBData(){
        byte bdata[] = UtilConversor.stringToByteArray(getValue());
        return new UnlinkedBData(bdata,0,bdata.length);
        //return data;
    }

    @Override
    public int compareTo(Field f) {
        if(f == null)return NULL_COMPARE;
        String val;
        if(!f.metadata.isString()){
            switch (Util.typeOfColumn(f.metadata)){
            case "boolean":
            case "string":
            case "long":
            case "int":
            case "double":
            case "float":
                val = String.valueOf(f.getBoolean());
                break;
            case "null":
            default:
                val = null;
            }
        }else{
            val = f.getString();
        }
        return getValue().compareTo(val);
    }
}
