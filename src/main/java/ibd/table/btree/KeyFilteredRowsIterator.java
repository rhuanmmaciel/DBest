/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.btree;

import ibd.index.btree.DictionaryPair;
import ibd.index.btree.Key;
import ibd.index.btree.table.BPlusTreeFileTable;
import ibd.index.btree.table.BinaryValue;
import ibd.query.lookup.LookupFilter;
import ibd.table.ComparisonTypes;
import ibd.table.lookup.RowLookupFilter;
import ibd.table.prototype.LinkedDataRow;
import java.util.Collections;
import java.util.Iterator;

/**
 * This class defines the behavior of the iterator functions next() and
 * hasNext()
 *
 * @author Sergio
 */
public class KeyFilteredRowsIterator extends RowsIterator {

    LookupFilter filter;
    Iterator<DictionaryPair> valueIterator = null;

    public KeyFilteredRowsIterator(BPlusTreeFileTable btree, Key key, LookupFilter filter, int compType) {
        super(btree);
        this.filter = filter;
        switch (compType) {
            case ComparisonTypes.EQUAL -> valueIterator = btree.equalKeyIterator(key);
            case ComparisonTypes.GREATER_EQUAL_THAN -> valueIterator = btree.largerKeyIterator(key, true);
            case ComparisonTypes.GREATER_THAN -> valueIterator = btree.largerKeyIterator(key, false);
            case ComparisonTypes.LOWER_EQUAL_THAN -> valueIterator = btree.smallerKeyIterator(key, true);
            case ComparisonTypes.LOWER_THAN -> valueIterator = btree.smallerKeyIterator(key, false);
            case ComparisonTypes.DIFF -> valueIterator = btree.differentKeyIterator(key);
            case ComparisonTypes.IS_NULL -> valueIterator = Collections.emptyIterator();
            case ComparisonTypes.IS_NOT_NULL -> valueIterator = btree.searchAllIterator();
            default -> {
            }
        }
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
            LinkedDataRow dataRow = v.rowData;
            if (filter.match(dataRow)) {
                nextValue = dataRow;
                return nextValue;
            }
        }

        return null;

    }
}
