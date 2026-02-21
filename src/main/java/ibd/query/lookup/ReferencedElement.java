/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.lookup;

import ibd.query.ColumnDescriptor;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import java.util.List;

/**
 *
 * @author ferna
 */
public class ReferencedElement extends ColumnElement{
    protected Comparable value;
    
    public ReferencedElement(String valueCol) throws Exception{
        super(valueCol);
    }
    
    public ReferencedElement(ColumnDescriptor columnDescriptor) throws Exception{
        super(columnDescriptor);
    }
    
    public void setValue(List<Tuple> processedTuples) {
        //value = processedTuples.get(tl.tupleIndex).rows[tl.rowIndex].getValue(valueColumn.getColumnName());
        //value = processedTuples.get(tl.tupleIndex).rows[tl.rowIndex].getValue(tl.colIndex);
        value = processedTuples.get(columnDescriptor.getColumnLocation().tupleIndex).rows[columnDescriptor.getColumnLocation().rowIndex].getValue(columnDescriptor.getColumnLocation().colIndex);
    }
    
    
    
    @Override
    public Comparable getValue(Tuple tuple) {
        return value;
    }
    public Comparable getValueFromRow(LinkedDataRow row){
        return value;
    }
    
}
