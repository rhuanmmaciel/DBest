/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency;

import ibd.transaction.instruction.Instruction;
import ibd.transaction.SimulatedIterations;
import ibd.transaction.Transaction;
import java.util.ArrayList;

/**
 * An item may refer to an interval of records, delimited by a lower and a upper bound.
 * @author pccli
 */
public class Item {

    
    public long firstPrimaryKey;//the lower bound
    public long lastPrimaryKey; //the upper bound


    public ArrayList<Lock> locks = new ArrayList<>();

    public Item(long firstpk, long lastpk) {
        firstPrimaryKey = firstpk;
        lastPrimaryKey = lastpk;
    }

    public Long getLower(){
        return firstPrimaryKey;
    }
    
    public Long getHigher(){
        return lastPrimaryKey;
    }
    
    

    public void removeTransaction(Transaction t) {
        for (int i = locks.size() - 1; i >= 0; i--) {
            Lock lock = locks.get(i);
            if (lock.transaction.equals(t)) {
                locks.remove(i);
            }

        }

    }

    public void printLocks() {
        System.out.print("Item:(" + SimulatedIterations.getString((int) firstPrimaryKey) 
                + "-"+SimulatedIterations.getString((int) lastPrimaryKey)
                + ")=>");
        for (int i = 0; i < locks.size(); i++) {
            Lock lock = locks.get(i);
            System.out.print(lock.transaction.getId() + ":" + Instruction.getModeType(lock.mode));

        }
        System.out.println("");

    }

    public boolean canBeLockedBy(Transaction t) {
        int currentMode = t.getCurrentInstruction().getMode();
        for (int i = 0; i < locks.size(); i++) {
            Lock l = locks.get(i);
            if (l.transaction.equals(t) && (l.mode == currentMode || l.mode>=Instruction.WRITE)) {
                return true;
            }
            if (l.mode >= Instruction.WRITE) {
                return false;
            }
            if (!l.transaction.equals(t) && currentMode >= Instruction.WRITE) {
                return false;
            }
        }
        System.out.println("não deveria chegar aqui");
        return false;
    }

    

}
