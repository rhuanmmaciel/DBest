/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.lookup;

import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;

/**
 * A lookup filter is responsible for finding tuples that satisfy a filter condition. 
 * The condition is defined by sub-classes that implement this interface.
 * Operations can use the match() function or access the conditions directly if the filter needs to be performed in a different  way.
 * For instance, to perform the lookup efficiently, one may pass the condition to an index.
 * @author Sergio
 */
public interface LookupFilter {
    
    /**
     * 
     * @param tuple the tuple to be compared
     * @return true if the tuple matches the condition determined by this filter
     */
    public boolean match(Tuple tuple) ;
    
    public boolean match(LinkedDataRow row) ;
}
