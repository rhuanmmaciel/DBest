/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.sort.external;


import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.query.binaryop.join.MergeJoin;
import ibd.query.binaryop.join.NestedLoopJoin;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.sourceop.FullTableScan;
import ibd.table.Params;
import ibd.table.Table;
import static ibd.table.Utils.createTable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class Main {

    private void createSortedBucket(Table table, String name) throws Exception {

        Operation scan1 = new FullTableScan("t1", table);

        OutputBucket out1 = new SortedOutputBucket("c:\\teste\\ibd\\sort", name, 10000, 0);
        Iterator<Tuple> tuples = scan1.run();
        int amount = 0;
        while (tuples.hasNext()) {
            Tuple t = tuples.next();
            out1.addTuple(t);
            amount++;
            //System.out.println(t);
        }
        System.out.println(amount);
        out1.saveBucket();
    }

    private void readBucket(String name) throws Exception {
        FileInputBucket in = new FileInputBucket("c:\\teste\\ibd\\sort", name, 10000);
        System.out.println("start reading bucket: "+name);
        int amount = 0;
        while (in.hasNext()) {
            Tuple t = in.next();
            System.out.println(t.toString());
            amount++;
        }
        System.out.println("amount: "+amount);
        System.out.println("finished reading bucket: "+name);
        
    }

    public void test1() throws Exception {

        Table table1 = createTable("c:\\teste\\ibd", "t1", Table.DEFULT_PAGE_SIZE, 1000, false, 2, 50);
        createSortedBucket(table1, "bucket1");

        Table table2 = createTable("c:\\teste\\ibd\\sort", "t2", Table.DEFULT_PAGE_SIZE, 1000, false, 3, 50);
        createSortedBucket(table2, "bucket2");

        FileInputBucket in1 = new FileInputBucket("c:\\teste\\ibd\\sort", "bucket1", 10);
        FileInputBucket in2 = new FileInputBucket("c:\\teste\\ibd\\sort", "bucket2", 10);
        OutputBucket out = new OutputBucket("c:\\teste\\ibd\\sort", "out", 10);

        ExternalMergeSort merge = new ExternalMergeSort(10000000, 10000000);
        merge.merge(in1, in2, out, 0);
        
        readBucket("out");

    }
    
    public void test1_1() throws Exception {

        Table table1 = createTable("c:\\teste\\ibd", "t1", Table.DEFULT_PAGE_SIZE, 500, false, 1, 50);
        createSortedBucket(table1, "bucket1");

        Table table2 = createTable("c:\\teste\\ibd\\sort", "t2", Table.DEFULT_PAGE_SIZE, 250000, false, 500, 50);
        createSortedBucket(table2, "bucket2");

        FileInputBucket in1 = new FileInputBucket("c:\\teste\\ibd\\sort", "bucket1", 10);
        FileInputBucket in2 = new FileInputBucket("c:\\teste\\ibd\\sort", "bucket2", 10);
        OutputBucket out = new OutputBucket("c:\\teste\\ibd\\sort", "out", 10);

        ExternalMergeSort merge = new ExternalMergeSort(10000000, 10000000);
        merge.merge(in1, in2, out, 0);
        
        readBucket("out");

    }

    public void test3(int size) throws Exception {
        Table table1 = createTable("c:\\teste\\ibd", "t1", Table.DEFULT_PAGE_SIZE, 1000, true, 2, 50);
        Table table2 = createTable("c:\\teste\\ibd", "t2", Table.DEFULT_PAGE_SIZE, 1000, true, 3, 50);
        Operation scan1 = new FullTableScan("t1", table1);
        ExternalSort s1 = new ExternalSort(scan1, "t1", 30, size);
        Operation scan2 = new FullTableScan("t2", table2);
        ExternalSort s2 = new ExternalSort(scan2, "t2", 30, size);
        Operation join1 = new MergeJoin(s1, s2, null);

        Params.BLOCKS_LOADED = 0;
        int amount = 0;
        Operation query = join1;
        Iterator<Tuple> tuples = scan1.run();
        while (tuples.hasNext()) {
            Tuple t = tuples.next();
            System.out.println(t.toString());
            amount++;

        }
        System.out.println("Number of tuples " + amount);
        
        
    }

    public void test2(int size) throws Exception {
        Table table1 = createTable("c:\\teste\\ibd", "t1", Table.DEFULT_PAGE_SIZE, 1000, true, 2, 50);
        Table table2 = createTable("c:\\teste\\ibd", "t2", Table.DEFULT_PAGE_SIZE, 1000, true, 3, 50);
        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new FullTableScan("t2", table2);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);

        ExternalSort s1 = new ExternalSort(join1, "t1", 30, size);
        Params.BLOCKS_LOADED = 0;
        int amount = 0;
        Operation query = s1;
        Iterator<Tuple> tuples = query.run();
        while (tuples.hasNext()) {
            Tuple t = tuples.next();
            System.out.println(t.toString());
            amount++;

        }
        System.out.println("Number of tuples " + amount);
    }
    
    public static void main(String[] args) {
        try {
            Main m = new Main();
            //é necessário rodar os testes um de cada vez
            //m.test1();
            //m.test1_1();
            //m.test2(30);
            m.test3(30);

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
