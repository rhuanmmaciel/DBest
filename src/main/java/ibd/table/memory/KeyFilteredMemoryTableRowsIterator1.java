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
public class KeyFilteredMemoryTableRowsIterator1 extends MemoryTableRowsIterator {

    RowLookupFilter filter;
    LinkedDataRow pkRow;

    public KeyFilteredMemoryTableRowsIterator1(MemoryTable memoryTable, LinkedDataRow pkRow, RowLookupFilter filter) throws Exception {
        super(memoryTable);
        this.filter = filter;
        this.pkRow = pkRow;
    }

    
    @Override
    protected boolean match(LinkedDataRow dataRow){
        return (filter.match(dataRow) && dataRow.compareTo(pkRow)==0);
    }
}
