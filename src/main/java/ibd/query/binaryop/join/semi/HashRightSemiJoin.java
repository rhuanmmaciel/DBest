/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join.semi;

import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.binaryop.join.HashJoin;
import ibd.query.binaryop.join.JoinPredicate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Performs a Hash Right Semi Join between the left and the right operations using
 * the terms provided by the join predicate.
 * Only tuples from the right side are returned, and only if they match at least
 * one tuple from the left side
 *
 * @author Sergio
 */
public class HashRightSemiJoin extends HashJoin {


    public HashRightSemiJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }

    @Override
    public void setExposedDataSources() throws Exception {

        //only data sources from the right side are used to produce returning tuples
        ReferedDataSource right[] = getRightOperation().getExposedDataSources();
        dataSources = new ReferedDataSource[right.length];
        System.arraycopy(right, 0, dataSources, 0, right.length);

    }
    
    @Override
    public Map<String, List<String>> getContentInfo() {
        //only data sources from the right side are used to produce returning tuples
        return getRightOperation().getContentInfo();
    }

    @Override
    public String getJoinAlgorithm() {
        return "Hash Right Semi Join";
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new HashJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the Hash Right Semi Join
     * between the two underlying operations.
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
                    
                    //uses the hash table to find the matched right side tuples that will be returned
                    List<Tuple> result = tuples.get(key);
                    
                    //the matched tuples are removed from the hash to assure that they are returned only once
                    if (result != null) {
                        tuples.remove(key);
                    }
                    
                    //sets the iterator over the matched right side tuples
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
                    tuple.setSourceRows(curTuple2);
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
