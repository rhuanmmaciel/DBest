/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.timestamp.ordering;

import ibd.transaction.log.EmptyLogger;
import ibd.transaction.instruction.Instruction;
import ibd.transaction.Transaction;
import ibd.transaction.concurrency.ConcurrencyManager;
import ibd.transaction.instruction.SingleReadInstruction;
import ibd.transaction.instruction.SingleUpdateInstruction;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class TimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;
    Hashtable<Transaction, Integer> transactionsStart = new Hashtable();
    Hashtable<String, List<Transaction>> ItensLastRead = new Hashtable();
    Hashtable<String, List<Transaction>> ItensLastWrite = new Hashtable();

    

    private Integer getLastReadTime(String item) {
        List<Transaction> list = ItensLastRead.get(item);
        if (list == null) {
            return -1;
        }
        if (list.isEmpty()) {
            return -1;
        }
        Transaction t = list.get(0);
        return getTransactionStartTime(t);
    }

    private void addLastReadTime(String item, Transaction t) {

        List<Transaction> list = ItensLastRead.get(item);
        if (list == null) {
            list = new ArrayList();
            ItensLastRead.put(item, list);
        }
        list.add(0, t);
    }

    private void addLastWriteTime(String item, Transaction t) {
       

        List<Transaction> list = ItensLastWrite.get(item);
        if (list == null) {
            list = new ArrayList();
            ItensLastWrite.put(item, list);
        }
        list.add(0, t);
    }

    private Integer getLastWriteTime(String item) {
        List<Transaction> list = ItensLastWrite.get(item);
        if (list == null) {
            return -1;
        }
        if (list.isEmpty()) {
            return -1;
        }
        Transaction t = list.get(0);
        return getTransactionStartTime(t);
    }

    /**
     *
     * @throws Exception
     */
    public TimestampOrderingConcurrencyManager() throws Exception {
        //logger = new Logger0("c:\\teste\\ibd", "log.txt");
        logger = new EmptyLogger();
    }

    /**
     * Tries to process the current instrucion of a transaction
     *
     * @param t the transaction from which the current instruction is to be
     * processed
     * @return the records affected by the instruction. If the instruction was
     * not executed by some reason, the return is null
     * @throws Exception
     */
    @Override
    public List<DataRow> processInstruction(Transaction t) throws Exception {

        boolean ignore = false;
        
        //increments the timestamp counter
        currentTime++;

        Integer tStart = getTransactionStartTime(t);

        //puts the transaction in the recovery log if its not already there
        logTansactionStart(t);

        Instruction i = t.getCurrentInstruction();

        if (i instanceof SingleUpdateInstruction) {
            SingleUpdateInstruction ui = (SingleUpdateInstruction) i;
            Integer lastReadTime = getLastReadTime(ui.getUniqueKey());
            if (lastReadTime > tStart) {
                abort(t);
                return null;
            }

            Integer lastWriteTime = getLastWriteTime(ui.getUniqueKey());
            if (lastWriteTime < tStart) {
                addLastWriteTime(ui.getUniqueKey(), t);
            }
            else ignore = true;
        } else if (i instanceof SingleReadInstruction) {
            SingleReadInstruction ri = (SingleReadInstruction) i;
            Integer lastWriteTime = getLastWriteTime(ri.getUniqueKey());
            if (lastWriteTime > tStart) {
                abort(t);
                return null;
            }
            Integer lastReadTime = getLastReadTime(ri.getUniqueKey());
            if (lastReadTime < tStart) {
                addLastReadTime(ri.getUniqueKey(), t);
            }

        }

        if (ignore){
            int iv = 0;
        }
            
            return processCurrentInstruction(t, ignore);
    }

    /**
     * Marks the moment when the transaction started, based on the currentTime
     * variable. If it has already started, nothing is done
     *
     * @param t
     */
    private Integer getTransactionStartTime(Transaction t) {
        Integer ts = transactionsStart.get(t);
        if (ts == null) {
            ts = currentTime;
            transactionsStart.put(t, currentTime);
        }
        return ts;
    }

    /**
     * Effectivelly processes the current instrucion of a transaction.
     *
     * @param t
     * @return
     * @throws Exception
     */
    protected List<DataRow> processCurrentInstruction(Transaction t, boolean ignore) throws Exception {
        
        if (ignore){
            t.advanceInstruction();
            return new ArrayList();
        }
        
        Instruction i = t.getCurrentInstruction();

        List<DataRow> recs = i.process();
        if (i.endProcessing()) {
            t.advanceInstruction();
        }

        return recs;
    }

    /**
     * Aborts a transaction
     *
     * @param t
     * @throws Exception
     */
    @Override
    protected void abort(Transaction t) throws Exception {
        //System.out.println("Aborting "+t);
        super.abort(t);
        transactionsStart.remove(t);
        for (List<Transaction> list : ItensLastRead.values()) {
            for (int i = list.size()-1; i >=0; i--) {
                Transaction t_ = list.get(i);
                if (t_.equals(t))
                    list.remove(i);
            }
        }
        
        for (List<Transaction> list : ItensLastWrite.values()) {
            for (int i = list.size()-1; i >=0; i--) {
                Transaction t_ = list.get(i);
                if (t_.equals(t))
                    list.remove(i);
            }
        }

    }

}
