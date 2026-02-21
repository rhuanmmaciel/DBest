/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join.anti;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.binaryop.join.LookupJoin;
import ibd.query.lookup.CompositeLookupFilter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Performs a nested loop join between the left and the right operations using
 * the terms provided by the join predicate.
 *
 * @author Sergio
 */
public class NestedLoopLeftAntiJoin extends LookupJoin {

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param joinPredicate the join predicate
     * @throws Exception
     */
    public NestedLoopLeftAntiJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }

    
    
    @Override
    public void setExposedDataSources() throws Exception {

        ReferedDataSource left[] = getLeftOperation().getExposedDataSources();
        dataSources = new ReferedDataSource[left.length];
        System.arraycopy(left, 0, dataSources, 0, left.length);

    }

    @Override
    public Map<String, List<String>> getContentInfo() {
        return getLeftOperation().getContentInfo();
    }

    @Override
    public String getJoinAlgorithm() {
        return "Nested Loop Left Anti Join";
    }

    
    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs am anti nested loop join over the
     * tuples from the left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new NestedLoopLeftAntiJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the anti join between the
     * two underlying operations.
     */
    private class NestedLoopLeftAntiJoinIterator extends UnpagedOperationIterator {

        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        
        public NestedLoopLeftAntiJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            //scan all tuples that comes from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false);
        }

        private boolean exists() {
            if (rightOperation.canProcessDelegatedFilters() || hasNoFilters) {
                return rightOperation.exists(processedTuples, true);
            }

            Iterator tuples = rightOperation.lookUp(processedTuples, false);
            while (tuples.hasNext()) {
                Tuple tuple = (Tuple) tuples.next();
                if (rightOperation.canProcessDelegatedFilters() || joinFilter.match(tuple)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        protected Tuple findNextTuple() {

            while (leftTuples.hasNext()) {
                Tuple currentLeftTuple = leftTuples.next();

                //the computed rows from the current left side tuple can be used by the right part of the join
                processedTuples.add(currentLeftTuple);

                //the lookup conditions are filled with values taken from the computed rows from the current left side
                fillFilter(currentLeftTuple);

                //check if there exists a correpondendence on the right side
                boolean exists = exists();

                //the computed tuple from the left is removed from the processed list, since the right side of the join already finished its processing
                processedTuples.remove(processedTuples.size() - 1);

                if (!exists) {
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(currentLeftTuple);
                    return tuple;
                }

            }

            //no more left tuples to be joined
            return null;
        }
    }

}
