package ibd.table.prototype;

import ibd.table.prototype.column.Column;
import ibd.table.prototype.query.fields.Field;
import java.util.Map;

import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * This class defines the content of a row not yet backed by a table. 
 *
 * @author Sergio
 */
public class BasicDataRow extends DataRow {

    //the mapping between the column name and the fields
    protected Map<String, Field> data;

    public BasicDataRow() {
        data = new TreeMap<>();
    }
    
    @Override
    public int getFieldsSize() {
        return data.size();
    }

    @Override
    public Comparable getValue(String column) {
        if (!this.data.containsKey(column)) {
            return null;
        }
        return (Comparable) this.data.get(column).getValue();
    }
    
    

//    @Override
//    public byte[] getData(String column) {
//        if (!this.data.containsKey(column)) {
//            return null;
//        }
//        BData data = this.data.get(column).getBData();
//        if (data == null) {
//            return null;
//        }
//        return data.getData();
//    }

    @Override
    public Integer getInt(String column) {
        if (!this.data.containsKey(column)) {
            return null;
        }
        return this.data.get(column).getInt();
    }

    @Override
    public Long getLong(String column) {
        if (!this.data.containsKey(column)) {
            return null;
        }
        return this.data.get(column).getLong();
    }

    @Override
    public Float getFloat(String column) {
        if (!this.data.containsKey(column)) {
            return null;
        }
        return this.data.get(column).getFloat();
    }

    @Override
    public Double getDouble(String column) {
        if (!this.data.containsKey(column)) {
            return null;
        }
        return this.data.get(column).getDouble();
    }

    @Override
    public String getString(String column) {
        if (!this.data.containsKey(column)) {
            return null;
        }
        return this.data.get(column).getString();
    }

    @Override
    public Boolean getBoolean(String column) {
        if (!this.data.containsKey(column)) {
            return null;
        }
        return this.data.get(column).getBoolean();
    }

    @Override
    public Field getField(String column) {
        return this.data.get(column);
    }

    @Override
    public Field getField(int index) {
        //should not have to implement this method
        return null;
    }

    /*
    public void setValue(String column, Comparable data) {
        
        if (prototype==null){
            unknownData.put(column, data);
            return;
        }
        
        Column col = prototype.getColumn(column);

        
        if (col==null){
            unknownData.put(column, data);
            return;
        }
        
        switch (col.getType()) {
            case "STRING" ->
                setString(column, (String) data);
            case "BOOLEAN" ->
                setBoolean(column, (Boolean) data);
            case "INTEGER" ->
                setInt(column, (Integer) data);
            case "FLOAT" ->
                setFloat(column, (Float) data);
            case "DOUBLE" ->
                setDouble(column, (Double) data);
            case "LONG" ->
                setLong(column, (Long) data);
        }
    }
     */
    @Override
    public void setField(String column, Field field) {
        if (field == null) {
            return;
        }
        if (!this.data.containsKey(column)) {
            fieldsSet++;
        }

        this.data.put(column, field);

    }
   
    @Override
    public boolean hasValue(String column) {
        Field f = this.data.get(column);
        return (f != null);
    }

    @Override
    public Field unset(String column) {
        Field f = this.data.get(column);
        setField(column, null);
        this.data.remove(column);
        return f;
    }

