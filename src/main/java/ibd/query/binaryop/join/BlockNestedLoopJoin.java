/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.lookup.ColumnElement;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.Element;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.table.ComparisonTypes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performs a block nested loop join between the left and the right operations
 * using the join conditions provided by the join terms.
 *
 * @author Sergio
 */
public class BlockNestedLoopJoin extends Join {

    /**
     * the join buffer size is the number of tuples from the left side that are
     * kept in a buffer
     */
    protected int joinBufferSize = 10;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param terms the terms of the join predicate
     * @param joinBufferSize the join buffer size
     * @throws Exception
     */
    public BlockNestedLoopJoin(Operation leftOperation, Operation rightOperation, JoinPredicate terms, int joinBufferSize) throws Exception {
        super(leftOperation, rightOperation, terms);
        this.joinBufferSize = joinBufferSize;

    }

    //sets the column indexes for the terms of the join predicate
    @Override
    protected void setJoinTermsIndexes() throws Exception {
        for (JoinTerm term : joinPredicate.getTerms()) {
            leftOperation.setColumnLocation(term.getLeftColumnDescriptor());
            rightOperation.setColumnLocation(term.getRightColumnDescriptor());
            term.getRightColumnDescriptor().getColumnLocation().rowIndex += leftOperation.getExposedDataSources().length;
        }
    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a block nested loop join over the
     * tuples from the left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new BlockNestedLoopJoinIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the block nested loop join
     * between the two underlying operations
     */
    private class BlockNestedLoopJoinIterator extends UnpagedOperationIterator {

        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the right side
        Iterator<Tuple> rightTuples;

        //this buffer contains tuples from the left side
        List<Tuple> joinBuffer = new ArrayList();
        //keeps the current state of the right side of the join: The current right tuple read
        Tuple rightTuple;
        //keeps the current state of the left side of the join: The current left index read
        int currentLeftIndex = 0;
        //indicates if the right side is empty
        boolean rightSideEmpty = false;

        public BlockNestedLoopJoinIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            //iterate over all tuples from the left side
            leftTuples = leftOperation.lookUp(processedTuples, false);
            //iterate over all tuples from the right side
            rightTuples = rightOperation.lookUp(processedTuples, false);
            if (!rightTuples.hasNext()) {
                rightSideEmpty = true;
            }
            feedBuffer();
        }

        /**
         * feeds the buffer with tuples from the left side
         */
        private void feedBuffer() {
            int count = 0;
            joinBuffer.clear();
            while (leftTuples.hasNext() && count < joinBufferSize) {
                joinBuffer.add(leftTuples.next());
                count++;
            }

        }

        /*
         * defines the lookup filter conditions used to find out if the current left tuple 
        correspond with the current right tuple
         */
        private CompositeLookupFilter createFilter()  {
            CompositeLookupFilter filter = new CompositeLookupFilter(CompositeLookupFilter.AND);
            for (JoinTerm term : joinPredicate.getTerms()) {
                Element elem1;
                Element elem2;
                try {
                    elem1 = new ColumnElement(term.getLeftColumnDescriptor());
                    elem2 = new ColumnElement(term.getRightColumnDescriptor());
                    SingleColumnLookupFilter f = new SingleColumnLookupFilter(elem1, ComparisonTypes.EQUAL, elem2);
                    filter.addFilter(f);
                } catch (Exception ex) {
                    Logger.getLogger(BlockNestedLoopJoin.class.getName()).log(Level.SEVERE, null, ex);
                }
                 
                
                
            }
            return filter;
        }

        @Override
        protected Tuple findNextTuple() {

            //the right side is empty
            if (rightSideEmpty) {
                return null;
            }

            //the block nested loop join inverses processing:the right tuple drives the search
            //the right tuple is checked against all buffered tuples from the left side
            while (true) {
                if (rightTuple == null) {
                    //advances the right tuple
                    rightTuple = rightTuples.next();
                    //resets the join buffer cursor
                    currentLeftIndex = 0;

                    //if all right tuples were processed, we need to start over from the right side and feed the join buffer again
                    if (rightTuple == null) {

                        feedBuffer();

                        //if no tuples were added to the join buffer, it means the left side tuples were all processed and we can end execution
                        if (joinBuffer.isEmpty()) {
                            return null;
                        }

                        //load all tuples from the right side
                        rightTuples = rightOperation.lookUp(processedTuples, false);

                        //sets the cursor to the first tuple from the right side
                        rightTuple = rightTuples.next();
                    }

                }
                //iterate through the join buffer to find tuples that satisfy the lookup
                for (int i = currentLeftIndex; i < joinBuffer.size(); i++) {

                    Tuple curLeftTuple = joinBuffer.get(i);
                    currentLeftIndex = i + 1;
                    //create returning tuple and add the joined tuples
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(curLeftTuple, rightTuple);
                    LookupFilter filter = createFilter();
                    if (!filter.match(tuple)) {
                        continue;
                    }

                    
                    return tuple;
                    
                }

                //All corresponding tuples from the right side processed. 
                //set null to allow right side cursor to advance
                rightTuple = null;

            }

        }

    }

    @Override
    public String getJoinAlgorithm() {
        return "Block Nested Loop Join";
    }
}
