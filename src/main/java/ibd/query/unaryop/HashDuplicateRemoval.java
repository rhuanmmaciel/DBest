/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This operation removes duplicated tuples that cmoe frmo the chilp operation.
 * The duplicates are identified by using a materialized hash table
 *
 * @author Sergio
 */
public class HashDuplicateRemoval extends UnaryOperation {


    //the materialized hash table
    HashMap<String, Tuple> allTuples;
    //the tuple size, used to control the size of the hash table
    int tupleSize = 0;
    
    /**
     *
     * @param op the operation to be connected into this unary operation
     * @throws Exception
     */
    public HashDuplicateRemoval(Operation op) throws Exception {
        super(op);
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();
        tupleSize = childOperation.getTupleSize();
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new HashDuplicateRemovalIterator(processedTuples, withFilterDelegation);
    }
    
    /**
     *
     * @return the name of the operation
     */
    @Override
    public String toString() {
        return "Hash Duplicate Removal";
    }

    /**
     * this class produces resulting tuples by removing duplicates from the
     * child operation
     */
    private class HashDuplicateRemovalIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;
        
        //the iterator over the child operation
        Iterator<Tuple> allTuplesIterator;
        
        public HashDuplicateRemovalIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            tuples = childOperation.lookUp(processedTuples, false);//accesses all tuples produced by the child operation 
            allTuples = new HashMap<>();
        }
        
        //gets the key to a tuple.
        //the key is the concatenation of all values that are part of the tupe
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

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                
                //the tuples are added to the hash table
                //duplicated keys are ignored
                String key = getKey(tp);
                allTuples.put(key, tp);
            }
            
            if (allTuplesIterator==null){
                allTuplesIterator = allTuples.values().iterator();
                QueryStats.MEMORY_USED += allTuples.size()*tupleSize;
            }
            
            //the tuples are returned from the hash table, which is duplicate free
            while(allTuplesIterator.hasNext()){
                Tuple tp = allTuplesIterator.next();
                return tp;
            }
            
            
            return null;
        }

    }
}
