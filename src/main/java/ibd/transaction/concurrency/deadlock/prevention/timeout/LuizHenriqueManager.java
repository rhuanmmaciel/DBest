package ibd.transaction.concurrency.deadlock.prevention.timeout;

import java.util.HashMap;
import ibd.transaction.concurrency.LockBasedConcurrencyManager;
import ibd.transaction.Transaction;
import java.util.List;
import ibd.table.prototype.DataRow;

public class LuizHenriqueManager extends LockBasedConcurrencyManager {
	
	private int maxAttempts;
	private HashMap<Integer,Integer> tableAttempts = new HashMap<Integer,Integer>();
	
	
	public LuizHenriqueManager(int maxAttempts) throws Exception {
		this.maxAttempts = maxAttempts;
	}
	
	
	protected int incrementAttempt(Transaction t) {
		Integer qtd = tableAttempts.get(t.getId());
		qtd = (qtd==null)?1:(qtd+1);
		tableAttempts.put(t.getId(),qtd);
		return qtd;
	}
	
	@Override
	protected boolean shouldAbort(Transaction t) {
		Integer qtd = incrementAttempt(t);
		if(qtd>=maxAttempts)return true;
		return super.shouldAbort(t);
	}	
	
	@Override
	protected void abort(Transaction t) throws Exception {
		tableAttempts.remove(t.getId());
		super.abort(t);
	}
	
	@Override
	public boolean commit(Transaction t) throws Exception {
		tableAttempts.remove(t.getId());
		return super.commit(t);
	}
	
	@Override
	protected List<DataRow> processCurrentInstruction(Transaction t) throws Exception {
		tableAttempts.remove(t.getId());
		return super.processCurrentInstruction(t);
	}
	
}
