/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency;

import ibd.transaction.SimulatedIterations;
import ibd.transaction.Transaction;
import ibd.transaction.log.Logger;
import java.util.ArrayList;
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author Sergio
 */
public abstract class ConcurrencyManager {

    public Logger logger;

    protected ArrayList<Transaction> activeTransactions = new ArrayList<>();

    /**
     * Logs the abort of the transaction in the recovery log.
     *
     * @param t
     * @return
     * @throws Exception
     */
    private void logTansactionAbort(Transaction t) throws Exception {
        logger.transactionAbort(t);
        activeTransactions.remove(t);
    }

    /**
     * this method need to be called to restore the database to a valid state if
     * transactions where interrupted during the previous database usage
     *
     * @throws Exception
     */
    public void recoverFromLog() throws Exception {
        logger.recover();
    }

    /**
     *
     * @throws Exception
     */
    public void clearLog() throws Exception {
        logger.clear();
    }

    /**
     * Logs the start of the transaction in the recovery log.
     *
     * @param t
     * @return
     * @throws Exception
     */
    protected void logTansactionStart(Transaction t) throws Exception {
        if (!activeTransactions.contains(t)) {
            activeTransactions.add(t);
            logger.transactionStart(t);
        }
    }

    /**
     * Logs the commit of the transaction in the recovery log.
     *
     * @param t
     * @return
     * @throws Exception
     */
    protected void logTansactionCommit(Transaction t) throws Exception {
        logger.transactionCommit(t);
        activeTransactions.remove(t);
    }

    /**
     * Writes the log to disk
     *
     * @throws Exception
     */
    public void flushLog() throws Exception {
        logger.writeLog();
    }

    public abstract List<DataRow> processInstruction(Transaction t) throws Exception;

    public boolean commit(Transaction t) throws Exception {
        t.commit();
        logTansactionCommit(t);
        return true;
    }

    protected void abort(Transaction t) throws Exception {

        System.out.println(SimulatedIterations.getTab(t.getId() - 1) + t.getId() + " Abort");
        t.abort();
        logTansactionAbort(t);

    }

}
