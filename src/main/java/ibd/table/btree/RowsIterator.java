/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.btree;

import ibd.index.btree.AllKeysIterator;
import ibd.index.btree.table.BPlusTreeFileTable;
import ibd.table.prototype.LinkedDataRow;
import java.util.Iterator;

/**
 * This class defines the behavior of the iterator functions next() and
 * hasNext()
 *
 * @author Sergio
 */
public abstract class RowsIterator implements Iterator<LinkedDataRow> {

    LinkedDataRow nextValue = null;
    
    BPlusTreeFileTable btree;

    public RowsIterator(BPlusTreeFileTable btree) {
        this.btree = btree;
    }
    

    /**
     * Finds the next value. If the next value was already computed by
     * hasNext(), use it.
     *
     * @return
     */
    @Override
    public LinkedDataRow next() {

        
        if (nextValue != null) {
            LinkedDataRow next_ = nextValue;
            nextValue = null;
            return next_;
        }

        return findNextTuple();

    }

    /**
     * verifies if there is a next value. Is there is one, store it so the call
     * to next may access it.
     *
     * @return true if there is a next value
     */
    @Override
    public boolean hasNext() {

        if (nextValue != null) {
            return true;
        }
        nextValue = findNextTuple();

        return (nextValue != null);
    }

    /**
     * This is the function that actually locates the next value.
     *
     * @return the next value, or null if there isnt any.
     */
    protected abstract LinkedDataRow findNextTuple();
}
