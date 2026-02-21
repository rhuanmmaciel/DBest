/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.set;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import java.util.Iterator;
import java.util.List;

/**
 * Performs a difference between the left and the right operations.
 *
 * @author Sergio
 */
public class Intersection extends Set { 

    public Intersection(Operation leftOperation, Operation rightOperation) throws Exception {
        super(leftOperation, rightOperation);
    }

    @Override
    public String toString() {
        return "Intersection";
    }

    /**
     * {@inheritDoc }
     *
     * @return an iterator that performs a difference over the
     * tuples from the left and right sides
     */
    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new IntersectionIterator(processedTuples, withFilterDelegation);
    }

    /**
     * the class that produces resulting tuples from the difference between the two
     * underlying operations.
     */
    private class IntersectionIterator extends UnpagedOperationIterator {

        Tuple rightTuple;
        //the iterator over the operation on the left side
        Iterator<Tuple> leftTuples;
        //the iterator over the operation on the left side
        Iterator<Tuple> rightTuples;

        public IntersectionIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());

            leftTuples = leftOperation.lookUp(processedTuples, false); //iterate over all tuples from the left side
            rightTuples = rightOperation.lookUp(processedTuples, false); //iterate over all tuples from the left side
        }

        @Override
        protected Tuple findNextTuple() {

            while (leftTuples.hasNext()) {
                Tuple leftTuple = leftTuples.next();
                if (hasEqual(leftTuple)) {
                    Tuple tuple = new Tuple();
                    tuple.setSourceRows(leftTuple);
                    return tuple;
                }
            }
            return null;
        }

//        private int compare(Tuple t1, Tuple t2) {
//            for (int i = 0; i < t1.rows.length; i++) {
//                LinkedDataRow st1 = t1.rows[i];
//                LinkedDataRow st2 = t2.rows[i];
//                for (int j = 0; i < st1.getFieldsSize(); i++) {
//                    Field f1 = st1.getField(j);
//                    Field f2 = st2.getField(j);
//                    int comp = f1.compareTo(f2);
//                    if (comp != 0) {
//                        return comp;
//                    }
//                }
//            }
//            return 0;
//        }

        /**
         * Retorna se a tupla de entrada possui uma correspondente na operação
         * direita
         */
        private boolean hasEqual(Tuple leftTuple) {
            while (rightTuple != null || rightTuples.hasNext()) {
                if (rightTuple == null) {
                    rightTuple = rightTuples.next();
                }
                int compare = leftTuple.compareTo(rightTuple);
                if (compare < 0) {
                    return false;
                } else if (compare == 0) {
                    //rightTuple = null;
                    return true;
                }
                rightTuple = null;
            }
            return false;
        }

    }

}
