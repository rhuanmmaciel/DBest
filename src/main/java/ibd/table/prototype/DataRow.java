package ibd.table.prototype;

import ibd.table.block.Block;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.metadata.BooleanMetadata;
import ibd.table.prototype.metadata.DoubleMetadata;
import ibd.table.prototype.metadata.FloatMetadata;
import ibd.table.prototype.metadata.IntegerMetadata;
import ibd.table.prototype.metadata.LongMetadata;
import ibd.table.prototype.metadata.Metadata;
import ibd.table.prototype.metadata.StringMetadata;
import ibd.table.prototype.query.fields.BinaryField;
import ibd.table.prototype.query.fields.Field;

/**
 * This class defines the content of a table's row. The content must satisfy the
 * table's schema (the prototype)
 *
 * @author Sergio
 */
public abstract class DataRow implements Comparable<DataRow> { //Iterable<Map.Entry<String, Field>>,  {

    

    long len = 0;
    Block block;

    protected int fieldsSet = 0;

    public DataRow() {

    }

    public abstract int getFieldsSize();

    public abstract void setField(String column, Field field);
    
    //public abstract void setField(Column column, Field field);

    public abstract Field unset(String column);

    public abstract Comparable getValue(String column);
    
    public abstract Field getField(String column);
    
    public abstract Field getField(int index);

    //public abstract byte[] getData(String column);

    public abstract Integer getInt(String column);

    public abstract Long getLong(String column);

    public abstract Float getFloat(String column);

    public abstract Double getDouble(String column);

    public abstract String getString(String column);

    public abstract Boolean getBoolean(String column);
    
    public abstract int partialMatch(DataRow r);

    

    public int fieldsSet() {
        return fieldsSet;
    }

//    public void setData(String column, byte[] data) {
//        BData bdata = new BData(data);
//        this.setField(column, new BinaryField(new Metadata((short) (data.length >> 8 + 1), Metadata.LSHIFT_8_SIZE_COLUMN), bdata));
//    }

    public void setInt(String column, int data) {
        //BData bdata = new BData(UtilConversor.intToByteArray(data));
        //this.setField(column, Field.createField(IntegerMetadata.generic, bdata));
        this.setField(column, Field.createField(IntegerMetadata.generic, data));
    }

    public void setLong(String column, long data) {
        //BData bdata = new BData(UtilConversor.longToByteArray(data));
        //this.setField(column, Field.createField(LongMetadata.generic, bdata));
        this.setField(column, Field.createField(LongMetadata.generic, data));
    }

    public void setString(String column, String data) {
        //BData bdata = new BData(UtilConversor.stringToByteArray(data));
        //this.setField(column, Field.createField(new StringMetadata((short) (data.length() + 1)), bdata));
        this.setField(column, Field.createField(new StringMetadata((short) (data.length() + 1)), data));
    }

    public void setFloat(String column, float data) {
        //BData bdata = new BData(UtilConversor.floatToByteArray(data));
        //this.setField(column, Field.createField(FloatMetadata.generic, bdata));
        this.setField(column, Field.createField(FloatMetadata.generic, data));
    }

    public void setDouble(String column, double data) {
        //BData bdata = new BData(UtilConversor.doubleToByteArray(data));
        //this.setField(column, Field.createField(DoubleMetadata.generic, bdata));
        this.setField(column, Field.createField(DoubleMetadata.generic, data));
    }

    public void setBoolean(String column, boolean data) {
        //BData bdata = new BData(new byte[]{(byte) (data ? 1 : 0)});
        //this.setField(column, Field.createField(BooleanMetadata.generic, bdata));
        this.setField(column, Field.createField(BooleanMetadata.generic, data));
    }
    
    public abstract boolean hasValue(String column);
    

    

    /*
    public String getAsString(String column) {
        Column col = getColumn(column);

        switch (col.getType()) {
            case "STRING" -> {
                return getString(column).toString();
            }
            case "BOOLEAN" -> {
                return getBoolean(column).toString();
            }
            case "INTEGER" -> {
                return getInt(column).toString();
            }
            case "FLOAT" -> {
                return getFloat(column).toString();
            }
            case "DOUBLE" -> {
                return getDouble(column).toString();
            }
            case "LONG" -> {
                return getLong(column).toString();
            }
        }
        return "";
    }
    */

    public int len() {
        //int len =  8 + this.getString("nome").getBytes().length + 2;
        return (int) this.len;

    }

//    @Override
//    public Iterator<Map.Entry<String, Field>> iterator() {
//        return data.entrySet().iterator();
//    }
    /**
     * @param block the block to set
     */
    public void setBlock(Block block) {
        this.block = block;
    }

    public Integer getBlockId() {
        return block.getPageID();
    }

}
