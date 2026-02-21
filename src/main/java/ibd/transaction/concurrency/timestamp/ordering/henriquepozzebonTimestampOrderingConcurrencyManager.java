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
import java.util.Iterator;
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class henriquepozzebonTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;
    ArrayList<ArrayList> TS;
    
    ArrayList<ArrayList> ts_w;
    ArrayList<ArrayList> ts_r;
    

    /**
     *
     * @throws Exception
     */
    public henriquepozzebonTimestampOrderingConcurrencyManager() throws Exception {
        logger = new EmptyLogger();
        TS = new ArrayList<ArrayList>();


        ts_w = new ArrayList<ArrayList>();
        ts_r = new ArrayList<ArrayList>();

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
        if (i instanceof SingleReadInstruction) {
            for (Iterator<ArrayList> ts_wIterator = ts_w.iterator(); ts_wIterator.hasNext(); ) {
                List<Object> writeEntry = (List<Object>) ts_wIterator.next();

                if (i.getPk() == (int) writeEntry.get(0)) {
                    if (tStart < (int) writeEntry.get(1)) {
                        abort(t);
                        return null;
                    }
                    if (tStart > (int) writeEntry.get(1)) {
                        for (Iterator<ArrayList> ts_rIterator = ts_r.iterator(); ts_rIterator.hasNext(); ) {
                            List<Object> readEntry = (List<Object>) ts_rIterator.next();

                            if (i.getPk() == (int) readEntry.get(0)) {
                                if (tStart > (int) readEntry.get(1)) {
                                    readEntry.add(1, tStart);
                                    return processCurrentInstruction(t, ignore);
                                }
                            }
                        }
                    }
                }
            }

            ArrayList data = new ArrayList();
            if (ts_r.isEmpty()) {
                ArrayList<Object> newReaddata = new ArrayList<>();
                newReaddata.add((int) i.getPk());
                newReaddata.add((int) tStart);
                ts_r.add(0, newReaddata);
            }

            boolean flag = ts_r.stream()
                    .flatMap(entry -> entry.stream().skip(1))
                    .anyMatch(entry -> (int) entry == tStart);

            if (!flag) {
                ArrayList<Object> newReaddata = new ArrayList<>();
                newReaddata.add((int) i.getPk());
                newReaddata.add((int) tStart);
                ts_r.add(0, newReaddata);
            }

        }
        else if (i instanceof SingleUpdateInstruction) {
            Boolean appeared = false;
            for (Iterator<ArrayList> ts_rIterator = ts_r.iterator(); ts_rIterator.hasNext(); ) {
                List<Object> readEntry = (List<Object>) ts_rIterator.next();

                if (i.getPk() == (int) readEntry.get(0)) {
                    appeared = true;
                    if (tStart < (int) readEntry.get(1)) {
                        abort(t);
                        return null;
                    }
                    if (tStart > (int) readEntry.get(1)) {
                        Boolean helper = false;
                        for (Iterator<ArrayList> ts_wIterator = ts_w.iterator(); ts_wIterator.hasNext(); ) {
                            List<Object> writeEntry = (List<Object>) ts_wIterator.next();

                            if (i.getPk() == (int) writeEntry.get(0)) {
                                if (tStart > (int) writeEntry.get(1)) {
                                    writeEntry.add(1, tStart);
                                } else if (tStart < (int) writeEntry.get(1)) {
                                    ignore = true;
                                }
                                return processCurrentInstruction(t, ignore);
                            }
                        }
                    }
                }
            }
            ArrayList another_data = new ArrayList();
            if (!appeared) {
                ArrayList<Object> newReaddata = new ArrayList<>();
                newReaddata.add((int) i.getPk());
                newReaddata.add(-1);
                ts_r.add(0, newReaddata);
            }

            if (ts_w.isEmpty()) {
                ArrayList<Object> newWritedata = new ArrayList<>();
                newWritedata.add((int) i.getPk());
                newWritedata.add((int) tStart);
                ts_w.add(0, newWritedata);
            }

            boolean flag = ts_w.stream()
                    .flatMap(entry -> entry.stream().skip(1))
                    .anyMatch(entry -> (int) entry == tStart);

            if (!flag) {
                ArrayList<Object> newWritedata = new ArrayList<>();
                newWritedata.add((int) i.getPk());
                newWritedata.add((int) tStart);
                ts_w.add(0, newWritedata);
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
        ArrayList<Object> data = new ArrayList<>();

        for (Iterator<ArrayList> iterator = TS.iterator(); iterator.hasNext(); ) {
            ArrayList<Object> entry = iterator.next();
            if ((int) entry.get(0) == t.getId()) {
                return (int) entry.get(1);
            }
        }

        data.add(t.getId());
        data.add(currentTime);
        TS.add(data);

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
        int time = getTransactionStartTime(t);
        // Remove elementos de 'ts_w'
        Iterator<ArrayList> ts_wIterator = ts_w.iterator();
        while (ts_wIterator.hasNext()) {
            List<Object> write = ts_wIterator.next();
            for (int j = 0; j < write.size(); j++) {
                if (time == (int) write.get(j) && j != 0) {
                    if (write.size() == 2) {
                        ts_wIterator.remove();
                        break;
                    } else {
                        write.remove(j);
                    }
                }
            }
        }

// Remove elementos de 'ts_r'
        Iterator<ArrayList> ts_rIterator = ts_r.iterator();
        while (ts_rIterator.hasNext()) {
            List<Object> read = ts_rIterator.next();
            for (int j = 0; j < read.size(); j++) {
                if (time == (int) read.get(j) && j != 0) {
                    if (read.size() == 2) {
                        ts_rIterator.remove();
                        break;
                    } else {
                        read.remove(j);
                    }
                }
            }
        }

// Remove elementos de 'TS'
        Iterator<ArrayList> transactionTSIterator = TS.iterator();
        while (transactionTSIterator.hasNext()) {
            List<Object> transaction = transactionTSIterator.next();
            if ((int) (transaction.get(0)) == t.getId()) {
                transactionTSIterator.remove();
            }
        }
        super.abort(t);

    }

}
