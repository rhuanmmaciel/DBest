/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.set;

import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.ReferedDataSource;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.Column;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Performs a difference between the left and the right operations.
 *
 * @author Sergio
 */
public class HashUnion1 extends Set {

    HashMap<String, Tuple> tuples;
    
    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @throws Exception
     */
    public HashUnion1(Operation leftOperation, Operation rightOperation) throws Exception {
        super(leftOperation, rightOperation);
    }

    /**
     *
     * @return the name of the operation
     */
    @Override
    public String toString() {
        return "Hash Union";
    }
    
    protected Prototype setPrototype() throws Exception {
        Prototype prototype = new Prototype();
        int leftSize = getColumnsSize(leftOperation);
        int rightSize = getColumnsSize(rightOperation);
        int minSize = Math.min(leftSize, rightSize);

        int currentColumn = 0;

        for (ReferedDataSource dataSource : getLeftOperation().getExposedDataSources()) {
            List<Column> columns = dataSource.prototype.getColumns();
            int columnsToCopy = Math.min(columns.size(), minSize - currentColumn);

            for (int i = 0; i < columnsToCopy; i++) {
                Column originalCol = columns.get(i);
                Column newCol = Prototype.cloneColumn(originalCol);
                prototype.addColumn(newCol);
                currentColumn++;
            }

            if (currentColumn >= minSize) {
                break;
            }
        }
        return prototype;
        
    }

    protected int getColumnsSize(Operation op) throws Exception {
        int colSize = 0;
        for (ReferedDataSource dataSource : op.getExposedDataSources()) {
            colSize += dataSource.prototype.getColumns().size();
        }
        return colSize;
    }

    @Override
    public void setExposedDataSources() throws Exception {

        dataSources = new ReferedDataSource[1];
        dataSources[0] = new ReferedDataSource();
        dataSources[0].alias = alias;

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
        return new UnionIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the union between the two
     * underlying operations.
     */
    private class UnionIterator extends UnpagedOperationIterator {

        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the left side
        Iterator<Tuple> rightTuples;
        
        int tupleSize = 0;

        /**
         * @param processedTuples the tuples that come from operations already
         * processed. The columns from these tuples can be used by the
         * unprocessed operations, like for filtering.
         * @param lookup the condition from the parent operation that needs to
         * be satisfied
         */
        public UnionIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            tuples = new HashMap<>();
            leftTuples = leftOperation.lookUp(processedTuples, false); //iterate over all tuples from the left side
            rightTuples = rightOperation.lookUp(processedTuples, false); //iterate over all tuples from the left side
        
            try {
                tupleSize = leftOperation.getTupleSize();
            } catch (Exception ex) {
            }
        }
        
        private String getKey(Tuple tuple){
            String key = "";
            for (LinkedDataRow row : tuple.rows) {
                for (int i = 0; i < row.getFieldsSize(); i++) {
                    Comparable value = row.getValue(i);
                    key += value;
                }
                
            }
            return key;
        }

        /**
         *
         * @return the next satisfying tuple, if any
         */
        @Override
        protected Tuple findNextTuple() {

                    while (leftTuples.hasNext()) {
                        Tuple leftTuple = leftTuples.next();
                        String key = getKey(leftTuple);
                        tuples.put(key, leftTuple);
                        QueryStats.MEMORY_USED += tupleSize;

                        Tuple returnTp = new Tuple();
                        returnTp.rows = new LinkedDataRow[1];
                        returnTp.rows[0] = buildRow(leftTuple);
                        return returnTp;
                        //return leftTuple;
                    }
                
       
                while (rightTuples.hasNext()) {
                    Tuple rightTuple = rightTuples.next();
                    String key = getKey(rightTuple);
                    if (!(tuples.containsKey(key))){
                        Tuple returnTp = new Tuple();
                        returnTp.rows = new LinkedDataRow[1];
                        returnTp.rows[0] = buildRow(rightTuple);
                        return returnTp;
                        //return rightTuple;
                    }
                }
            return null;
                
        }


    }
    
    protected LinkedDataRow buildRow(Tuple tuple) {
        List<Column> columns = dataSources[0].prototype.getColumns();
        LinkedDataRow row = new LinkedDataRow(dataSources[0].prototype, false);
        int totalColumns = columns.size();
        int currentIndex = 0;

        outerLoop:
        for (LinkedDataRow row1 : tuple.rows) {
            int fieldSize = row1.getFieldsSize();
            for (int i = 0; i < fieldSize; i++) {
                Comparable value = row1.getValue(i);
                row.setValue(currentIndex, value);
                currentIndex++;
                if (currentIndex == totalColumns) {
                    break outerLoop;
                }
            }
        }

        return row;
    }

}
