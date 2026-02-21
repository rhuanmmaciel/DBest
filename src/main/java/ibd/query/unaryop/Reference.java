/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.OperationUtils;
import ibd.query.UnpagedOperationIterator;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.BooleanColumn;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.column.StringColumn;
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
public class Reference extends Operation {

    String tableName = "REFERENCE";
    List<ColumnDescriptor> projectionColumns;

    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param columns the list of columns that will form the non-duplicated
     * tuples. The name can be prefixed by the table name (e.g. tab.col)
     * @throws Exception
     */
    public Reference(String columns[]) throws Exception {
        super();
        this.alias = "ref";
        projectionColumns = new ArrayList();
        for (String col : columns) {
            ColumnDescriptor sortColumn = new ColumnDescriptor(col);
            projectionColumns.add(sortColumn);
        }
    }

    @Override
    public void prepare() throws Exception {

        super.prepare();

        for (ColumnDescriptor colDesc : projectionColumns) {
            boolean found = setColumnLocationFromProcessedOperations(colDesc);
            if (!found) throw new Exception("Could not find "+colDesc+" in Reference operation");
        }
    }

    protected Prototype createPrototype() throws Exception {
        Prototype prototype = new Prototype();
        List<Operation> correlatedOperations = OperationUtils.findLeftSideCorrelations(this);
        for (ColumnDescriptor col : projectionColumns) {
            Column refCol = getReferencedColumn(correlatedOperations, col);
            Column newCol = null;
            if (refCol == null) {
                newCol = new StringColumn(col.getTableName() + "_" + col.getColumnName());
            }
            else newCol = Column.copyColumn(refCol, col.getTableName() + "_" + col.getColumnName());
            prototype.addColumn(newCol);
        }
        return prototype;

    }

    private Column getReferencedColumn(List<Operation> correlatedOperations, ColumnDescriptor col) throws Exception {
        for (Operation correlatedOperation : correlatedOperations) {
            ReferedDataSource[] dataSources_ = correlatedOperation.getExposedDataSources();
            for (ReferedDataSource dataSource : dataSources_) {
                if (dataSource.alias.equals(col.getTableName())) {
                    Column column = dataSource.prototype.getColumn(col.getColumnName());
                    return column;
                }
            }
        }
        return null;
    }

    @Override
    public void setConnectedDataSources() throws Exception {

        connectedDataSources = new ReferedDataSource[1];
        connectedDataSources[0] = new ReferedDataSource();
        connectedDataSources[0].alias = alias;

        connectedDataSources[0].prototype = createPrototype();
    }

    @Override
    public void setExposedDataSources() throws Exception {

        dataSources = connectedDataSources;

    }

    @Override
    public Map<String, List<String>> getContentInfo() {
        HashMap<String, List<String>> map = new LinkedHashMap<>();
        for (ReferedDataSource dataSource : dataSources) {
            List list = new ArrayList<String>();
            for (Column column : dataSource.prototype.getColumns()) {
                list.add(column.getName());
            }
            map.put(dataSource.alias, list);
        }

        return map;
    }

//    @Override
//    public void setConnectedDataSources() throws Exception {
//        //the data sources are not copied from the child operation.
//        //instead, the operation itself is considered a data source that provides tuples that conform to the 
//        //list of projected columns
//        connectedDataSources = new ReferedDataSource[1];
//        connectedDataSources[0] = new ReferedDataSource();
//        connectedDataSources[0].alias = tableName;
//
//        //the prototype of the operation's data source needs to be set after the childOperation.setDataSourcesInfo() call
//        connectedDataSources[0].prototype = setPrototype();
//    }
//    
//    @Override
//    public void setExposedDataSources() throws Exception {
//
//        dataSources = connectedDataSources;
//
//    }
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new ReferenceIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public String toString() {
        return "Reference";
    }

    /**
     * this class produces resulting tuples by removing duplicates from the
     * child operation
     */
    private class ReferenceIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;

        public ReferenceIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            List<Tuple> singleTupleList = new ArrayList();

            if (processedTuples.isEmpty()) {
                tuples = singleTupleList.iterator();
                return;
            }

            Tuple returnTp = new Tuple();
            returnTp.rows = new LinkedDataRow[dataSources.length];
            LinkedDataRow row = new LinkedDataRow(dataSources[0].prototype, false);
            returnTp.rows[0] = row;
            int i = 0;
            for (ColumnDescriptor col : projectionColumns) {
                Tuple tp = processedTuples.get(col.getColumnLocation().tupleIndex);
                Comparable value = tp.rows[col.getColumnLocation().rowIndex].getValue(col.getColumnLocation().colIndex);
                row.setValue(i, value);
                i++;
            }
            singleTupleList.add(returnTp);
            tuples = singleTupleList.iterator();
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                return tp;

            }
            return null;
        }

    }
}
