/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join.anti;

import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.binaryop.join.HashJoin;
import ibd.query.binaryop.join.JoinPredicate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Performs a Hash Left Anti Join between the left and the right operations using
 * the terms provided by the join predicate.
 * Only tuples from the left side are returned, and only if they have no matches 
 * with tuples from the right side
 *
 * @author Sergio
 */
public class HashLeftAntiJoin extends HashJoin {


    public HashLeftAntiJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }

    @Override
    public String getJoinAlgorithm() {
        return "Hash Left Anti Join";
    }
    
    @Override
    public void setExposedDataSources() throws Exception {

        //only data sources from the left side are used to produce returning tuples
        ReferedDataSource left[] = getLeftOperation().getExposedDataSources();
        dataSources = new ReferedDataSource[left.length];
        System.arraycopy(left, 0, dataSources, 0, left.length);

    }
    
    @Override
    public Map<String, List<String>> getContentInfo() {
        //only data sources from the left side are used to produce returning tuples
        return getLeftOperation().getContentInfo();
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new HashJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the Hash Left Anti Join
     * between the two underlying operations.
     */
    private class HashJoinIterator extends UnpagedOperationIterator {

        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;

        public HashJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            //scan all tuples that comes from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false);

            //builds a hash for the right side tuples
            buildHash(processedTuples);

        }

        

        @Override
        protected Tuple findNextTuple() {

            //the left side cursor only advances if the current left side tuple is done.
            //it means all corresponding tuples from the right side were processed
            while (leftTuples.hasNext()) {
                Tuple currentLeftTuple = leftTuples.next();

                //the lookup conditions are filled with values taken from the computed rows from the current left side
                String key = fillKey(currentLeftTuple);
                
                //checks if the left-side tuples matches tuples from the right side
                boolean exists = tuples.containsKey(key);

                //if there is NO match, the left side tuple is returned
                if (!exists) {
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(currentLeftTuple);
                    return tuple;

                }

            }

            //no more tuples to be joined
            return null;
        }
    }

}
