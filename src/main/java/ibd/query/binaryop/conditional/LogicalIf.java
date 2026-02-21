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
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.Element;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.ReferencedElement;
import ibd.query.lookup.SingleColumnLookupFilter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Performs a difference between the left and the right operations.
 *
 * @author Sergio
 */
public class LogicalIf extends BinaryOperation {

    
    LookupFilter filter = null;
    
    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * order for the operation to be effective
     * @throws Exception
     */
    public LogicalIf(Operation leftOperation, Operation rightOperation, LookupFilter filter) throws Exception {
        super(leftOperation, rightOperation);
        this.filter = filter;
    }

    
    @Override
    public LookupFilter getFilters() {
        return filter;
    }
    
    

    @Override
    public void setExposedDataSources() throws Exception {
        
        ReferedDataSource left[] = getLeftOperation().getExposedDataSources();
        dataSources = new ReferedDataSource[left.length];
        System.arraycopy(left, 0, dataSources, 0, left.length);
        
    }
    
    @Override
    public Map<String, List<String>> getContentInfo() {
        return leftOperation.getContentInfo();
    }
    
    private void setFilterValue(LookupFilter filter, List<Tuple> processedTuples) {
        if (filter instanceof CompositeLookupFilter) {
            setFilterValue((CompositeLookupFilter) filter, processedTuples);

        } else if (filter instanceof SingleColumnLookupFilter) {
            setFilterValue((SingleColumnLookupFilter) filter, processedTuples);
        }
    }

    //sets the tuple indexes for all parts of this composite filter
    private void setFilterValue(CompositeLookupFilter filter, List<Tuple> processedTuples) {
        for (LookupFilter filter1 : filter.getFilters()) {
            setFilterValue(filter1, processedTuples);
        }
    }

    //sets the tuple index for this single column filter
    private void setFilterValue(SingleColumnLookupFilter filter, List<Tuple> processedTuples) {
        Element elem = filter.getFirstElement();
        if (elem instanceof ReferencedElement) {
            ((ReferencedElement) elem).setValue(processedTuples);
        }

        elem = filter.getSecondElement();
        if (elem instanceof ReferencedElement) {
            ((ReferencedElement) elem).setValue(processedTuples);
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
        return new LogicalIfIterator(processedTuples, withFilterDelegation);
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
    private class LogicalIfIterator extends UnpagedOperationIterator {

        Tuple leftTuple = null;
        Tuple rightTuple = null;
        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the right side
        Iterator<Tuple> rightTuples;
        boolean done = false;

        public LogicalIfIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            leftTuples = leftOperation.lookUp(processedTuples, false); //iterate over all tuples from the left side
            rightTuples = rightOperation.lookUp(processedTuples, false); //iterate over all tuples from the right side
        }

        @Override
        protected Tuple findNextTuple() {

            if (done) return null;
            done = true;
            setFilterValue(filter, processedTuples);
            
            if (filter.match((Tuple) null)) {
                if (leftTuple==null) leftTuple = leftTuples.next();
                return leftTuple;
            }
            else {
            if (rightTuple==null) rightTuple = rightTuples.next();
                return rightTuple;
            }
        }

    }

}
