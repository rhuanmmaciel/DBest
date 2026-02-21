/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.locktable;

import ibd.table.Table;
import ibd.transaction.instruction.Instruction;
import ibd.transaction.Transaction;
import ibd.transaction.concurrency.Item;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author Sergio
 */
public class LockTables {

    Hashtable<Table, LockTable> tables = new Hashtable();

    private LockTable getLockTable(Table table) {
        LockTable lockTable = tables.get(table);
        if (lockTable == null) {
            lockTable = new LockTable();
            tables.put(table, lockTable);
        }
        return lockTable;
    }

    /*
    * Adds an item related to the records accessed by the instruction. 
    * the recors are identified by their primary keys
    * If an item already exists, just returns it. 
     */
    public Item addItem(Instruction i) {

        LockTable lockTable = getLockTable(i.getTable());
        return lockTable.addItem(i);
    }

    /*
    * returns all items affected by the records acessed by the instruction. 
     */
    public Iterable<Item> getItems(Instruction i) {

        LockTable lockTable = getLockTable(i.getTable());
        return lockTable.getItems(i);

    }

    /*
    * removes all references to a transaction (its instructions) from the items of the lock table 
     */
    public void removeTransaction(Transaction t) {

        ArrayList<Table> tabs = new ArrayList();

        for (Instruction i : t.getInstrutions()) {
            Table table = i.getTable();
            if (!tabs.contains(table)) {
                tabs.add(table);
            }
        }
        

        for (Table table : tabs) {
            LockTable lockTable = getLockTable(table);
            lockTable.removeTransaction(t);
        }

    }

}
