/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query;

import java.util.Iterator;

/**
 * This class defines the behavior of the iterator functions next() and
 * hasNext() sub-classes need to implement the findNextTuple() function to
 * indicate what the iterator is accessing and to control the cursor state.
 *
 * @author Sergio
 */
public abstract class OperationIterator implements Iterator<Tuple> {

    //stores the next tuple to be returned by the next() function
    Tuple nextTuple = null;

    /**
     * Finds the next tuple. If the next tuple was already computed by
     * hasNext(), use it.
     *
     * @return
     */
    @Override
    public Tuple next() {

        ibd.query.QueryStats.NEXT_CALLS++;
        if (nextTuple != null) {
            Tuple next_ = nextTuple;
            nextTuple = null;
            return next_;
        }

        return findNextTuple();

    }

    /**
     * verifies if there is a next tuple. Is there is one, store it so the call
     * to next may access it.
     *
     * @return true if there is a next tuple
     */
    @Override
    public boolean hasNext() {

        if (nextTuple != null) {
            return true;
        }
        nextTuple = findNextTuple();

        return (nextTuple != null);
    }

    /**
     * This is the function that actually locates the next tuple.
     *
     * @return the next tuple, or null if there isnt any.
     */
    protected abstract Tuple findNextTuple();

}
