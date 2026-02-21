package ibd.transaction.concurrency.deadlock.prevention.timeout;

import ibd.transaction.concurrency.LockBasedConcurrencyManager;
import ibd.transaction.Transaction;

import java.util.HashMap;
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author artur
 */
public class ArturManager extends LockBasedConcurrencyManager{
    
    int mTicks; // Numero maximo de ticks ateh time out
    private final HashMap<Integer, Integer> controlTable; // Controle do time out
    
    public ArturManager(int mTicks) throws Exception {
        
        this.mTicks = mTicks;
        this.controlTable = new HashMap<>();
    }
    
    @Override
    
    //Adiciona ou aumenta na tabela de controle
    
    public List<DataRow> processInstruction(Transaction trans) throws Exception{
        
        if(!activeTransactions.contains(trans)){
            activeTransactions.add(trans);
            logger.transactionStart(trans);
        }
        
        Integer transId = trans.getId();
        this.controlTable.computeIfAbsent(transId, ticks -> 0);
        
        if (!trans.waitingLockRelease()){
            Transaction toAbort = queueTransaction(trans.getCurrentInstruction());
            if (toAbort != null){
                this.abort(toAbort);
                return null;
            }
        }
        if (canExecuteCurrentInstruction(trans)){
            return this.processCurrentInstruction(trans);
        } else {
            this.controlTable.computeIfPresent(transId, (transacao, ticks) -> ticks +1);
            boolean abortion = this.shouldAbort(trans);
            if (abortion){
                this.abort(trans);
            }
            return null;
        }
        
    }
    
    @Override
    // Faz o reset para 0 do numero ticks no controle antes da instrucao ser executada
    protected List<DataRow> processCurrentInstruction(Transaction trans) throws Exception {
        
        this.resetTransactionTicks(trans);
        return super.processCurrentInstruction(trans);
        
    }
    
    @Override
    
    // Aborta ao atingir o numero maximo na espera
    
    protected boolean shouldAbort(Transaction trans){
        
        Integer transTicks = this.controlTable.get(trans.getId());
        return transTicks  > this.mTicks;
        
    }
    @Override
    
    // Faz o reset do registro na tabela ao abortar
    
    protected void abort(Transaction trans) throws Exception{
        System.out.println(" -- Transaction" +trans.getId() + "timed out --");
        super.abort(trans);
        this.resetTransactionTicks(trans);
    }
    
    // Faz o reset para 0 do numero de ticks na tabela de controle
    private void resetTransactionTicks(Transaction trans){
        this.controlTable.put(trans.getId(), 0);
    }
}
   
