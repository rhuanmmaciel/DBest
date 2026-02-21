/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import java.util.Iterator;
import java.util.List;

/**
 * Performs a nested loop join between the left and the right operations using
 * the terms provided by the join predicate.
 *
 * @author Sergio
 */
public class NestedLoopJoin extends LookupJoin {

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param joinPredicate the join predicate
     * @throws Exception
     */
    public NestedLoopJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }


    /**
     *
     * @return the name of the operation
     */
    @Override
    public String getJoinAlgorithm() {
        return "Nested Loop Join";
    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a simple nested loop join over the
     * tuples from the left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new NestedLoopJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the nested loop join between the two
     * underlying operations.
     */
    private class NestedLoopJoinIterator extends UnpagedOperationIterator {

        //keeps the current state of the left side of the join: The current left tuple read
        Tuple currentLeftTuple;
        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the right side
        Iterator<Tuple> rightTuples;

        public NestedLoopJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            currentLeftTuple = null;
            //scan all tuples that comes from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false);
        }

        
        @Override
        protected Tuple findNextTuple() {

            //the left side cursor only advances if the current left side tuple is done.
            //it means all corresponding tuples from the right side were processed
            while (currentLeftTuple != null || leftTuples.hasNext()) {
                if (currentLeftTuple == null) {
                    currentLeftTuple = leftTuples.next();

                    //the computed rows from the current left side tuple can be used by the right part of the join
                    processedTuples.add(currentLeftTuple);

                    //the lookup conditions are filled with values taken from the computed rows from the current left side
                    fillFilter(currentLeftTuple);
                    //lookup the tuples from the right side
                    rightTuples = rightOperation.lookUp(processedTuples, true);

                    //the computed tuple from the left is removed from the processed list, since the right side of the join already finished its processing
                    processedTuples.remove(processedTuples.size() - 1);
                }
                
                processedTuples.add(currentLeftTuple);

                //iterate through the right side tuples that satisfy the lookup
                while (rightTuples.hasNext()) {
                    //sgbd.info.Query.COMPARE_JOIN++;
                    Tuple curTuple2 = (Tuple) rightTuples.next();
                    
                    if (!(rightOperation.canProcessDelegatedFilters() || joinFilter.match(curTuple2)))
                        continue;
                    
                    //create a returning tuple and add the joined tuples
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(currentLeftTuple, curTuple2);
                    
                    processedTuples.remove(processedTuples.size() - 1);
                    return tuple;

                }
                //All corresponding tuples from the right side processed. 
                //set null to allow left side cursor to advance
                currentLeftTuple = null;
                
                //the computed tuple from the left is removed from the processed list, since the right side of the join already finished its processing
                processedTuples.remove(processedTuples.size() - 1);
            }

            //no more tuples to be joined
            return null;
        }
    }

}
