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
import java.util.HashMap;
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author João Fritsch
 */
public class JoaoFritschTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;

    HashMap<Integer, Integer> transactionTimes;
    HashMap<String, HashMap<String, ArrayList<Transaction>>> transactionControl;

    /**
     *
     * @throws Exception
     */
    public JoaoFritschTimestampOrderingConcurrencyManager() throws Exception {
        logger = new EmptyLogger();

        transactionTimes = new HashMap<>();
        transactionControl = new HashMap<>();
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
        Transaction lastTransaction;

        if (i instanceof SingleUpdateInstruction) {
            lastTransaction = getLastItemTransaction(i, "READ");

            if(lastTransaction == null || transactionTimes.get(lastTransaction.getId()) < tStart || lastTransaction.getId() == t.getId()) {
                if(needToUpdateItemTransactions(i, "WRITE", t, tStart)) {
                    updateItemTransactions(i, "WRITE", t);
                } else {
                    ignore = true;
                }
            } else {
                abort(t);
                return null;
            }

        } else if (i instanceof SingleReadInstruction) {
            lastTransaction = getLastItemTransaction(i, "WRITE");
        
            if(lastTransaction == null || transactionTimes.get(lastTransaction.getId()) < tStart || lastTransaction.getId() == t.getId()) {
                if(needToUpdateItemTransactions(i, "READ", t, tStart)) {
                    updateItemTransactions(i, "READ", t);
                }
            } else {
                abort(t);
                return null;
            }
        }

        return processCurrentInstruction(t, ignore);
    }

    private Boolean needToUpdateItemTransactions(Instruction i, String operation, Transaction t, Integer currentTime) {
        Transaction lastTransaction = getLastItemTransaction(i, operation);
        Integer lastTransactionTime = lastTransaction == null ? -1 : transactionTimes.get(lastTransaction.getId());

        return (currentTime > lastTransactionTime);
    }

    private void updateItemTransactions(Instruction i, String operation, Transaction t) {
        ArrayList<Transaction> transactions = transactionControl.get(i.getUniqueKey()).get(operation);
        transactions.add(t);
    }

    private Transaction getLastItemTransaction(Instruction i, String operation) {
        if(!transactionControl.containsKey(i.getUniqueKey())) {
            transactionControl.put(i.getUniqueKey(), new HashMap<>());
            transactionControl.get(i.getUniqueKey()).put("READ", new ArrayList<>());            
            transactionControl.get(i.getUniqueKey()).put("WRITE", new ArrayList<>());
        }

        ArrayList<Transaction> transactions = transactionControl.get(i.getUniqueKey()).get(operation);

        return transactions.isEmpty() ? null : transactions.get(transactions.size() - 1);
    }

    /**
     * Marks the moment when the transaction started, based on the currentTime
     * variable. If it has already started, nothing is done
     *
     * @param t
     */
    private Integer getTransactionStartTime(Transaction t) {
        if(!transactionTimes.containsKey(t.getId()) || transactionTimes.get(t.getId()) == -1) {
            transactionTimes.put(t.getId(), currentTime);
        }

        return transactionTimes.get(t.getId());
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

        if(transactionTimes.containsKey(t.getId())) {
            transactionTimes.put(t.getId(), -1);
        }

        for(HashMap<String, ArrayList<Transaction>> items : transactionControl.values()) {
            for(ArrayList<Transaction> transactions : items.values()) {
                ArrayList<Transaction> transactionsToRemove = new ArrayList<Transaction>();

                for(Transaction transaction : transactions) {
                    if(transaction.getId() == t.getId()) {
                        transactionsToRemove.add(transaction);
                    }
                }

                for(Transaction transactionToRemove : transactionsToRemove) {
                    transactions.remove(transactionToRemove);
                }
            }
        }
    }

}