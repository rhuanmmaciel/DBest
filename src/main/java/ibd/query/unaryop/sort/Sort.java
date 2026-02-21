/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.sort;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.query.unaryop.UnaryOperation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A sort operation sorts tuples that come from its child operation. Tuples are
 * sorted according to the value of a specified column. This operation is
 * materialized.
 *
 * @author Sergio
 */
public class Sort extends UnaryOperation {

    /*
    * a materialized shared collection of objects.
    * all queries over this operation share the same collection.
     */
    ArrayList<Tuple> tuples;

    List <ColumnDescriptor> sortColumns;
    
    boolean ascending = true;

    
    /**
     *
     * @param childOperation the child operation
     * @param col the name of the column used to sort the tuples. The name can
     * be prefixed by the table name (e.g. tab.col)
     * @throws Exception
     */
    public Sort(Operation childOperation, String col, boolean ascending) throws Exception {
        super(childOperation);
        this.ascending = ascending;
        sortColumns = new ArrayList();
            ColumnDescriptor sortColumn = new ColumnDescriptor(col);
            sortColumns.add(sortColumn);
        
    }
    
    /**
     *
     * @param childOperation the child operation
     * @param columns the name of the column used to sort the tuples. 
     * @throws Exception
     */
    public Sort(Operation childOperation, String[] columns, boolean ascending) throws Exception {
        super(childOperation);
        this.ascending = ascending;
        sortColumns = new ArrayList();
        for (String col : columns) {
            ColumnDescriptor sortColumn = new ColumnDescriptor(col);
            sortColumns.add(sortColumn);
        }
        
    }

    @Override
    public void prepare() throws Exception {
        
        //sets the index of the tuple that contains the column used to sort the tuples
        //tupleIndex = childOperation.getTupleIndex(sortColumn.getTableName());
        setSortColumnsIndexes();
        
        tuples = null;
        
        super.prepare();
        
    }
    
    private void setSortColumnsIndexes() throws Exception{
        for (ColumnDescriptor sortColumn : sortColumns) {
            //int index = childOperation.getRowIndex(sortColumn.getTableName());
            //sortColumn.setTupleIndex(index);
            childOperation.setColumnLocation(sortColumn);
        }
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples,  boolean withFilterDelegation) {
        return new SortIterator(processedTuples,  withFilterDelegation);
    }

    /**
     *
     * @return the comparator class used to sort tuples.
     */
    public Comparator<Tuple> createComparator() {
        return new TupleComparator();
    }

    /**
     * this class produces resulting tuples from the sorting over the tuples
     * that come from the child operation. The incoming tuples are materialized
     * in memory, and then sorted.
     */
    private class SortIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> it;

        /**
         * @param processedTuples the tuples that come from operations already processed.  The columns from these tuples can be used by the unprocessed operations, like for filtering. 
         * @param lookup the condition from the parent operation that needs to
         * be satisfied
         */
        public SortIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
        super(processedTuples, withFilterDelegation, getDelegatedFilters());   
           
            //build materialized collection, if one does not exist yet.
            if (tuples == null) {
                tuples = new ArrayList<>();
                try {

                    //ExternalMergeSort sorter = new ExternalMergeSort(5000, 5000);
                    //sorter.setTupleIndex(sourceTupleIndex);
                    //accesses and stores all tuples that come from the child operation
                    int tupleSize = childOperation.getTupleSize();
                    it = childOperation.lookUp(processedTuples,  false);
                    while (it.hasNext()) {
                        Tuple tuple = (Tuple) it.next();
                        tuples.add(tuple);
                        //sorter.addTuple(tuple);

                    }
                    QueryStats.MEMORY_USED+=tuples.size()*tupleSize;
                    //sort collection
                    Comparator comparator = createComparator();
                    if (!ascending)
                        comparator = Collections.reverseOrder(comparator);
                    Collections.sort(tuples, comparator);
                    
                    ibd.query.QueryStats.SORT_TUPLES+=tuples.size();

                    //sortedInputBucket = sorter.sort();
                } catch (Exception ex) {
                    Logger.getLogger(Sort.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            //build iterator
            it = tuples.iterator();

            //it = new InputBucketWrapper(sortedInputBucket);
        }

        @Override
        protected Tuple findNextTuple() {
            //iterates over all tuples from the materialized collection.
            while (it.hasNext()) {
                Tuple tp = it.next();
                return tp;
            }
            return null;
        }

    }

    /**
     * A comparator class that defines how tuples are sorted. The tuples are
     * compared by the order of the sortColumns.
     */
    public class TupleComparator implements Comparator<Tuple> {

        @Override
        public int compare(Tuple tt1, Tuple tt2) {
            for (ColumnDescriptor sortColumn : sortColumns) {
                Comparable value1 = tt1.rows[sortColumn.getColumnLocation().rowIndex].getValue(sortColumn.getColumnLocation().colIndex);
                Comparable value2 = tt2.rows[sortColumn.getColumnLocation().rowIndex].getValue(sortColumn.getColumnLocation().colIndex);
                int comp = value1.compareTo(value2);
                if (comp!=0) return comp;
            }
            
            
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Sort(" + sortColumns+")";
    }

}
