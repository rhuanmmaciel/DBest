/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.lookup;


/**
 * this filter finds rows based on a single column comparison against a literal value. 
 * @author Sergio
 */
public class SingleRowColumnValueLookupFilter extends SingleRowColumnLookupFilter{

    RowValueElement elem2;

    public SingleRowColumnValueLookupFilter(int colIndex, int comparisonType, Comparable value) throws Exception {
        super(colIndex, comparisonType);
        elem2 = new RowValueElement(value);
    }
    
    
    
    /**
     *
     * @return the literal value placed at the right side of the comparison.
     */
    @Override
    public Comparable getValue(){
        return elem2.getValue(null);
    }
    
    
}
