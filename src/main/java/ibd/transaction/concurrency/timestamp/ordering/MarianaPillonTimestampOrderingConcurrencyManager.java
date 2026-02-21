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
import java.util.Map;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class MarianaPillonTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;
    private Map<Integer, Integer> map = new HashMap<>();
    /**
     *
     * @throws Exception
     */
    public MarianaPillonTimestampOrderingConcurrencyManager() throws Exception {
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
        long q = i.getPk();

        if (i instanceof SingleUpdateInstruction) {
            if (tStart < q) {
                abort(t);
                return null;
            } else if (tStart > q) {
                // atualiza q
                ((SingleUpdateInstruction) i).setPk(tStart);
            }
        } else if (i instanceof SingleReadInstruction) {
            if (tStart < q) {
                abort(t);
                return null;
            } else {
                if (tStart > q) {
                    // atualiza q
                    ((SingleReadInstruction) i).setPk(tStart);
                }
            }
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
        if (map.containsKey(t.getId())) {
            return map.get(t.getId());
        } else {
            map.put(t.getId(), currentTime);
            return currentTime;
        }
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
        //System.out.println("Aborting "+t);
        map.remove(t.getId());
        super.abort(t);
    }

}