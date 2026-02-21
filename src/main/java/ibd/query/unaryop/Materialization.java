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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This unary operation is materialized. It creates a list containing all the
 * tuples that come from the child operation. The scan over the materialized
 * tuples is cheaper than scanning tuples from persistent storage.
 *
 * @author Sergio
 */
public class Materialization extends UnaryOperation {

    /*
    materialized list of tuples.
    This collection is shared among all private iterators, because we need all queries issued over this operation to use the same collection.
     */
    List<Tuple> tuples;

    /**
     *
     * @param childOperation the child operation
     * @throws Exception
     */
    public Materialization(Operation childOperation) throws Exception {
        super(childOperation);
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();

        //erases the previously built list.
        //a new one is created when the first query is executed. 
        tuples = null;
    }
    
    @Override
    public boolean canProcessDelegatedFilters() {
        return true;
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new MaterializedIndexIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public String toString() {
        return "Materialized";
    }

    /**
     * the class that materializes a collection of tuples that come from the
     * child operation. The query is answered using the materialized collection.
     */
    public class MaterializedIndexIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> it = null;

        public MaterializedIndexIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {

            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            buildList();
            it = tuples.iterator();
        }

        private void buildList() {
            //build list, if one does not exist yet
            if (tuples == null) {
                tuples = new ArrayList<>();
                int tupleSize = 0;
                try {
                    tupleSize = childOperation.getTupleSize();
                } catch (Exception ex) {
                }
                //accesses and indexes all tuples that come from the child operation
                it = childOperation.lookUp(processedTuples, false);

                while (it.hasNext()) {
                    Tuple tuple = (Tuple) it.next();
                    tuples.add(tuple);
                }

                QueryStats.MEMORY_USED += tuples.size() * tupleSize;
            }

        }

        @Override
        protected Tuple findNextTuple() {
            while (it.hasNext()) {
                Tuple tp = it.next();
                //a tuple must satisfy the lookup filter that comes from the parent operation
                //only the unhashed filters need to be checked
                if (lookup.match(tp)) {
                    return tp;
                }

            }
            return null;
        }

    }

}
