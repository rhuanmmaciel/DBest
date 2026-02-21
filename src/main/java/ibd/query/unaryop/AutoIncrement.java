/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.IntegerColumn;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This operation adds an auto-increment row to the resulting rows of a tuple. 
 * The row has a single column whose value is incremented as tuples are generated.
 *
 * @author Sergio
 */
public class AutoIncrement extends UnaryOperation {

    //the name of the column to be part of the auto-increment row
    String col;
    //the increment value
    int increment = 0;
    
    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param alias the name used to refer to the schema that contains the auto-increment row
     * @param col the name of the column to be part of the auto-increment row
     * @throws Exception
     */
    public AutoIncrement(Operation op, String alias, String col, int increment) throws Exception {
        super(op);
        this.alias = alias;
        this.col = col;
        this.increment = increment;
        }



    /**
     * creates the prototype of the single colum schema
     */
    protected Prototype createPrototype() throws Exception {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn(col));
        return pt;
        
    }
    
    @Override
    public Map<String, List<String>> getContentInfo() {
        Map<String,List<String>> map = childOperation.getContentInfo();
        map.put(alias,Arrays.asList(col));
        return map;
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new AutoIncrementIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public void setConnectedDataSources() throws Exception {
        
        //the data sources reached are the ones reached by the child operation 
        //plus the auto-increment source defined by this operation
        
        ReferedDataSource s[] = childOperation.getExposedDataSources();
        connectedDataSources = new ReferedDataSource[s.length+1];
        System.arraycopy(s, 0, connectedDataSources, 1, s.length);
   
        //the first data source is the auto-increment source, so the first column that appears is the auto-increment column
        connectedDataSources[0] = new ReferedDataSource();
        connectedDataSources[0].alias = alias;
        connectedDataSources[0].prototype = createPrototype();
    }
    
    @Override
    public void setExposedDataSources() throws Exception {
        
        //the data sources reached are the ones reached by the child operation 
        //plus the auto-increment source defined by this operation
        
        ReferedDataSource s[] = childOperation.getExposedDataSources();
        dataSources = new ReferedDataSource[s.length+1];
        System.arraycopy(s, 0, dataSources, 1, s.length);
   
        //the first data source is the auto-increment source, so the first column that appears is the auto-increment column
        dataSources[0] = new ReferedDataSource();
        dataSources[0].alias = alias;
        dataSources[0].prototype = createPrototype();
    }

    @Override
    public String toString() {
        return "Auto Increment(" + col+")";
    }

    /**
     * this class produces resulting tuples by taking the child tuples and incrementing the value of the auto-increment column
     */
    private class AutoIncrementIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;
        
        int autoInc = 1;

        
        public AutoIncrementIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            autoInc = increment;
            tuples = childOperation.lookUp(processedTuples, false);//accesses all tuples produced by the child operation 
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                
                //sets the row of the auto-increment schema
                LinkedDataRow row = new LinkedDataRow(dataSources[0].prototype, false);
                row.setValue(0, autoInc);
                autoInc++;
                
                //joins the row with the ones produced by the child operation.
                //the auto-increment row comes first
                LinkedDataRow rows[] = new LinkedDataRow[tp.rows.length+1];
                System.arraycopy(tp.rows, 0, rows, 1, tp.rows.length);
                rows[0] = row;
                Tuple returnTp = new Tuple();
                returnTp.setSourceRows(rows);
                
                return returnTp;

            }
            return null;
        }


    }
}
