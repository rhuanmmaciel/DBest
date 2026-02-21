package ibd.transaction.concurrency.timestamp.ordering;

import ibd.transaction.Transaction;
import ibd.transaction.concurrency.ConcurrencyManager;
import ibd.transaction.instruction.Instruction;
import ibd.transaction.instruction.SingleReadInstruction;
import ibd.transaction.instruction.SingleUpdateInstruction;
import ibd.transaction.log.EmptyLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ibd.table.prototype.DataRow;

public class RobsonDanielTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    private int currentTime = 0;
    private Map<Transaction, Integer> transactionTimestamps = new HashMap<>();
    private Map<Integer, Integer> itemReadTimestamps = new HashMap<>();
    private Map<Integer, Integer> itemWriteTimestamps = new HashMap<>();

    /**
     * @throws Exception
     */
    public RobsonDanielTimestampOrderingConcurrencyManager() throws Exception {
        logger = new EmptyLogger();
    }

    @Override
    public List<DataRow> processInstruction(Transaction t) throws Exception {
        currentTime++;
        if (!t.isCommited()) {
            
            Integer tStart = getTransactionStartTime(t);
            boolean ignore = false;
            logTansactionStart(t);

            Instruction i = t.getCurrentInstruction();

            if (i instanceof SingleUpdateInstruction) {
                int item = i.getPk();

                if (transactionTimestamps.get(t) < itemReadTimestamps.getOrDefault(item, 0)) {
            
                    abort(t);
                    return null;
                }

                if (transactionTimestamps.get(t) > itemWriteTimestamps.getOrDefault(item, 0)) {
                    itemWriteTimestamps.put(item, transactionTimestamps.get(t));
                } else {
                    ignore = true;
                }
            } else if (i instanceof SingleReadInstruction) {
                int item = i.getPk();
                if (transactionTimestamps.get(t) < itemWriteTimestamps.getOrDefault(item, 0)) {
                    abort(t);
                    return null;
                }

                if (transactionTimestamps.get(t) > itemReadTimestamps.getOrDefault(item, 0)) {
                    itemReadTimestamps.put(item, transactionTimestamps.get(t));
                }
            }

            return processCurrentInstruction(t, ignore);
        }

        return null; // Retorna null se a transação já foi abortada
    }

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


    private Integer getTransactionStartTime(Transaction t) {
        return transactionTimestamps.computeIfAbsent(t, k -> currentTime);
    }

    @Override
    protected void abort(Transaction t) throws Exception {
        super.abort(t);

        // Limpa os dados de controle associados à transação que está abortando
        transactionTimestamps.remove(t);

        // Remove a transação da leitura e escrita de cada item
        for (Integer item : itemReadTimestamps.keySet()) {
            if (itemReadTimestamps.get(item).equals(transactionTimestamps.get(t))) {
                itemReadTimestamps.remove(item);
            }
        }

        for (Integer item : itemWriteTimestamps.keySet()) {
            if (itemWriteTimestamps.get(item).equals(transactionTimestamps.get(t))) {
                itemWriteTimestamps.remove(item);
            }
        }
    }
}
