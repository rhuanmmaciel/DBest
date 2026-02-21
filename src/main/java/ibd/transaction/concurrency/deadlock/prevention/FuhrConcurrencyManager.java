package ibd.transaction.concurrency.deadlock.prevention;


import ibd.transaction.Transaction;
import ibd.transaction.concurrency.ConcurrencyManager;
import ibd.transaction.concurrency.Item;
import ibd.transaction.concurrency.Lock;
import ibd.transaction.concurrency.LockBasedConcurrencyManager;
import ibd.transaction.instruction.Instruction;
import java.util.ArrayList;

public class FuhrConcurrencyManager extends LockBasedConcurrencyManager{
	boolean preemptive;
	
	public FuhrConcurrencyManager(boolean preemptive) throws Exception{
		this.preemptive = preemptive;
	}
	
	@Override
	public Transaction addToQueue(Item item, Instruction instruction) {
		if(this.preemptive) {
            Transaction t = instruction.getTransaction();
            Lock l = new Lock(t, instruction.getMode());
            item.locks.add(l);
            //instruction.setItem(item);
            
            if(!item.canBeLockedBy(t) && !ahead(item.locks, t)) {
            	return lockToBeAborted(item.locks, t);
            }
            return null;
		}
		else {
		  Transaction t = instruction.getTransaction();
          Lock l = new Lock(t, instruction.getMode());
          item.locks.add(l);
          //instruction.setItem(item);
          
          if(!item.canBeLockedBy(t) && !older(item.locks, t)) {
            return t;
          }
          return null;
		}
	}
	
	public Transaction lockToBeAborted(ArrayList<Lock> locks, Transaction t) {
		Transaction toBeAborted = locks.get(0).transaction;
		for(int i = 0; i < locks.size(); i++) {
			Transaction atual = locks.get(i).transaction;
			
			if(atual.getId() > toBeAborted.getId()) {
				toBeAborted = atual;
			}
		}
		if(toBeAborted.getId() == t.getId())
			return null;
		return toBeAborted;
	}
	
	public boolean ahead(ArrayList<Lock> locks, Transaction t) {
		Transaction frente = locks.get(0).transaction;
		if(frente.getId() == t.getId())
			return true;
		
		return false;
	}
	
	public boolean older(ArrayList<Lock> locks, Transaction t) {
		Transaction older = locks.get(0).transaction;
		for(int i = 0; i < locks.size(); i++) {
			Transaction atual = locks.get(i).transaction;
			if(older.getId() > atual.getId())
				older = atual;			
		}
		return older.getId() == t.getId();
	}
}