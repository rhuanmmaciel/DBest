/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.conditional;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import ibd.query.binaryop.BinaryOperation;
import ibd.table.prototype.LinkedDataRow;
import java.util.Iterator;
import java.util.List;

/**
 * Performs a difference between the left and the right operations.
 *
 * @author Sergio
 */
public class Exists extends BinaryOperation {

    //indicates if both sides should contain results in order for the operation to be effective
    boolean conjunctive = true;

    //a null tuple that is used when no left side tuple exists
    Tuple nullLeftTuple;

    //a null tuple that is used when no right side tuple exists
    Tuple nullRightTuple;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param conjunctive indicates if both sides should contain results in
     * order for the operation to be effective
     * @throws Exception
     */
    public Exists(Operation leftOperation, Operation rightOperation, boolean conjunctive) throws Exception {
        super(leftOperation, rightOperation);
        this.conjunctive = conjunctive;
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();
        setNullLeftTuple();
        setNullRightTuple();
    }

    @Override
    public String toString() {
        return "Exists";
    }

    protected void setNullLeftTuple() throws Exception {

        ReferedDataSource left[] = getLeftOperation().getExposedDataSources();
        nullLeftTuple = new Tuple();
        nullLeftTuple.rows = new LinkedDataRow[left.length];
        for (int i = 0; i < left.length; i++) {
            LinkedDataRow row = new LinkedDataRow(left[i].prototype, false);
            nullLeftTuple.rows[i] = new LinkedDataRow();
            nullLeftTuple.rows[i] = row;
        }

    }

    protected void setNullRightTuple() throws Exception {

        ReferedDataSource right[] = getRightOperation().getExposedDataSources();
        nullRightTuple = new Tuple();
        nullRightTuple.rows = new LinkedDataRow[right.length];
        for (int i = 0; i < right.length; i++) {
            LinkedDataRow row = new LinkedDataRow(right[i].prototype, false);
            nullRightTuple.rows[i] = new LinkedDataRow();
            nullRightTuple.rows[i] = row;
        }

    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a simple nested loop join over the
     * tuples from the left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new ExistsIterator(processedTuples, withFilterDelegation);
    }

//    @Override
//    public boolean exists(List<Tuple> processedTuples, boolean withFilterDelegation) {
//        
//        boolean leftSideExists = leftOperation.exists(processedTuples,  false); 
//        if (leftSideExists && !conjunctive) 
//            return true;
//        boolean rightSideExists = rightOperation.exists(processedTuples,  false); 
//        if (rightSideExists && !conjunctive) 
//            return true;
//        if (conjunctive && leftSideExists && rightSideExists)
//            return true;
//        return false;
//    }
    /**
     * the class that produces resulting tuples checking if there exists results
     * coming from the underlying operations.
     */
    private class ExistsIterator extends UnpagedOperationIterator {

        //keeps the current state of the left side of the join: The current tuple read
        Tuple leftTuple;

        Tuple rightTuple;
        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the left side
        Iterator<Tuple> rightTuples;

        boolean finished = false;

        public ExistsIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            leftTuple = null;

        }

        @Override
        protected Tuple findNextTuple() {

            if (finished) {
                return null;
            }

            finished = true;
            //iterate over all tuples from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false);
            //boolean leftExists = leftOperation.exists(processedTuples, new NoLookupFilter());

            if (leftTuple == null) {
                if (leftTuples.hasNext()) {
                    while (leftTuples.hasNext()) {
                        leftTuple = leftTuples.next();
                        if (!conjunctive) {
                            Tuple tuple = new Tuple();
                            tuple.setSourceRows(leftTuple, nullRightTuple);
                            return tuple;
                        }
                        break;
                    }
                }
            }
            //iterate over all tuples from the right side
            rightTuples = rightOperation.lookUp(processedTuples, false);

            if (rightTuple == null) {
                while (rightTuples.hasNext()) {
                    rightTuple = rightTuples.next();
                    if (!conjunctive) {
                        Tuple tuple = new Tuple();
                        tuple.setSourceRows(nullLeftTuple, rightTuple);
                        return tuple;
                    }
                    break;
                }
            }

            if (leftTuple == null && rightTuple == null) {
                return null;
            }

            if ((leftTuple == null || rightTuple == null) && conjunctive) {
                return null;
            }

            //if it gets here, it means both sides contain values and conjunctive is true 
            Tuple tuple = new Tuple();
            tuple.setSourceRows(leftTuple, rightTuple);
            return tuple;
        }

    }

}
