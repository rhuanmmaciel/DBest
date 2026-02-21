/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction;

import ibd.table.Table;
import ibd.transaction.instruction.SingleReadInstruction;
import ibd.transaction.instruction.SingleUpdateInstruction;
import ibd.transaction.concurrency.ConcurrencyManager;
import ibd.table.Utils;
import static ibd.transaction.SimulatedIterations.getValue;
import ibd.transaction.concurrency.LockBasedConcurrencyManager;
import ibd.transaction.concurrency.deadlock.detection.DeadlockDetectionConcurrencyManager;

/**
 *
 * @author pccli
 */
public class Main {

    

    //Exercise of lesson 21 - Controle de Concorrência - Protocolos baseados em lock
    public void test1(ConcurrencyManager manager) throws Exception {
        Table table1 = Utils.createTable("c:\\teste\\ibd", "t1", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);
        Transaction t1 = new Transaction();
        t1.addInstruction(new SingleReadInstruction(table1, getValue("A")));
        t1.addInstruction(new SingleUpdateInstruction(table1, getValue("B"), "bla"));

        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleReadInstruction(table1, getValue("D")));
        t2.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t2.addInstruction(new SingleUpdateInstruction(table1, getValue("C"), "bla"));
        t2.addInstruction(new SingleReadInstruction(table1, getValue("H")));

        Transaction t3 = new Transaction();
        t3.addInstruction(new SingleUpdateInstruction(table1, getValue("D"), "bla"));
        t3.addInstruction(new SingleReadInstruction(table1, getValue("E")));
        t3.addInstruction(new SingleReadInstruction(table1, getValue("B")));

        Transaction t4 = new Transaction();
        t4.addInstruction(new SingleReadInstruction(table1, getValue("F")));
        t4.addInstruction(new SingleReadInstruction(table1, getValue("G")));
        t4.addInstruction(new SingleReadInstruction(table1, getValue("A")));

        Transaction t5 = new Transaction();
        t5.addInstruction(new SingleUpdateInstruction(table1, getValue("B"), "bla"));
        t5.addInstruction(new SingleUpdateInstruction(table1, getValue("F"), "bla"));
        t5.addInstruction(new SingleReadInstruction(table1, getValue("G")));

