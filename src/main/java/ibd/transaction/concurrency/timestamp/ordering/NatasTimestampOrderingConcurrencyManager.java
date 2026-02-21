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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class NatasTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;
    HashMap<String, ArrayList<String>> tsrd = new HashMap<>();
    HashMap<String, ArrayList<String>> tswr = new HashMap<>();
    HashMap<String, Integer> ts = new HashMap<>();

    private char letter(long a) {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt((int)a);
    }

    /**
     *
     * @throws Exception
     */
    public NatasTimestampOrderingConcurrencyManager() throws Exception {
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
        // System.out.println(tStart +" "+ t.toString());

        //puts the transaction in the recovery log if its not already there
        logTansactionStart(t);

        Instruction i = t.getCurrentInstruction();

        String tname = t.toString();
        String l = "" + letter(i.getPk());

        if (tswr.get(l) == null) tswr.put(l, new ArrayList<String>());
        if (tsrd.get(l) == null) tsrd.put(l, new ArrayList<String>());
        
        ArrayList<String> v, vwr;
        if (i instanceof SingleUpdateInstruction) {
            v = tsrd.get(l);
            vwr = tswr.get(l);
            ignore = vwr != null && vwr.size() > 0 && ts.get(vwr.get(vwr.size()-1)) > tStart;

            if (v != null && v.size() > 0 && tStart < ts.get(v.get(v.size()-1))) {
                abort(t);
                return null;
            }
            if (!ignore) tswr.get(l).add(tname);
        } else if (i instanceof SingleReadInstruction) {
            v = tswr.get(l);
            
            if (v != null && v.size() > 0 && tStart < ts.get(v.get(v.size()-1))) {
                abort(t);
                return null;
            }
            tsrd.get(l).add(tname);
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
        Integer value = ts.get(t.toString());
        if (value == null) {
            ts.put(t.toString(), currentTime);
            return currentTime;
        } else {
            return value;
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
        // System.out.println("Aborting "+t.toString());
        super.abort(t);
        
        Iterator<Map.Entry<String, ArrayList<String>>> iterator_rd = tsrd.entrySet().iterator();
        while (iterator_rd.hasNext()) {
            ArrayList<String> v = iterator_rd.next().getValue();
            if (v.size() > 0 && v.get(v.size()-1).equals(t.toString())) iterator_rd.remove();
        }

        Iterator<Map.Entry<String, ArrayList<String>>> iterator_wr = tswr.entrySet().iterator();
        while (iterator_wr.hasNext()) {
            ArrayList<String> v = iterator_wr.next().getValue();
            if (v.size() > 0 && v.get(v.size()-1).equals(t.toString())) iterator_wr.remove();
        }

        ts.remove(t.toString());
    }

}
