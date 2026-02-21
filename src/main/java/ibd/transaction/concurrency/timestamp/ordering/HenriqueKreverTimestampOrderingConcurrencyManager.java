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
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class HenriqueKreverTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;
    ArrayList<ArrayList<Integer>> reads;
    ArrayList<ArrayList<Integer>> writes;
    ArrayList<ArrayList<Integer>> transaction_TS;

    /**
     *
     * @throws Exception
     */
    public HenriqueKreverTimestampOrderingConcurrencyManager() throws Exception {
        logger = new EmptyLogger();
        transaction_TS = new ArrayList<ArrayList<Integer>>();
        reads = new ArrayList<ArrayList<Integer>>();
        writes = new ArrayList<ArrayList<Integer>>();

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
            for (int j = 0; j < writes.size(); j++)
            {
                if (i.getPk()==(int)writes.get(j).get(0))
                {
                    if(tStart<writes.get(j).get(1))
                    {
                        abort(t);
                        return null;
                    }
                    if(tStart>writes.get(j).get(1))
                    {
                        for (int k = 0; k < reads.size(); k++) {
                            if (i.getPk()==reads.get(k).get(0))
                            {
                                if (tStart>reads.get(k).get(1))
                                {
                                    reads.get(k).add(1,tStart);
                                    return processCurrentInstruction(t, ignore);
                                }
                            }
                        }
                    }
                }
            }

            ArrayList tuple = new ArrayList();
            if (reads.size() == 0)
            {
                tuple.add((int)i.getPk());
                tuple.add((int)tStart);
                reads.add(0,tuple);
            }
            Boolean flag=false;
            for (int j = 0; j < reads.size(); j++) {
                for (int k = 1; k < reads.get(j).size(); k++) {
                    if ((int)reads.get(j).get(k)==tStart)
                    {
                        flag=true;
                    }
                }

            }
            if (!flag)
            {
                tuple.add((int)i.getPk());
                tuple.add((int)tStart);
                reads.add(0,tuple);
            }
        }
        else if (i instanceof SingleUpdateInstruction) {
            Boolean appeared = false;
            for (int j = 0; j < reads.size(); j++)
            {
                if (i.getPk()==(int)reads.get(j).get(0))
                {
                    appeared=true;
                    if(tStart<reads.get(j).get(1))
                    {
                        abort(t);
                        return null;
                    }
                    if(tStart>reads.get(j).get(1))
                    {
                        Boolean helper = false;
                        for (int k = 0; k < writes.size(); k++) {
                            if (i.getPk()==writes.get(k).get(0))
                            {
                                if (tStart>writes.get(k).get(1))
                                {
                                    writes.get(k).add(1,tStart);
                                }
                                else if(tStart<writes.get(k).get(1))
                                {
                                    ignore=true;
                                }
                                return processCurrentInstruction(t, ignore);

                            }
                        }
                    }
                }
            }
            ArrayList another_tuple = new ArrayList();
            if(!appeared)
            {
                another_tuple.add((int)i.getPk());
                another_tuple.add(-1);
                reads.add(0,another_tuple);
            }
            ArrayList tuple = new ArrayList();
            if (writes.size() == 0)
            {
                tuple.add((int)i.getPk());
                tuple.add((int)tStart);
                writes.add(0,tuple);
            }
            Boolean flag=false;
            for (int j = 0; j < writes.size(); j++) {
                for (int k = 1; k < writes.get(j).size(); k++) {
                    if ((int)writes.get(j).get(k)==tStart)
                    {
                        flag=true;
                    }
                }

            }
            if (!flag)
            {
                tuple.add((int)i.getPk());
                tuple.add((int)tStart);
                writes.add(0,tuple);
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
        ArrayList tupla = new ArrayList<>();
        for (int i = 0; i < transaction_TS.size(); i++) {
            if ((int)(transaction_TS.get(i).get(0))==t.getId())
            {
                return (int)transaction_TS.get(i).get(1);
            }

        }
        tupla.add(t.getId());
        tupla.add(currentTime);
        transaction_TS.add(tupla);
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
        for (int i = 0; i < writes.size(); i++) {
            for (int j = 0; j < writes.get(i).size(); j++) {
                if (time==writes.get(i).get(j) && j!=0)
                {
                    if (writes.get(i).size()==2)
                    {
                        writes.remove(i);
                        break;
                    }
                    else
                    {
                        writes.get(i).remove(j);

                    }
                }
            }
        }
        for (int i = 0; i < reads.size(); i++) {
            for (int j = 0; j < reads.get(i).size(); j++) {
                if (time==reads.get(i).get(j)&& j!=0)
                {
                    if (reads.get(i).size()==2)
                    {
                        reads.remove(i);
                        break;
                    }
                    else
                    {
                        reads.get(i).remove(j);

                    }
                }
            }
        }
        for (int i = 0; i < transaction_TS.size(); i++) {

            if ((transaction_TS.get(i).get(0))==t.getId())
            {
                transaction_TS.remove(i);
            }
        }
        super.abort(t);

    }

}
