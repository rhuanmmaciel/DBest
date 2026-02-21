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
 * the join conditions provided by the join terms.
 *
 * @author Sergio
 */
public class CrossJoin extends Join {

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @throws Exception
     */
    public CrossJoin(Operation leftOperation, Operation rightOperation) throws Exception {
        super(leftOperation, rightOperation, new JoinPredicate());
    }
    
    
    @Override
    public boolean useLeftSideLookups() {
        return true;
    }

    /**
     *
     * @return the name of the operation
     */
    @Override
    public String getJoinAlgorithm() {
        return "Cross Join";
    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a cross join over the
     * tuples from the left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples,  boolean withFilterDelegation) {
        return new CrossJoinIterator(processedTuples,  withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the cross join between the two
     * underlying operations.
     */
    private class CrossJoinIterator extends UnpagedOperationIterator {

        //keeps the current state of the left side of the join: The current tuple read
        Tuple currentLeftTuple;
        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the left side
        Iterator<Tuple> rightTuples;
        
        
        public CrossJoinIterator(List<Tuple> processedTuples,  boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            currentLeftTuple = null;
            
            //leftTuples = leftOperation.run(); //iterate over all tuples that comes from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false); //iterate over all tuples that comes from the left side
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

                    //lookup the tuples from the right side
                    rightTuples = rightOperation.lookUp(processedTuples, false);

                    processedTuples.remove(processedTuples.size() - 1);
                    
                }
                
                processedTuples.add(currentLeftTuple);

                //iterate through the right side tuples that satisfy the lookup
                while (rightTuples.hasNext()) {
                    //sgbd.info.Query.COMPARE_JOIN++;
                    Tuple curTuple2 = (Tuple) rightTuples.next();
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
