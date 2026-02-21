package ibd.transaction.concurrency.deadlock.prevention.timeout;

import java.util.WeakHashMap;

import ibd.transaction.concurrency.LockBasedConcurrencyManager;
import ibd.transaction.Transaction;
import java.util.List;
import ibd.table.prototype.DataRow;

public class SamuelSchultzeManager extends LockBasedConcurrencyManager {

    // weak map previne memory leak
    private WeakHashMap<Transaction, Integer> waitTicks = new WeakHashMap<Transaction, Integer>();
    private int maxTicks = 0;

    public SamuelSchultzeManager(int maxTicks) throws Exception {
        super();
        this.maxTicks = maxTicks;
    }

    @Override
    public List<DataRow> processCurrentInstruction(Transaction t) throws Exception {
        waitTicks.put(t, 0); // reset
        return super.processCurrentInstruction(t);
    }

    @Override
    protected void abort(Transaction t) throws Exception {
        waitTicks.put(t, 0); // reset
        super.abort(t);
    }

    @Override
    protected boolean shouldAbort(Transaction t) {
        Integer waits = waitTicks.containsKey(t) ? waitTicks.get(t) : 0;
        waitTicks.put(t, ++waits); // incrementa
        return waits >= this.maxTicks;
    }

}