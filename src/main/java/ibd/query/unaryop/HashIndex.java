/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.lookup.ColumnElement;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.Element;
import ibd.query.lookup.LiteralElement;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.NoLookupFilter;
import ibd.query.lookup.ReferencedElement;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.table.ComparisonTypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This unary operation is materialized. It creates a hash containing all the
 * tuples that come from the child operation. Useful for lookups based on
 * equivalence conditions.
 *
 * @author Sergio
 */
public class HashIndex extends UnaryOperation {

    /*
    materialized hash of tuples, using as key the columns that are part of conjunctive equality search conditions.
    It can answer lookups based on equivalence conditions efficiently.
    This collection is shared among all private iterators, because we need all queries issued over this operation to use the same collection.
     */
    HashMap<String, List<Tuple>> tuples;

    List<Tuple> constantTuples;

    /**
     * the list of conjunctive equality filters conditions that form the key of
     * the hash
     */
    List<SingleColumnLookupFilter> hashedFilters;

    /**
     * the query filters after taking the hashed filters off
     */
    LookupFilter unhashedFilters;

    List<SingleColumnLookupFilter> hashedFilters2;

    /**
     *
     * @param childOperation the child operation
     * @throws Exception
     */
    public HashIndex(Operation childOperation) throws Exception {
        super(childOperation);
    }

    @Override
    public boolean canProcessDelegatedFilters() {
        return true;
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();

        //sets the list of columns that will be part of the hash keys
        prepareHashColumns();

        //erases the previously built hash.
        //a new one is created when the first query is executed. 
        tuples = null;
    }
    
    @Override
    public void close() throws Exception {
        // Clean up hash tables to free memory (handles both complete and partial hashes)
        if (tuples != null) {
            // Calculate approximate memory being freed
            try {
                int tupleSize = getTupleSize();
                long memoryFreed = 0;
                
                for (List<Tuple> tupleList : tuples.values()) {
                    if (tupleList != null) {
                        memoryFreed += tupleList.size() * tupleSize;
                    }
                }
                if (constantTuples != null) {
                    memoryFreed += constantTuples.size() * tupleSize;
                }
                
                // Clear the collections aggressively
                for (List<Tuple> tupleList : tuples.values()) {
                    if (tupleList != null) {
                        tupleList.clear();
                    }
                }
                tuples.clear();
                tuples = null;
                
                if (constantTuples != null) {
                    constantTuples.clear();
                    constantTuples = null;
                }
                
                // Update memory statistics
                QueryStats.MEMORY_USED = Math.max(0, QueryStats.MEMORY_USED - memoryFreed);
                
                // Force garbage collection hint (not guaranteed, but helps)
                System.gc();
                
            } catch (Exception e) {
                // If we can't calculate exact size, still clear the collections aggressively
                try {
                    if (tuples != null) {
                        for (List<Tuple> tupleList : tuples.values()) {
                            if (tupleList != null) {
                                tupleList.clear();
                            }
                        }
                        tuples.clear();
                        tuples = null;
                    }
                    if (constantTuples != null) {
                        constantTuples.clear();
                        constantTuples = null;
                    }
                    // Force garbage collection hint
                    System.gc();
                } catch (Exception ignored) {
                    // Final safety: set references to null
                    tuples = null;
                    constantTuples = null;
                }
            }
        }
        
        // Call parent close to clean up child operations
        super.close();
    }
    
    //sets the list of columns that will be part of the hash keys
    private void prepareHashColumns() throws Exception {

        hashedFilters = new ArrayList();
        hashedFilters2 = new ArrayList();
        unhashedFilters = new NoLookupFilter();
        if (hasDelegatedfilters()) {
            LookupFilter joinFilter = parentOperation.getFilters();

            if (hasSingleDisjunction(joinFilter)) {
                CompositeLookupFilter rowFilter = transformDisjunctiveFilter((CompositeLookupFilter) joinFilter);
                if (rowFilter != null) {
                    joinFilter = rowFilter;
                }

            }

            prepareHashColumns(joinFilter);

            unhashedFilters = copyUnhashedFilters(joinFilter);
        }
    }

    //sets the list of columns that will be part of the hash keys
    private void prepareHashColumns(LookupFilter filter) throws Exception {
        if (filter instanceof CompositeLookupFilter compositeLookupFilter) {
            if (compositeLookupFilter.getBooleanConnector() == CompositeLookupFilter.OR) {
                return;
            }
            for (LookupFilter f : compositeLookupFilter.getFilters()) {
                prepareHashColumns(f);
            }
        } else if (filter instanceof SingleColumnLookupFilter singleColumnLookupFilter) {
            if (singleColumnLookupFilter.getComparisonType() == ComparisonTypes.EQUAL) {
                singleColumnLookupFilter = createFilter(singleColumnLookupFilter);
                if (singleColumnLookupFilter != null) {
                    hashedFilters.add(singleColumnLookupFilter);
                }
            }

        }

    }

