/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.Column;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This operation removes tuples whose value of an specified column is already
 * part of another accepted tuple.
 *
 * @author Sergio
 */
public class Projection1 extends UnaryOperation {

    String alias;
    List<ColumnDescriptor> projectionColumns;

    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param alias the name used to refer to tuples produced by this operation
     * @param columns the list of columns that will form the non-duplicated
     * tuples. The name can be prefixed by the table name (e.g. tab.col)
     * @throws Exception
     */
    public Projection1(Operation op, String alias, String[] columns) throws Exception {
        super(op);
        this.alias = alias;
        projectionColumns = new ArrayList();
        for (String col : columns) {
            ColumnDescriptor sortColumn = new ColumnDescriptor(col);
            projectionColumns.add(sortColumn);
        }

    }

    @Override
    public void prepare() throws Exception {
        
        super.prepare();
        
        setColumnsIndexes();
    }

    /**
     * defines the index information required to locate the content of the projected columns
     */
    private void setColumnsIndexes() throws Exception {
        for (ColumnDescriptor col : projectionColumns) {
            //int index = childOperation.getRowIndex(col.getTableName());
            //col.setTupleIndex(index);
            childOperation.setColumnLocation(col);
        }
    }

    /**
     * 
     * The tuples produced by a this operation contains a single schema, which contains all the projected columns.This function sets this schema.
     * @throws java.lang.Exception
     */
    protected Prototype setPrototype() throws Exception {
        Prototype prototype = new Prototype();
        for (ColumnDescriptor col : projectionColumns) {
            childOperation.setColumnLocation(col);
            Column originalCol = childOperation.getExposedDataSources()[col.getColumnLocation().rowIndex].prototype.getColumn(col.getColumnName());
            Column newCol = Prototype.cloneColumn(originalCol);
            prototype.addColumn(newCol);
        }
        return prototype;
        
    }
    
    @Override
    public Map<String, List<String>> getContentInfo() {
        HashMap<String,List<String>> map = new LinkedHashMap<>();
        List list = new ArrayList<String>();
        for (ColumnDescriptor colDescriptor : projectionColumns) {
            list.add(colDescriptor.getColumnName());
        }
        map.put(alias,list);
        return map;
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new ProjectionIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public void setConnectedDataSources() throws Exception {
        //the data sources are not copied from the child operation.
        //instead, the operation itself is considered a data source that provides tuples that conform to the 
        //list of projected columns
        connectedDataSources = new ReferedDataSource[1];
        connectedDataSources[0] = new ReferedDataSource();
        connectedDataSources[0].alias = alias;
        
        //the prototype of the operation's data source needs to be set after the childOperation.setDataSourcesInfo() call
        connectedDataSources[0].prototype = setPrototype();
    }
    
    @Override
    public void setExposedDataSources() throws Exception {
        //the data sources are not copied from the child operation.
        //instead, the operation itself is considered a data source that provides tuples that conform to the 
        //list of projected columns
        dataSources = new ReferedDataSource[1];
        dataSources[0] = new ReferedDataSource();
        dataSources[0].alias = alias;
        
        //the prototype of the operation's data source needs to be set after the childOperation.setDataSourcesInfo() call
        dataSources[0].prototype = setPrototype();
    }

    @Override
    public String toString() {
        return "projection(" + projectionColumns+")";
    }

    /**
     * this class produces resulting tuples by removing duplicates from the
     * child operation
     */
    private class ProjectionIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;

        
        public ProjectionIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            tuples = childOperation.lookUp(processedTuples, false);//accesses all tuples produced by the child operation 
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                
                LinkedDataRow row = new LinkedDataRow(dataSources[0].prototype, false);
                //find the column from the accessed tuple and add it to the resulting row
                for (int i = 0; i < projectionColumns.size(); i++) {
                    ColumnDescriptor col = projectionColumns.get(i);
                    Comparable value = tp.rows[col.getColumnLocation().rowIndex].getValue(col.getColumnLocation().colIndex);
                    row.setValue(i, value);
                }

                //the row is added to the resulting tuple
                Tuple returnTp = new Tuple();
                returnTp.rows = new LinkedDataRow[1];
                returnTp.rows[0] = new LinkedDataRow();
                returnTp.rows[0] = row;
                
                return returnTp;

            }
            return null;
        }


    }
}
