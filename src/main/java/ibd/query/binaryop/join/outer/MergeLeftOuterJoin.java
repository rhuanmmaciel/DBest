/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join.outer;

import ibd.query.binaryop.join.*;
import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Performs a merge loop join between the left and the right operations using
 * the join conditions provided by the join terms.
 *
 * @author Sergio
 */
public class MergeLeftOuterJoin extends Join {

    //an array to contain values from the left side used as the join condition
    Comparable leftTupleArray[];
    //an array to contain values from the right side used as the join condition
    Comparable rightTupleArray[];

    //an array to contain values from the left side used as the join condition
    Comparable nextLeftTupleArray[];
    //an array to contain values from the right side used as the join condition
    Comparable nextRightTupleArray[];
    
    //a null tuple that is shared with all left side tuples that fail to join with right side tuples
    Tuple nullRightTuple;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param terms the terms of the join predicate
     * @throws Exception
     */
    public MergeLeftOuterJoin(Operation leftOperation, Operation rightOperation, JoinPredicate terms) throws Exception {
        super(leftOperation, rightOperation, terms);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void prepare() throws Exception {

        super.prepare();

        //creates the arrays used to join tuples
        leftTupleArray = new Comparable[joinPredicate.size()];
        rightTupleArray = new Comparable[joinPredicate.size()];

        nextLeftTupleArray = new Comparable[joinPredicate.size()];
        nextRightTupleArray = new Comparable[joinPredicate.size()];
        
        setNullRightTuple();
    }
    
    protected void setNullRightTuple() throws Exception {
        //flowOperations = new ArrayList();

        ReferedDataSource right[] = getRightOperation().getExposedDataSources();
        nullRightTuple = new Tuple();
        nullRightTuple.rows = new LinkedDataRow[right.length];
        for (int i = 0; i < right.length; i++) {
            LinkedDataRow row = new LinkedDataRow(right[i].prototype, false);
            nullRightTuple.rows[i] = new LinkedDataRow();
            nullRightTuple.rows[i] = row;
        }

    }

    @Override
    public String getJoinAlgorithm() {
        return "Merge Left Outer Join";
    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a merge join over the tuples from the
     * left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new MergeJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the merge join between the
     * two underlying operations.
     */
    private class MergeJoinIterator extends UnpagedOperationIterator {

        //keeps the current state of the left side of the join: The current tuple read
        Tuple leftTuple;

        Tuple rightTuple;
        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the left side
        Iterator<Tuple> rightTuples;

        List<Tuple> leftDuplicates = new ArrayList<>();
        List<Tuple> rightDuplicates = new ArrayList<>();
        boolean returningDuplicates = false;
        int rightIndex = 0;
        int leftIndex = 0;

        public MergeJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            leftTuple = null;

            leftTuples = leftOperation.lookUp(processedTuples, false); //iterate over all tuples from the left side
            rightTuples = rightOperation.lookUp(processedTuples, false); //iterate over all tuples from the right side

        }

        //fills the left side join values
        private void fillLeftTupleArray(Comparable tupleArray[], Tuple currentLeftTuple) {
            int i = 0;
            for (JoinTerm term : joinPredicate.getTerms()) {
                Comparable value = currentLeftTuple.rows[term.getLeftColumnDescriptor().getColumnLocation().rowIndex].getValue(term.getLeftColumnIndex());
                tupleArray[i] = value;
                i++;
            }
        }

        //fills the right side join values
        private void fillRightTupleArray(Comparable tupleArray[], Tuple currentRightTuple) {
            int i = 0;
            for (JoinTerm term : joinPredicate.getTerms()) {
                Comparable value = currentRightTuple.rows[term.getRightColumnDescriptor().getColumnLocation().rowIndex].getValue(term.getRightColumnIndex());
                tupleArray[i] = value;
                i++;
            }
        }

        @Override
        protected Tuple findNextTuple() {

            while (true) {
                // If we have stored duplicates, return combinations of them
                if (returningDuplicates) {
                    if (leftIndex < leftDuplicates.size()) {
                        if (rightIndex < rightDuplicates.size()) {
                            Tuple tuple = new Tuple();
                            tuple.setSourceRows(leftDuplicates.get(leftIndex), rightDuplicates.get(rightIndex));
                            rightIndex++;
                            if (rightIndex >= rightDuplicates.size()) {
                                rightIndex = 0;
                                leftIndex++;
                            }
                            return tuple;
                        }
                    } else {
                        // Done returning duplicates, reset and proceed to normal processing
                        returningDuplicates = false;
                        leftDuplicates.clear();
                        rightDuplicates.clear();
                    }
                }

                // Prepare the left side tuple
                if (leftTuple == null && leftTuples.hasNext()) {
                    leftTuple = leftTuples.next();
                    fillLeftTupleArray(leftTupleArray, leftTuple);
                }

                // Prepare the right side tuple
                if (rightTuple == null && rightTuples.hasNext()) {
                    rightTuple = rightTuples.next();
                    fillLeftTupleArray(rightTupleArray, rightTuple);
                }

                if (leftTuple == null || rightTuple == null) {
                    return null; // No more tuples to process
                }

                int comp = Arrays.compare(leftTupleArray, rightTupleArray);

                if (comp == 0) {
                    // A match was found, gather all duplicates
                    if (leftDuplicates.isEmpty()) {
                        leftDuplicates.add(leftTuple);
                        rightDuplicates.add(rightTuple);
                    }

                    leftTuple = null;
                    // Gather more duplicates from the left side
                    while (leftTuples.hasNext()) {
                        Tuple nextLeft = leftTuples.next();
                        fillLeftTupleArray(nextLeftTupleArray, nextLeft);
                        if (Arrays.compare(nextLeftTupleArray, leftTupleArray) == 0) {
                            leftDuplicates.add(nextLeft);
                        } else {
                            leftTuple = nextLeft;
                            System.arraycopy(nextLeftTupleArray, 0, leftTupleArray, 0, leftTupleArray.length);
                            break;
                        }
                    }

                    rightTuple = null;
                    // Gather more duplicates from the right side
                    while (rightTuples.hasNext()) {
                        Tuple nextRight = rightTuples.next();
                        fillRightTupleArray(nextRightTupleArray, nextRight);
                        if (Arrays.compare(nextRightTupleArray, rightTupleArray) == 0) {
                            rightDuplicates.add(nextRight);
                        } else {
                            rightTuple = nextRight;
                            System.arraycopy(nextRightTupleArray, 0, rightTupleArray, 0, rightTupleArray.length);
                            break;
                        }
                    }

                    // If we finished gathering all possible duplicates, start returning them
                    returningDuplicates = true;
                    leftIndex = 0;
                    rightIndex = 0;

                } else if (comp < 0) {// Left tuple is smaller
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(leftTuple, nullRightTuple);
                        // move to the next left tuple
                        leftTuple = null;
                        return tuple;
                    
                } else {
                    // Right tuple is smaller, move to the next right tuple
                    rightTuple = null;
                }
            }

        }

    }

}
