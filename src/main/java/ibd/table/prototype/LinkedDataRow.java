package ibd.table.prototype;

import ibd.table.prototype.column.Column;
import ibd.table.prototype.metadata.BooleanMetadata;
import ibd.table.prototype.metadata.DoubleMetadata;
import ibd.table.prototype.metadata.FloatMetadata;
import ibd.table.prototype.metadata.IntegerMetadata;
import ibd.table.prototype.metadata.LongMetadata;
import ibd.table.prototype.metadata.StringMetadata;
import ibd.table.prototype.query.fields.Field;

/**
 * This class defines the content of a table's row. The content must satisfy the
 * table's schema (the prototype)
 *
 * @author Sergio
 */
public class LinkedDataRow extends DataRow {

    Field fields[];
    
    //the schema of the table
    Prototype prototype = null;
    
    byte data[];
    
    public LinkedDataRow() {
    }

    public LinkedDataRow(Prototype prototype, boolean onlyPk) {
        this.prototype = prototype;
        if (onlyPk) {
            fields = new Field[prototype.getPKColumns().size()];
        } else {
            fields = new Field[prototype.getColumns().size()];
        }
    }
    
    public LinkedDataRow(LinkedDataRow row) {
        this.prototype = row.prototype;
        fields = new Field[prototype.getColumns().size()];
        for (int i = 0; i < row.getFieldsSize(); i++) {
            this.setValue(i, row.getValue(i));
        }
    }
    
    public void setData(byte data[]){
        this.data = data;
    }
    
    public Prototype getPrototype(){
        return prototype;
    }
    
    public int getFieldsSize() {
        return fields.length;
    }
    
    @Override
    public Field getField(int index) {
        return fields[index];
    }

    @Override
    public boolean hasValue(String column) {
        Column col = prototype.getColumn(column);
        return (fields[col.index]!=null);
    }
    
    public void setValue(String column, Comparable data) {
        Column col = prototype.getColumn(column);
        setValue(col, data);
    }
    
    public void setValue(int colIndex, Comparable data) {
        Column col = prototype.getColumn(colIndex);
        setValue(col, data);
    }

    private void setValue(Column col, Comparable data) {
        if (col==null || data ==null){
            return;
        }
        
        switch (col.getType()) {
            case "STRING" ->
                setString(col, (String) data);
            case "BOOLEAN" ->
                setBoolean(col, (Boolean) data);
            case "INTEGER" ->
                setInt(col, (Integer) data);
            case "FLOAT" ->
                setFloat(col, (Float) data);
            case "DOUBLE" ->
                setDouble(col, (Double) data);
            case "LONG" ->
                setLong(col, (Long) data);
        }
    }

    @Override
    public void setField(String column, Field field) {
        Column col = prototype.getColumn(column);
        setField(col, field);
    }
    
    public void setField(int colIndex, Field field) {
        Column col = prototype.getColumn(colIndex);
        setField(col, field);
    }
    
    private void setField(Column col, Field field) {
        if (fields[col.index] == null) {
            fieldsSet++;
        }
        fields[col.index] = field;
    }
    
    
    private void setInt(Column column, int data) {
        //BData bdata = new BData(UtilConversor.intToByteArray(data));
        //this.setField(column, Field.createField(IntegerMetadata.generic, bdata));
        this.setField(column, Field.createField(IntegerMetadata.generic, data));
    }

    private void setLong(Column column, long data) {
        //BData bdata = new BData(UtilConversor.longToByteArray(data));
        //this.setField(column, Field.createField(LongMetadata.generic, bdata));
        this.setField(column, Field.createField(LongMetadata.generic, data));
    }

    private void setString(Column column, String data) {
        //BData bdata = new BData(UtilConversor.stringToByteArray(data));
        //this.setField(column, Field.createField(new StringMetadata((short) (data.length() + 1)), bdata));
        if (data!=null)
            this.setField(column, Field.createField(new StringMetadata((short) (data.length() + 1)), data));
        else this.setField(column, Field.createField(new StringMetadata((short) 0), data));
    }

    private void setFloat(Column column, float data) {
        //BData bdata = new BData(UtilConversor.floatToByteArray(data));
        //this.setField(column, Field.createField(FloatMetadata.generic, bdata));
        this.setField(column, Field.createField(FloatMetadata.generic, data));
    }

    private void setDouble(Column column, double data) {
        //BData bdata = new BData(UtilConversor.doubleToByteArray(data));
        //this.setField(column, Field.createField(DoubleMetadata.generic, bdata));
        this.setField(column, Field.createField(DoubleMetadata.generic, data));
    }

