/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.optimizer;

import ibd.table.Directory;
import ibd.query.binaryop.join.NestedLoopJoin;
import ibd.table.Params;
import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.query.Utils;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.lookup.ColumnElement;
import ibd.query.lookup.LiteralElement;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.sourceop.FullTableScan;
import ibd.query.sourceop.IndexScan;
import ibd.query.unaryop.filter.Filter;
import static ibd.table.ComparisonTypes.*;
import ibd.table.Table;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class Main1 {

    public void testOptimization(QueryOptimizer opt, Operation query, boolean showTree, boolean runQuery) throws Exception {

        Params.BLOCKS_LOADED = 0;
        Params.BLOCKS_SAVED = 0;

        System.out.println("BEFORE");
        if (showTree) {
            Utils.toString(query, 0);
            System.out.println("");
        }

        if (runQuery) {
            System.out.println("Data");
            Params.BLOCKS_LOADED = 0;
            Params.BLOCKS_SAVED = 0;
            Iterator<Tuple> it = query.run();
            while (it.hasNext()) {
                Tuple r = (Tuple) it.next();
                System.out.println(r);
            }
            query.close();

            System.out.println("blocks loaded during reorganization " + Params.BLOCKS_LOADED);
            System.out.println("blocks saved during reorganization " + Params.BLOCKS_SAVED);
        }

        query = opt.optimize(query);

        System.out.println("AFTER");
        if (showTree) {
            Utils.toString(query, 0);
            System.out.println("");
        }

        if (runQuery) {
            System.out.println("Data");
            Params.BLOCKS_LOADED = 0;
            Params.BLOCKS_SAVED = 0;
            Iterator<Tuple> it = query.run();
            while (it.hasNext()) {
                Tuple r = (Tuple) it.next();
                System.out.println(r);
            }
            query.close();

            System.out.println("blocks loaded during reorganization " + Params.BLOCKS_LOADED);
            System.out.println("blocks saved during reorganization " + Params.BLOCKS_SAVED);
        }
        System.out.println("");
    }

    private Operation createQuery01() throws Exception {

        Table table1 = Directory.getTable( "c:\\teste\\ibd", "t1",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2",  null, 999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new IndexScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);

        SingleColumnLookupFilter filter_ = new SingleColumnLookupFilter(new ColumnElement("id"), EQUAL,new LiteralElement(6));
        
        Operation filter1 = new Filter(scan2, filter_);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        Operation join1 = new NestedLoopJoin(scan1, filter1, terms);

        return join1;
    }

    private Operation createQuery02() throws Exception {
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3",  null, 999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new IndexScan("t1", table1);
        Operation scan2 = new FullTableScan("t2", table2);
        Operation scan3 = new IndexScan("t3", table3);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);
        Operation join2 = new NestedLoopJoin(join1, scan3, terms);
        
        SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"), EQUAL,new LiteralElement(2));
        SingleColumnLookupFilter filter_2 = new SingleColumnLookupFilter(new ColumnElement("id"),  LOWER_EQUAL_THAN,new LiteralElement(30));

        Operation filter1 = new Filter(join2, filter_1);
        Operation filter2 = new Filter(filter1, filter_2);

        return filter2;
    }

    private Operation createQuery03() throws Exception {

        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2",  null, 999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new IndexScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);
        
        SingleColumnLookupFilter filter = new SingleColumnLookupFilter(new ColumnElement("id"), EQUAL,new LiteralElement(6));

        Operation filter1 = new Filter(join1, filter);

        return filter1;
    }

    private Operation createQuery04() throws Exception {

        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3",  null, 999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new IndexScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);
        Operation scan3 = new IndexScan("t3", table3);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);
        Operation join2 = new NestedLoopJoin(join1, scan3, terms);
        
        SingleColumnLookupFilter filter = new SingleColumnLookupFilter(new ColumnElement("id"),  EQUAL,new LiteralElement(60));

        Operation filter1 = new Filter(join2, filter);

        return filter1;
    }

    private Operation createQuery05() throws Exception {

        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2",  null, 999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new IndexScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);
        
        SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("nome"),  EQUAL,new LiteralElement("Alexandre"));
        SingleColumnLookupFilter filter_2 = new SingleColumnLookupFilter(new ColumnElement("id"),  EQUAL, new LiteralElement(20));
        SingleColumnLookupFilter filter_3 = new SingleColumnLookupFilter(new ColumnElement("nome"),  EQUAL,new LiteralElement("Alice"));

        Operation filter1 = new Filter(scan2, filter_1);
        Operation filter2 = new Filter(filter1, filter_2);
        Operation filter3 = new Filter(filter2, filter_3);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        Operation join1 = new NestedLoopJoin(scan1, filter3, terms);

        return join1;
    }

    private Operation createQuery06() throws Exception {
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table4 = Directory.getTable("c:\\teste\\ibd", "t4",  null, 999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new IndexScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);
        Operation scan3 = new IndexScan("t3", table3);
        Operation scan4 = new IndexScan("t4", table4);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);
        Operation join2 = new NestedLoopJoin(scan3, scan4, terms);

        Operation diff2 = new NestedLoopJoin(join1, join2, terms);
        
        SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"),  GREATER_EQUAL_THAN,new LiteralElement(10));
        SingleColumnLookupFilter filter_2 = new SingleColumnLookupFilter(new ColumnElement("id"), EQUAL,new LiteralElement(12));

        Operation filter1 = new Filter(diff2, filter_1);
        Operation filter2 = new Filter(filter1, filter_2);

        return filter2;
    }

    private Operation createQuery07() throws Exception {

        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2",  null, 999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new IndexScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);
        
        SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("nome"), EQUAL,new LiteralElement("Alexandre"));

        Operation filter1 = new Filter(scan2, filter_1);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        Operation join1 = new NestedLoopJoin(scan1, filter1, terms);

        return join1;
    }

    private Operation createQuery08() throws Exception {

        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2",  null, 999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new IndexScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);
        
        SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("nome"), EQUAL,new LiteralElement("Alexandre"));
        SingleColumnLookupFilter filter_2 = new SingleColumnLookupFilter(new ColumnElement("nome"),  EQUAL,new LiteralElement("Alice"));

        Operation filter1 = new Filter(scan2, filter_1);
        Operation filter2 = new Filter(filter1, filter_2);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        Operation join1 = new NestedLoopJoin(scan1, filter2, terms);

        return join1;
    }

    private Operation createQuery09() throws Exception {

        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3",  null, 999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new IndexScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);
        
        SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"), DIFF,new LiteralElement(20));
                
        Operation filter1 = new Filter(scan2, filter_1);

        Operation scan3 = new IndexScan("t3", table3);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        Operation join1 = new NestedLoopJoin(scan1, filter1, terms);

        Operation join2 = new NestedLoopJoin(join1, scan3, terms);

        return join2;
    }

    private Operation createQuery10() throws Exception {

        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2",  null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3",  null, 999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new IndexScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);

        Operation scan3 = new IndexScan("t3", table3);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);
        
        SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"), LOWER_THAN,new LiteralElement(20));
        SingleColumnLookupFilter filter_2 = new SingleColumnLookupFilter(new ColumnElement("id"), GREATER_THAN,new LiteralElement(20));
        SingleColumnLookupFilter filter_3 = new SingleColumnLookupFilter(new ColumnElement("id"),  LOWER_THAN,new LiteralElement(10));


        Operation filterX = new Filter(join1, filter_1);
        Operation filterY = new Filter(scan3, filter_2);
        
        Operation join2 = new NestedLoopJoin(filterX, filterY, terms);
        Operation filter1 = new Filter(join2, filter_3);

        return filter1;
    }

    public static void main(String[] args) {
        try {
            Main1 m = new Main1();
            QueryOptimizer opt = null;

            //coloque o código aqui
            System.out.println("**********TESTE 01 - Nada deve mudar");
            m.testOptimization(opt, m.createQuery01(), true, true);

            System.out.println("**********TESTE 02 - Nada deve mudar");
            m.testOptimization(opt, m.createQuery02(), true, true);

            System.out.println("**********TESTE 03 - Puxar pk filter para baixo");
            m.testOptimization(opt, m.createQuery03(), true, true);

            System.out.println("**********TESTE 04 - Puxar pk filter para baixo");
            m.testOptimization(opt, m.createQuery04(), true, true);

            System.out.println("**********TESTE 05 - Puxar pk filter para baixo");
            m.testOptimization(opt, m.createQuery05(), true, true);

            System.out.println("**********TESTE 06 - Puxar pk filter para baixo");
            m.testOptimization(opt, m.createQuery06(), true, true);

            System.out.println("**********TESTE 07 - Puxar filtros para cima");
            m.testOptimization(opt, m.createQuery07(), true, true);

            System.out.println("**********TESTE 08 - Puxar filtros para cim");
            m.testOptimization(opt, m.createQuery08(), true, true);

            System.out.println("**********TESTE 09 - Puxar filtros para cim");
            m.testOptimization(opt, m.createQuery09(), true, true);

            System.out.println("**********TESTE 10 - Puxar filtros para cim");
            m.testOptimization(opt, m.createQuery10(), true, true);

        } catch (Exception ex) {
            Logger.getLogger(Main1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