    /**
     * Created a row that is linked to a schema. It means that the row fields are mapped to columns.
     * @param prototype the schema used to create the linked row
     * @return the linked row
     */
    public LinkedDataRow getLinkedDataRow(Prototype prototype) {
        LinkedDataRow ldr = new LinkedDataRow(prototype, false);

        //create fields for the values with known datatype using the provided column names
        for (String colName : data.keySet()) {

            Column col = prototype.getColumn(colName);
            if (col == null) {
                continue;
            }
            Field f = data.get(colName);
            addField(ldr, col, f);
        }
        return ldr;

    }
/*
    public void setMetadata(Prototype prototype) {
        //byteSize = 0;
        fields = new Field[prototype.getColumns().size()];
        //create fields for the values with unknown datatype using provided the column names and the prototype
        for (Entry<String, Comparable> entry : unknownData.entrySet()) {
            Column col = prototype.getColumn(entry.getKey());
            if (col == null) {
                continue;
            }
            adjustField(col, entry.getValue());
        }

        for (String colName : data.keySet()) {

            if (metadata.containsKey(colName)) {
                continue;
            }
            Column col = prototype.getColumn(colName);
            if (col == null) {
                continue;
            }
            metadata.put(colName, col);
            Field f = data.get(colName);
            if (f.getType().equals(Column.UNKNOWN_TYPE)) {
                adjustField(col, f);
            }
            fields[col.index] = f;
            //byteSize+=col.getSizeinBytes();
        }
        //byteSize = prototype.getSizeInBytes();
        //this.prototype = prototype;
        len = prototype.maxRecordSize();
    }
*/
    private void addField(LinkedDataRow ldr, Column col, Field value) {
        switch (col.getType()) {
            case Column.BOOLEAN_TYPE -> {
                ldr.setBoolean(col.getName(), value.getBoolean());
                break;
            }
            case Column.DOUBLE_TYPE -> {
                ldr.setDouble(col.getName(), value.getDouble());
                break;
            }
            case Column.FLOAT_TYPE -> {
                ldr.setFloat(col.getName(), value.getFloat());
                break;
            }
            case Column.INTEGER_TYPE -> {
                ldr.setInt(col.getName(), value.getInt());
                break;
            }
            case Column.LONG_TYPE -> {
                ldr.setLong(col.getName(), value.getLong());
                break;
            }
            case Column.STRING_TYPE -> {
                ldr.setString(col.getName(), value.getString());
                break;
            }

        }
        //metadata.put(col.getName(), col);
    }

    /*
    private void adjustField(Column col, Object value) {
        switch (col.getType()) {
            case Column.BOOLEAN_TYPE -> {
                this.setBoolean(col.getName(), (Boolean) value);
                break;
            }
            case Column.DOUBLE_TYPE -> {
                this.setDouble(col.getName(), (Double) value);
                break;
            }
            case Column.FLOAT_TYPE -> {
                this.setFloat(col.getName(), (Float) value);
                break;
            }
            case Column.INTEGER_TYPE -> {
                this.setInt(col.getName(), (Integer) value);
                break;
            }
            case Column.LONG_TYPE -> {
                this.setLong(col.getName(), (Long) value);
                break;
            }
            case Column.STRING_TYPE -> {
                this.setString(col.getName(), (String) value);
                break;
            }

        }
        metadata.put(col.getName(), col);
    }
*/

    @Override
    public int compareTo(DataRow r) {
        //int val = checkSum - r.checkSum;
//        if (val != 0) {
//            return val;
//        }
//        val = data.size() - r.data.size();
//        if (val != 0) {
//            return val;
//        }
        int val;
        for (Map.Entry<String, Field> entry
                : data.entrySet()) {
            Field f = r.getField(entry.getKey());
            if (f == null) {
                continue;
            }
            val = entry.getValue().compareTo(f);
            if (val != 0) {
                return val;
            }
        }
        return fieldsSet() - r.fieldsSet();
        //if (r.data.size()> data.size()) return -1;
        //return 0;
    }

    @Override
    public int partialMatch(DataRow r) {
        int val = 0;
        for (Map.Entry<String, Field> entry
                : data.entrySet()) {
            Field f = r.getField(entry.getKey());
            if (f == null) {
                continue;
            }
            val = entry.getValue().compareTo(f);
            if (val != 0) {
                return val;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        String str = "row(";
        for (Entry<String, Field> entry : data.entrySet()) {
            str += entry.getKey() + ":" + entry.getValue().getComparable();
            str += ", ";
        }
      
        str += ")";
        return str;
    }

}
