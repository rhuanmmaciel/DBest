/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query;

import dsl.DslParser;
import ibd.query.binaryop.BinaryOperation;
import ibd.query.lookup.LiteralElement;
import ibd.query.lookup.LookupFilter;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.unaryop.UnaryOperation;
import ibd.query.unaryop.filter.Filter;
import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class ERBD {

    public static int pageSize = 4096;
    public static int cacheSize = 99999999;

    //public static final String folder = "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\";
    public static final String folder = "C:\\Users\\ferna\\Documents\\dbest\\oficina\\testeConversao\\";
    //public static final String folder = "./";

    

    /* *********** QUERIES CREATION ****************/
    public Operation createQuery(boolean hash) {
        try {
            
            //return join2;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
    
    public void changeFilter(Operation op, Integer value){
        if (op instanceof Filter f){
            LookupFilter lf = f.getFilters();
            if (lf instanceof SingleColumnLookupFilter sclf){
                sclf.setSecondElement(new LiteralElement(value));
            }
        }
        for (Operation child : op.getChildOperations()) {
            changeFilter(child, value);
        }
        
        
    }
    
    public void runQuery(Operation query, int tuplesToRead) throws Exception {
        TuplesPrinter printer = new TuplesPrinter();
        printer.execQueryAndPrint(query, tuplesToRead);
    }

    public void execQuery(Operation query) throws Exception {
        Iterator<Tuple> tuples = query.run();
        while (tuples.hasNext()) {
            tuples.next();
        }
    }

    public void evaluateQuery(int times, Supplier<Operation> supplier) throws Exception {
        List<Operation> warmUpQueries = new ArrayList();
        List<Operation> testQueries = new ArrayList();
        for (int i = 0; i < times; i++) {
            Operation warmUpQuery = supplier.get();
            warmUpQueries.add(warmUpQuery);
            Operation testQuery = supplier.get();
            testQueries.add(testQuery);
        }


        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            execQuery(warmUpQueries.get(i));
        }
        long end = System.currentTimeMillis();
        System.out.println("warm up time: " + (end - start) / times);

        Stats.passes = 0;
        ibd.query.QueryStats.MEMORY_USED = 0;
        engine.info.Parameters.BLOCKS_ACCESSED = 0;
        
        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            execQuery(testQueries.get(i));
        }
        end = System.currentTimeMillis();
        System.out.println("measured time: " + (end - start) / times);
        System.out.println("MEMORY USED: " + ibd.query.QueryStats.MEMORY_USED/times);
        System.out.println("BLOCKS ACCESSED: "+engine.info.Parameters.BLOCKS_ACCESSED/times);
        //System.out.println("passes " + Stats.passes);
        System.out.println("-----------------------");
    }
    
    public static void main(String[] args)  {

        ERBD main = new ERBD();

        Operation op;
        try {
            op = DslParser.readQuery(new File("C:\\Users\\ferna\\Documents\\erbd 2025\\semi join\\movieHashJoin.txt"));
            main.changeFilter(op, 1940);
            //main.runQuery(op, -1);
            
            main.evaluateQuery(10, ()->op);
        } catch (Exception ex) {
            Logger.getLogger(ERBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }
}
