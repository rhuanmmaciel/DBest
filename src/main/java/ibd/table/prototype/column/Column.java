package ibd.table.prototype.column;

import ibd.exceptions.DataBaseException;
import ibd.table.prototype.metadata.Metadata;

public class Column extends Metadata {

    public static final String BOOLEAN_TYPE = "BOOLEAN";
    public static final String DOUBLE_TYPE = "DOUBLE";
    public static final String FLOAT_TYPE = "FLOAT";
    public static final String INTEGER_TYPE = "INTEGER";
    public static final String LONG_TYPE = "LONG";
    public static final String STRING_TYPE = "STRING";
    public static final String BINARY_TYPE = "BINARY";
    public static final String NULL_TYPE = "NULL";
     public static final String UNKNOWN_TYPE = "UNKNOWN";

    protected String name;
    
    public int index;
    
    protected String type;

    public Column(String name, int size, short flags) {
        super(size, flags);
        this.name = name;
        checkErrors();
    }

    public Column(Column c) {
        super(c);
        this.name = c.name;
        checkErrors();
    }

    private void checkErrors() {
        if (getSize() == 0) {
            String error = "Uma coluna não pode ter tamanho zero!";
            String validator = "size_column > 0";
            throw new DataBaseException("Column->Constructor", error, validator);
        }
        if (isShift8Size() && !isDinamicSize()) {
            String error = "Uma coluna com tamanho expandido deve ser dinamica!";
            String validator = "SHIFT_8_SIZE + DINAMIC_SIZE == VALID";
            throw new DataBaseException("Column->Constructor", error, validator);
        }
        if (isBoolean() && isDinamicSize()) {
            String error = "Uma coluna do tipo boolean não pode ter tamanho dinamico!";
            String validator = "BOOLEAN + DINAMIC_SIZE == INVALID";
            throw new DataBaseException("Column->Constructor", error, validator);
        }
        int strl = name.length();
        if (strl > 240 || strl == 0) {
            String error = "Uma coluna com nome de tamanho inválido!";
            DataBaseException ex = new DataBaseException("Column->Constructor", error);
            ex.addValidation("Max:240");
            ex.addValidation("Min:1");
            throw ex;
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getSizeinBytes() {
        return getType().getBytes().length + Integer.BYTES
                + getName().getBytes().length + Integer.BYTES
                + Integer.BYTES
                + Short.BYTES;
    }

    
    public static Column copyColumn(Column col, String colName){
    Column newCol = null;
        switch (col.getType()) {
            case Column.INTEGER_TYPE ->
                newCol = new IntegerColumn(colName);
            case Column.STRING_TYPE ->
                newCol = new StringColumn(colName);
            case Column.LONG_TYPE ->
                newCol = new LongColumn(colName);
            case Column.DOUBLE_TYPE ->
                newCol = new DoubleColumn(colName);
            case Column.FLOAT_TYPE ->
                newCol = new FloatColumn(colName);
        }
        return newCol;
    }
}
