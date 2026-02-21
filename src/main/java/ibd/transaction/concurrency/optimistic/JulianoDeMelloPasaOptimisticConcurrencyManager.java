/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.optimistic;

import ibd.table.prototype.BasicDataRow;
import ibd.transaction.log.EmptyLogger;
import ibd.transaction.instruction.Instruction;
import ibd.transaction.log.Logger;
import ibd.transaction.instruction.SingleReadInstruction;
import ibd.transaction.SimulatedIterations;
import ibd.transaction.Transaction;
import ibd.transaction.instruction.SingleUpdateInstruction;
import ibd.transaction.concurrency.ConcurrencyManager;

import java.util.*;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class JulianoDeMelloPasaOptimisticConcurrencyManager extends ConcurrencyManager{
    int currentTime = 0;
    HashMap<Transaction, TransactionInfo> transactions = new HashMap<>();
    boolean hasAnyCommits = false;
    public Logger logger;

    /**
     *
     * @throws Exception
     */
    public JulianoDeMelloPasaOptimisticConcurrencyManager() throws Exception {
        logger = new EmptyLogger();
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
     * @param t the transaction to set the start time
     */
    private void setTransactionStartTime(Transaction t) {
        if (transactions.containsKey(t)) return;

        TransactionInfo transactionInfo = new TransactionInfo(currentTime);
        transactions.put(t, transactionInfo);
    }

    /**
     * Marks the moment when the transaction commited, based on the currentTime variable.
     *
     * @param t the transaction to set the finish time
     */
    private void setTransactionCommitTime(Transaction t) {
        transactions.get(t).finish = currentTime;
    }
    
    /**
     * Adds an instruction into the write instructions of a transaction (The Write Set).
     *
     * @param t the transaction to add a WriteInstruction to its set
     */
    private void addWriteInstruction(Transaction t, SingleUpdateInstruction i) {
        transactions.get(t).WS.add(i);
    }

    /**
     * Adds an instruction into the read instructions of a transaction (The Read Set).
     *
     * @param t the transaction to add a ReadInstruction to its set
     */
    private void addReadInstruction(Transaction t, SingleReadInstruction i) {
        transactions.get(t).RS.add(i);
    }

    
    /**
     * Returns a write instruction associated with the same record of a readInstruction.
     * Returns null if it does not exist
     *
     * @param readInstruction readInstruct which we want to get the associated write instruction
     */
    private SingleUpdateInstruction getWriteInstruction(SingleReadInstruction readInstruction){
        TransactionInfo transactionInfo = transactions.get(readInstruction.getTransaction());
        for (SingleUpdateInstruction writeInstruction : transactionInfo.WS) {
            if (writeInstruction.getPk() == readInstruction.getPk()) return writeInstruction;
        }

        return null;
    }
    
    /**
     * Gets the Write Instructions of a transaction (The Write Set).
     *
     * @param t 
     */
    private Iterator<SingleUpdateInstruction> getWriteInstructions(Transaction t){
        return transactions.get(t).WS.iterator();
    }
    
    /**
     * Removes a transaction from the control data structures
     * @param t
     */
    private void removeTransaction(Transaction t){
        transactions.remove(t);
    }
    
    /**
     * Verifies if a transaction can commit, using an optimistic strategy
     * @param t
     */
    private boolean validateTransaction(Transaction t) {
        TransactionInfo currentTInfo = transactions.get(t);
        if (!hasAnyCommits){
            hasAnyCommits = true;
            return true;
        }
        for (TransactionInfo transactionInfo : transactions.values())
        {
            if (transactionInfo == currentTInfo) continue;
            if (transactionInfo.finish < currentTInfo.start) continue;
            for (SingleReadInstruction readInstruction : currentTInfo.RS)
            {
                if (transactionInfo.WS.stream().anyMatch(v -> v.getPk() == readInstruction.getPk())) return false;
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

    private class TransactionInfo {
        public int start = 0;
        public int finish = 0;
        public ArrayList<SingleReadInstruction> RS = new ArrayList<>();
        public ArrayList<SingleUpdateInstruction> WS = new ArrayList<>();

        public TransactionInfo(int start){
            this.start = start;
        }
    }
}


