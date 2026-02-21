/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.deadlock.prevention.timeout;


import ibd.transaction.concurrency.LockBasedConcurrencyManager;
import ibd.transaction.Transaction;
import java.util.Hashtable;
import java.util.List;
import ibd.table.prototype.DataRow;


/**
 *
 * @author pccli
 */
public class TimeOutPreventionConcurrencyManager extends LockBasedConcurrencyManager{

    int maxTicks;
    Hashtable<Transaction, Integer> dic = new Hashtable<>();
    
    public TimeOutPreventionConcurrencyManager(int maxTicks) throws Exception{
        super();
        this.maxTicks = maxTicks;
        
    }
    
    @Override
    protected List<DataRow> processCurrentInstruction(Transaction t) throws Exception{
        List<DataRow> recs = super.processCurrentInstruction(t);
        dic.put(t, 0);
        //ystem.out.println(t.getId()+"tick "+0);
        return recs;
    }
    
    @Override
    protected boolean shouldAbort(Transaction t){
        Integer count = dic.get(t);
        if (count==null)
            count = 1;
        else count ++;
        dic.put(t, count);
        //System.out.println(t.getId()+"tick "+count);
        return (count>maxTicks);
    }
    
    protected void abort(Transaction t) throws Exception{
        super.abort(t);
        dic.put(t, 0);
        //System.out.println(t.getId()+"tick "+0);
    }
}
