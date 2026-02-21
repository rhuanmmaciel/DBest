/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.Operation;
import ibd.query.Tuple;
import java.util.Iterator;
import java.util.List;

/**
 * This operation limits the number of tuples that are accessed from its underlying child operation.
 *
 * @author Sergio
 */
public class Limit extends UnaryOperation {

    
    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param tuplesToRead the amount of tuples to be read
     * @param startingTuple the first tuple to be read
     * @throws Exception
     */
    public Limit(Operation op, int tuplesToRead, int startingTuple) throws Exception {
        super(op);
        this.tuplesToRead = tuplesToRead;
        this.startingTuple = startingTuple;
    }


    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        //childOperation.setPageInfo(startingTupleX, tuplesToReadX);
        Iterator<Tuple> tuples =  childOperation.lookUp(processedTuples, withFilterDelegation);
        //childOperation.setPageInfo(-1, -1);
        return tuples;
    }

}
