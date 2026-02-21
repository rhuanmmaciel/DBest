package ibd.transaction.concurrency.timestamp.ordering;

import ibd.transaction.log.EmptyLogger;
import ibd.transaction.instruction.Instruction;
import ibd.transaction.Transaction;
import ibd.transaction.concurrency.ConcurrencyManager;
import ibd.transaction.instruction.SingleReadInstruction;
import ibd.transaction.instruction.SingleUpdateInstruction;

import java.security.InvalidParameterException;
import java.util.*;
import ibd.table.prototype.DataRow;

/**
 * @author pccli
 */
public class MatheusDelazeriTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;

    /**
     * O uso de 2 estruturas permite uma versão rápida
     * para solucionar tanto o abort quanto as validações.
     * O 'problema' é que o timestamp é salvo 2x
     * <p>
     * Example: { Transaction => timestamp }
     */
    public HashMap<Transaction, Integer> tManager = new HashMap<>();
    /**
     * Example: { instructionKey => timestamp }
     */
    public HashMap<String, Integer> iManager = new HashMap<>();

    /**
     * @throws Exception
     */
    public MatheusDelazeriTimestampOrderingConcurrencyManager() throws Exception {
        logger = new EmptyLogger();
    }

    /**
     * Tries to process the current instrucion of a transaction
     *
     * @param t the transaction from which the current instruction is to be
     *          processed
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
        String iKey = getInstructionKey(i);
        String vKey;

        if (i instanceof SingleUpdateInstruction) {
            vKey = getInstructionKey(i, Instruction.READ);
        } else if (i instanceof SingleReadInstruction) {
            vKey = getInstructionKey(i, Instruction.UPDATE);
        } else {
            throw new InvalidParameterException("Current instruction type not allowed!");
        }

        if (iManager.containsKey(vKey) && tStart < iManager.get(vKey)) {
            abort(t);
            return null;
        }

        if (!iManager.containsKey(iKey) || iManager.get(iKey) < tStart) {
            iManager.put(iKey, tStart);
        }

        return processCurrentInstruction(t, ignore);
    }

    private String getInstructionKey(Instruction i, Integer mode) {
        return i.getUniqueKey() + "-" + mode;
    }

    private String getInstructionKey(Instruction i) {
        return getInstructionKey(i, i.getMode());
    }

    /**
     * Marks the moment when the transaction started, based on the currentTime
     * variable. If it has already started, nothing is done
     *
     * @param t
     */
    private Integer getTransactionStartTime(Transaction t) {
        if (!tManager.containsKey(t)) {
            tManager.put(t, currentTime);
        }

        return tManager.get(t);
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
        super.abort(t);
        for (Instruction i: t.getInstrutions()) {
            String iKey = getInstructionKey(i);
            /** Evita remover instruções de outras transactions */
            if (Objects.equals(iManager.get(iKey), tManager.get(t))) {
                iManager.remove(getInstructionKey(i));
            }
        }

        tManager.remove(t);
    }

}
