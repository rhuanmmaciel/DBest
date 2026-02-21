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
public class EnzoTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;
    Hashtable<Integer, Integer> timestamps = new Hashtable<Integer, Integer>();
    Hashtable<Integer, List<Integer>> readAccesses = new Hashtable<Integer, List<Integer>>();
    Hashtable<Integer, List<Integer>> writeAccesses = new Hashtable<Integer, List<Integer>>();
    /**
     *
     * @throws Exception
     */
    public EnzoTimestampOrderingConcurrencyManager() throws Exception {
        logger = new EmptyLogger();
    }

    /**
     * Tries to process the current instruction of a transaction
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
        //puts the transaction in the recovery log if It's not already there
        logTansactionStart(t);

        Instruction i = t.getCurrentInstruction();
        int pk = i.getPk();
        if (i instanceof SingleUpdateInstruction) {
            if (tStart >= getMostRecentRead(pk)) {
                if (tStart >= getMostRecentWrite(pk)) {
                    writeAccesses.computeIfAbsent(pk, k -> new LinkedList<>()).add(0,t.getId());
                } else {
                    ignore = true;
                }
            } else {
                abort(t);
                return null;
            }
        } else if (i instanceof SingleReadInstruction) {
            if (tStart >= getMostRecentWrite(pk)) {
                if (tStart >= getMostRecentRead(pk)) {
                    readAccesses.computeIfAbsent(pk, k -> new LinkedList<>()).add(0, t.getId());
                }
            } else {
                abort(t);
                return null;
            }
        }

        return processCurrentInstruction(t, ignore);
    }
    private Integer getMostRecentRead(int pk) {
        return (readAccesses.containsKey(pk) && !readAccesses.get(pk).isEmpty()) ?
                readAccesses.get(pk).get(0) : -1;
    }

    private Integer getMostRecentWrite(int pk) {
        return (writeAccesses.containsKey(pk) && !writeAccesses.get(pk).isEmpty()) ?
                writeAccesses.get(pk).get(0) : -1;
    }

    /**
     * Marks the moment when the transaction started, based on the currentTime
     * variable. If it has already started, nothing is done
     *
     * @param t
     */
    private Integer getTransactionStartTime(Transaction t) {
        timestamps.putIfAbsent(t.getId(), currentTime);
        return timestamps.get(t.getId());
    }

    /**
     * Effectively processes the current instruction of a transaction.
     *
     * @param t
     * @return
     * @throws Exception
     */
    protected List<DataRow> processCurrentInstruction(Transaction t, boolean ignore) throws Exception {

        if (ignore) {
            t.advanceInstruction();
            return new ArrayList<>();
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
        timestamps.remove(t.getId());
        for (List<Integer> ls : readAccesses.values()) {
            ls.removeIf(e -> e.equals(t.getId()));
        }
        for (List<Integer> ls : writeAccesses.values()) {
            ls.removeIf(e -> e.equals(t.getId()));
        }
        super.abort(t);
    }

}