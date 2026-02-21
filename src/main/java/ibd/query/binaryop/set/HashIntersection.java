/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.set;

import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.query.fields.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Performs a difference between the left and the right operations.
 *
 * @author Sergio
 */
public class HashIntersection extends Set {

    HashMap<String, Tuple> tuples;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @throws Exception
     */
    public HashIntersection(Operation leftOperation, Operation rightOperation) throws Exception {
        super(leftOperation, rightOperation);
    }

    /**
     *
     * @return the name of the operation
     */
    @Override
    public String toString() {
        return "Hash Intersection";
    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a simple nested loop join over the
     * tuples from the left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new UnionIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the union between the two
     * underlying operations.
     */
    private class UnionIterator extends UnpagedOperationIterator {

        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the left side
        Iterator<Tuple> rightTuples;
        
        int tupleSize = 0;

        /**
         * @param processedTuples the tuples that come from operations already
         * processed. The columns from these tuples can be used by the
         * unprocessed operations, like for filtering.
         * @param lookup the condition from the parent operation that needs to
         * be satisfied
         */
        public UnionIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            tuples = new HashMap<>();
            leftTuples = leftOperation.lookUp(processedTuples, false); //iterate over all tuples from the left side
            rightTuples = rightOperation.lookUp(processedTuples, false); //iterate over all tuples from the left side
            try {
                tupleSize = rightOperation.getTupleSize();
            } catch (Exception ex) {
            }
        }

        private String getKey(Tuple tuple) {
            String key = "";
            for (LinkedDataRow row : tuple.rows) {
                for (int i = 0; i < row.getFieldsSize(); i++) {
                    Comparable value = row.getValue(i);
                    key += value;
                }

            }
            return key;
        }

        /**
         *
         * @return the next satisfying tuple, if any
         */
        @Override
        protected Tuple findNextTuple() {

            while (rightTuples.hasNext()) {
                Tuple rightTuple = rightTuples.next();
                String key = getKey(rightTuple);
                tuples.put(key, rightTuple);
                QueryStats.MEMORY_USED += tupleSize;
            }
            

            while (leftTuples.hasNext()) {
                Tuple leftTuple = leftTuples.next();
                String key = getKey(leftTuple);
                if ((tuples.containsKey(key))) {
                    return leftTuple;
                }
            }

            return null;

        }

    }

}
