/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract classe that implements the basic strutucture of a hash join.
 * A hash join computes joins between the left and the right operations using
 * the terms provided by the join predicate. Tuples from the right side are hashed
 * to speed up lookups. The hash uses as key the columns from the join predicate
 *
 * @author Sergio
 */
public abstract class HashJoin extends Join {


    /*
    materialized hash of tuples, using as key the columns from the join predicate.
    This collection is shared among all private iterators, because we need all queries issued over this operation to use the same collection.
     */
    protected HashMap<String, List<Tuple>> tuples;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param joinPredicate the join predicate
     * @throws Exception
     */
    public HashJoin(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation, joinPredicate);
    }

    @Override
    public void prepare() throws Exception {

        super.prepare();

        //erases the previously built hash.
        //a new one is created when the first query is executed. 
        tuples = null;

    }

    protected void buildHash(List<Tuple> processedTuples) {
        //build hash, if one does not exist yet
        if (tuples == null) {
            tuples = new HashMap();
            long memoryUsed = 0;
            try {
                //accesses and indexes all tuples that come from the child operation
                Iterator<Tuple> it = rightOperation.lookUp(processedTuples, false);
                int tupleSize = rightOperation.getTupleSize();
                while (it.hasNext()) {
                    Tuple tuple = (Tuple) it.next();
                    String key = "";
                    for (JoinTerm term : joinPredicate.getTerms()) 
                    {
                        ColumnDescriptor col = term.getRightColumnDescriptor();
                        key += tuple.rows[col.getColumnLocation().rowIndex].getValue(col.getColumnLocation().colIndex).toString();
                    }

                    List tupleList = tuples.get(key);
                    if (tupleList == null) {
                        tupleList = new ArrayList();
                        tuples.put(key, tupleList);
                    }
                    tupleList.add(tuple);
                    memoryUsed += tupleSize;
                }

            } catch (Exception ex) {
            }
            QueryStats.MEMORY_USED += memoryUsed;
        }

    }

    //set the key with the left-side values that are necessary to perform the lookup.
    //used during query execution, when a left-side tuple performs a lookup
    protected String fillKey(Tuple currentLeftTuple) {
        String key = "";
        List<JoinTerm> joinTerms = joinPredicate.getTerms();
        for (int i = 0; i < joinTerms.size(); i++) {
            JoinTerm joinTerm = joinTerms.get(i);
            Comparable value = currentLeftTuple.rows[joinTerm.getLeftColumnDescriptor().getColumnLocation().rowIndex].getValue(joinTerm.getLeftColumnDescriptor().getColumnLocation().colIndex);
            key += value;

        }
        return key;
    }

}
