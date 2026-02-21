/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.locktable.items;

import ibd.table.ComparisonTypes;
import ibd.table.Table;
import ibd.transaction.concurrency.ConcurrencyManager;
import ibd.table.Utils;
import ibd.table.prototype.BasicDataRow;
import ibd.transaction.instruction.MultiReadInstruction;
import ibd.transaction.SimulatedIterations;
import static ibd.transaction.SimulatedIterations.getValue;
import ibd.transaction.instruction.SingleReadInstruction;
import ibd.transaction.instruction.SingleWriteInstruction;
import ibd.transaction.Transaction;
import ibd.transaction.concurrency.LockBasedConcurrencyManager;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class Main {

    public void test1(ConcurrencyManager manager, String minItem, String maxItem, String toWriteItem) throws Exception {
        Table table1 = Utils.createTable("c:\\teste\\ibd", "t1", Table.DEFULT_PAGE_SIZE, 100, false, 1, 50);
        
        BasicDataRow rowData = new BasicDataRow();
        rowData.setInt("id", (int)getValue(toWriteItem));
        table1.removeRecord(rowData);

        SimulatedIterations simulation = new SimulatedIterations();

        addTransactions(simulation, table1, minItem, maxItem);

        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleWriteInstruction(table1, toWriteItem, "X"));
        t2.addInstruction(new SingleReadInstruction(table1, getValue("G")));

        simulation.addTransaction(t2);

        simulation.run(-1, true, manager);

    }

    public void test2(ConcurrencyManager manager, String minItem, String maxItem, String toWriteItem) throws Exception {
        Table table1 = Utils.createTable("c:\\teste\\ibd", "t1", Table.DEFULT_PAGE_SIZE, 100, false, 1, 50);
        BasicDataRow rowData = new BasicDataRow();
        rowData.setInt("id", (int)getValue(toWriteItem));
        table1.removeRecord(rowData);

        SimulatedIterations simulation = new SimulatedIterations();

        Transaction t1 = new Transaction();
        t1.addInstruction(new SingleWriteInstruction(table1, toWriteItem, "X"));
        t1.addInstruction(new SingleReadInstruction(table1, getValue("G")));
        t1.addInstruction(new SingleReadInstruction(table1, getValue("H")));
        simulation.addTransaction(t1);

        addTransactions(simulation, table1, minItem, maxItem);

        simulation.run(-1, true, manager);

    }

    /*
    public void test3(ConcurrencyManager manager, String minItem1, String maxItem1,String minItem2, String maxItem2, String toWriteItem) throws Exception {
        Table table1 = Utils.createTable2("c:\\teste\\ibd", "t10", GenericTable1_1.DEFULT_PAGE_SIZE, 100, false, 1);
        table1.removeRecord(getValue(toWriteItem));

        SimulatedIterations simulation = new SimulatedIterations();

        addTransactions(simulation, table1, minItem1, maxItem1);
        
        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleWriteInstruction(table1, toWriteItem, "X"));
        t2.addInstruction(new SingleReadInstruction(table1,  getValue("G")));

        simulation.addTransaction(t2);

        addTransactions(simulation, table1, minItem2, maxItem2);

        simulation.run(-1, true, manager);

    }
     */

    private Transaction addTransaction(Table table, int lower, int higher) throws Exception {
        Transaction t1 = new Transaction();
        t1.addInstruction(new MultiReadInstruction(table, lower, ComparisonTypes.GREATER_EQUAL_THAN,
                higher, ComparisonTypes.LOWER_EQUAL_THAN));
        t1.addInstruction(new MultiReadInstruction(table, lower, ComparisonTypes.GREATER_EQUAL_THAN,
                higher, ComparisonTypes.LOWER_EQUAL_THAN));
        return t1;
    }

    private void addTransactions(SimulatedIterations simulation, Table table1, String minItem, String maxItem) throws Exception {
        int minItem_ = getValue(minItem);
        int maxItem_ = getValue(maxItem);
        int range = maxItem_ - minItem_;
        int max = minItem_ + (int) Math.ceil((double) range / 2);
        int i = 0;
        for (i = minItem_; i <= max; i += 2) {
            Transaction t1 = addTransaction(table1, i, i + 1);
            simulation.addTransaction(t1);
        }

        if (range % 2 == 0) {
            Transaction t1 = addTransaction(table1, maxItem_, maxItem_);
            simulation.addTransaction(t1);
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        try {
            //m.test1(new LockBasedConcurrencyManager(), "A", "E","D");
            m.test2(new LockBasedConcurrencyManager(), "A", "E", "D");
            //m.test2(new LockBasedConcurrencyManager(), "A","C", "B","C","C");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
