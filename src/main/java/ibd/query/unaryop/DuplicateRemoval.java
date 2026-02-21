/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import java.util.Iterator;
import java.util.List;

/**
 * This operation remove a tuple whose rows are equal to the rows of the previously accessed tuple.
 * If tuples are sorted, this operation removes all duplicated tuples
 *
 * @author Sergio
 */
public class DuplicateRemoval extends UnaryOperation {


    /**
     *
     * @param op the operation to be connected into this unary operation
     * @throws Exception
     */
    public DuplicateRemoval(Operation op) throws Exception {
        super(op);
    }

    /**
     *
     * @return the name of the operation
     */
    @Override
    public String toString() {
        return "Sorted Duplicate Removal";
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new DuplicateRemovalIterator(processedTuples, withFilterDelegation);
    }

    /**
     * this class produces resulting tuples by removing duplicates from the
     * child operation
     */
    private class DuplicateRemovalIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;
        Tuple prevTuple = null;

        public DuplicateRemovalIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            tuples = childOperation.lookUp(processedTuples, false);//accesses all tuples produced by the child operation 
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                
                //the tuple is returned only if it differs from the previous tuple
                if (prevTuple == null || (!tp.equals(prevTuple))) {
                    prevTuple = tp;
                    return tp;
                }

            }
            return null;
        }

    }
}
