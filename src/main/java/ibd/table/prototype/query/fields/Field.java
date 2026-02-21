package ibd.table.prototype.query.fields;

import ibd.table.prototype.BData;
import ibd.table.prototype.metadata.Metadata;
import ibd.table.util.Util;

public abstract class Field<T> implements Comparable<Field>{

    protected static final int NOT_DEFINED = -1;
    protected static final int NULL_COMPARE = -1;

    protected final BData data;
    protected final Metadata metadata;
    protected T value;
    protected Field(Metadata metadata,BData data) {
        this.data = data;
        this.metadata = metadata;
        //System.out.println("nao deeria vir aqui");
        //this.bufferedData = constructData();
    }

    protected Field(Metadata metadata,T value){
        this.value = value;
        this.metadata = metadata;
        this.data = null;
    }

    public static Field createField(Metadata metadata,BData data){
        switch (Util.typeOfColumn(metadata)){
            case "boolean":
                return new BooleanField(metadata,data);
            case "string":
                return new StringField(metadata,data);
            case "long":
                return new LongField(metadata,data);
            case "int":
                return new IntegerField(metadata,data);
            case "double":
                return new DoubleField(metadata,data);
            case "float":
                return new FloatField(metadata,data);
            case "null":
            default:
                return new NullField(metadata);
        }
    }
    
    public static Field createField2(Metadata metadata,BData data){
        switch (Util.typeOfColumn(metadata)){
            case "boolean":
                return new BooleanField(metadata,data.getBoolean());
            case "string":
                return new StringField(metadata,data.getString());
            case "long":
                return new LongField(metadata,data.getLong());
            case "int":
                return new IntegerField(metadata,data.getInt());
            case "double":
                return new DoubleField(metadata,data.getDouble());
            case "float":
                return new FloatField(metadata,data.getFloat());
            case "null":
            default:
                return new NullField(metadata);
        }
    }
    
    public static Field createField(Metadata metadata,Comparable data){
        switch (Util.typeOfColumn(metadata)){
            case "boolean":
                return new BooleanField(metadata,(boolean)data);
            case "string":
                return new StringField(metadata,(String)data);
            case "long":
                return new LongField(metadata,(long)data);
            case "int":
                return new IntegerField(metadata,(int)data);
            case "double":
                return new DoubleField(metadata,(double)data);
            case "float":
                return new FloatField(metadata,(float)data);
            case "null":
            default:
                return new NullField(metadata);
        }
    }

    public abstract String getType();

    public T getValue(){
        return value;
    }

    public abstract BData getBData();
    
    public Metadata getMetadata(){
        return metadata;
    }

    public Integer getInt(){
        return (Integer)getValue();
    }
    public Long getLong(){
        return (Long)getValue();
    }
    public Boolean getBoolean(){
        return (Boolean)getValue();
    }
    public Float getFloat(){
        return (Float)getValue();
    }
    public Double getDouble(){
        return (Double)getValue();
    }
    public String getString(){
        return (String)getValue();
    }
    
    public Comparable getComparable(){
        return (Comparable)getValue();
    }

    @Override
    public abstract int compareTo(Field f);

   
}
