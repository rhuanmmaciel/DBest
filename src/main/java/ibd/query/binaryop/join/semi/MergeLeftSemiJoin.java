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
import ibd.query.binaryop.join.Join;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.binaryop.join.JoinTerm;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Performs a merge loop join between the left and the right operations using
 * the join conditions provided by the join terms.
 *
 * @author Sergio
 */
public class MergeLeftSemiJoin extends Join {

    //an array to contain values from the left side used as the join condition
    Comparable leftTupleArray[];
    //an array to contain values from the right side used as the join condition
    Comparable rightTupleArray[];

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param terms the terms of the join predicate
     * @throws Exception
     */
    public MergeLeftSemiJoin(Operation leftOperation, Operation rightOperation, JoinPredicate terms) throws Exception {
        super(leftOperation, rightOperation, terms);
    }

    @Override
    public void setExposedDataSources() throws Exception {

        ReferedDataSource left[] = getLeftOperation().getExposedDataSources();
        dataSources = new ReferedDataSource[left.length];
        System.arraycopy(left, 0, dataSources, 0, left.length);

    }
    
    @Override
    public Map<String, List<String>> getContentInfo() {
        return getLeftOperation().getContentInfo();
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
    }

    @Override
    public String getJoinAlgorithm() {
        return "Merge Left Semi Join";
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

        public MergeJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            leftTuple = null;

            leftTuples = leftOperation.lookUp(processedTuples, false); //iterate over all tuples from the left side
            rightTuples = rightOperation.lookUp(processedTuples, false); //iterate over all tuples from the right side

        }

        //fills the left side join values
        private void fillLeftTupleArray(Tuple currentLeftTuple) {
            int i = 0;
            for (JoinTerm term : joinPredicate.getTerms()) {
                Comparable value = currentLeftTuple.rows[term.getLeftColumnDescriptor().getColumnLocation().rowIndex].getValue(term.getLeftColumnIndex());
                leftTupleArray[i] = value;
                i++;
            }
        }

        //fills the right side join values
        private void fillRightTupleArray(Tuple currentRightTuple) {
            int i = 0;
            for (JoinTerm term : joinPredicate.getTerms()) {
                Comparable value = currentRightTuple.rows[term.getRightColumnDescriptor().getColumnLocation().rowIndex].getValue(term.getRightColumnIndex());
                rightTupleArray[i] = value;
                i++;
            }
        }

        @Override
        protected Tuple findNextTuple() {

            while (true) {
                //prepares left side join array
                if (leftTuple == null) {
                    if (leftTuples.hasNext()) {
                        leftTuple = leftTuples.next();
                        fillLeftTupleArray(leftTuple);
                    }
                }

                //prepares right side join array
                if (rightTuple == null) {
                    if (rightTuples.hasNext()) {
                        rightTuple = rightTuples.next();
                        fillRightTupleArray(rightTuple);
                    }
                }
                if (leftTuple == null || rightTuple == null) {
                    return null;
                }

                int comp = Arrays.compare(leftTupleArray, rightTupleArray);
                if (comp == 0) {
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(leftTuple);
                    rightTuple = null;
                    leftTuple = null;
                    return tuple;
                    
                } else if (comp < 0) {

                    leftTuple = null;
                } else {
                    rightTuple = null;
                }

            }
        }

    }

}