    private SingleColumnLookupFilter createFilter(SingleColumnLookupFilter singleColumnLookupFilter) throws Exception {
        Element elem1 = singleColumnLookupFilter.getFirstElement();
        Element elem2 = singleColumnLookupFilter.getSecondElement();
        if (elem1 instanceof ColumnElement && (elem2 instanceof LiteralElement || elem2 instanceof ReferencedElement)) {
            return singleColumnLookupFilter;
        } else if (elem2 instanceof ColumnElement && (elem1 instanceof LiteralElement || elem1 instanceof ReferencedElement)) {
            SingleColumnLookupFilter invertedFilter = new SingleColumnLookupFilter(
                    singleColumnLookupFilter.getSecondElement(),
                    ComparisonTypes.EQUAL,
                    singleColumnLookupFilter.getFirstElement());
            hashedFilters2.add(singleColumnLookupFilter);
            return invertedFilter;
        }
        return null;
    }

    //Copies the unhashed filters into a new structure
    private LookupFilter copyUnhashedFilters(LookupFilter filter) throws Exception {

        if (filter instanceof CompositeLookupFilter clf) {

            CompositeLookupFilter rowFilter = new CompositeLookupFilter(clf.getBooleanConnector());
            for (LookupFilter f : clf.getFilters()) {
                LookupFilter rlf = copyUnhashedFilters(f);
                if (!(rlf instanceof NoLookupFilter)) {
                    rowFilter.addFilter(rlf);
                }
            }
            if (rowFilter.getFilters().isEmpty()) {
                return new NoLookupFilter();
            }
            return rowFilter;
        } else if (filter instanceof SingleColumnLookupFilter f) {
            if (hashedFilters2.contains(f)) {
                return new NoLookupFilter();
            }
            return f;
        } else {
            return new NoLookupFilter();
        }
    }

    private boolean hasSingleDisjunction(LookupFilter filter) throws Exception {
        if (!(filter instanceof CompositeLookupFilter compositeLookupFilter)) {
            return false;
        }

        if (compositeLookupFilter.getBooleanConnector() == CompositeLookupFilter.AND) {
            return false;
        }

        for (LookupFilter f : compositeLookupFilter.getFilters()) {
            if (f instanceof CompositeLookupFilter) {
                return false;
            }
        }
        return true;
    }

    List<LookupFilter> nullFilters = new ArrayList();

    private CompositeLookupFilter transformDisjunctiveFilter(CompositeLookupFilter compositeLookupFilter) throws Exception {

        boolean alreadyFoundNonConstant = false;
        CompositeLookupFilter rowFilter = new CompositeLookupFilter(CompositeLookupFilter.AND);
        for (LookupFilter f : compositeLookupFilter.getFilters()) {
            //LookupFilter filter = createConstantFilter(f);
            if (hasConstantFilter(f)) {
                //if (filter!=null) {
                nullFilters.add(f);
                continue;
            }
            if (alreadyFoundNonConstant) {
                nullFilters.clear();
                return null;
            }

            rowFilter.addFilter(f);
            alreadyFoundNonConstant = true;

        }

        return rowFilter;
    }

    private boolean hasValidNullFilter(ColumnElement colElem, int compType, LiteralElement litElem) throws Exception {
        Comparable value = litElem.getValue(null);
        return true;
        //return (value == null);
    }

    private boolean hasConstantFilter(LookupFilter f) {
        SingleColumnLookupFilter filter = (SingleColumnLookupFilter) f;
        Element elem1 = filter.getFirstElement();
        Element elem2 = filter.getSecondElement();
        if (elem1 instanceof ColumnElement && elem2 instanceof LiteralElement) {
            return true;
        }

        if (elem2 instanceof ColumnElement && elem1 instanceof LiteralElement) {
            return true;
        }

        return false;
    }

