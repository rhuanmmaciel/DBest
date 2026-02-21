/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query;

import java.util.Arrays;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.query.fields.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A tuple is a concatenation of data rows. It is produced as data rows are
 * combined, for instance, by join operations.
 *
 * @author Sergio
 */
public class Tuple implements Comparable<Tuple> {

    /**
     * the data rows combined.
     */
    public LinkedDataRow rows[];

    /**
     * Defines a single source of information for this tuple. Useful when the
     * tuples are produced by a source operation that has direct access to the
     * records.
     *
     * @param dataSourceAlias the alias of the data source
     * @param row the row accessed through the data source
     */
    public void setSingleSourceRow(String dataSourceAlias, LinkedDataRow row) {
        rows = new LinkedDataRow[1];
        rows[0] = row;
    }

    /**
     * sets the source rows directly.
     *
     * @param rows
     */
    public void setSourceRows(LinkedDataRow rows[]) {
        this.rows = rows;
    }

    /**
     * Copies the tuple source rows from one tuple to this tuple. Useful when
     * the tuple is produced by taking data from a single tuple, as in unary
     * operations.
     *
     * @param t
     */
    public void setSourceRows(Tuple t) {
        rows = new LinkedDataRow[t.rows.length];
        int count = 0;
        for (LinkedDataRow sourceTuple : t.rows) {
            rows[count] = sourceTuple;
            count++;
        }

    }
    
    
    public void setSourceRows(List<Tuple> tuples) {
        List<LinkedDataRow> rowList = new ArrayList();
        for (Tuple tuple : tuples) {
            for (LinkedDataRow row : tuple.rows) {
                rowList.add(row);
            }
        }
        
        rows = new LinkedDataRow[rowList.size()];
        int count = 0;
        for (LinkedDataRow sourceTuple : rowList) {
            rows[count] = sourceTuple;
            count++;
        }

    }

    /**
     * Copies the tuple sources from two tuples to this tuple. Useful when the
     * tuple is produced as the result of a join operation.
     *
     * @param t1
     * @param t2
     */
    public void setSourceRows(Tuple t1, Tuple t2) {
        rows = new LinkedDataRow[t1.rows.length + t2.rows.length];
        int count = 0;
        for (LinkedDataRow sourceTuple : t1.rows) {
            rows[count] = sourceTuple;
            count++;
        }
        for (LinkedDataRow sourceTuple : t2.rows) {
            rows[count] = sourceTuple;
            count++;
        }

    }

    @Override
    public boolean equals(Object otherObject) {
        Tuple otherTuple = (Tuple) otherObject;
        if (rows.length != otherTuple.rows.length) {
            return false;
        }
        for (int i = 0; i < rows.length; i++) {
            if (rows[i].compareTo(otherTuple.rows[i]) != 0) {
                return false;
            }

        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Arrays.deepHashCode(this.rows);
        return hash;
    }

    /**
     *
     * @return
     */
    public int size() {
        //return sourceTuples.length * Record.RECORD_SIZE;
        //need to adjust if tuple size is variable
        int size = 0;
        for (LinkedDataRow row : rows) {
            size+=row.getPrototype().getSizeInBytes();
        }
        return size;
        //return rows.length * 1;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        String str = new String();
        for (DataRow st : rows) {
            str += st.toString() + ", ";
        }
        return str;
    }

    @Override
    public int compareTo(Tuple t2) {
        for (int i = 0; i < rows.length; i++) {
            LinkedDataRow st1 = rows[i];
            LinkedDataRow st2 = t2.rows[i];
            for (int j = 0; j < st1.getFieldsSize(); j++) {
                Field f1 = st1.getField(j);
                Field f2 = st2.getField(j);
                int comp = f1.compareTo(f2);
                if (comp != 0) {
                    return comp;
                }
            }
        }
        return 0;
    }

}
