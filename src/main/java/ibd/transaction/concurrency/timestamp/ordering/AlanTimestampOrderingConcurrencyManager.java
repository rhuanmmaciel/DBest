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

public class AlanTimestampOrderingConcurrencyManager extends ConcurrencyManager {
	HashMap<Transaction, Integer> tsTransaction = new HashMap<>();
	HashMap<Long , List<Transaction>> tsRead = new HashMap<>();
	HashMap<Long , List<Transaction>> tsWrite = new HashMap<>();
    int currentTime = 0;

    /**
     *
     * @throws Exception
     */
    public AlanTimestampOrderingConcurrencyManager() throws Exception {
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
    	List<Transaction> wtransactions = null;
    	List<Transaction> rtransactions = null;
        boolean ignore = false;
        int flag = 0;

        //increments the timestamp counter
        currentTime++;

        Integer ts = tsTransaction.get(t);
        if(ts == null) {
        	ts = getTransactionStartTime(t);
        	tsTransaction.put(t, ts);
        }
        //puts the transaction in the recovery log if its not already there
        logTansactionStart(t);

        Instruction i = t.getCurrentInstruction();
        long pk = i.getPk();
        Integer timestamp;
        if (i instanceof SingleUpdateInstruction) {
        	rtransactions = tsRead.get(pk);
        	if (rtransactions != null ) {
        	    for (Transaction rtransaction : rtransactions) {
        	        timestamp = tsTransaction.get(rtransaction);
        	        if (timestamp > ts) {
        	           flag = 1; 
        	           break;
        	        }
        	    }
        	}
        	if(flag == 1) {
        		abort(t);
        		return null;
        	}
        	wtransactions = tsWrite.get(pk);
        	// Se tem alguma transação mais velha com um write nesse item, ignore o write da transação atual
        	if (wtransactions != null) {
        	    for (Transaction wtransaction : wtransactions) {
        	        timestamp = tsTransaction.get(wtransaction);
        	        if (timestamp > ts) {
        	           ignore = true;
        	        }
        	    }
        	}
        	if(ignore == false) {
        		if (wtransactions == null) {
        			wtransactions = new ArrayList<>();
        			tsWrite.put(pk, wtransactions);
        		}
            	wtransactions.add(t);
            }
        } else if (i instanceof SingleReadInstruction) {
        	wtransactions = tsWrite.get(pk);
        	if (wtransactions != null ) {
        	    for (Transaction wtransaction : wtransactions) {
        	        timestamp = tsTransaction.get(wtransaction);
        	        if (timestamp > ts) {
        	           flag = 1; 
        	           break;
        	        }
        	    }
        	}
        	if(flag == 1) {
        		abort(t);
        		return null;
        	}
        	rtransactions = tsRead.get(pk);
            if (rtransactions == null) {
                rtransactions = new ArrayList<>();
                tsRead.put(pk, rtransactions);
            }
            rtransactions.add(t);
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
        super.abort(t);
        // remove timestamp da transação, e sua conexão com listas relacionadas aos itens
        tsTransaction.remove(t);
        for (List<Transaction> transactions : tsRead.values()) {
            transactions.remove(t);
        }
        for (List<Transaction> transactions : tsWrite.values()) {
            transactions.remove(t);
        }
    }
}