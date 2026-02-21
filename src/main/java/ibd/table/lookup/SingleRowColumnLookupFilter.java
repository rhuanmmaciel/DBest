/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.lookup;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.LinkedDataRow;


/**
 * this filter finds rows based on a single column comparison against a literal value. 
 * @author Sergio
 */
public abstract class SingleRowColumnLookupFilter implements RowLookupFilter{

    RowColumnElement elem1;
    
    /*
    * the comparison type
    */
    protected int comparisonType;
    
    
    
    /**
     *
     * @param colIndex the index of the column to be placed at the left side of the comparison.
     * @param comparisonType the comparison type
     * @throws java.lang.Exception
     */
    public SingleRowColumnLookupFilter(int colIndex, int comparisonType) throws Exception{
        this.comparisonType = comparisonType;
        this.elem1 = new RowColumnElement(colIndex); 
    }
    
    /**
     * @return the comparisonType
     */
    public int getComparisonType() {
        return comparisonType;
    }
    
    
    public abstract Comparable getValue();
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean match(LinkedDataRow row) {
        
        //compares the left side column against a right side value
        //return ComparisonTypes.match(tuple.rows[tupleIndex].getValue(column.getColumnName()), value, comparisonType);
        //System.out.println(tuple.rows[tupleIndex].getValue(colIndex));
        return ComparisonTypes.match(elem1.getValue(row), getValue(), comparisonType);
    }
    
    @Override
    public String toString() {
        String col = "table"+"."+"name";
        String compType = ComparisonTypes.getComparisonOperation(comparisonType);
        return col+compType+getValue();
    }
    
}
