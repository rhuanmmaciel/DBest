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

import java.util.*;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class PedroRossatoOptimisticConcurrencyManager extends ConcurrencyManager{


    int currentTime = 0;
    Map<Integer,Integer> starts = new HashMap<>();
    Map<Integer,Integer> finishs = new HashMap<>();
    Map<Integer, Set<SingleReadInstruction>> readSet = new HashMap<>();
    Map<Integer, Set<SingleUpdateInstruction>> writeSet = new HashMap<>();
    Map<SingleReadInstruction,SingleUpdateInstruction> associatedInstructions = new HashMap<>();
    /**
     *
     * @throws Exception
     */
    public PedroRossatoOptimisticConcurrencyManager() throws Exception {
    }
    

    
    /**
     * Process the current instrucion of a transaction
     *
     * @param transaction the transaction from which the current instruction is to be
     * processed
     * @return the record affected by the instruction. If the instruction was
     * not executed for some reason, the return is null
     * @throws Exception
     */
    @Override
    public List<DataRow> processInstruction(Transaction transaction) throws Exception {

        //increments the timestamp counter
        currentTime++;
        
        setTransactionStartTime(transaction);

        Instruction instruction = transaction.getCurrentInstruction();

        if (instruction instanceof SingleUpdateInstruction) {
            
            SingleUpdateInstruction writeInstruction = (SingleUpdateInstruction)instruction;
            
            //adds instruction into the write set
            addWriteInstruction(transaction, writeInstruction);
            
            //cannot return a record yet, since the record is only effectivelly updated after commit. 
            //returns a fake record instead
            BasicDataRow fakeRecord = new BasicDataRow();
            fakeRecord.setInt("id",writeInstruction.getPk());
            fakeRecord.setString("nome", writeInstruction.getContent());
            
            transaction.advanceInstruction();
            List<DataRow> list = new ArrayList();
            list.add(fakeRecord);
            return list;
        } else {//it is a read set.
            SingleReadInstruction readInstruction = (SingleReadInstruction)instruction;
            //checks if the transaction has updated the item it wants to read
            SingleUpdateInstruction writeInstruction = getWriteInstruction(readInstruction);
            if (writeInstruction != null) {
                //reads from the updated value.
                //cannot return a record yet, since the record is only effectivelly updated after commit. 
                //returns a fake record instead
                BasicDataRow fakeRecord = new BasicDataRow();
                fakeRecord.setInt("id",writeInstruction.getPk());
                fakeRecord.setString("nome", writeInstruction.getContent());
                transaction.advanceInstruction();
                List<DataRow> list = new ArrayList();
                list.add(fakeRecord);
                return list;
            } else {
                //reads from the database
                List<DataRow> abstractRecords = instruction.process();
                //adds instruction into the read set
                addReadInstruction(transaction, readInstruction);

                transaction.advanceInstruction();
                return abstractRecords;
                
            }

        }
    }

    
    
    /**
     * Commits the transaction.
     *
     * @param transaction the transaction to be committed
     * @return a flag indicating the status of the operation
     * @throws Exception
     */
    @Override
    public boolean commit(Transaction transaction) throws Exception {

        boolean validate = validateTransaction(transaction);
        if (!validate) {
            abort(transaction);
            return false;
        }
        
        setTransactionCommitTime(transaction);
        
        //effectivelly performs the write instructions of the commited transaction
        Iterator<SingleUpdateInstruction> it = getWriteInstructions(transaction);
        while (it.hasNext()) {
            Instruction instruction = it.next();
            instruction.process();
        }

        transaction.commit();
        return true;
    }


    /**
     * Marks the moment when the transaction started, based on the currentTime variable.
     * If it has already started, nothing is done
     *
     * @param transaction
     */
    private void setTransactionStartTime(Transaction transaction) {
        starts.computeIfAbsent(transaction.getId(), k -> currentTime);
    }

    /**
     * Marks the moment when the transaction commited, based on the currentTime variable.
     *
     * @param transaction
     */
    private void setTransactionCommitTime(Transaction transaction) {
        finishs.computeIfAbsent(transaction.getId(),k -> currentTime);
    }

    
    /**
     * Adds an instruction into the write instructions of a transaction (The Write Set).
     *
     * @param transaction
     */
    private void addWriteInstruction(Transaction transaction, SingleUpdateInstruction writeInstruction) {
        Set<SingleUpdateInstruction> transactionWriteSet = writeSet.get(transaction.getId());
        if(transactionWriteSet == null) transactionWriteSet = new HashSet<>();
        transactionWriteSet.add(writeInstruction);
        writeSet.put(transaction.getId(),transactionWriteSet);
    }

    /**
     * Adds an instruction into the read instructions of a transaction (The Read Set).
     *
     * @param transaction
     */
    private void addReadInstruction(Transaction transaction, SingleReadInstruction readInstruction) {
        Set<SingleReadInstruction> transactionReadSet = readSet.get(transaction.getId());
        if(transactionReadSet == null) transactionReadSet = new HashSet<>();
        transactionReadSet.add(readInstruction);
        readSet.put(transaction.getId(),transactionReadSet);
    }

    
    /**
     * Returns a write instruction associated with the same record of a readInstruction.
     * Returns null if it does not exist
     *
     * @param readInstruction
     */
    private SingleUpdateInstruction getWriteInstruction(Instruction readInstruction){
        return associatedInstructions.get((SingleReadInstruction) readInstruction);
    }
    
    /**
     * Gets the Write Instructions of a transaction (The Write Set).
     *
     * @param transaction
     */
    private Iterator<SingleUpdateInstruction>  getWriteInstructions(Transaction transaction){
        if(writeSet.get(transaction.getId()) != null)
            return writeSet.get(transaction.getId()).iterator();
        return new HashSet<SingleUpdateInstruction>().iterator();
    }
    
    /**
     * Removes a transaction from the control data structures
     * @param transaction
     * @throws Exception
     */
    private void removeTransaction(Transaction transaction){
        starts.remove(transaction.getId());
        finishs.remove(transaction.getId());
        readSet.remove(transaction.getId());
        writeSet.remove(transaction.getId());
    }
    
    /**
     * Verifies if a transaction can commit, using an optimistic strategy
     * @param transaction
     * @throws Exception
     */
    private boolean validateTransaction(Transaction transaction) {
        int tkStartTime = starts.get(transaction.getId());
        Set<SingleReadInstruction> tkReadSet = readSet.get(transaction.getId());
        boolean validTransaction = true;
        for (Integer tiId: starts.keySet()){
            if (tiId == transaction.getId()) continue;
            Set<SingleUpdateInstruction> tiWriteSet = writeSet.get(tiId);
            if(tiWriteSet == null) continue;
            Integer tiFinishTime = finishs.get(tiId);
            if(tiFinishTime != null && tkStartTime < tiFinishTime){
                for (SingleUpdateInstruction writeInstruction : tiWriteSet){
                    if(tkReadSet.stream().anyMatch(readInstruction -> readInstruction.getPk() == writeInstruction.getPk())){
                        validTransaction = false;
                    }
                }
            }
        }
        return validTransaction;
    }

    
    
    
    /**
     * Aborts a transaction
     * @param transaction
     * @throws Exception
     */
    @Override
    protected void abort(Transaction transaction) throws Exception {
        //System.out.println("Aborting "+t);
        removeTransaction(transaction);
        System.out.println(SimulatedIterations.getTab(transaction.getId() - 1) + transaction.getId() + " Abort");
        transaction.abort();
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
