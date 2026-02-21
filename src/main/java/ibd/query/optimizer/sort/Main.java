/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.optimizer.sort;

import ibd.table.Directory;
import ibd.query.binaryop.join.NestedLoopJoin;
import ibd.table.Params;
import static ibd.table.Utils.createTable;

import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.query.Utils;
import ibd.query.binaryop.join.MergeJoin;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.lookup.ColumnElement;
import ibd.query.lookup.LiteralElement;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.sourceop.FullTableScan;
import ibd.query.unaryop.filter.Filter;
import ibd.query.unaryop.sort.external.ExternalSort;
import static ibd.table.ComparisonTypes.EQUAL;
import ibd.table.Table;
import java.util.Iterator;

/**
 *
 * @author Sergio
 */
public class Main {

    
    boolean filter = false;
    

    public void testMergeSortOptimization(QueryOptimizer opt, Operation query, boolean showTree, boolean runQuery) throws Exception {

        runQuery = true;
        
        Params.BLOCKS_LOADED = 0;
        Params.BLOCKS_SAVED = 0;
        
        System.out.println("BEFORE");
        if (showTree) {
            System.out.println("\nExecution Tree");
            Utils.toString(query, 0);
            System.out.println("");
        }

        if (runQuery) {
            System.out.println("\nRecords");
            Params.BLOCKS_LOADED = 0;
            Params.BLOCKS_SAVED = 0;
            Iterator<Tuple> tuples = query.run();
            while (tuples.hasNext()) {
                Tuple r = (Tuple) tuples.next();
                System.out.println(r);
            }
            query.close();

            System.out.println("\nStats");
            System.out.println("blocks loaded during reorganization " + Params.BLOCKS_LOADED);
            System.out.println("blocks saved during reorganization " + Params.BLOCKS_SAVED);
        }

        query = opt.transform(query);

        
        System.out.println("\nAFTER");
        if (showTree) {
            System.out.println("\nExecution Tree");
            Utils.toString(query, 0);
            System.out.println("");
        }

        if (runQuery) {
            System.out.println("\nRecords");
            Params.BLOCKS_LOADED = 0;
            Params.BLOCKS_SAVED = 0;
            Iterator<Tuple> tuples = query.run();
            while (tuples.hasNext()) {
                Tuple r = (Tuple) tuples.next();
                System.out.println(r);
            }
            query.close();

            System.out.println("\nStats");
            System.out.println("blocks loaded during reorganization " + Params.BLOCKS_LOADED);
            System.out.println("blocks saved during reorganization " + Params.BLOCKS_SAVED);
        }
    }

    private Operation createQuery1() throws Exception{
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new FullTableScan("t1", table1);
        Operation sort = new ExternalSort(scan1);
        
        if (filter){
            SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"),  EQUAL,new LiteralElement(20));
            sort = new Filter(sort, filter_1);
        }
        return sort;
    }
    
