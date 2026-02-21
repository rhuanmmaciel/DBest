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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Performs a Hash Right Anti Join between the left and the right operations using
 * the terms provided by the join predicate.
 * Only tuples from the right side are returned, and only if they have no matches 
 * with tuples from the left side
 * 
 * @author Sergio
 */
public class HashRightAntiJoin extends HashJoin {


    
    public HashRightAntiJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
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
        return "Hash Right Anti Join";
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new HashJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the Hash Right Anti Join
     * between the two underlying operations.
     */
    private class HashJoinIterator extends UnpagedOperationIterator {

        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;

        //the class that traverses the tuples stored in the hash table
        TupleTraverser tupleTraverser = null;

        public HashJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            //scan all tuples that comes from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false);

            //builds a hash for the right side tuples
            buildHash(processedTuples);

        }

        

        @Override
        protected Tuple findNextTuple() {

            //processes all left side tuples
            while (leftTuples.hasNext()) {
                Tuple currentLeftTuple = leftTuples.next();

                //the lookup conditions are filled with values taken from the computed rows from the current left side
                String key = fillKey(currentLeftTuple);
                //found tuples are removed from the hash. They will not be returned
                tuples.remove(key);
            }

            //the tuple traverser is created only once
            if (tupleTraverser == null) {
                tupleTraverser = new TupleTraverser(tuples);
            }
            
            //iterate through the right side tuples that have NO matches
            while (tupleTraverser.hasNext()) {
                //sgbd.info.Query.COMPARE_JOIN++;
                Tuple curTuple2 = (Tuple) tupleTraverser.nextTuple();
                //create a returning tuple and add the joined tuples
                Tuple tuple = new Tuple();
                tuple.setSourceRows(curTuple2);
                return tuple;

            }

            //no more tuples to be joined
            return null;
        }
    }

    class TupleTraverser {

        private final HashMap<String, List<Tuple>> tuples;
        private Iterator<Map.Entry<String, List<Tuple>>> mapIterator;
        private List<Tuple> currentList;
        private int currentIndex;

        public TupleTraverser(HashMap<String, List<Tuple>> tuples) {
            this.tuples = tuples;
            this.mapIterator = tuples.entrySet().iterator();
            this.currentList = null;
            this.currentIndex = 0;
        }

        public Tuple nextTuple() {
            // Check if the current list is exhausted or not initialized
            if (currentList == null || currentIndex >= currentList.size()) {
                // Move to the next entry in the map
                if (mapIterator.hasNext()) {
                    Map.Entry<String, List<Tuple>> entry = mapIterator.next();
                    currentList = entry.getValue();
                    currentIndex = 0;
                } else {
                    // No more tuples left
                    return null;
                }
            }

            // Get the next tuple from the current list
            Tuple nextTuple = currentList.get(currentIndex);
            currentIndex++;
            return nextTuple;
        }

        public boolean hasNext() {
            // Check if there are more tuples in the current list or in the map
            return (currentList != null && currentIndex < currentList.size()) || mapIterator.hasNext();
        }
    }

}
