/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join.outer;

import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.binaryop.join.HashJoin;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.table.prototype.LinkedDataRow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Performs a Hash Full Outer Join between the left and the right operations using
 * the terms provided by the join predicate.
 * Left and right side tuples that have no matches are also returned, complemented with null values.
 *
 * @author Sergio
 */
public class HashFullOuterJoin extends HashJoin {


    //the table that keeps the matched tuples from the right side
    HashMap<String, List<Tuple>> existingTuples;

    //a null tuple that is shared with all left side tuples that fail to join with right side tuples
    Tuple nullRightTuple;

    //a null tuple that is shared with all left side tuples that fail to join with right side tuples
    Tuple nullLeftTuple;

    public HashFullOuterJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }

    @Override
    public void prepare() throws Exception {

        super.prepare();

        //creates the null tuples
        setNullLeftTuple();
        setNullRightTuple();

    }

    /**
     * Creates a null tuple that contains null rows for all data sources that comes
     * from the left side
     * 
     * @throws java.lang.Exception
     */
    protected void setNullLeftTuple() throws Exception {
        ReferedDataSource left[] = getLeftOperation().getExposedDataSources();
        nullLeftTuple = new Tuple();
        nullLeftTuple.rows = new LinkedDataRow[left.length];
        for (int i = 0; i < left.length; i++) {
            LinkedDataRow row = new LinkedDataRow(left[i].prototype, false);
            //nullLeftTuple.rows[i] = new LinkedDataRow();
            nullLeftTuple.rows[i] = row;
        }

    }

    /**
     * Creates a null tuple that contains null rows for all data sources that comes
     * from the right side  
     * 
     * @throws java.lang.Exception
     */
    protected void setNullRightTuple() throws Exception {
        ReferedDataSource right[] = getRightOperation().getExposedDataSources();
        nullRightTuple = new Tuple();
        nullRightTuple.rows = new LinkedDataRow[right.length];
        for (int i = 0; i < right.length; i++) {
            LinkedDataRow row = new LinkedDataRow(right[i].prototype, false);
            //nullRightTuple.rows[i] = new LinkedDataRow();
            nullRightTuple.rows[i] = row;
        }

    }

    @Override
    public String getJoinAlgorithm() {
        return "Hash Full Outer Join";
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new HashJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the Hash Full Outer  Join
     * between the two underlying operations.
     */
    private class HashJoinIterator extends UnpagedOperationIterator {

        //keeps the current state of the left side of the join: The current left tuple read
        Tuple currentLeftTuple;
        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the right side
        Iterator<Tuple> rightTuples;

        boolean foundJoin = false;

        //the class that traverses the tuples stored in the hash table
        TupleTraverser tupleTraverser = null;

        public HashJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            currentLeftTuple = null;
            
            //scan all tuples that comes from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false);
            
            if (tuples == null) {
                existingTuples = new HashMap();
            }
            
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
                    foundJoin = false;
                    //the lookup conditions are filled with values taken from the computed rows from the current left side
                    String key = fillKey(currentLeftTuple);
                    
                    //moves tuples from the hash table into the existingTuples table
                    List<Tuple> result = existingTuples.get(key);
                    if (result == null) {
                        result = tuples.get(key);
                        if (result != null) {
                            existingTuples.put(key, result);
                            tuples.remove(key);
                        }
                    }
                    
                    //sets the iterator over the matched tuples from the right side
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

                    foundJoin = true;
                    return tuple;

                }

                //the left side tuples with no matches are complemented with null values
                if (!foundJoin) {
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(currentLeftTuple, nullRightTuple);
                    currentLeftTuple = null;
                    return tuple;
                }

                //All corresponding tuples from the right side processed. 
                //set null to allow left side cursor to advance
                currentLeftTuple = null;

            }
            
            //all left side tuples were processed.
            //it is time to return the right side tuples that have no matches
            //these tuples are the ones that remained in the hash table after the
            //found tuples were removed

            //the tuple traversed is created only once
            if (tupleTraverser == null) {
                tupleTraverser = new TupleTraverser(tuples);
            }

            while (tupleTraverser.hasNext()) {
                Tuple rightTuple = tupleTraverser.nextTuple();
                Tuple tuple = new Tuple();
                tuple.setSourceRows(nullLeftTuple, rightTuple);
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
