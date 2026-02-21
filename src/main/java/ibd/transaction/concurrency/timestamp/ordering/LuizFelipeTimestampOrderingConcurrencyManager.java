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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ibd.table.prototype.DataRow;


/**
 *
 * @author pccli
 */
public class LuizFelipeTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;

    private class Listas {
    	LinkedList<Integer> tsr;
        LinkedList<Integer> tsw;
        
        public Listas() {
        	tsr = new LinkedList<>();
        	tsw = new LinkedList<>();
        }
    }
    
    private Map<Integer, Listas> tabelaControle = new HashMap<>();
    private Map<Integer, Integer> tabelaTs = new HashMap<>();
    /**
     *
     * @throws Exception
     */
    public LuizFelipeTimestampOrderingConcurrencyManager() throws Exception {
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

        //puts the transaction in the recovery log if its not already there
        logTansactionStart(t);

        Instruction i = t.getCurrentInstruction();
        int itemInstrucao = (int) i.getPk();
        
        
        // tabelaControle - Se o item nao esta na tabela, adiciona
    	if(!tabelaControle.containsKey(itemInstrucao)) {
    		tabelaControle.put(itemInstrucao, new Listas());
    		//seta os valores de tsr e tsw com -1
    		tabelaControle.get(itemInstrucao).tsr.addLast(-1);
    		tabelaControle.get(itemInstrucao).tsw.addLast(-1);
    	}
    	
    	Integer tStart = getTransactionStartTime(t);
        
        if (i instanceof SingleUpdateInstruction) {
        	
        	
        	int idTsr = tabelaControle.get(itemInstrucao).tsr.getLast();
        	// se o ts da transacao for maior que o ts-r do item
        	if ((idTsr != -1 && tStart >= tabelaTs.get(idTsr)) || (idTsr == -1 && tStart > idTsr)) {
        		// adiciona a transacao no historico
        		if (tStart > tabelaControle.get(itemInstrucao).tsw.getLast()) {
        			tabelaControle.get(itemInstrucao).tsw.addLast(t.getId());
        		} 
        		else {
        			ignore = true;
        		}
        		
        	} else {
        		abort(t);
        		return null;
        	}
        	
        } else if (i instanceof SingleReadInstruction) {
        	// se o ts da transacao for maior que o ts-w do item
        	int idTsw = tabelaControle.get(itemInstrucao).tsw.getLast();

        	if ((idTsw != -1 && tStart >= tabelaTs.get(idTsw)) || (idTsw == -1 && tStart > idTsw)) {
        		if (tStart > tabelaControle.get(itemInstrucao).tsr.getLast()) {
        			tabelaControle.get(itemInstrucao).tsr.addLast(t.getId());
        		}	
        	
        	} else {
        		abort(t);
        		return null;
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
    	// tabelaTs - Se a transacao nao estiver nela, adiciona
        if(!tabelaTs.containsKey(t.getId()) || tabelaTs.get(t.getId()) == -1) {
        	tabelaTs.put(t.getId(), currentTime);
        }
    	
    	return tabelaTs.get(t.getId());
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
        
        //remove valor da tabelaTs
        tabelaTs.put(t.getId(), -1);
        
        //remove transacao da tabelaControle
        for (Map.Entry<Integer, Listas> entry : tabelaControle.entrySet()) {
            Listas listas = entry.getValue();
            
            // Iterar sobre a pilha tsr e remover o valor t.getId()
            listas.tsr.removeIf(transacao -> transacao.equals(t.getId()));
            if (listas.tsr.isEmpty()) {
                listas.tsr.add(-1);
            }

            // Iterar sobre a pilha tsw e remover o valor t.getId()
            listas.tsw.removeIf(transacao -> transacao.equals(t.getId()));
            if (listas.tsw.isEmpty()) {
                listas.tsw.add(-1);
            }

        }

    }

}