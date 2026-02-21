/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.optimistic;

import ibd.table.prototype.BasicDataRow;
import ibd.transaction.instruction.Instruction;
import ibd.transaction.instruction.SingleReadInstruction;
import ibd.transaction.SimulatedIterations;
import ibd.transaction.Transaction;
import ibd.transaction.instruction.SingleUpdateInstruction;
import ibd.transaction.concurrency.ConcurrencyManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class DanielOptimisticConcurrencyManager extends ConcurrencyManager{
    int currentTime = 0;
    Hashtable<Integer, Integer> transactionStarts = new Hashtable<>();
    Hashtable<Integer, Integer> transactionCommits = new Hashtable<>();

    Hashtable<Integer, HashSet<Instruction>> readSets = new Hashtable<>();
    Hashtable<Integer, HashSet<Instruction>> writeSets = new Hashtable<>();
    Hashtable<Integer, HashSet<Instruction>> recordWriteInstructions = new Hashtable<>();

    /**
     *
     * @throws Exception
     */
    public DanielOptimisticConcurrencyManager() throws Exception {
    }
    

    
    /**
     * Process the current instrucion of a transaction
     *
     * @param t the transaction from which the current instruction is to be
     * processed
     * @return the record affected by the instruction. If the instruction was
     * not executed for some reason, the return is null
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


    /**
     * Marks the moment when the transaction started, based on the currentTime variable.
     * If it has already started, nothing is done
     *
     * @param t 
     */
    private void setTransactionStartTime(Transaction t) {
        this.transactionStarts.put(t.getId(), this.currentTime);
    }

    /**
     * Marks the moment when the transaction commited, based on the currentTime variable.
     *
     * @param t 
     */
    private void setTransactionCommitTime(Transaction t) {
        System.out.println("Commit "+t);
        this.transactionCommits.put(t.getId(), this.currentTime);
    }

    
    /**
     * Adds an instruction into the write instructions of a transaction (The Write Set).
     *
     * @param t 
     */
    private void addWriteInstruction(Transaction t, SingleUpdateInstruction i) {
//        System.out.println("ADD WRITE "+i.getUniqueKey());
        int transactionId = t.getId();

        HashSet<Instruction> set = this.writeSets.get(transactionId);
        HashSet<Instruction> recordWriteSet = this.recordWriteInstructions.get(i.hashCode());

        if (set == null) {
            set = new HashSet<>();
            this.writeSets.put(transactionId, set);
        }

        if (recordWriteSet == null) {
            recordWriteSet = new HashSet<>();
            this.recordWriteInstructions.put(i.hashCode(), recordWriteSet);
        }

        set.add(i);
        recordWriteSet.add(i);
    }

    /**
     * Adds an instruction into the read instructions of a transaction (The Read Set).
     *
     * @param t 
     */
    private void addReadInstruction(Transaction t, SingleReadInstruction i) {
        int transactionId = t.getId();
        HashSet<Instruction> set = this.readSets.get(transactionId);
        if (set == null) {
            set = new HashSet<>();
            this.readSets.put(transactionId, set);
        }
        set.add(i);
    }

    
    /**
     * Returns a write instruction associated with the same record of a readInstruction.
     * Returns null if it does not exist
     *
     * @param t 
     */
    private SingleUpdateInstruction getWriteInstruction(Instruction readInstruction){
        HashSet<Instruction> set = this.recordWriteInstructions.get(readInstruction.hashCode());
        if (set != null && !set.isEmpty()) {
            return (SingleUpdateInstruction) set.iterator().next();
        }

        return null;
    }
    
    /**
     * Gets the Write Instructions of a transaction (The Write Set).
     *
     * @param t 
     */
    private Iterator<SingleUpdateInstruction>  getWriteInstructions(Transaction t){
        return (Iterator<SingleUpdateInstruction>) this.writeSets.get(t.getId());
    }
    
    /**
     * Removes a transaction from the control data structures
     * @param t
     * @throws Exception
     */
    private void removeTransaction(Transaction t){
        HashSet<Instruction> writeSet = this.writeSets.remove(t.getId());
        this.readSets.remove(t.getId());
        this.transactionStarts.remove(t.getId());
        this.transactionCommits.remove(t.getId());

        if (writeSet != null && !writeSet.isEmpty()) {
            for (Instruction i : writeSet) {
                HashSet<Instruction> s = this.recordWriteInstructions.get(i.hashCode());
                if (s != null && !s.isEmpty()) {
                    s.remove(i);
                }
            }
        }
    }
    
    /**
     * Verifies if a transaction can commit, using an optimistic strategy
     * @param t
     * @throws Exception
     */
    private boolean validateTransaction(Transaction t) {
        HashSet<Instruction> readSet = this.readSets.get(t.getId());

        if (readSet != null && !readSet.isEmpty()) {
            for (Instruction i : readSet) {
                Instruction relatedWI = this.getWriteInstruction(i);
                if (relatedWI != null) {
                    return this.transactionStarts.get(t.getId()) > this.transactionCommits.get(relatedWI.getTransaction().getId());
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
//        System.out.println("Aborting "+t);
        removeTransaction(t);
        System.out.println(SimulatedIterations.getTab(t.getId() - 1) + t.getId() + " Abort");
        t.abort();
    }
    
    @Override
    public void recoverFromLog() throws Exception {
        
    }

    @Override
    public void clearLog() throws Exception {

    }

    @Override
    public void flushLog() throws Exception {
        
    }
}