    private Operation createQuery2() throws Exception{
        
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1.ibd",null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2.ibd",null, 999, Table.DEFULT_PAGE_SIZE,false);

        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new FullTableScan("t2", table2);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);

        if (filter){
            SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"), EQUAL, new LiteralElement(20));
            join1 = new Filter(join1, filter_1);
        }
        return join1;
    }
    
    
    private Operation createQuery3() throws Exception{
        
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new FullTableScan("t2", table2);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join1 = new NestedLoopJoin(new ExternalSort(scan1), scan2, terms);

        if (filter){
            SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"),  EQUAL,new LiteralElement(20));
            join1 = new Filter(join1, filter_1);
        }
        return join1;
    }
    
    
    private Operation createQuery4() throws Exception{
        
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new FullTableScan("t2", table2);
        Operation scan3 = new FullTableScan("t3", table3);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);
        Operation join2 = new NestedLoopJoin(new ExternalSort(join1), new ExternalSort(scan3), terms);

        if (filter){
            SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"), EQUAL,new LiteralElement(20));
            join2 = new Filter(join2, filter_1);
        }
        return join2;
    }
    
    
    private Operation createQuery5() throws Exception{
        
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new FullTableScan("t2", table2);
        Operation scan3 = new FullTableScan("t3", table3);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join1 = new MergeJoin(new ExternalSort(scan1), new ExternalSort(scan2), terms);
        Operation join2 = new NestedLoopJoin(join1, new ExternalSort(scan3), terms);

        if (filter){
            SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"), EQUAL,new LiteralElement(20));
            join2 = new Filter(join2, filter_1);
        }
        return join2;
    }
    
    private Operation createQuery6() throws Exception{
        
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new FullTableScan("t2", table2);
        Operation scan3 = new FullTableScan("t3", table3);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);
        Operation join2 = new NestedLoopJoin(join1, new ExternalSort(scan3), terms);

        if (filter){
            SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"), EQUAL,new LiteralElement(20));
            join2 = new Filter(join2, filter_1);
        }
        return join2;
    }
    
    private Operation createQuery7() throws Exception{
        
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new FullTableScan("t2", table2);
        Operation scan3 = new FullTableScan("t3", table3);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join1 = new MergeJoin(new ExternalSort(scan1), new ExternalSort(scan2), terms);
        Operation join2 = new NestedLoopJoin(join1, scan3, terms);

        if (filter){
            SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"), EQUAL,new LiteralElement(20));
            join2 = new Filter(join2, filter_1);
        }
        return join2;
    }
    
    private Operation createQuery8() throws Exception{
        
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new FullTableScan("t2", table2);
        Operation scan3 = new FullTableScan("t3", table3);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);
        Operation join2 = new NestedLoopJoin(join1, scan3, terms);

        if (filter){
            SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"),EQUAL,new LiteralElement(20));
            join2 = new Filter(join2,filter_1);
        }
        return join2;
    }
    
    private Operation createQuery9() throws Exception{
        
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new FullTableScan("t2", table2);
        Operation scan3 = new FullTableScan("t3", table3);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);
        Operation join2 = new MergeJoin(new ExternalSort(join1), new ExternalSort(scan3), terms);

        if (filter){
            SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"),  EQUAL,new LiteralElement(20));
            join2 = new Filter(join2, filter_1);
        }
        return join2;
    }
    
    private Operation createQuery10() throws Exception{
        
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3.ibd",null, 999,Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new FullTableScan("t2", table2);
        Operation scan3 = new FullTableScan("t3", table3);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join1 = new NestedLoopJoin(scan1, new ExternalSort(scan2), terms);
        Operation join2 = new NestedLoopJoin(join1, scan3, terms);

        if (filter){
            SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"),  EQUAL,new LiteralElement(20));
            join2 = new Filter(join2, filter_1);
        }
        return join2;
    }
    
    public static void main(String[] args) {
        try {
            Main m = new Main();
            QueryOptimizer opt = new SortQueryOptimizer();
            
            createTable("c:\\teste\\ibd", "t1.ibd",4096, 100, false, 1, 50);
            createTable("c:\\teste\\ibd", "t2.ibd",4096, 100, false, 1, 50);
            createTable("c:\\teste\\ibd", "t3.ibd",4096, 100, false, 1, 50);
            
            m.filter = false;
            
            //coloque o código aqui
            System.out.println("\n**********TESTE 1");
            m.testMergeSortOptimization(opt,m.createQuery1(), true, false);
            
            System.out.println("\n**********TESTE 2");
            m.testMergeSortOptimization(opt,m.createQuery2(), true, false);

            System.out.println("\n**********TESTE 3");
            m.testMergeSortOptimization(opt,m.createQuery3(), true, false);
            
            System.out.println("\n**********TESTE 4");
            m.testMergeSortOptimization(opt,m.createQuery4(), true, false);
            
            System.out.println("\n**********TESTE 5");
            m.testMergeSortOptimization(opt,m.createQuery5(), true, false);
            
            System.out.println("\n**********TESTE 6");
            m.testMergeSortOptimization(opt,m.createQuery6(), true, false);
            
            System.out.println("\n**********TESTE 7");
            m.testMergeSortOptimization(opt,m.createQuery7(), true, false);
            
            System.out.println("\n**********TESTE 8");
            m.testMergeSortOptimization(opt,m.createQuery8(), true, false);
            
            System.out.println("\n**********TESTE 9");
            m.testMergeSortOptimization(opt,m.createQuery9(), true, false);
            
            System.out.println("\n**********TESTE 10");
            m.testMergeSortOptimization(opt,m.createQuery10(), true, false);

        } catch (Exception ex) {
            
        }
    }
}
