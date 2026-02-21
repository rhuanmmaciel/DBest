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
public class EmptyLogger extends Logger{

    
    @Override
    public void transactionStart(Transaction transaction)  throws Exception{}
    @Override
    public void transactionCommit(Transaction transaction)  throws Exception{}
    @Override
    public void transactionAbort(Transaction transaction)  throws Exception{}
    @Override
    public void transactionWrite(Transaction transaction, Table table, int pk, String oldValue, String newValue)  throws Exception{}
    @Override
    public void writeLog() throws Exception{}
    @Override
    public void recover() throws Exception{}
    @Override
    public void clear() throws Exception{}
        
    
}
