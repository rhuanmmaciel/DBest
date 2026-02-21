/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query;

import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.NoLookupFilter;
import java.util.List;

/**
 * Unpaged iterators are the ones that actually do the scanning work of an operator. The paged iterators end up calling an unpaged iterator to find the next tuple to process.
 * TO find a next tuple, an unpaged iterator may need to perform filters that come from a parent and may access tuples that were already processed by operators that were executed before.
 * 
 *
 * @author Sergio
 */
public abstract class UnpagedOperationIterator extends OperationIterator {


    //the lookup filter that needs to be satisfied by this operator
    //it is either the filters from the parent operator or an empty filter
    protected LookupFilter lookup;
    
    //the tuples that come from operations already processed.  The columns from rows that are part of these tuples can be used by the unprocessed operations, like for filtering.  
    protected List<Tuple> processedTuples;

    /**
     * @param processedTuples the tuples that come from operations already processed. 
     * The rows from these tuples can be used by the unprocessed
     * operations, like for filtering.
     * @param withFilterDelegation indicates if the filters created by the parent operation need to be processed. 
     * Set this to false if the execution starts from this operation.
     * @param parentFilters the filters created by the parent operation
     */
    public UnpagedOperationIterator(List<Tuple> processedTuples, boolean withFilterDelegation, LookupFilter parentFilters) {
        this.processedTuples = processedTuples;
        if (withFilterDelegation) {
            this.lookup = parentFilters;
        } else {
            this.lookup = new NoLookupFilter();
        }
    }

}
