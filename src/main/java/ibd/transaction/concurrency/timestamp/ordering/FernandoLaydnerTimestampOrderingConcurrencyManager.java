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
import java.util.Map;
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class FernandoLaydnerTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;
    Map<String, List<Integer>> ts_r = new HashMap<String, List<Integer>>();
    Map<String, List<Integer>> ts_w = new HashMap<String, List<Integer>>();
    Map<Integer, Integer> ts = new HashMap<Integer, Integer>();

    /**
     *
     * @throws Exception
     */
    public FernandoLaydnerTimestampOrderingConcurrencyManager() throws Exception {
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

        String item = getString((int) i.getPk());

        if (!ts_r.containsKey(item)){
            ts_r.put(item , new ArrayList<Integer>());
            ts_w.put(item , new ArrayList<Integer>());
        }

        if (i instanceof SingleUpdateInstruction) {

            // Aqui vai atualizar a lista de write
            List<Integer> temp = ts_w.get(item);
            if (!temp.contains(t.getId())){
                temp.add(t.getId());
            }
            for (Integer a: ts_r.get(item)){
                if(ts.get(a) <= tStart){
                    ts_w.put(item, temp);
                }
                else{
                    abort(t);
                    return null;
                }
            }

            // No write tem que atualizar o ignore se for necessário
            for (Integer a: ts_w.get(item)){
                if (ts.containsKey(t.getId())){
                    if(ts.get(a) > tStart){
                        ignore = true;
                    }
                }
            }
        } else if (i instanceof SingleReadInstruction) {
            // Aqui vai atualizar a lista de read
            List<Integer> temp = ts_r.get(item);
            if (!temp.contains(t.getId())){
                temp.add(t.getId());
            }
            for (Integer a: ts_w.get(item)){
                if(ts.get(a) <= tStart){
                    ts_r.put(item, temp);
                }
                else{
                    abort(t);
                    return null;
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
        if (ts.get(t.getId()) == null){
            ts.put(t.getId(), currentTime);
        }
        return ts.get(t.getId());
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
            return new ArrayList<DataRow>();
        }

        Instruction i = t.getCurrentInstruction();


        List<DataRow> recs = i.process();
        if (i.endProcessing()) {
            t.advanceInstruction();
        }

        return recs;
    }

    public static String getString(int i) {
        String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (i >= 0 && i <= 25) {
            return LETTERS.substring(i, i + 1);
        } else {
            return String.valueOf(i);
        }
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

        Integer aborted_ts =  t.getId();

        for (Map.Entry<String, List<Integer>> each : ts_r.entrySet()){
            for (int i = 0; i < each.getValue().size(); i++) {
                if (each.getValue().get(i) == aborted_ts){
                    each.getValue().remove(i);
                    ts_r.put(each.getKey(), each.getValue());
                }
            }
        }

        for (Map.Entry<String, List<Integer>> each : ts_w.entrySet()){
            for (int i = 0; i < each.getValue().size(); i++) {
                if (each.getValue().get(i) == aborted_ts){
                    each.getValue().remove(i);
                    ts_w.put(each.getKey(), each.getValue());
                }
            }
        }
        ts.remove(aborted_ts);
    }

}
