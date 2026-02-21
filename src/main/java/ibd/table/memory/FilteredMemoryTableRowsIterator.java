/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.memory;

import ibd.table.csv.*;
import ibd.table.lookup.RowLookupFilter;
import ibd.table.prototype.LinkedDataRow;
import sources.csv.InvalidCsvException;

/**
 * This class defines the behavior of the iterator functions next() and
 * hasNext()
 *
 * @author Sergio
 */
public class FilteredMemoryTableRowsIterator extends MemoryTableRowsIterator {

    RowLookupFilter filter;

    public FilteredMemoryTableRowsIterator(MemoryTable memoryTable, RowLookupFilter filter) throws Exception {
        super(memoryTable);
        this.filter = filter;
    }

    
    @Override
    protected boolean match(LinkedDataRow dataRow){
        return filter.match(dataRow);
    }
    
}
