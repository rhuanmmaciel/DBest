/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.log;

import ibd.table.Table;
import ibd.transaction.Transaction;

/**
 *
 * @author pccli
 */
public abstract class Logger {

    
    public abstract void transactionStart(Transaction transaction)  throws Exception;
    public abstract void transactionCommit(Transaction transaction)  throws Exception;
    public abstract void transactionAbort(Transaction transaction)  throws Exception;
    public abstract void transactionWrite(Transaction transaction, Table table, int pk, String oldValue, String newValue)  throws Exception;
    public abstract void writeLog() throws Exception;
    public abstract void recover() throws Exception;
    public abstract void clear() throws Exception;
        
    
}
