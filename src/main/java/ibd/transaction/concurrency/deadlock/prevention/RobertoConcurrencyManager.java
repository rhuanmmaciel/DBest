package ibd.transaction.concurrency.deadlock.prevention;

import ibd.transaction.Transaction;
import ibd.transaction.concurrency.ConcurrencyManager;
import ibd.transaction.concurrency.Item;
import ibd.transaction.concurrency.Lock;
import ibd.transaction.concurrency.LockBasedConcurrencyManager;
import ibd.transaction.instruction.Instruction;



public class RobertoConcurrencyManager extends LockBasedConcurrencyManager {


    private boolean preemptive;

    public RobertoConcurrencyManager(boolean preemptive) throws Exception {
        this.preemptive = preemptive;
    }

    @Override
    public Transaction addToQueue(Item item, Instruction instruction) {
        if (preemptive) return woundWait(item, instruction);
        return waitDie(item, instruction);
    }

    private Transaction waitDie(Item item, Instruction instruction) {
        Transaction t = instruction.getTransaction();
        for (Lock lock : item.locks) {
            if (lock.transaction.getId() < t.getId()) {
                if (lock.mode == Instruction.WRITE || instruction.getMode() == Instruction.WRITE)
                    return t;
            }
        }
        super.addToQueue(item, instruction);
        return null;
    }

    //Essa implementação não trata casos onde é necessário aborts em cascata
    //Seria necessário implementar uma lista de retorno de transações, e retornar todas que fossem mais novas que a transação a ser adicionada na fila
    //modificando assim a assinatura do método, porém, como o framework não foi construido para suportar aborts em cascata, optei
    //por não implementar solução para esses casos, retornando a primeira transação mais nova encontrada na fila de locks
    private Transaction woundWait(Item item, Instruction instruction) {
        Transaction t = instruction.getTransaction();
        for (Lock lock : item.locks) {
            if (t.getId() < lock.transaction.getId()) {
                if (lock.mode == Instruction.WRITE || instruction.getMode() == Instruction.WRITE) {
                    super.addToQueue(item, instruction);
                    return lock.transaction;
                }
            }
        }
        super.addToQueue(item, instruction);
        return null;
    }

}
