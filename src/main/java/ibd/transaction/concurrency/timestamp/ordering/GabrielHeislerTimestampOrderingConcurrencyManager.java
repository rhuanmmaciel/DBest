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
public class GabrielHeislerTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;
    HashMap<Transaction, Integer> transactionSTable = new HashMap<>();

    HashMap<Long, Stack<Transaction>> tsr = new HashMap<>();
    HashMap<Long, Stack<Transaction>> tsw = new HashMap<>();

    /**
     *
     * @throws Exception
     */
    public GabrielHeislerTimestampOrderingConcurrencyManager() throws Exception {
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
        long ipk = i.getPk();
        if (i instanceof SingleUpdateInstruction) {
            if(tStart < getStartTimeTSR(ipk)){//Se ts(T1)< ts_r(Q)
                abort(t); //T1 aborta
                return null;
            }else{ //Senao
                if(tStart > getStartTimeTSW(ipk)){ //Se ts(T1)> ts_w(Q)
                    tsw.get(ipk).push(t);// ts-w(Q) é atualizado
                    return processCurrentInstruction(t, ignore); //T1 executa
                }else{
                    ignore = true;
                }
            }

        } else if (i instanceof SingleReadInstruction) {
            if(tStart < getStartTimeTSW(ipk)){ //Se ts(T1)< ts-w(Q)
                abort(t); //T1 aborta
                return null;
            }else{ //Senao
                if(tStart > getStartTimeTSR(ipk)){ //Se ts(T1)> ts-r(Q)
                    tsr.get(ipk).push(t);//ts-r(Q) é atualizado
                }
            }
        }

        return processCurrentInstruction(t, ignore);
    }

    private Integer getStartTimeTSR(Long ipk){
        if(!tsr.containsKey(ipk)){
            tsr.put(ipk, new Stack<>());
        }else{
            if(!tsr.get(ipk).empty()) {
                return getTransactionStartTime(tsr.get(ipk).peek());
            }
        }
        return -1;
    }

    private Integer getStartTimeTSW(Long ipk){
        if(!tsw.containsKey(ipk)){
            tsw.put(ipk, new Stack<>());
        }else{
            if(!tsw.get(ipk).empty()) {
                return getTransactionStartTime(tsw.get(ipk).peek());
            }
        }
        return -1;
    }

    /**
     * Marks the moment when the transaction started, based on the currentTime
     * variable. If it has already started, nothing is done
     *
     * @param t
     */
    private Integer getTransactionStartTime(Transaction t) {
        if(!transactionSTable.containsKey(t)){
            transactionSTable.put(t, currentTime);
        }
        return transactionSTable.get(t);
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
        //Remover a transação t das 3 hash
        transactionSTable.remove(t);
        tsr.forEach((key, value) -> {
            tsr.get(key).remove(t);
        });
        tsw.forEach((key, value) -> {
            tsw.get(key).remove(t);
        });


    }

}