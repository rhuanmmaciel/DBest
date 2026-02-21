/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.conditional;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import ibd.query.binaryop.BinaryOperation;
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
 * Performs a difference between the left and the right operations.
 *
 * @author Sergio
 */
public class LogicalOr1 extends BinaryOperation {

    String tableName = "CONDITION";
    String colName = "EVAL";
    Tuple fixedTuple;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation order for the operation to
     * be effective
     * @throws Exception
     */
    public LogicalOr1(Operation leftOperation, Operation rightOperation) throws Exception {
        super(leftOperation, rightOperation);
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

        fixedTuple = new Tuple();
        LinkedDataRow row = new LinkedDataRow(prototype, false);
        row.setValue(0, true);
        fixedTuple.setSourceRows(new LinkedDataRow[]{row});
        
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
    public void setExposedDataSources() throws Exception {

        dataSources = new ReferedDataSource[1];
        dataSources[0] = new ReferedDataSource();
        dataSources[0].alias = tableName;

        //the prototype of the operation's data source needs to be set after the childOperation.setDataSourcesInfo() call
        dataSources[0].prototype = setPrototype();
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

            boolean satisfied = leftOperation.exists(processedTuples, true);
            if (satisfied) {
                return fixedTuple;
            }
            satisfied = rightOperation.exists(processedTuples, true);
            if (satisfied) {
                return fixedTuple;
            };

            return null;
        }

    }

}
