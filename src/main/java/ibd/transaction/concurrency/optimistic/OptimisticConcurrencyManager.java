/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.optimistic;

import ibd.table.prototype.BasicDataRow;
import ibd.transaction.instruction.Instruction;
import ibd.transaction.instruction.SingleReadInstruction;
import ibd.transaction.Transaction;
import ibd.transaction.instruction.SingleUpdateInstruction;
import ibd.transaction.concurrency.ConcurrencyManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class OptimisticConcurrencyManager extends ConcurrencyManager{

    Hashtable<Transaction, TransactionValidation> transactions = new Hashtable();

    int currentTime = 0;

    /**
     *
     * @throws Exception
     */
    public OptimisticConcurrencyManager() throws Exception {
    }
    

    
    /**
     * Process the current instrucion of a transaction
     *
     * @param t the transaction from which the current instruction is to be
     * processed
     * @return the record affected by the instruction. If the instruction was
     * not executed by some reason, the return is null
     * @throws Exception
     */
    @Override
    public List<DataRow> processInstruction(Transaction t) throws Exception {

        //increments the timestamp counter
        currentTime++;
        
        setTransactionStartTime(t);

        Instruction i = t.getCurrentInstruction();

        if (i instanceof SingleUpdateInstruction) {
            
            SingleUpdateInstruction wi = (SingleUpdateInstruction)i;
            
            //adds instruction into the write set
            addWriteInstruction(t, wi);
            
            //cannot return a record yet, since the record is only effectivelly updated after commit. 
            //returns a fake record instead
            BasicDataRow fakeRecord = new BasicDataRow();
            fakeRecord.setInt("id",wi.getPk());
            fakeRecord.setString("nome", wi.getContent());
            
            t.advanceInstruction();
            List<DataRow> list = new ArrayList();
            list.add(fakeRecord);
            return list;
        } else {//it is a read set.
            SingleReadInstruction ri = (SingleReadInstruction)i;
            //checks if the transaction has updated the item it wants to read
            SingleUpdateInstruction wi = getWriteInstruction(ri);
            if (wi != null) {
                //reads from the updated value.
                //cannot return a record yet, since the record is only effectivelly updated after commit. 
                //returns a fake record instead
                BasicDataRow fakeRecord = new BasicDataRow();
                fakeRecord.setInt("id",wi.getPk());
                fakeRecord.setString("nome", wi.getContent());
                t.advanceInstruction();
                List<DataRow> list = new ArrayList();
                list.add(fakeRecord);
                return list;
            } else {
                //reads from the database
                List<DataRow> recs = i.process();
                //adds instruction into the read set
                addReadInstruction(t, ri);

                t.advanceInstruction();
                return recs;
                
            }

        }
    }

    
    
    /**
     * Commits the transaction.
     *
     * @param t the transaction to be committed
     * @return a flag indicating the status of the operation
     * @throws Exception
     */
    @Override
    public boolean commit(Transaction t) throws Exception {

        boolean validate = validateTransaction(t);
        if (!validate) {
            abort(t);
            return false;
        }
        
        setTransactionCommitTime(t);
        
        //effectivelly performs the write instructions of the commited transaction
        Iterator<SingleUpdateInstruction> it = getWriteInstructions(t);
        while (it.hasNext()) {
            Instruction inst = it.next();
            inst.process();
        }

        t.commit();
        return true;
    }

    
    class TransactionValidation {

        int startTime = Integer.MAX_VALUE;
        int commitTime = Integer.MAX_VALUE;
        Transaction transaction;
        Hashtable<String, SingleUpdateInstruction> writeInstructions = new Hashtable();
        Hashtable<String, SingleReadInstruction> readInstructions = new Hashtable();

    }

    /**
     * Marks the moment when the transaction started, based on the currentTime variable.
     * If it has already started, nothing is done
     *
     * @param t 
     */
    private void setTransactionStartTime(Transaction t) {
        TransactionValidation tv = transactions.get(t);
        if (tv == null) {
            tv = new TransactionValidation();
            tv.transaction = t;
            transactions.put(t, tv);
        }
        if (tv.startTime == Integer.MAX_VALUE) {
            tv.startTime = currentTime;
        }
    }

    /**
     * Marks the moment when the transaction commited, based on the currentTime variable.
     *
     * @param t 
     */
    private void setTransactionCommitTime(Transaction t) {
        TransactionValidation tv = transactions.get(t);
        if (tv == null) {
            tv = new TransactionValidation();
            tv.transaction = t;
            transactions.put(t, tv);
        }
        if (tv.commitTime == Integer.MAX_VALUE) {
            tv.commitTime = currentTime;
        }
    }

    
    /**
     * Adds a instruction into the write instructions of a transacion (The Write Set).
     *
     * @param t 
     */
    private void addWriteInstruction(Transaction t, SingleUpdateInstruction i) {
        String key = i.getUniqueKey();
        TransactionValidation tv = transactions.get(t);
        tv.writeInstructions.put(key, i);

    }

    /**
     * Adds a instruction into the read instructions of a transacion (The Read Set).
     *
     * @param t 
     */
    private void addReadInstruction(Transaction t, SingleReadInstruction i) {
        String key = i.getUniqueKey();
        TransactionValidation tv = transactions.get(t);
        tv.readInstructions.put(key, i);

    }

    

    
    /**
     * returns a write instruction associated with the same record of a readInstruction.
     * Returns null if it does not exist
     *
     * @param t 
     */
    private SingleUpdateInstruction getWriteInstruction(Instruction readInstruction){
        String key = readInstruction.getUniqueKey();
        TransactionValidation tv = transactions.get(readInstruction.getTransaction());
        return tv.writeInstructions.get(key);
    }
    
    /**
     * Gets the Write Instructions of a transacion (The Write Set).
     *
     * @param t 
     */
    private Iterator<SingleUpdateInstruction>  getWriteInstructions(Transaction t){
    TransactionValidation ti = transactions.get(t);
    return ti.writeInstructions.values().iterator();
    }
    
    /**
     * Removes a transaction from the control data structures
     * @param t
     * @throws Exception
     */
    private void removeTransaction(Transaction t){
        transactions.remove(t);
    }
    
    /**
     * Verifies if a transaction can commit, using an optimistic strategy
     * @param t
     * @throws Exception
     */
    private boolean validateTransaction(Transaction t) {
        TransactionValidation ti = transactions.get(t);
        Iterator<TransactionValidation> i = transactions.values().iterator();
        while (i.hasNext()) {
            TransactionValidation tk = i.next();
            if (ti.equals(tk)) {
                continue;
            }
            if (!tk.transaction.isCommited()) {
                continue;
            }

            if (tk.commitTime < ti.startTime) {
                continue;
            }

            Enumeration<String> enum_i = ti.readInstructions.keys();
            while (enum_i.hasMoreElements()) {
                String elem_i = enum_i.nextElement();
                Enumeration<String> enum_j = tk.writeInstructions.keys();
                while (enum_j.hasMoreElements()) {
                    String elem_j = enum_j.nextElement();
                    if (elem_i.equals(elem_j)) {
                        return false;
                    }
                }

            }

        }

        return true;

    }

    
    
    
    /**
     * Aborts a transaction
     * @param t
     * @throws Exception
     */
    @Override
    protected void abort(Transaction t) throws Exception {
        //System.out.println("Aborting "+t);
        super.abort(t);
        removeTransaction(t);
    }
    
    
}
