/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.memory;

import ibd.table.csv.*;
import ibd.table.prototype.LinkedDataRow;
import sources.csv.InvalidCsvException;

/**
 * This class defines the behavior of the iterator functions next() and
 * hasNext()
 *
 * @author Sergio
 */
public class AllMemoryTableRowsIterator extends MemoryTableRowsIterator {


    public AllMemoryTableRowsIterator(MemoryTable memoryTable) throws Exception {
        super(memoryTable);
    }

    
    @Override
    protected boolean match(LinkedDataRow dataRow){
    return true;
    }
}
