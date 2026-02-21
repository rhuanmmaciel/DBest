/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.unaryop.sort.external;

import ibd.query.Tuple;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ferna
 */
public class InputBucketWrapper implements Iterator<Tuple>{

    InputBucket ib = null;
    
    public InputBucketWrapper(InputBucket ib){
        this.ib = ib;
    }
    
    @Override
    public boolean hasNext() {
        try {
            return ib.hasNext();
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Tuple next() {
        try {
            return ib.next();
        } catch (Exception ex) {
            return null;
        }
    }
    
}
