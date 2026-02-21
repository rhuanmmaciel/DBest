/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.lookup;

import ibd.query.ColumnDescriptor;
import ibd.query.Tuple;
import ibd.table.ComparisonTypes;
import ibd.table.prototype.LinkedDataRow;


/**
 * this filter finds tuples based on a single column comparison against a literal value. 
 * @author Sergio
 */
public class SingleColumnLookupFilter implements LookupFilter{

   Element elem1;
    
    /*
    * the comparison type
    */
    protected int comparisonType;
    
    Element elem2;
    
    /**
     *
     * @param col the name of the column to be placed at the left side of the comparison. The name can be prefixed by the table name (e.g. 'tab.col')
     * @param comparisonType the comparison type
     * @throws java.lang.Exception
     */
    public SingleColumnLookupFilter(Element element1, int comparisonType, Element element2) throws Exception{
        this.comparisonType = comparisonType;
        this.elem1 = element1;
        this.elem2 = element2;
    }
    
    
    
    public ColumnDescriptor getColumnDescriptor(){
        return ((ColumnElement)elem1).getColumnDescriptor();
    }
    
    public Element getFirstElement(){
        return elem1;
    }
    
    public Element getSecondElement(){
        return elem2;
    }
         
    
    public void setFirstElement(Element elem){
        this.elem1 = elem;
    }
    
    public void setSecondElement(Element elem){
        this.elem2 = elem;
    }
    
    /**
     * @return the comparisonType
     */
    public int getComparisonType() {
        return comparisonType;
    }
    
    public Element getComparedElement(){
        return elem2;
    }
    
    public Comparable getValue(Tuple tuple){
        return elem2.getValue(tuple);
    }
    
    public Comparable getValueFromRow(LinkedDataRow row){
        return elem2.getValueFromRow(row);
    }
    
    @Override
    public boolean match(Tuple tuple) {
        //ibd.query.QueryStats.COMPARE_FILTER++;
        //compares the left side column against a right side value
        //return ComparisonTypes.match(tuple.rows[tupleIndex].getValue(column.getColumnName()), value, comparisonType);
        //System.out.println(tuple.rows[tupleIndex].getValue(colIndex));
        return ComparisonTypes.match(elem1.getValue(tuple), elem2.getValue(tuple), comparisonType);
    }
    
   @Override
    public boolean match(LinkedDataRow row){
        return ComparisonTypes.match(elem1.getValueFromRow(row), elem2.getValueFromRow(row), comparisonType);
    }
    
    @Override
    public String toString() {
        String compType = ComparisonTypes.getComparisonOperation(comparisonType);
        return elem1+compType+elem2;
    }
}
