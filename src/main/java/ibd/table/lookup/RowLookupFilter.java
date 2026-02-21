/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.lookup;

import ibd.table.prototype.LinkedDataRow;

/**
 *
 * @author ferna
 */
public interface RowLookupFilter {
    
    /**
     * 
     * @param row the row to be compared
     * @return true if the row matches the condition determined by this filter
     */
    public boolean match(LinkedDataRow row) ;
    
   
    
}
