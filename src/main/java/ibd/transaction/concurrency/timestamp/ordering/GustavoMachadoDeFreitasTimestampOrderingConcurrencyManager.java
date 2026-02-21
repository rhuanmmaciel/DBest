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
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class GustavoMachadoDeFreitasTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    private Hashtable<Transaction, Integer> transactionController = new Hashtable<>();
    private Hashtable<Long, ItemInfo> itemController = new Hashtable<>();

    private Integer currentTime = 0;

    /**
     *
     * @throws Exception
     */
    public GustavoMachadoDeFreitasTimestampOrderingConcurrencyManager() throws Exception {
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

        // Pk acessado pela instrução atual
        long item = i.getPk();

        // Se o item ainda não esta sendo trackeado, adiciona-o
        if (!itemController.containsKey(item)) {
            ItemInfo itemInfo = new ItemInfo();
            itemController.put(item, itemInfo);
        }

        // Verifica se é um update
        if (i instanceof SingleUpdateInstruction) {
            // Uma transação mais nova já leu o valor do item.
            if (tStart < tsReader(item)) {
                abort(t);
                return null;
            }
            // É mais recente que a última transação a escrever no item.
            else if (tStart > tsWriter(item))
            {
                itemController.get(item).tsw.push(t);
            }
            // Se não for, seu valor não é relevante.
            else
            {
                ignore = true;
            }
        } else if (i instanceof SingleReadInstruction) {
            // Está tentando ler um valor de uma transação mais recente.
            if (tStart < tsWriter(item)) {
                abort(t);
                return null;
            }
            // É mais recente que a última transação a ler do item.
            else if (tStart > tsReader(item))
            {
                itemController.get(item).tsr.push(t);
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
        if (!transactionController.containsKey(t))
        {
            transactionController.put(t, currentTime);
            return currentTime;
        }

        return transactionController.get(t);
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

        transactionController.remove(t);

        for (Long item : itemController.keySet())
        {
            ItemInfo itemInfo = itemController.get(item);
            itemInfo.tsw.remove(t);
            itemInfo.tsr.remove(t);
        }
    }

    private long tsReader(long item)
    {
        if (itemController.get(item).tsr.empty())
        {
            return -1;
        }

        Transaction lastReader = itemController.get(item).tsr.peek();
        return getTransactionStartTime(lastReader);
    }

    private long tsWriter(long item)
    {
        if (itemController.get(item).tsw.empty())
        {
            return -1;
        }

        Transaction lastWriter = itemController.get(item).tsw.peek();
        return getTransactionStartTime(lastWriter);
    }

    private class ItemInfo
    {
        Stack<Transaction> tsr = new Stack<>();
        Stack<Transaction> tsw = new Stack<>();
    }
}