/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.transaction.concurrency;

import ibd.transaction.Transaction;

/**
 *
 * @author pccli
 */
public class Lock {
    public Transaction transaction;
    public int mode;
    
    public Lock(Transaction t, int mode){
        transaction =  t;
        this.mode = mode;
    }
    
}
