/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.filter;

import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.Element;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.ReferencedElement;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.BooleanColumn;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A filter operation filters tuples that come from its child operation. This
 * operation defines the filter and asks for the child operation to resolve it.
 * The child operation apply the filter over all tuples that is generates. Is
 * the child operation is an index scan, and depending on the filters defined,
 * the search can be performed efficiently by index lookups.
 *
 * @author Sergio
 */
public class Condition extends Operation {

    LookupFilter filter = null;
    String tableName = "CONDITION";
    String colName = "EVAL";
    
    //List singleTupleList = null;
    //List emptyTupleList = new ArrayList<Tuple>();
    
    Tuple fixedTrueTuple;
    Tuple fixedFalseTuple;

    /**
     *
     * @param filter the filter condition that needs to be satisfied
     * @throws Exception
     */
    public Condition(LookupFilter filter) throws Exception {
        this.filter = filter;
        
    }

    @Override
    public LookupFilter getFilters() {
        return filter;
    }

    
    @Override
    public void prepare() throws Exception{
    
        super.prepare();
        fixedTrueTuple = new Tuple();
        LinkedDataRow row = new LinkedDataRow(dataSources[0].prototype, false);
        row.setValue(0, true);
        fixedTrueTuple.setSourceRows(new LinkedDataRow[]{row});
        
        fixedFalseTuple = new Tuple();
        row = new LinkedDataRow(dataSources[0].prototype, false);
        row.setValue(0, false);
        fixedFalseTuple.setSourceRows(new LinkedDataRow[]{row});
    }

    
    protected Prototype setPrototype() throws Exception {
        Prototype prototype = new Prototype();
        prototype.addColumn(new BooleanColumn(colName));
        
        
        return prototype;
    }

    @Override
    public Map<String, List<String>> getContentInfo() {
        HashMap<String, List<String>> map = new LinkedHashMap<>();
        List list = new ArrayList<String>();
        list.add(colName);
        map.put(tableName, list);
        return map;
    }

    @Override
    public void setConnectedDataSources() throws Exception {
        //the data sources are not copied from the child operation.
        //instead, the operation itself is considered a data source that provides tuples that conform to the 
        //list of projected columns
        connectedDataSources = new ReferedDataSource[1];
        connectedDataSources[0] = new ReferedDataSource();
        connectedDataSources[0].alias = tableName;

        //the prototype of the operation's data source needs to be set after the childOperation.setDataSourcesInfo() call
        connectedDataSources[0].prototype = setPrototype();
    }
    
    @Override
    public void setExposedDataSources() throws Exception {

        dataSources = connectedDataSources;

    }

    @Override
    public String toString() {
        return "Condition(" + filter + ")";
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
        return new ConditionOperationIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public void close() throws Exception {
    }

    /**
     * this class produces resulting tuples from a filter over the child
     * operation
     */
    public class ConditionOperationIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;
        boolean conditionSatisfied = false;
        boolean finished = false;

        public ConditionOperationIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            setFilterValue(filter, processedTuples);
            
            conditionSatisfied = filter.match((Tuple) null);
            


        }

        @Override
        protected Tuple findNextTuple() {
            if (finished) {
                return null;
            }
            finished = true;
            
            if (conditionSatisfied)
                return fixedTrueTuple;
            else return fixedFalseTuple;
        }


    }
    
}
