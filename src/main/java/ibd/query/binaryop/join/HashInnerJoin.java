/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Performs a Hash inner join between the left and the right operations using
 * the terms provided by the join predicate. Tuples from the right side are hashed
 * to speed up lookups. The hash uses as key the columns from the join predicate.
 * The hash inner join returns tuples from the left side joined with the matching
 * tuples from the right side
 *
 * @author Sergio
 */
public class HashInnerJoin extends HashJoin {


    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param joinPredicate the join predicate
     * @throws Exception
     */
    public HashInnerJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }

    
    /**
     *
     * @return the name of the operation
     */
    @Override
    public String getJoinAlgorithm() {
        return "Hash Inner Join";
    }
    
    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs the hash join
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new HashJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the hash join iterator.
     */
    private class HashJoinIterator extends UnpagedOperationIterator {

        //keeps the current state of the left side of the join: The current left tuple read
        Tuple currentLeftTuple;
        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the right side
        Iterator<Tuple> rightTuples;

        public HashJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            currentLeftTuple = null;
            //scan all tuples that comes from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false);
            
            //builds a hash for the right side tuples
            buildHash(processedTuples);

        }

        

        @Override
        protected Tuple findNextTuple() {

            //the left side cursor only advances if the current left side tuple is done.
            //it means all corresponding tuples from the right side were processed
            while (currentLeftTuple != null || leftTuples.hasNext()) {
                if (currentLeftTuple == null) {
                    currentLeftTuple = leftTuples.next();

                    //the lookup conditions are filled with values taken from the computed rows from the current left side
                    String key = fillKey(currentLeftTuple);
                    List<Tuple> result = tuples.get(key);
                    if (result != null) {
                        rightTuples = result.iterator();
                    } else {
                        rightTuples = new ArrayList<Tuple>().iterator();
                    }

                }

                //iterate through the right side tuples that satisfy the lookup
                while (rightTuples.hasNext()) {
                    //sgbd.info.Query.COMPARE_JOIN++;
                    Tuple curTuple2 = (Tuple) rightTuples.next();
                    
                    //create a returning tuple and add the joined tuples
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(currentLeftTuple, curTuple2);
                    return tuple;
                    
                }
                //All corresponding tuples from the right side processed. 
                //set null to allow left side cursor to advance
                currentLeftTuple = null;

            }

            //no more tuples to be joined
            return null;
        }
    }

}
