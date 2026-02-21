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

import java.util.*;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class JosueTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;

    Map<Transaction, Integer> TransactionsTimestamps = new HashMap<>();

    Map<Long, ArrayList<Transaction>> WriteControlStructure = new HashMap<>();
    Map<Long, ArrayList<Transaction>> ReadControlStructure = new HashMap<>();

    /**
     *
     * @throws Exception
     */
    public JosueTimestampOrderingConcurrencyManager() throws Exception {
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

        //puts the transaction in the recovery log if it's not already there
        logTansactionStart(t);

        Integer transactionTimestamp = getTransactionStartTime(t);

        Instruction i = t.getCurrentInstruction();
        long item = i.getPk();

        if (i instanceof SingleUpdateInstruction) {
            int latestReadTimestamp = findLastTransactionTs(getReadTs(item));
            int latestWriteTimestamp = findLastTransactionTs(getWriteTs(item));

            if(transactionTimestamp < latestReadTimestamp){
                abort(t);
                return null;
            } else if (transactionTimestamp > latestWriteTimestamp){
                WriteControlStructure.get(item).add(t);
            } else {
                ignore = true;
            }

        } else if (i instanceof SingleReadInstruction) {
            int latestWriteTimestamp = findLastTransactionTs(getWriteTs(item));
            int latestReadTimestamp = findLastTransactionTs(getReadTs(item));

            if(transactionTimestamp < latestWriteTimestamp){
                abort(t);
                return null;
            } else if (transactionTimestamp > latestReadTimestamp){
                ReadControlStructure.get(item).add(t);
            }
        }

        return processCurrentInstruction(t, ignore);
    }

    private ArrayList<Transaction> getWriteTs(long item) {
        if(!WriteControlStructure.containsKey(item)){
            WriteControlStructure.put(item, new ArrayList<>());
        }

        return WriteControlStructure.get(item);
    }

    private ArrayList<Transaction> getReadTs(long item) {
        if(!ReadControlStructure.containsKey(item)){
            ReadControlStructure.put(item, new ArrayList<>());
        }

        return ReadControlStructure.get(item);
    }

    /**
     * Look for a given transaction in a given Array
     * returning the start time if found.
    */
    private int findLastTransactionTs(ArrayList<Transaction> transactionsControl) {
        if(transactionsControl.isEmpty()){
            return -1;
        }

        int index = transactionsControl.size() - 1;
        return getTransactionStartTime(transactionsControl.get(index));
    }

    /**
     * Marks the moment when the transaction started, based on the currentTime
     * variable. If it has already started, nothing is done
     *
     * @param t
     */
    private Integer getTransactionStartTime(Transaction t) {
        //If transaction registered
        if(TransactionsTimestamps.containsKey(t)){
            //Returns its start time
            return TransactionsTimestamps.get(t);
        }

        //Else if transaction no registered, then register
        TransactionsTimestamps.put(t, currentTime);
        return currentTime;
    }

    /**
     * Effectivelly processes the current instrucion of a transaction.
     *
     * @param t
     * @return
     * @throws Exception
     */
    protected List<DataRow> processCurrentInstruction(Transaction t, boolean ignore) throws Exception {

        if (ignore) {
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
        super.abort(t);

        TransactionsTimestamps.remove(t);

        ReadControlStructure.forEach((key, value) -> {
            ReadControlStructure.get(key).remove(t);
        });

        WriteControlStructure.forEach((key, value) -> {
            WriteControlStructure.get(key).remove(t);
        });
    }
}