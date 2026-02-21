/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.sort.external;

import ibd.query.Tuple;
import java.util.Comparator;

/**
 *
 * @author Sergio
 */
public class TupleComparator implements Comparator<Tuple>{


    int sortIndex;
    public TupleComparator(int sortIndex){
        this.sortIndex = sortIndex; 
    }

    @Override
    public int compare(Tuple t1, Tuple t2) {
//        return t1.sourceTuples[sortIndex].record.getValue("id").compareTo(
//                        t2.sourceTuples[sortIndex].record.getValue("id"));
        return 0;
    }
    
}
