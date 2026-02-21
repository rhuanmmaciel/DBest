/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.sourceop;

import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.lookup.ColumnElement;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.Element;
import ibd.query.lookup.LiteralElement;
import ibd.query.lookup.NoLookupFilter;
import ibd.query.lookup.ReferencedElement;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.table.ComparisonTypes;
import ibd.table.Table;
import ibd.table.lookup.NoRowLookupFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.column.Column;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Performs an index scan over a table
 *
 * @author Sergio
 */
public class IndexScan extends SourceOperation {

    private List<String> columns;

    /**
     * the table reached from this operation
     */
    public Table table;

    /**
     * indicates if there are filter conditions that enable efficient table
     * lookups
     */
    boolean canLookup = false;

    /**
     * the list of conjunctive equality filters conditions that enable efficient
     * table lookups
     */
    List<SingleColumnLookupFilter> fastFilters;

    List<SingleColumnLookupFilter> fastDisjunctiveFilters;

    HashMap<SingleColumnLookupFilter, SingleColumnLookupFilter> modifiedFilters;

    /**
     * the row to be used as a parameter to the efficient table lookup. It must
     * be filled with values taken from the lookup filters
     */
    LinkedDataRow fastLookupRow;

    /*
    The filter to be used for those columns that cannot be efficiently looked-up
     */
    LookupFilter slowLookupFilter;

    int compType;

    /**
     *
     * @param tableAlias the alias of the table reached by this operation
     * @param table the table reached from this operation
     */
    public IndexScan(String tableAlias, Table table) {
        super(tableAlias);
        this.table = table;
        columns = new ArrayList<>();

        for (Column c : table.getPrototype().getColumns()) {
            //if(c.ignore())continue;
            columns.add(c.getName());
        }
    }

    @Override
    public void prepare() throws Exception {

        table.open();
        
        super.prepare();

        //separates filters into slow and fast, based on the tables hability to efficiently handle the search condition
        separateFilters();

        //creates the single instance of the lookup row to be used whenever a lookup is required
        fastLookupRow = new LinkedDataRow(table.getPrototype(), true);
    }

    @Override
    public void setExposedDataSources() throws Exception {
        super.setExposedDataSources();
        dataSources[0].prototype = table.getPrototype();
    }

    @Override
    public void setConnectedDataSources() throws Exception {
        super.setConnectedDataSources();
        connectedDataSources[0].prototype = table.getPrototype();
    }

    @Override
    public String getDataSourceName() {
        return table.getName();
    }

    @Override
    public boolean canProcessDelegatedFilters() {
        return true;
    }

    //separates filters into slow and fast, based on the tables hability to efficiently handle the search condition
    private void separateFilters() throws Exception {

        //an empty row filter is prepared, just in case
        slowLookupFilter = new NoLookupFilter();

        //reset variable
        canLookup = false;

        //the parent operator has delegated filters to the index scan
        if (hasDelegatedFilters) {

            //extract the conjunctive equality filters that can be efficiently searched
            LookupFilter joinFilter = parentOperation.getFilters();

            //builds a preliminary list of fast filters
            fastFilters = new ArrayList();
            modifiedFilters = new HashMap();

//            if (hasSingleDisjunction(joinFilter)) {
//                CompositeLookupFilter rowFilter = transformDisjunctiveFilter((CompositeLookupFilter) joinFilter);
//                if (rowFilter != null) {
//                    joinFilter = rowFilter;
//                }
//
//            }
            fillFastFilters(joinFilter);

            // Identify the columns used by the fast filters
            List<String> columns = new ArrayList<>();
            for (SingleColumnLookupFilter f : fastFilters) {
                columns.add(f.getColumnDescriptor().getColumnName());
            }

            // Check which of the identified columns are part of the index prefix.
            // If at least one, an efficient lookup is possible
            List<String> pkColumns = table.getPrototype().pKPrefix(columns);
            if (!pkColumns.isEmpty()) {
                canLookup = true;
            }

            // Reorganize and filter the fast filters based on the index prefix order
            List<SingleColumnLookupFilter> reorganizedFilters = new ArrayList<>();
            for (String pkColumn : pkColumns) {
                for (int i = 0; i < fastFilters.size(); i++) {
                    SingleColumnLookupFilter filter = fastFilters.get(i);
                    String colName = filter.getColumnDescriptor().getColumnName();

                    // Match column from PK prefix
                    if (colName.equals(pkColumn)) {
                        reorganizedFilters.add(filter);
                        compType = filter.getComparisonType();
                        fastFilters.remove(i); // Remove to avoid reprocessing
                        if (filter.getComparisonType() != ComparisonTypes.EQUAL) {
                            // Stop adding filters if the comparison type is not EQUALS
                            fastFilters.clear(); // Clear remaining filters
                            break;
                        }
                        break; // Move to the next PK column
                    }
                }
            }            // Replace the original fastFilters list with the reorganized version
            fastFilters.clear();
            fastFilters.addAll(reorganizedFilters);

            // Set column location information for each fast filter's ColumnDescriptor
            // This is essential to prevent NullPointerException when accessing colIndex later
            for (SingleColumnLookupFilter fastFilter : fastFilters) {
                try {
                    setColumnLocation(fastFilter.getColumnDescriptor());
                } catch (Exception e) {
                    // If setColumnLocation fails, the filter won't work properly
                    // Log the error and continue - this will be handled at runtime
                    System.err.println("Warning: Could not set column location for filter: " + fastFilter);
                }
            }

            List<SingleColumnLookupFilter> unnModifiedFastFilters = new ArrayList();
            for (SingleColumnLookupFilter fastFilter : fastFilters) {
                SingleColumnLookupFilter originalFilter = modifiedFilters.get(fastFilter);
                if (originalFilter != null) {
                    unnModifiedFastFilters.add(originalFilter);
                } else {
                    unnModifiedFastFilters.add(fastFilter);
                }
            }

            //fills the slow Filter with the lookup filters that are not part of the fast Filters list
            slowLookupFilter = fillSlowFilter(parentOperation.getFilters(), unnModifiedFastFilters);
        }

    }