    private LookupFilter createConstantFilter(LookupFilter f) throws Exception {
        SingleColumnLookupFilter filter = (SingleColumnLookupFilter) f;
        Element elem1 = filter.getFirstElement();
        Element elem2 = filter.getSecondElement();
        if (elem1 instanceof ColumnElement && elem2 instanceof LiteralElement) {
            return f;
        }

        if (elem2 instanceof ColumnElement && elem1 instanceof LiteralElement) {
            return new SingleColumnLookupFilter(elem2, ComparisonTypes.getSwitchedComparisonType(filter.getComparisonType()), elem1);
        }

        return null;
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new MaterializedIndexIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public String toString() {
        return "Hash";
    }

    /**
     * the class that materializes a collection of tuples that come from the
     * child operation using a hash. The query is answered using the hash.
     */
    public class MaterializedIndexIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> it = null;

        public MaterializedIndexIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {

            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            //only the unhashed filters from the parent need to be verififed. The others will be satisfied by the hash search.
            this.lookup = unhashedFilters;
            buildHash();
            queryHash();
        }

        private void queryHash() {
            //here is where we build an iterator to traverse the query results
            //the hash is queried using as key the values of the hashed filter columns
            
            // Check if the hash was cancelled/cleaned up
            if (tuples == null) {
                // Return empty iterator if hash was cancelled
                it = new ArrayList<Tuple>().iterator();
                return;
            }
            
            String key = "";
            for (SingleColumnLookupFilter lookupFilter : hashedFilters) {
                Element elem2 = lookupFilter.getSecondElement();
                key += elem2.getValue(null).toString();
            }

            List<Tuple> result = tuples.get(key);
            if (result != null) {
                //it = result.iterator();
                // Also check constantTuples for null safety
                if (constantTuples != null) {
                    it = new TwoListsIterator(constantTuples.iterator(), result.iterator());
                } else {
                    it = result.iterator();
                }
            } else {
                // Safe check for constantTuples as well
                if (constantTuples != null) {
                    it = constantTuples.iterator();
                } else {
                    it = new ArrayList<Tuple>().iterator();
                }
            }
        }

        private void buildHash() {
            //build hash, if one does not exist yet
            if (tuples == null) {
                // Clear thread interrupted status to allow fresh start
                Thread.currentThread().interrupted();
                
                tuples = new HashMap();
                constantTuples = new ArrayList();
                long memoryUsed = 0;
                
                try {
                    //accesses and indexes all tuples that come from the child operation
                    it = childOperation.lookUp(processedTuples, false);
                    int tupleSize = childOperation.getTupleSize();
                    int processedCount = 0;
                    while (it.hasNext()) {
                        // Check for cancellation every 1000 tuples to allow responsive cancellation
                        if (processedCount % 1000 == 0 && Thread.currentThread().isInterrupted()) {
                            // Clear partially built hash and exit
                            tuples.clear();
                            constantTuples.clear();
                            tuples = null;
                            constantTuples = null;
                            return;
                        }
                        
                        Tuple tuple = (Tuple) it.next();
                        processedCount++;

                        boolean foundNullTuple = false;
                        for (LookupFilter lookupFilter : nullFilters) {
                            //ColumnElement colElem = (ColumnElement)lookupFilter.getFirstElement();
                            //ColumnDescriptor col = colElem.getColumnDescriptor();
                            //Comparable value = tuple.rows[col.getColumnLocation().rowIndex].getValue(col.getColumnLocation().colIndex);
                            if (lookupFilter.match(tuple)) //if (value==null)
                            {
                                constantTuples.add(tuple);
                                memoryUsed += tupleSize;
                                foundNullTuple = true;
                                break;
                            }
                        }
                        if (foundNullTuple) {
                            continue;
                        }

                        String key = "";
                        for (SingleColumnLookupFilter lookupFilter : hashedFilters) {
                            ColumnElement colElem = (ColumnElement) lookupFilter.getFirstElement();
                            ColumnDescriptor col = colElem.getColumnDescriptor();
                            key += tuple.rows[col.getColumnLocation().rowIndex].getValue(col.getColumnLocation().colIndex).toString();
                        }

                        //String key = tuple.rows[hashColumn.getColumnLocation().rowIndex].getValue(hashColumn.getColumnName()).toString();
                        List tupleList = tuples.get(key);
                        if (tupleList == null) {
                            tupleList = new ArrayList();
                            tuples.put(key, tupleList);
                        }
                        tupleList.add(tuple);
                        memoryUsed += tupleSize;
                    }

                } catch (Exception ex) {
                    
                }
                
                // Only update memory stats if hash building completed successfully
                if (tuples != null && !Thread.currentThread().isInterrupted()) {
                    QueryStats.MEMORY_USED += memoryUsed;
                }
            }
        }

        @Override
        protected Tuple findNextTuple() {
            while (it.hasNext()) {
                Tuple tp = it.next();
                //a tuple must satisfy the lookup filter that comes from the parent operation
                //only the unhashed filters need to be checked
                if (lookup.match(tp)) {
                    return tp;
                }

            }
            return null;
        }

    }

    private class TwoListsIterator implements Iterator {

        private final Iterator<Tuple> it1;
        private final Iterator<Tuple> it2;

        public TwoListsIterator(Iterator<Tuple> it1, Iterator<Tuple> it2) {
            this.it1 = it1;
            this.it2 = it2;
        }

        @Override
        public boolean hasNext() {
            return it1.hasNext() || it2.hasNext();
        }

        @Override
        public Tuple next() {
            return it1.hasNext() ? it1.next() : it2.next();
        }

    }

}
