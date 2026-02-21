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
 * This operation removes columns from the tuples that come from the underlying
 * child operation.
 *
 * @author Sergio
 */
public class RemoveColumns extends UnaryOperation {

    String alias;
    List<ColumnDescriptor> columnsToProject;
    List<ColumnDescriptor> columnsToRemove;

    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param alias the name used to refer to tuples produced by this operation
     * @param columnsToRemove_ the list of columns to be removed from the
     * accessed tuples
     * @throws Exception
     */
    public RemoveColumns(Operation op, String alias, String[] columnsToRemove_) throws Exception {
        super(op);
        this.alias = alias;
        columnsToRemove = new ArrayList();
        for (String col : columnsToRemove_) {
            ColumnDescriptor colToRemove = new ColumnDescriptor(col);
            columnsToRemove.add(colToRemove);
        }

    }

    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param alias the name used to refer to tuples produced by this operation
     * @param columnsToRemove_ the list of columns to be removed from the
     * accessed tuples
     * @throws Exception
     */
    public RemoveColumns(Operation op, String alias, List<String> columnsToRemove_) throws Exception {
        super(op);
        this.alias = alias;
        columnsToRemove = new ArrayList();
        for (String col : columnsToRemove_) {
            ColumnDescriptor colToRemove = new ColumnDescriptor(col);
            columnsToRemove.add(colToRemove);
        }

    }

    @Override
    public void prepare() throws Exception {

        super.prepare();

        setColumnsIndexes();
    }

    /**
     * defines the index information required to locate the content of the
     * projected columns
     */
    private void setColumnsIndexes() throws Exception {
        for (ColumnDescriptor col : columnsToProject) {
            //int index = childOperation.getRowIndex(col.getTableName());
            //col.setTupleIndex(index);
            childOperation.setColumnLocation(col);
        }
    }

    /**
     * 
     * The tuples produced by this operation contains a single schema, which contains all the projected columns.This function sets this schema.
     * @throws java.lang.Exception
     */
    protected Prototype setPrototype() throws Exception {
        Prototype prototype = new Prototype();
        for (ColumnDescriptor col : columnsToProject) {
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
        if (columnsToProject == null || columnsToProject.isEmpty()) {
            try {
                setConnectedDataSources();
                setProjectedColumns();
            } catch (Exception ex) {
            }

        }
        List list = new ArrayList<String>();
        for (ColumnDescriptor colDescriptor : columnsToProject) {
            list.add(colDescriptor.getColumnName());
        }
        map.put(alias, list);
        return map;
    }

    /**
     * sets the columns that need to be projected
     */
    private void setProjectedColumns() throws Exception {
        ReferedDataSource childSources[] = getChildOperation().getExposedDataSources();
        columnsToProject = new ArrayList();
        for (ReferedDataSource dataSource : childSources) {

            for (Column col : dataSource.prototype.getColumns()) {
                boolean toRemove = false;
                for (ColumnDescriptor colToRemove : columnsToRemove) {
                    if (colToRemove.getTableName().equals(dataSource.alias)) {
                        if (colToRemove.getColumnName().equals(col.getName())) {
                            toRemove = true;
                        }
                    }
                }
                if (!toRemove) {
                    ColumnDescriptor colDesc = new ColumnDescriptor(dataSource.alias + "." + col.getName());
                    columnsToProject.add(colDesc);
                }
            }
        }

    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new RemoveColumnsIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public void setConnectedDataSources() throws Exception {
        
        connectedDataSources = new ReferedDataSource[1];
        connectedDataSources[0] = new ReferedDataSource();
        connectedDataSources[0].alias = alias;

        setProjectedColumns();
        connectedDataSources[0].prototype = setPrototype();
    }
    
    @Override
    public void setExposedDataSources() throws Exception {
        
        dataSources = connectedDataSources;
    }

    @Override
    public String toString() {
        return "Remove Columns:" + columnsToRemove;
    }

    /**
     * this class produces resulting tuples by removing columns from the
     * accesses tuples frmo the child operation
     */
    private class RemoveColumnsIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;

        public RemoveColumnsIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            tuples = childOperation.lookUp(processedTuples, false);//accesses all tuples produced by the child operation 
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();

                LinkedDataRow row = new LinkedDataRow(dataSources[0].prototype, false);
                //find the column from the accessed tuple and add it to the resulting row
                for (int i = 0; i < columnsToProject.size(); i++) {
                    ColumnDescriptor col = columnsToProject.get(i);
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