    //fills the list of fast filters
    private void fillFastFilters(LookupFilter filter) throws Exception {
        if (filter instanceof CompositeLookupFilter compositeLookupFilter) {
            if (compositeLookupFilter.getBooleanConnector() == CompositeLookupFilter.OR) {
                return;
            }
            for (LookupFilter f : compositeLookupFilter.getFilters()) {
                fillFastFilters(f);
            }
        } else if (filter instanceof SingleColumnLookupFilter singleColumnLookupFilter) {
            //if (singleColumnLookupFilter.getComparisonType() == ComparisonTypes.EQUAL) 
            {
                Element elem1 = singleColumnLookupFilter.getFirstElement();
                Element elem2 = singleColumnLookupFilter.getSecondElement();
                if (elem1 instanceof ColumnElement && (elem2 instanceof LiteralElement || elem2 instanceof ReferencedElement)) {
                    compType = singleColumnLookupFilter.getComparisonType();
                    fastFilters.add(singleColumnLookupFilter);
                } else if (elem2 instanceof ColumnElement && (elem1 instanceof LiteralElement || elem1 instanceof ReferencedElement)) {
                    int compType_ = ComparisonTypes.getSwitchedComparisonType(singleColumnLookupFilter.getComparisonType());
                    SingleColumnLookupFilter invertedFilter = new SingleColumnLookupFilter(
                            singleColumnLookupFilter.getSecondElement(),
                            compType_,
                            singleColumnLookupFilter.getFirstElement());
                    fastFilters.add(invertedFilter);
                    modifiedFilters.put(invertedFilter, singleColumnLookupFilter);
                }

            }
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

    SingleColumnLookupFilter nullFilter;

    private CompositeLookupFilter transformDisjunctiveFilter(CompositeLookupFilter compositeLookupFilter) throws Exception {

        boolean foundNonNullTerm = false;
        CompositeLookupFilter rowFilter = new CompositeLookupFilter(CompositeLookupFilter.AND);
        for (LookupFilter f : compositeLookupFilter.getFilters()) {
            SingleColumnLookupFilter filter = (SingleColumnLookupFilter) f;
            Element elem1 = filter.getFirstElement();
            Element elem2 = filter.getSecondElement();
            int compType = filter.getComparisonType();
            if (elem1 instanceof ColumnElement colElem && elem2 instanceof LiteralElement litElem) {
                nullFilter = createNullFilter(colElem, compType, litElem);
                if (nullFilter == null) {
                    return null;
                } else {
                    if (foundNonNullTerm) {
                        return null;
                    }
                    rowFilter.addFilter(f);
                    foundNonNullTerm = true;
                }
            } else if (elem2 instanceof ColumnElement colElem && elem1 instanceof LiteralElement litElem) {
                nullFilter = createNullFilter(colElem, compType, litElem);
                nullFilter = new SingleColumnLookupFilter(colElem, compType, litElem);
                if (nullFilter == null) {
                    return null;
                } else {
                    if (foundNonNullTerm) {
                        return null;
                    }
                    rowFilter.addFilter(f);
                    foundNonNullTerm = true;
                }
            }

        }
        return rowFilter;
    }

    private SingleColumnLookupFilter createNullFilter(ColumnElement colElem, int compType, LiteralElement litElem) throws Exception {
        Comparable value = litElem.getValue(null);
        if (value == null) {
            List<String> list = new ArrayList();
            list.add(colElem.getColumnDescriptor().getColumnName());
            List result = table.getPrototype().pKPrefix(list);
            if (result.isEmpty()) {
                return null;
            }
            return new SingleColumnLookupFilter(colElem, compType, litElem);
        }
        return null;
    }

    //fills the list of slow filters
    private LookupFilter fillSlowFilter(LookupFilter filter, List<SingleColumnLookupFilter> unnModifiedFastFilters) throws Exception {

        if (filter instanceof CompositeLookupFilter clf) {

            CompositeLookupFilter rowFilter = new CompositeLookupFilter(clf.getBooleanConnector());
            for (LookupFilter f : clf.getFilters()) {
                LookupFilter rlf = fillSlowFilter(f, unnModifiedFastFilters);
                if (!(rlf instanceof NoLookupFilter)) {
                    rowFilter.addFilter(rlf);
                }
            }
            if (rowFilter.getFilters().isEmpty()) {
                return new NoLookupFilter();
            }
            return rowFilter;
        } else if (filter instanceof SingleColumnLookupFilter f) {
            if (unnModifiedFastFilters.contains(f)) {
                return new NoLookupFilter();
            }
            //SingleColumnLookupLookupFilter singleRowFilter = new SingleColumnLookupLookupFilter(f.getColumnDescriptor().getColumnLocation().colIndex, f.getComparisonType(), f);
            //return singleRowFilter;
            return f;
        } else {
            return new NoLookupFilter();
        }
    }

@Override
public String toString() {
        return "[" + alias + "] Index Scan";
    }

    /**
     * {@inheritDoc }
     *
     * @param lookup
     * @return an iterator that performs an index scan over a table
     */
    @Override
public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new IndexScanIterator(processedTuples, withFilterDelegation);
    }

    @Override
public boolean exists(List<Tuple> processedTuples, boolean withFilterDelegation) {
        if (canLookup) {
            //sgbd.info.Query.PK_SEARCH++;
            //the lookup row is filled with values from the lookup filters
            for (SingleColumnLookupFilter filter_ : fastFilters) {
                //row.setValue(filter.getColumn(), (Integer) filter.getValue());
                fastLookupRow.setValue(filter_.getColumnDescriptor().getColumnLocation().colIndex, filter_.getSecondElement().getValue(null));
            }
            ibd.query.QueryStats.PK_SEARCH++;
            return table.contains(fastLookupRow);
        } else {
            if (slowLookupFilter instanceof NoRowLookupFilter) {
                Iterator iterator;
                try {
                    iterator = table.getAllRecordsIterator();
                    return iterator.hasNext();
                } catch (Exception ex) {
                    //should not return false. An exception should be thrown.
                    return false;
                }

            } else {
                try {
                    Iterator iterator = table.getFilteredRecordsIterator(slowLookupFilter);
                    return iterator.hasNext();
                } catch (Exception ex) {
                    //should not return false. An exception should be thrown.
                    return false;
                }
            }
        }
    }

    @Override
public void close() throws Exception {
    }

    @Override
public Map<String, List<String>> getContentInfo() {
        HashMap<String, List<String>> map = new LinkedHashMap<>();
        map.put(alias, new ArrayList<>(columns));
        return map;

}

    /**
     * the class that produces resulting tuples from an index scan
     */
    public class IndexScanIterator extends UnpagedOperationIterator {

    //the iterator over the child operation
    Iterator<LinkedDataRow> iterator;

    public IndexScanIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
        super(processedTuples, withFilterDelegation, getDelegatedFilters());
//          

        //the lookup filters from the parent were already divided into fast and slos filters.
        //now its time to use them
        try {

            //if there exists fast filters
            if (canLookup) {
                ibd.query.QueryStats.PK_SEARCH++;
                //the lookup row is filled with values from the fast filters
                for (SingleColumnLookupFilter filter : fastFilters) {
                    fastLookupRow.setValue(filter.getColumnDescriptor().getColumnLocation().colIndex, filter.getSecondElement().getValue(null));
                }

                //the lookup occurs
                iterator = table.getPKFilteredRecordsIterator(fastLookupRow, slowLookupFilter, compType);
                return;

            }

            //if it gets here, no fast filters exist, so we need to scan the entire table
            //In this case, either all filters are slow or there are no filters
            if (slowLookupFilter instanceof NoLookupFilter) {
                //if no filter needs to be performed
                iterator = table.getAllRecordsIterator();
                //iterator = table.getAllRecords().iterator();
                return;
            }
            //there exists slow filters to be satisfied
            //the scan has to traverse the entire table and resolve the filters as the rows are accessed
            iterator = table.getFilteredRecordsIterator(slowLookupFilter);
            //iterator = table.getFilteredRecords(slowLookupFilter).iterator();

        } catch (Exception ex) {
        }

    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    protected Tuple findNextTuple() {

        while (iterator.hasNext()) {
            LinkedDataRow row = iterator.next();
            Tuple tuple = new Tuple();
            //the resulting tuple contains a single row taken from the table
            tuple.setSingleSourceRow(dataSources[0].alias, row);
            //no filters need to be applied. They were already processed, wither as fast or slow filters.
            return tuple;

        }

        return null;
    }

}
}
