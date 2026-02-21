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
import ibd.query.sourceop.IndexScan;
import ibd.query.unaryop.filter.Filter;
import static ibd.table.ComparisonTypes.EQUAL;
import ibd.table.Table;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class Main {

    

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
    
        
    private Operation createQuery1() throws Exception{
        
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1", null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2", null, 999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new IndexScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);
        
        SingleColumnLookupFilter filter_ = new SingleColumnLookupFilter(new ColumnElement("id"),  EQUAL, new LiteralElement(6L));

        Operation filter1 = new Filter(join1,  filter_);

        return filter1;
    }
    
    private Operation createQuery2() throws Exception{
        
        Table table1 = Directory.getTable("c:\\teste\\ibd", "t1", null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable("c:\\teste\\ibd", "t2", null, 999, Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable("c:\\teste\\ibd", "t3", null, 999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new IndexScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);
        Operation scan3 = new IndexScan("t3", table3);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        Operation diff = new NestedLoopJoin(scan1, scan2, terms);
        Operation join2 = new NestedLoopJoin(diff, scan3, terms);

        Operation filter1 = new Filter(join2, null);

        return filter1;
    }
    
    
    public static void main(String[] args) {
        try {
            Main m = new Main();
            QueryOptimizer opt = null;
            
            //uncomment these lines the first time you execute
            //createTable("c:\\teste\\ibd", "t1", Table.DEFULT_PAGE_SIZE, 100, false, 2,50);
            //createTable("c:\\teste\\ibd", "t2", Table.DEFULT_PAGE_SIZE, 100, false, 3,50);
            //createTable("c:\\teste\\ibd", "t3", Table.DEFULT_PAGE_SIZE, 100, false, 4,50);
            //createTable("c:\\teste\\ibd", "t4", Table.DEFULT_PAGE_SIZE, 100, false, 1,50);
            
            
            System.out.println("**********TESTE 1");
            m.testOptimization(opt,m.createQuery1(), true, true);
            
            System.out.println("**********TESTE 2");
            m.testOptimization(opt,m.createQuery2(), true, true);


        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
