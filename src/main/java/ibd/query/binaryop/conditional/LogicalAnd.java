/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.conditional;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.ReferedDataSource;
import ibd.query.SingleSource;
import ibd.query.Tuple;
import ibd.query.binaryop.BinaryOperation;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.BooleanColumn;
import ibd.table.prototype.column.Column;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Performs a difference between the left and the right operations.
 *
 * @author Sergio
 */
public class LogicalAnd extends BinaryOperation implements SingleSource{

    
    //String tableName = "condition";
    String colName = "EVAL";
    boolean leftSideIsCondition = false;
    boolean rightSideIsCondition = false;
    Tuple fixedTrueTuple;
    Tuple fixedFalseTuple;
    
    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * order for the operation to be effective
     * @throws Exception
     */
    public LogicalAnd(Operation leftOperation, Operation rightOperation) throws Exception {
        super(leftOperation, rightOperation);
        alias = "condition";
    }
    
    public LogicalAnd(Operation leftOperation, Operation rightOperation, String alias) throws Exception {
        this(leftOperation, rightOperation);
        if (!alias.isBlank())
            this.alias = alias;
    }

    /**
     *
     * The tuples produced by a this operation contains a single schema, which
     * contains all the projected columns.This function sets this schema.
     *
     * @throws java.lang.Exception
     */
    protected Prototype setPrototype() throws Exception {
        Prototype prototype = new Prototype();
        prototype.addColumn(new BooleanColumn(colName));
        
        
        fixedTrueTuple = new Tuple();
        LinkedDataRow row = new LinkedDataRow(prototype, false);
        row.setValue(0, true);
        fixedTrueTuple.setSourceRows(new LinkedDataRow[]{row});
        
        fixedFalseTuple = new Tuple();
        row = new LinkedDataRow(prototype, false);
        row.setValue(0, false);
        fixedFalseTuple.setSourceRows(new LinkedDataRow[]{row});
        
        return prototype;
    }

    @Override
    public Map<String, List<String>> getContentInfo() {
        HashMap<String, List<String>> map = new LinkedHashMap<>();
        List list = new ArrayList<String>();
        list.add(colName);
        map.put(alias, list);
        return map;
    }

    private boolean isBooleanCondition(Operation op) throws Exception{
        if (op.getExposedDataSources().length>1) return false;
        
        ReferedDataSource source = op.getExposedDataSources()[0];
        if (source.prototype.size()>1) return false;
        
        Column col = source.prototype.getColumn(0);
        if (col.isBoolean()) return true;
        
        return false;
        
    }
    
    @Override
    public void setExposedDataSources() throws Exception {
        
        dataSources = new ReferedDataSource[1];
        dataSources[0] = new ReferedDataSource();
        dataSources[0].alias = alias;

        //the prototype of the operation's data source needs to be set after the childOperation.setDataSourcesInfo() call
        dataSources[0].prototype = setPrototype();
        
        leftSideIsCondition = isBooleanCondition(getLeftOperation());
        rightSideIsCondition = isBooleanCondition(getRightOperation());
    }
    
        @Override
    public boolean exists(List<Tuple> processedTuples, boolean withFilterDelegation) {
        
        LogicalAndIterator it = new LogicalAndIterator(processedTuples, withFilterDelegation);
        if (!it.hasNext()) return false;
        Tuple tuple = it.next();
        return (tuple.rows[0].getBoolean(colName));
    }
    
    
    
    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a simple nested loop join over the
     * tuples from the left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new LogicalAndIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public void setDataSourceAlias(String sourceAlias) {
        this.alias = sourceAlias;
    }

    @Override
    public String getDataSourceAlias() {
        return alias;
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
    private class LogicalAndIterator extends UnpagedOperationIterator {

        boolean finished = false;

        public LogicalAndIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
        }

        @Override
        protected Tuple findNextTuple() {

            if (finished) {
                return null;
            }
            finished = true;

            boolean satisfied = true;
            Iterator<Tuple> tuples = leftOperation.lookUp(processedTuples, true);
            if (!tuples.hasNext()) return fixedFalseTuple;
            Tuple tuple = tuples.next();
            if (leftSideIsCondition)
                satisfied = ((Boolean)tuple.rows[0].getValue(0));
            
            if (!satisfied) return fixedFalseTuple;
            
            tuples = rightOperation.lookUp(processedTuples, true);
            if (!tuples.hasNext()) return fixedFalseTuple;
            tuple = tuples.next();
            if (rightSideIsCondition)
                satisfied = ((Boolean)tuple.rows[0].getValue(0));
            
            if (!satisfied) return fixedFalseTuple;
            
            return fixedTrueTuple;
        }

    }

}