        SimulatedIterations simulation = new SimulatedIterations();
        simulation.addTransaction(t1);
        simulation.addTransaction(t2);
        simulation.addTransaction(t3);
        simulation.addTransaction(t4);
        simulation.addTransaction(t5);
        simulation.run(100, false, manager);
    }

    //granting read lock because transacton already has write lock
    public void test2(ConcurrencyManager manager) throws Exception {
        Table table1 = Utils.createTable("c:\\teste\\ibd", "t2", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);

        Transaction t1 = new Transaction();

        t1.addInstruction(new SingleUpdateInstruction(table1, getValue("B"), "xx"));
        t1.addInstruction(new SingleReadInstruction(table1, getValue("B")));

        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t2.addInstruction(new SingleReadInstruction(table1, getValue("A")));

        Transaction t3 = new Transaction();
        t3.addInstruction(new SingleReadInstruction(table1, getValue("C")));
        t3.addInstruction(new SingleUpdateInstruction(table1, getValue("D"), "ggg"));

        SimulatedIterations simulation = new SimulatedIterations();
        simulation.addTransaction(t1);
        simulation.addTransaction(t2);
        simulation.addTransaction(t3);

        simulation.run(100, false, manager);

    }

    //write waits for read
    public void test3(ConcurrencyManager manager) throws Exception {
        Table tab = Utils.createTable("c:\\teste\\ibd", "t1", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);

        Transaction t1 = new Transaction();
        t1.addInstruction(new SingleReadInstruction(tab, getValue("A")));
        t1.addInstruction(new SingleUpdateInstruction(tab, getValue("B"), "X"));

        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleReadInstruction(tab, getValue("B")));
        t2.addInstruction(new SingleUpdateInstruction(tab, getValue("C"), "Y"));

        SimulatedIterations simulation = new SimulatedIterations();
        simulation.addTransaction(t1);
        simulation.addTransaction(t2);
        simulation.run(100, false, manager);

    }
    
    //granting write lock on a transaction that already has a read lock is not possible because a conflicting transaction has priority
    public void test4(ConcurrencyManager manager) throws Exception {
        Table table1 = Utils.createTable("c:\\teste\\ibd", "t2", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);

        Transaction t1 = new Transaction();

        t1.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t1.addInstruction(new SingleUpdateInstruction(table1, getValue("B"), "xx"));

        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t2.addInstruction(new SingleReadInstruction(table1, getValue("A")));

        Transaction t3 = new Transaction();
        t3.addInstruction(new SingleReadInstruction(table1, getValue("C")));
        t3.addInstruction(new SingleUpdateInstruction(table1, getValue("D"), "ggg"));

        SimulatedIterations simulation = new SimulatedIterations();
        simulation.addTransaction(t1);
        simulation.addTransaction(t2);
        simulation.addTransaction(t3);

        simulation.run(100, false, manager);

    }
    
    
    //more than one conflict to be managed
    public void test5(ConcurrencyManager manager) throws Exception {
        Table table1 = Utils.createTable("c:\\teste\\ibd", "t6", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);
        Transaction t1 = new Transaction();
        t1.addInstruction(new SingleUpdateInstruction(table1, getValue("B"),"B1"));
        t1.addInstruction(new SingleUpdateInstruction(table1, getValue("C"), "C1"));
        t1.addInstruction(new SingleReadInstruction(table1, getValue("A")));

        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t2.addInstruction(new SingleReadInstruction(table1, getValue("A")));

        Transaction t3 = new Transaction();
        t3.addInstruction(new SingleReadInstruction(table1, getValue("C")));
        t3.addInstruction(new SingleUpdateInstruction(table1, getValue("A"), "A1"));

        SimulatedIterations simulation = new SimulatedIterations();
        simulation.addTransaction(t1);
        simulation.addTransaction(t2);
        simulation.addTransaction(t3);
        simulation.run(100, false, manager);

    }
    
    //more than one conflict to be managed
    public void test51(ConcurrencyManager manager) throws Exception {
        Table table1 = Utils.createTable("c:\\teste\\ibd", "t6", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);
        Transaction t1 = new Transaction();
        t1.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t1.addInstruction(new SingleReadInstruction(table1, getValue("A")));
        t1.addInstruction(new SingleReadInstruction(table1, getValue("C")));

        
        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleUpdateInstruction(table1, getValue("C"), "C1"));
        t2.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        
        Transaction t3 = new Transaction();
        t3.addInstruction(new SingleUpdateInstruction(table1, getValue("B"), "C1"));
        t3.addInstruction(new SingleReadInstruction(table1, getValue("A")));
        t3.addInstruction(new SingleUpdateInstruction(table1, getValue("C"), "C1"));
        

        SimulatedIterations simulation = new SimulatedIterations();
        simulation.addTransaction(t1);
        simulation.addTransaction(t2);
        simulation.addTransaction(t3);
        simulation.run(100, false, manager);

    }

    //classic deadlock
    public void test6(ConcurrencyManager manager) throws Exception {
        Table table1 = Utils.createTable("c:\\teste\\ibd", "t3", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);
        Transaction t1 = new Transaction();
        t1.addInstruction(new SingleReadInstruction(table1, getValue("A")));
        t1.addInstruction(new SingleUpdateInstruction(table1, getValue("B"), "bla"));

        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t2.addInstruction(new SingleUpdateInstruction(table1, getValue("A"), "bla"));

        SimulatedIterations simulation = new SimulatedIterations();
        simulation.addTransaction(t1);
        simulation.addTransaction(t2);
        simulation.run(100, false, manager);
    }

    //deadlock exercise of lesson 22 - Controle de Concorrência - Deadlock
    public void test7(ConcurrencyManager manager) throws Exception {
        Table table1 = Utils.createTable("c:\\teste\\ibd", "t4", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);
        Transaction t1 = new Transaction();
        t1.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t1.addInstruction(new SingleUpdateInstruction(table1, getValue("A"), "bla"));
        t1.addInstruction(new SingleUpdateInstruction(table1, getValue("C"), "bla"));

        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleReadInstruction(table1, getValue("A")));
        t2.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t2.addInstruction(new SingleReadInstruction(table1, getValue("C")));

        Transaction t3 = new Transaction();
        t3.addInstruction(new SingleUpdateInstruction(table1, getValue("C"), "bla"));
        t3.addInstruction(new SingleReadInstruction(table1, getValue("A")));
        t3.addInstruction(new SingleUpdateInstruction(table1, getValue("B"), "bla"));

        SimulatedIterations simulation = new SimulatedIterations();
        simulation.addTransaction(t1);
        simulation.addTransaction(t2);
        simulation.addTransaction(t3);
        simulation.run(100, false, manager);

    }

    //deadlock test
    public void test8(ConcurrencyManager manager) throws Exception {
        Table table1 = Utils.createTable("c:\\teste\\ibd", "t5", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);
        Transaction t1 = new Transaction();
        t1.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t1.addInstruction(new SingleUpdateInstruction(table1, getValue("A"), "bla"));
        t1.addInstruction(new SingleUpdateInstruction(table1, getValue("C"), "bla"));

        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleReadInstruction(table1, getValue("A")));
        t2.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t2.addInstruction(new SingleReadInstruction(table1, getValue("C")));

        Transaction t3 = new Transaction();
        t3.addInstruction(new SingleUpdateInstruction(table1, getValue("C"), "bla"));
        t3.addInstruction(new SingleReadInstruction(table1, getValue("A")));
        t3.addInstruction(new SingleUpdateInstruction(table1, getValue("B"), "bla"));

        Transaction t4 = new Transaction();
        t4.addInstruction(new SingleUpdateInstruction(table1, getValue("B"), "bla"));
        t4.addInstruction(new SingleReadInstruction(table1, getValue("F")));

        SimulatedIterations simulation = new SimulatedIterations();
        simulation.addTransaction(t1);
        simulation.addTransaction(t2);
        simulation.addTransaction(t3);
        simulation.addTransaction(t4);
        simulation.run(100, false, manager);
    }

    //deadlock test
    public void test9(ConcurrencyManager manager) throws Exception {
        Table table1 = Utils.createTable("c:\\teste\\ibd", "t6", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);
        Transaction t1 = new Transaction();
        t1.addInstruction(new SingleReadInstruction(table1, getValue("A")));
        t1.addInstruction(new SingleUpdateInstruction(table1, getValue("C"), "bla"));
        t1.addInstruction(new SingleReadInstruction(table1, getValue("D")));

        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleUpdateInstruction(table1, getValue("A"), "ds"));
        t2.addInstruction(new SingleUpdateInstruction(table1, getValue("C"), "ds"));

        Transaction t3 = new Transaction();
        t3.addInstruction(new SingleReadInstruction(table1, getValue("C")));
        t3.addInstruction(new SingleUpdateInstruction(table1, getValue("B"), "dd"));
        t3.addInstruction(new SingleUpdateInstruction(table1, getValue("A"), "bla"));

        Transaction t4 = new Transaction();
        t4.addInstruction(new SingleReadInstruction(table1, getValue("F")));
        t4.addInstruction(new SingleUpdateInstruction(table1, getValue("E"), "ss"));

        Transaction t5 = new Transaction();
        t5.addInstruction(new SingleUpdateInstruction(table1, getValue("C"), "dsds"));
        t5.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t5.addInstruction(new SingleUpdateInstruction(table1, getValue("A"), "bla"));

        SimulatedIterations simulation = new SimulatedIterations();
        simulation.addTransaction(t1);
        simulation.addTransaction(t2);
        simulation.addTransaction(t3);
        simulation.addTransaction(t4);
        simulation.addTransaction(t5);
        simulation.run(100, false, manager);

    }

    //deadlock test
    public void test10(ConcurrencyManager manager) throws Exception {
        Table table1 = Utils.createTable("c:\\teste\\ibd", "t7", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);

        Transaction t1 = new Transaction();
        t1.addInstruction(new SingleReadInstruction(table1, getValue("A")));
        t1.addInstruction(new SingleUpdateInstruction(table1, getValue("B"), "xx"));

        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleReadInstruction(table1, getValue("B")));
        t2.addInstruction(new SingleUpdateInstruction(table1, getValue("A"), "yy"));
        /*
        t2.addInstruction(new SingleUpdateInstruction(table1,  getValue("C"), "bla"));
        t2.addInstruction(new ReadInstruction(table1,  getValue("H")));

        Transaction t3 = new Transaction();
        t3.addInstruction(new SingleUpdateInstruction(table1,  getValue("D"), "bla"));
        t3.addInstruction(new ReadInstruction(table1,  getValue("E")));
        t3.addInstruction(new ReadInstruction(table1,  getValue("B")));

        Transaction t4 = new Transaction();
        t4.addInstruction(new ReadInstruction(table1,  getValue("F")));
        t4.addInstruction(new ReadInstruction(table1,  getValue("G")));
        t4.addInstruction(new ReadInstruction(table1,  getValue("A")));

        Transaction t5 = new Transaction();
        t5.addInstruction(new SingleUpdateInstruction(table1,  getValue("B"), "bla"));
        t5.addInstruction(new SingleUpdateInstruction(table1,  getValue("F"), "bla"));
        t5.addInstruction(new ReadInstruction(table1,  getValue("G")));
         */
        /*Transaction t1 = new Transaction();
        t1.addInstruction(new ReadInstruction(table1,  getValue("A")));
        t1.addInstruction(new ReadInstruction(table1,  getValue("B")));

        Transaction t2 = new Transaction();
        t2.addInstruction(new SingleUpdateInstruction(table1,  getValue("A"), "as"));
        t2.addInstruction(new ReadInstruction(table1,  getValue("C")));

        Transaction t3 = new Transaction();
        t3.addInstruction(new SingleUpdateInstruction(table1,  getValue("B"), "ab"));
        t3.addInstruction(new SingleUpdateInstruction(table1,  getValue("C"), "ac"));
         */
        SimulatedIterations simulation = new SimulatedIterations();
        simulation.addTransaction(t1);
        simulation.addTransaction(t2);
        //simulation.addTransaction(t3);
        //simulation.addTransaction(t4);
        //simulation.addTransaction(t5);

        simulation.run(100, false, manager);

    }

    ConcurrencyManager manager;
    public ConcurrencyManager getManager(boolean recreateManager) throws Exception{
        if (recreateManager)
            return new DeadlockDetectionConcurrencyManager();
        if (manager==null){
            manager = new DeadlockDetectionConcurrencyManager();
        }
        return manager;
        
    }

    public static void main(String[] args) {
        Main m = new Main();
        try {
                boolean recreateManager = false;
            
                System.out.println("TESTE 1");
                m.test1(m.getManager(recreateManager));
            
                System.out.println("TESTE 2");
                m.test2(m.getManager(recreateManager));
            
                System.out.println("TESTE 3");
                m.test3(m.getManager(recreateManager));
            
                System.out.println("TESTE 4");
                m.test4(m.getManager(recreateManager));
            
                System.out.println("TESTE 5");
                m.test5(m.getManager(recreateManager));
            
                System.out.println("TESTE 6");
                m.test6(m.getManager(recreateManager));
            
                System.out.println("TESTE 7");
                m.test7(m.getManager(recreateManager));
            
                System.out.println("TESTE 8");
                m.test8(m.getManager(recreateManager));
            
                System.out.println("TESTE 9");
                m.test9(m.getManager(recreateManager));
            
                System.out.println("TESTE 10");
                m.test10(m.getManager(recreateManager));
            

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
