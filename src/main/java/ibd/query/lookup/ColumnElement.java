/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.lookup;

import ibd.query.ColumnDescriptor;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;

/**
 *
 * @author ferna
 */
public class ColumnElement extends Element{
    protected ColumnDescriptor columnDescriptor = null;
    
    

    public ColumnElement(String colName) throws Exception{
        this.columnDescriptor = new ColumnDescriptor(colName);
    }
    
    public ColumnElement(ColumnDescriptor columnDescriptor) throws Exception{
        this.columnDescriptor = columnDescriptor;
    }
    
    public ColumnDescriptor getColumnDescriptor(){
        return columnDescriptor;
    }
    
    
    @Override
    public Comparable getValue(Tuple tuple) {
        return tuple.rows[columnDescriptor.getColumnLocation().rowIndex].getValue(columnDescriptor.getColumnLocation().colIndex);
        
    }
    
    @Override
    public Comparable getValueFromRow(LinkedDataRow row){
        return row.getValue(columnDescriptor.getColumnLocation().colIndex);
    }
    
    @Override
    public String toString(){
    return columnDescriptor.getTableName() + "." + columnDescriptor.getColumnName();
    }
}
