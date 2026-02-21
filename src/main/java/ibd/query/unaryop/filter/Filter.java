/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.filter;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.Element;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.ReferencedElement;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.unaryop.UnaryOperation;
import java.util.Iterator;
import java.util.List;

/**
 * A filter operation filters tuples that come from its child operation. This
 * operation defines the filter and asks for the child operation to resolve it.
 * The child operation apply the filter over all tuples that is generates. Is
 * the child operation is an index scan, and depending on the filters defined,
 * the search can be performed efficiently by index lookups.
 *
 * @author Sergio
 */
public class Filter extends UnaryOperation {

    LookupFilter filter = null;

    /**
     *
     * @param childOperation the child operation
     * @param filter the filter condition that needs to be satisfied
     * @throws Exception
     */
    public Filter(Operation childOperation, LookupFilter filter) throws Exception {
        super(childOperation);
        this.filter = filter;
    }


    @Override
    public LookupFilter getFilters() {
        return filter;
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
     * @return an iterator that performs a filter over the child operation
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new FilterOperationIterator(processedTuples, withFilterDelegation);
    }

    /**
     * this class produces resulting tuples from a filter over the child
     * operation
     */
    public class FilterOperationIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;

        public FilterOperationIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            setFilterValue(filter, processedTuples);

//            if (filter instanceof SingleColumnLookupFilterByReference) {
//                SingleColumnLookupFilterByReference f1 = (SingleColumnLookupFilterByReference) filter;
//                f1.setValue(processedTuples);
//            }
            tuples = childOperation.lookUp(processedTuples, true);//pushes filter down to the child operation
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                //a tuple must satisfy the lookup filter that comes from the parent operation
                if (childOperation.canProcessDelegatedFilters() || filter.match(tp)) {
                    return tp;
                }

            }
            return null;
        }

    }

    @Override
    public String toString() {
        return "Filter (" + filter + ")";
    }
}