    private void setBoolean(Column column, boolean data) {
        //BData bdata = new BData(new byte[]{(byte) (data ? 1 : 0)});
        //this.setField(column, Field.createField(BooleanMetadata.generic, bdata));
        this.setField(column, Field.createField(BooleanMetadata.generic, data));
    }
    
    
    public void setInt(int colIndex, int data) {
        //BData bdata = new BData(UtilConversor.intToByteArray(data));
        //this.setField(column, Field.createField(IntegerMetadata.generic, bdata));
        this.setField(colIndex, Field.createField(IntegerMetadata.generic, data));
    }

    public void setLong(int colIndex, long data) {
        //BData bdata = new BData(UtilConversor.longToByteArray(data));
        //this.setField(column, Field.createField(LongMetadata.generic, bdata));
        this.setField(colIndex, Field.createField(LongMetadata.generic, data));
    }

    public void setString(int colIndex, String data) {
        //BData bdata = new BData(UtilConversor.stringToByteArray(data));
        //this.setField(column, Field.createField(new StringMetadata((short) (data.length() + 1)), bdata));
        this.setField(colIndex, Field.createField(new StringMetadata((short) (data.length() + 1)), data));
    }

    public void setFloat(int colIndex, float data) {
        //BData bdata = new BData(UtilConversor.floatToByteArray(data));
        //this.setField(column, Field.createField(FloatMetadata.generic, bdata));
        this.setField(colIndex, Field.createField(FloatMetadata.generic, data));
    }

    

    public void setBoolean(int colIndex, boolean data) {
        //BData bdata = new BData(new byte[]{(byte) (data ? 1 : 0)});
        //this.setField(column, Field.createField(BooleanMetadata.generic, bdata));
        this.setField(colIndex, Field.createField(BooleanMetadata.generic, data));
    }

    @Override
    public Comparable getValue(String column) {
        Column col = this.prototype.getColumn(column);
        if (col == null) {
            return null;
        }
        
        if (fields[col.index]==null) return null;

        return fields[col.index].getComparable();
    }
    
    public Comparable getValue(int colIndex){
        Field f = fields[colIndex];
        if (f==null) return null;
        return (Comparable) fields[colIndex].getValue();
    }

    public byte[] getData(String column) {
        Column col = this.prototype.getColumn(column);
        if (col == null) {
            return null;
        }

        BData data = fields[col.index].getBData();
        if (data == null) {
            return null;
        }
        return data.getData();
    }

   

    @Override
    public Integer getInt(String column) {
        Column col = this.prototype.getColumn(column);
        if (col == null) {
            return null;
        }

        if (fields[col.index] == null) {
            return null;
        }

        return fields[col.index].getInt();
    }

    @Override
    public Long getLong(String column) {
        Column col = this.prototype.getColumn(column);
        if (col == null) {
            return null;
        }

        return fields[col.index].getLong();
    }

    @Override
    public Float getFloat(String column) {
        Column col = this.prototype.getColumn(column);
        if (col == null) {
            return null;
        }

        return fields[col.index].getFloat();
    }

    @Override
    public Double getDouble(String column) {
        Column col = this.prototype.getColumn(column);
        if (col == null) {
            return null;
        }

        return fields[col.index].getDouble();
    }

    @Override
    public String getString(String column) {
        Column col = this.prototype.getColumn(column);
        if (col == null) {
            return null;
        }

        return fields[col.index].getString();
    }

    @Override
    public Boolean getBoolean(String column) {
        Column col = this.prototype.getColumn(column);
        if (col == null) {
            return null;
        }

        return fields[col.index].getBoolean();
    }
    
    @Override
    public Field getField(String column) {
        Column col = this.prototype.getColumn(column);
        if (col == null) {
            return null;
        }
        return fields[col.index];
    }
    
    @Override
    public Field unset(String column) {
        Field f = getField(column);
        setField(column, null);
        return f;
    }

    @Override
    public int compareTo(DataRow r) {
        int val;
        int end = (fieldsSet < r.fieldsSet() ? fieldsSet : r.fieldsSet());
        for (int i = 0; i < end; i++) {
            Field o = getField(i);
            Field f = r.getField(i);
            val = o.compareTo(f);
            if (val != 0) {
                return val;
            }
        }
        return fieldsSet - r.fieldsSet();
    }

    @Override
    public int partialMatch(DataRow r) {
        int val;
        int end = (fieldsSet < r.fieldsSet() ? fieldsSet : r.fieldsSet());
        for (int i = 0; i < end; i++) {
            Field o = getField(i);
            Field f = r.getField(i);
            val = o.compareTo(f);
            if (val != 0) {
                return val;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        String str = "row(";
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] != null) {
                String col = prototype.getColumn(i).getName();
                str += col + ":" + fields[i].getComparable();
            }
            str += ", ";
        }
        str += ")";
        return str;
    }

    public static void main(String[] args) {
        Comparable num1 = new Integer(1);
        Comparable str = new String("1");
        if (num1.compareTo(str)==0)
            System.out.println("igual");
    }
}
