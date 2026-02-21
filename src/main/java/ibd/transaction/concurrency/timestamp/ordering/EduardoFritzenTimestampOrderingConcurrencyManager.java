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
import java.util.List;
import java.util.Iterator;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class EduardoFritzenTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;
    ArrayList<ArrayList> transactionTimestamps; 
    ArrayList<ArrayList> writeTimestamps;
    ArrayList<ArrayList> readTimestamps;

    /**
     *
     * @throws Exception
     */
    public EduardoFritzenTimestampOrderingConcurrencyManager() throws Exception {
        logger = new EmptyLogger();
        transactionTimestamps = new ArrayList<ArrayList>();
        writeTimestamps = new ArrayList<ArrayList>();
        readTimestamps = new ArrayList<ArrayList>();
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

        Integer transactionTimestampstart = getTransactionStartTime(t);

        //putransactionTimestamps the transaction in the recovery log if itransactionTimestamps not already there
        logTansactionStart(t);

        Instruction i = t.getCurrentInstruction();

        if (i instanceof SingleUpdateInstruction) {
            for (List<Object> writeEntry : writeTimestamps) {
                if (i.getPk() == (int) writeEntry.get(0)) {
                    if (transactionTimestampstart < (int) writeEntry.get(1)) {
                        abort(t);
                        return null;
                    }
                    if (transactionTimestampstart > (int) writeEntry.get(1)) {
                        for (List<Object> readEntry : readTimestamps) {
                            if (i.getPk() == (int) readEntry.get(0)) {
                                if (transactionTimestampstart > (int) readEntry.get(1)) {
                                    readEntry.add(1, transactionTimestampstart);
                                    return processCurrentInstruction(t, ignore);
                                }
                            }
                        }
                    }
                }
            }

            if (readTimestamps.isEmpty() || !readTimestamps.stream().anyMatch(entry -> (int) entry.get(0) == (int) i.getPk() && (int) entry.get(1) == (int) transactionTimestampstart)) {
                ArrayList<Object> newReadData = new ArrayList<>();
                newReadData.add((int) i.getPk());
                newReadData.add((int) transactionTimestampstart);
                readTimestamps.add(0, newReadData);
            }
    
        } 
        else if (i instanceof SingleReadInstruction) {
            Boolean wasPresent = false;
            for (List<Object> readEntry : readTimestamps) {
                if (i.getPk() == (int) readEntry.get(0)) {
                    wasPresent = true;
                    if (transactionTimestampstart < (int) readEntry.get(1)) {
                        abort(t);
                        return null;
                    }
                    if (transactionTimestampstart > (int) readEntry.get(1)) {
                        Boolean isHelper = false;
                        for (List<Object> writeEntry : writeTimestamps) {
                            if (i.getPk() == (int) writeEntry.get(0)) {
                                if (transactionTimestampstart > (int) writeEntry.get(1)) {
                                    writeEntry.add(1, transactionTimestampstart);
                                } else if (transactionTimestampstart < (int) writeEntry.get(1)) {
                                    ignore = true;
                                }
                                return processCurrentInstruction(t, ignore);
                            }
                        }
                    }
                }
            }
            
            if (!wasPresent) {
                ArrayList<Object> newReadData = new ArrayList<>();
                newReadData.add((int) i.getPk());
                newReadData.add(-1);
                readTimestamps.add(0, newReadData);
            }
            
            if (writeTimestamps.isEmpty()) {
                ArrayList<Object> newWriteData = new ArrayList<>();
                newWriteData.add((int) i.getPk());
                newWriteData.add((int) transactionTimestampstart);
                writeTimestamps.add(0, newWriteData);
            }
            
            boolean flag = writeTimestamps.stream()
                    .flatMap(entry -> entry.stream().skip(1))
                    .anyMatch(entry -> (int) entry == transactionTimestampstart);
            
            if (!flag) {
                ArrayList<Object> newWriteData = new ArrayList<>();
                newWriteData.add((int) i.getPk());
                newWriteData.add((int) transactionTimestampstart);
                writeTimestamps.add(0, newWriteData);
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

        for (ArrayList<Object> entry : transactionTimestamps) {
            if ((int) entry.get(0) == t.getId()) {
                return (int) entry.get(1);
            }
        }
    
        // Se não encontrarmos um timestamp para a transação, criamos um novo
        ArrayList<Object> newTransactionEntry = new ArrayList<>();
        newTransactionEntry.add(t.getId());
        newTransactionEntry.add(currentTime);
        transactionTimestamps.add(newTransactionEntry);
    
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
        int transactionStartTime = getTransactionStartTime(t);

        // Remove elementos de 'ts_w' relacionados à transação abortada
        removeTransactionEntries(writeTimestamps, transactionStartTime);
    
        // Remove elementos de 'ts_r' relacionados à transação abortada
        removeTransactionEntries(readTimestamps, transactionStartTime);
    
        // Remove a entrada da transação na lista de timestamps
        removeTransactionEntry(transactionTimestamps, t.getId());

        super.abort(t);
    }

    // Método para remover entradas relacionadas à transação abortada
    private void removeTransactionEntries(ArrayList<ArrayList> timestamps, int transactionStartTime) {
        Iterator<ArrayList> iterator = timestamps.iterator();
        while (iterator.hasNext()) {
            List<Object> entry = iterator.next();
            if ((int) entry.get(1) == transactionStartTime) {
                iterator.remove();
                break; // Remover apenas uma entrada associada à transação
            }
        }
    }

    // Método para remover entrada de transação na lista de timestamps
    private void removeTransactionEntry(ArrayList<ArrayList> timestamps, int transactionId) {
        Iterator<ArrayList> iterator = timestamps.iterator();
        while (iterator.hasNext()) {
            List<Object> entry = iterator.next();
            if ((int) entry.get(0) == transactionId) {
                iterator.remove();
                break; // Remover apenas a entrada da transação
            }
        }
    }
}