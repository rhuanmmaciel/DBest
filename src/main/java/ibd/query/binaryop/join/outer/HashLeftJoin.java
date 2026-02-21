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
import java.util.Iterator;
import java.util.List;

/**
 * Performs a Hash Left Inner Join between the left and the right operations using
 * the terms provided by the join predicate.
 * Left side tuples that have no matches are also returned, complemented with null values.
 *
 * @author Sergio
 */
public class HashLeftJoin extends HashJoin {


    //a null tuple that is shared with all left side tuples that fail to join with right side tuples
    Tuple nullRightTuple;

    
    public HashLeftJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }

    @Override
    public void prepare() throws Exception {

        super.prepare();

        //creates the null tuple
        setNullRightTuple();

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
        return "Hash Left Join";
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new HashJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the hash left join
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
                    foundJoin = false;
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

            //no more tuples to be joined
            return null;
        }
    }

}
