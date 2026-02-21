/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.ColumnDescriptor;
import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import java.util.Iterator;
import java.util.List;

/**
 * This operation explodes a tuple into multiple tuples by splitting the content
 * of a column
 *
 * @author Sergio
 */
public class Explode extends UnaryOperation {

    ColumnDescriptor columnToExplode;
    String delimiter;
    int colIndex;
    int rowIndex;

    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param columnToExplode the name of the column whose content is be to
     * split. The name can be prefixed by the table name (e.g. tab.col)
     * @param delimiter the character used to split the content of the selected
     * column
     * @throws Exception
     */
    public Explode(Operation op, String columnToExplode, String delimiter) throws Exception {
        super(op);
        this.columnToExplode = new ColumnDescriptor(columnToExplode);
        this.delimiter = delimiter;
    }

    @Override
    public void prepare() throws Exception {

        super.prepare();

        setColumnIndexes();
    }

    /**
     * sets the column and row indexes of the column to be split. the indexes
     * are used to speed up locating the column value
     */
    private void setColumnIndexes() throws Exception {
        childOperation.setColumnLocation(columnToExplode);
        colIndex = columnToExplode.getColumnLocation().colIndex;
        rowIndex = columnToExplode.getColumnLocation().rowIndex;
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new ExplodeColumnIterator(processedTuples, withFilterDelegation);
    }

    @Override
    public String toString() {
        return "Explode(" + columnToExplode + ")";
    }

    /**
     * this class produces resulting tuples by exploding a tuple into multiple
     * tuples by splitting the content of a column that comes from the child
     * operation
     */
    private class ExplodeColumnIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;

        LinkedDataRow currentRow;
        Tuple currentTuple;
        String[] explodedValues;
        int explodedValuesIndex = -1;

        public ExplodeColumnIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            tuples = childOperation.lookUp(processedTuples, false);//accesses all tuples produced by the child operation 
        }

        /**
         * creates a tuple to be returned. The tupple is the same of the tuple
         * accessed from the child operation except that the row that contains
         * the selected column is replaced by a row where the selected column
         * has only a token of the splited value 
         */
        private Tuple createNewTuple() {
            //copies the selected row, only replacing the content of the column that was splitted
            LinkedDataRow newRow = new LinkedDataRow(currentRow);
            //the content is the next token of the splitted value
            newRow.setValue(colIndex, explodedValues[explodedValuesIndex].trim());
            //the token advances
            explodedValuesIndex++;
            
            //a tuple is produced 
            Tuple newTuple = new Tuple();
            //it is a copy of the original tuple
            newTuple.setSourceRows(currentTuple);
            //only one row needs to be replaced
            newTuple.rows[rowIndex] = newRow;
            return newTuple;
        }

        @Override
        protected Tuple findNextTuple() {

            //the iteration proceed while there are still tuples to be read of the current tuple has tokens to be processed
            while (tuples.hasNext() || (explodedValuesIndex > -1 && explodedValuesIndex < explodedValues.length)) {
                
                //there are tokens of the current tuple to be processed
                if (explodedValuesIndex > -1 && explodedValuesIndex < explodedValues.length) {
                    //the next token is used to produce a resulting tuple
                    Tuple newTuple = createNewTuple();
                    return newTuple;
                }

                //if it gets here, all tokens were processed and we need to read a new tuple
                currentTuple = tuples.next();
                
                //the value of the selected column is splitted
                currentRow = currentTuple.rows[rowIndex];
                String value = currentRow.getValue(colIndex).toString();
                explodedValues = value.split(delimiter);
                //if the split fails, we make sure at least one empty token is generated
                if (explodedValues.length == 0) {
                    explodedValues = new String[]{""};
                }
                //defines the next token to be read as the first one
                explodedValuesIndex = 0;
                //returns a new tuple by taking the first token
                Tuple newTuple = createNewTuple();
                return newTuple;
            }

            return null;
        }

    }
}
