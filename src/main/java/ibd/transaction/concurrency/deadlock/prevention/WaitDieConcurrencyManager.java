/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.deadlock.prevention;

import ibd.transaction.concurrency.LockBasedConcurrencyManager;
import ibd.transaction.instruction.Instruction;
import ibd.transaction.concurrency.Item;
import ibd.transaction.concurrency.Lock;
import ibd.transaction.Transaction;
import static ibd.transaction.instruction.Instruction.WRITE;

/**
 *
 * @author pccli
 */
public class WaitDieConcurrencyManager extends LockBasedConcurrencyManager {

    public WaitDieConcurrencyManager() throws Exception {
        super();
    }

    @Override
    public Transaction addToQueue(Item item, Instruction instruction) {

        Transaction t = instruction.getTransaction();
        //estratégia de prevenção de deadlock wait-die
        boolean needToAbort = isYounger(item, t, instruction.getMode());
        if (needToAbort) {
            return t;
        }

        Lock l = new Lock(t, instruction.getMode());
        item.locks.add(l);
        //instruction.setItem(item);

        return null;
    }

    //wait-die
    private boolean isYounger(Item item, Transaction t, int mode) {
        for (int i = 0; i < item.locks.size(); i++) {
            Lock l = item.locks.get(i);
            if (!l.transaction.equals(t)) {
                if (mode >= WRITE || l.mode >= WRITE) {
                    if (t.getId() > l.transaction.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
