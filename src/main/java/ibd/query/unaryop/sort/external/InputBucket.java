/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.sort.external;

import ibd.query.Tuple;

/**
 *
 * @author Sergio
 */
public interface InputBucket {

    public boolean hasNext()  throws Exception;

    public Tuple next() throws Exception;
    
    public void close() throws Exception;
     

}
