/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.csv;

import ibd.table.prototype.LinkedDataRow;
import sources.csv.InvalidCsvException;

/**
 * This class defines the behavior of the iterator functions next() and
 * hasNext()
 *
 * @author Sergio
 */
public class AllCSVRowsIterator extends CSVRowsIterator {


    public AllCSVRowsIterator(CSVTable csvTable) throws InvalidCsvException {
        super(csvTable);
    }

    
    @Override
    protected boolean match(LinkedDataRow dataRow){
    return true;
    }
}
