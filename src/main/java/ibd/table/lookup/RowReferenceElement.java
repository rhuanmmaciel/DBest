/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.lookup;

import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.table.prototype.LinkedDataRow;

/**
 *
 * @author ferna
 */
public class RowReferenceElement extends RowElement{
    SingleColumnLookupFilter filter;
    
    public RowReferenceElement(SingleColumnLookupFilter filter){
        this.filter = filter;
    }
    
    @Override
    public  Comparable getValue(LinkedDataRow row){
        return filter.getValue(null);
    }
}
