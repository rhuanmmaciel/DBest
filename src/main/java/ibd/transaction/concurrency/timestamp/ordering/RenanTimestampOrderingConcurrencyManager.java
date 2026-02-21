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
public class RenanTimestampOrderingConcurrencyManager extends ConcurrencyManager {
    private class Timestamp{
        private Integer readTimestamp;
        private Integer writeTimestamp;

        public Timestamp(Integer readTimestamp, Integer writeTimestamp) {
            this.readTimestamp = readTimestamp;
            this.writeTimestamp = writeTimestamp;
        }

        public Integer getReadTimestamp() {
            return readTimestamp;
        }

        public void setReadTimestamp(Integer readTimestamp) {
            this.readTimestamp = readTimestamp;
        }

        public Integer getWriteTimestamp() {
            return writeTimestamp;
        }

        public void setWriteTimestamp(Integer writeTimestamp) {
            this.writeTimestamp = writeTimestamp;
        }
    }

    int currentTime = 0;
    private Map<Transaction, Integer> transactionTimestamps = new HashMap<>();
    private Map<Long, List<Transaction>> itemTransactions = new HashMap<>();
    private Map<Long, Timestamp> itemTimestamps = new HashMap<>();

    /**
     *
     * @throws Exception
     */
    public RenanTimestampOrderingConcurrencyManager() throws Exception {
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
    //DICAS:
    //As regras exigem que informações referentes às
    //transações sejam mantidas em estruturas de controle
    //• Timestamp de cada transação
    //• Lista de transações que acessaram cada item

    //• Investigue quais estruturas de dados do Java mais se
    //adequam para resolver o problema proposto

    @Override
    public List<DataRow> processInstruction(Transaction t) throws Exception {
        //A validação das regras também poderá fazer com que a instrução seja aceita, mas não processada
        //nesse caso, o ignore será true
        boolean ignore = false;

        //increments the timestamp counter
        currentTime++;

        Integer tStart = getTransactionStartTime(t);


        //puts the transaction in the recovery log if its not already there
        logTansactionStart(t);

        Instruction i = t.getCurrentInstruction();


        long instructionPk = i.getPk();
        if (i instanceof SingleUpdateInstruction) {
            if (tStart < getLatestReadTimestamp(instructionPk)) {
                abort(t);
                return null;
            } else{
                if (tStart > getLatestWriteTimestamp(instructionPk)) {
                    List<Transaction> itemAccessTransactions = itemTransactions.get(instructionPk);
                    if (itemAccessTransactions == null) {
                        itemAccessTransactions = new ArrayList<>();
                    }

                    itemAccessTransactions.add(t);
                    itemTransactions.put(instructionPk, itemAccessTransactions);

                    Timestamp ts = itemTimestamps.get(instructionPk);
                    if (ts == null) {
                        ts = new Timestamp(0, tStart);
                        itemTimestamps.put(instructionPk, ts);
                    } else {
                        ts.setWriteTimestamp(tStart);
                    }
                }
            }
        } else if (i instanceof SingleReadInstruction) {
            if (tStart < getLatestWriteTimestamp(instructionPk)) {
                abort(t);
                return null;
            } else{
                List<Transaction> itemAccessTransactions = itemTransactions.get(instructionPk);
                if (itemAccessTransactions == null) {
                    itemAccessTransactions = new ArrayList<>();
                }

                itemAccessTransactions.add(t);
                itemTransactions.put(instructionPk, itemAccessTransactions);

                if (tStart > getLatestReadTimestamp(instructionPk)) {
                    Timestamp ts = itemTimestamps.get(instructionPk);
                    if (ts == null) {
                        ts = new Timestamp(tStart, 0);
                        itemTimestamps.put(instructionPk, ts);
                    } else {
                        ts.setReadTimestamp(tStart);
                    }
                }
            }
            transactionTimestamps.put(t, tStart);
        }

        return processCurrentInstruction(t, ignore);
    }

    private Integer getLatestReadTimestamp(Long instructionPk) {
        List<Transaction> itemAccessTransactions = itemTransactions.get(instructionPk);
        Integer latestTimestamp = 0;
        if (itemAccessTransactions == null || itemAccessTransactions.isEmpty()) {
            return latestTimestamp;
        }
        Timestamp ts_r = itemTimestamps.get(instructionPk);
        if (ts_r != null) {
            latestTimestamp = ts_r.getReadTimestamp();
        }
        return latestTimestamp;
    }

    private Integer getLatestWriteTimestamp(Long instructionPk) {
        List<Transaction> itemAccessTransactions = itemTransactions.get(instructionPk);
        Integer latestTimestamp = 0;
        if (itemAccessTransactions == null || itemAccessTransactions.isEmpty()) {
            return latestTimestamp;
        }
        Timestamp ts_w = itemTimestamps.get(instructionPk);
        if (ts_w != null) {
            latestTimestamp = ts_w.getWriteTimestamp();
        }
        return latestTimestamp;
    }

    /**
     * Marks the moment when the transaction started, based on the currentTime
     * variable. If it has already started, nothing is done
     *
     * @param t
     */
    private Integer getTransactionStartTime(Transaction t) {
        Integer timestamp = transactionTimestamps.get(t);

        if (timestamp == null) {
            timestamp = currentTime;
            transactionTimestamps.put(t, timestamp);
        }

        return timestamp;
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
        // Esta funcao exclui a transacao da lista de transacoes que acessaram o item
        transactionTimestamps.remove(t);
        super.abort(t);
    }

}