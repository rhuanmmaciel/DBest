/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.btree;

import ibd.table.btree.RowsIterator;
import ibd.index.btree.DictionaryPair;
import ibd.index.btree.AllKeysIterator;
import ibd.index.btree.table.BPlusTreeFileTable;
import ibd.index.btree.table.BinaryValue;
import ibd.table.prototype.LinkedDataRow;
import java.util.Iterator;

/**
 * This class defines the behavior of the iterator functions next() and
 * hasNext()
 *
 * @author Sergio
 */
public class AllRowsIterator extends RowsIterator {

    Iterator<DictionaryPair> valueIterator = null;
    public AllRowsIterator(BPlusTreeFileTable btree) {
        super(btree);
        valueIterator = btree.searchAllIterator();
    }
    
    

    /**
     * This is the function that actually locates the next value.
     *
     * @return the next value, or null if there isnt any.
     */
    @Override
    protected LinkedDataRow findNextTuple() {

        while (valueIterator.hasNext()) {
            // Iterate through the dictionary of each node
            DictionaryPair dp = valueIterator.next();
            BinaryValue v = (BinaryValue) dp.getValue();

            //byte bytes_[] = (byte[]) v.get(0);
            //LinkedDataRow dataRow = btree.prototype.convertBinaryToRowData(bytes_);
            //nextValue = dataRow;
            nextValue = v.rowData;
            return nextValue;

        }

        return null;

    }
}
