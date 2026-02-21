/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.lookup;

import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;

/**
 * This filter negates another filter
 * @author sergio
 */
public class NegationLookUp implements LookupFilter{
    
    
    LookupFilter filter;
    
    /**
     *
     * @param filter the filter to be nagated
     */
    public NegationLookUp(LookupFilter filter){
        this.filter = filter;
    }

    /**
     *
     * @param tuple
     * @return true if the tuple does not match this filter predicates
     */
    @Override
    public boolean match(Tuple tuple) {
        return !(filter.match(tuple));
    }
    
    @Override
    public boolean match(LinkedDataRow row) {
        return !(filter.match(row));
    }
    
    @Override
    public String toString() {
        return "NOT "+filter.toString();
    }
    
    
}
