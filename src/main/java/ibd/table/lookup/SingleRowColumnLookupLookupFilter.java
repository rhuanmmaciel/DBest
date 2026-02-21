/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.lookup;

import ibd.query.lookup.SingleColumnLookupFilter;


/**
 * this filter finds rows based on a single column comparison against a literal value. 
 * @author Sergio
 */
public class SingleRowColumnLookupLookupFilter extends SingleRowColumnLookupFilter{

    /*
    * the filter that contains the filtered value
    */
    RowReferenceElement elem2;

    public SingleRowColumnLookupLookupFilter(int colIndex, int comparisonType, SingleColumnLookupFilter filter) throws Exception {
        super(colIndex, comparisonType);
        elem2 = new RowReferenceElement(filter);
    }
    
    
    @Override
    public Comparable getValue() {
        return elem2.getValue(null);
    }
    
    
}
