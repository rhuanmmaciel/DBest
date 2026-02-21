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
 * This operation collapses all data sources coming from the child operarion
 * into one. One single alias is then used to refer to all columns produced by
 * the collapsed data sources. The operation fails if columns from different
 * data sources have the same name.
 *
 * @author Sergio
 */
public class SourceRename1 extends UnaryOperation {

    List<ColumnDescriptor> columns;

    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param alias the name used to refer to tuples produced by this operation
     * @throws Exception
     */
    public SourceRename1(Operation op, String alias) throws Exception {
        super(op);
        this.alias = alias;

    }

    //Sets the list of columns to be used by the collapsed data source.
    //Columns with the same name are added to the columns lists, but they 
    //produce errors when the list is converted into a schema
    private void setColumns() throws Exception {
        ReferedDataSource childSources[] = getChildOperation().getExposedDataSources();
        columns = new ArrayList();
        for (ReferedDataSource dataSource : childSources) {
            for (Column col : dataSource.prototype.getColumns()) {
                ColumnDescriptor colDesc = new ColumnDescriptor(dataSource.alias + "." + col.getName());
                columns.add(colDesc);
            }

        }

    }

    @Override
    public void prepare() throws Exception {

        super.prepare();

        setColumnsIndexes();
    }

    //is this necessary? The method is already called by the set prototype method
    private void setColumnsIndexes() throws Exception {
        for (ColumnDescriptor col : columns) {
            //int index = childOperation.getRowIndex(col.getTableName());
            //col.setTupleIndex(index);
            childOperation.setColumnLocation(col);
        }
    }

    //sets the schema of the collapsed data source by taking its collected columns
    protected Prototype setPrototype() throws Exception {
        Prototype prototype = new Prototype();
        for (ColumnDescriptor col : columns) {
            childOperation.setColumnLocation(col);
            Column originalCol = childOperation.getExposedDataSources()[col.getColumnLocation().rowIndex].prototype.getColumn(col.getColumnName());
            Column newCol = Prototype.cloneColumn(originalCol);
            prototype.addColumn(newCol);
        }
        return prototype;
        
    }

    @Override
    public Map<String, List<String>> getContentInfo() {
        HashMap<String, List<String>> map = new LinkedHashMap<>();

        try {
            //columns are not yet set if the operation was never run
            //they are only set when the operation is run from the first time
            //in this case, we need to define the data sources info by hand in order to get the content info
            if (columns == null || columns.size() == 0) {
                setConnectedDataSources();
                setColumns();
            }
        } catch (Exception ex) {
        }
        List list = new ArrayList<String>();
        for (ColumnDescriptor colDescriptor : columns) {
            list.add(colDescriptor.getColumnName());
        }
        map.put(alias, list);
        return map;
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new SourceRenameIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public void setConnectedDataSources() throws Exception {
        
        connectedDataSources = new ReferedDataSource[1];
        connectedDataSources[0] = new ReferedDataSource();
        connectedDataSources[0].alias = alias;

        setColumns();
        connectedDataSources[0].prototype = setPrototype();
    }
    
    @Override
    public void setExposedDataSources() throws Exception {

        dataSources = connectedDataSources;

    }

    @Override
    public String toString() {
        return "Source Rename:" + columns;
    }

    /**
     * this class transforms tuples that come from the child operation by
     * collapsing all data sources into one
     */
    private class SourceRenameIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;

        public SourceRenameIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            tuples = childOperation.lookUp(processedTuples, false);//accesses all tuples produced by the child operation 
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                //creates the single row of the resulting tuple
                //and add all collapsed columns 
                LinkedDataRow row = new LinkedDataRow(dataSources[0].prototype, false);
                for (int i = 0; i < columns.size(); i++) {
                    ColumnDescriptor col = columns.get(i);
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
