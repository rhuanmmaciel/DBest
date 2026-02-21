/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.deadlock.detection;

import ibd.transaction.concurrency.LockBasedConcurrencyManager;
import ibd.transaction.instruction.Instruction;
import ibd.transaction.concurrency.Item;
import ibd.transaction.concurrency.Lock;
import ibd.transaction.Transaction;
import java.util.ArrayList;

/**
 *
 * @author pccli
 */
public class DeadlockDetectionConcurrencyManager extends LockBasedConcurrencyManager {

    CycleDetection cd = new CycleDetection();

    public DeadlockDetectionConcurrencyManager() throws Exception {
        super();
    }

    @Override
    public boolean commit(Transaction t) throws Exception {
        boolean ok = super.commit(t);
        cd.removeDependencies(t);
        return ok;
    }

    @Override
    public void abort(Transaction t) throws Exception {
        super.abort(t);
        cd.removeDependencies(t);
    }

    @Override
    public Transaction addToQueue(Item item, Instruction instruction) {

        Transaction t = instruction.getTransaction();

        //estratégia de detecção de deadlock
        ArrayList cycleList = createDependencies(item, t);
        boolean needToAbort = (cycleList != null);

        Lock l = new Lock(t, instruction.getMode());
        item.locks.add(l);
        //instruction.setItem(item);
        if (needToAbort) {
            return choseTransaction(cycleList);
        } else {
            return null;
        }
    }

    private ArrayList createDependencies(Item item, Transaction t) {
        ArrayList cycleList = null;
        int currentMode = t.getCurrentInstruction().getMode();
        for (int i = 0; i < item.locks.size(); i++) {
            Lock l = item.locks.get(i);
            if (!t.equals(l.transaction)) {
                if (l.mode >= Instruction.WRITE || currentMode >= Instruction.WRITE) {
                    ArrayList list = addDependency(t, l.transaction);
                    if (list != null) {
                        cycleList = list;
                    }
                }
            }
        }
        return cycleList;
    }

    public Transaction choseTransaction_(ArrayList<Transaction> transactions) {

        Transaction chosenT = transactions.get(0);
        for (int i = 1; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            int missingChosen = chosenT.getInstructionsSize() - chosenT.getCurrentInstructionIndex();
            int missing = t.getInstructionsSize() - t.getCurrentInstructionIndex();
            if (missing > missingChosen) {
                chosenT = t;
            } else if (missing == missingChosen) {
                if (t.getCurrentInstructionIndex() < chosenT.getCurrentInstructionIndex()) {
                    chosenT = t;
                }
            }

        }
        return chosenT;
    }

    public Transaction choseTransaction(ArrayList<Transaction> transactions) {

        Transaction chosenT = transactions.get(0);
        for (int i = 1; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            if (t.getId() > chosenT.getId()) {
                chosenT = t;
            }

        }
        return chosenT;
    }

    private ArrayList addDependency(Transaction t1, Transaction t2) {
        return cd.addDependency(t1, t2);
    }

}
