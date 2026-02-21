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
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class ThiagoRSTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;
    HashMap<String, Integer> DictItemRead = new HashMap<>();
    HashMap<String, Integer> DictItemWrite = new HashMap<>();
    HashMap<Transaction, Integer> DictTransactionTimestamp = new HashMap<>();
    

    /**
     *
     * @throws Exception
     */
    public ThiagoRSTimestampOrderingConcurrencyManager() throws Exception {
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

        currentTime++;
        Integer tStart = getTransactionStartTime(t);


        logTansactionStart(t);

        DictTransactionTimestamp.get(t);
        Instruction i = t.getCurrentInstruction();

        String item = i.getUniqueKey();


        if (i instanceof SingleUpdateInstruction) 
        {
            if(tStart < DictItemRead.getOrDefault(item, -1))
            {
                abort(t);
                return null;
            }
            else
            {
                if(tStart > DictItemWrite.getOrDefault(item, -1))
                {
                    DictItemWrite.put(item, tStart);
                }
                else
                {
                    ignore = true;
                }
            }
        } 
        else if (i instanceof SingleReadInstruction) //Leitura
        {
            if(tStart < DictItemWrite.getOrDefault(item, -1))
            {
                abort(t);
                return null;
            }
            else
            {
                DictItemRead.put(item, tStart);

                if(tStart > DictItemRead.getOrDefault(item, -1))
                {
                    DictItemRead.put(item, tStart);
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
        Integer timestamp;

        //Verificar se precisa criar um novo timestamp
        timestamp = DictTransactionTimestamp.get(t);
        if(timestamp == null) 
        {
            //System.out.println("O VALOR DO TIMESTAMP é NULO");
            timestamp = currentTime;
            DictTransactionTimestamp.put(t, timestamp);
            // TODO: handle exception
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
        //System.out.println("Aborting "+t);
            
        //Ao abortar, remove o timestamp do dicionário.
        
        super.abort(t);

        DictTransactionTimestamp.put(t, null);
        //DictTransactionTimestamp.remove(t);

    }

}
