/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import enums.ColumnDataType;
import ibd.table.Table;
import ibd.table.prototype.Header;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.BooleanColumn;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.column.DoubleColumn;
import ibd.table.prototype.column.FloatColumn;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.LongColumn;
import ibd.table.prototype.column.StringColumn;

/**
 *
 * @author ferna
 */
public class RowConverter {

    Table table = null;
    Header header;


    private Prototype convertPrototype() {
        
        Prototype prot = header.getPrototype();
        if (prot==null) return null;
        
        Prototype newProt = new Prototype();

        for (Column c : prot) {
            
            newProt.addColumn(convertColumn(c));
        }
        return newProt;
    }
    
    public static ColumnDataType convertDataType(Column col){
    ColumnDataType type;

        switch (col.getType()) {
            case "STRING":
                type = ColumnDataType.STRING;
                break;
            case "INTEGER":
                type = ColumnDataType.INTEGER;
                break;
            case "LONG":
                type = ColumnDataType.LONG;
                break;
            case "FLOAT":
                type = ColumnDataType.FLOAT;
                break;
            case "DOUBLE":
                type = ColumnDataType.DOUBLE;
                break;
            case "BOOLEAN":
                type = ColumnDataType.BOOLEAN;
                break;
            default:
                throw new AssertionError();
        }

        return type;
    }
    
    public static Column convertColumn(Column col) {
        Column newCol;

        switch (col.getType()) {
            case "STRING":
                newCol = new StringColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "INTEGER":
                newCol = new IntegerColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "LONG":
                newCol = new LongColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "FLOAT":
                newCol = new FloatColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "DOUBLE":
                newCol = new DoubleColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "BOOLEAN":
                newCol = new BooleanColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            default:
                throw new AssertionError();
        }

        return newCol;
    }

//    public BasicDataRow convertRow(BasicDataRow r) {
//        BasicDataRow dataRow = new BasicDataRow();
//        
//        Iterator<Entry<String, sgbd.prototype.query.fields.Field>> i = r..iterator();
//        while (i.hasNext()) {
//            Entry<String, sgbd.prototype.query.fields.Field> entry = i.next();
//            String col = entry.getKey();
//            sgbd.prototype.query.fields.Field field = entry.getValue();
//
//            switch (Util.typeOfColumn(field.getMetadata())) {
//                case "boolean":
//                    dataRow.setBoolean(col, field.getBoolean());
//                    break;
//                case "string":
//                    dataRow.setString(col, field.getString());
//                    break;
//                case "long":
//                    dataRow.setLong(col, field.getLong());
//                    break;
//                case "int":
//                    dataRow.setInt(col, field.getInt());
//                    break;
//                case "double":
//                    dataRow.setDouble(col, field.getDouble());
//                    break;
//                case "float":
//                    dataRow.setFloat(col, field.getFloat());
//                    break;
//                case "null":
//                default:
//                //return new NullField(metadata);
//            }
//
//        }
//        return dataRow;
//    }

    
}
