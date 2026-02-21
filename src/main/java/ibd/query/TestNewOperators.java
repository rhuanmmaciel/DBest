/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query;

import dsl.DslParser;
import ibd.query.binaryop.join.HashInnerJoin;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.sourceop.IndexScan;
import ibd.query.unaryop.Explode;
import ibd.query.unaryop.HashIndex;
import ibd.table.Directory;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author Sergio
 */
public class TestNewOperators {

    public static int pageSize = 4096;
    public static int cacheSize = 99999999;

    //public static final String folder = "C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs\\";
    public static final String folder = "C:\\Users\\ferna\\Documents\\dbest\\oficina\\testeConversao\\";
    //public static final String folder = "./";

    //8192, 16384, 32768
    /* *********** SCHEMA CREATION ****************/
    public void createTable() throws Exception {
        Prototype schema = createSchema();

        Table table = Directory.getTable(folder, "test", schema, cacheSize, pageSize, true);
        addRecords(table);
        table.flushDB();

    }

    public Prototype createSchema() {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("id", true));
        pt.addColumn(new IntegerColumn("col1"));
        pt.addColumn(new StringColumn("col2", (short) 90));
        return pt;
    }


    /* *********** ADDING ROWS ****************/
    public void addRecords(Table table) throws Exception {
        
                BasicDataRow row = new BasicDataRow();
                row.setInt("id", 1);
                row.setInt("col1", 1000);
                row.setString("col2", "val1, val2, val3");
                table.addRecord(row, true);
                
                row = new BasicDataRow();
                row.setInt("id", 2);
                row.setInt("col1", 2000);
                row.setString("col2", "valll");
                table.addRecord(row, true);
                
                row = new BasicDataRow();
                row.setInt("id", 3);
                row.setInt("col1", 2000);
                row.setString("col2", "");
                table.addRecord(row, true);
                
                row = new BasicDataRow();
                row.setInt("id", 4);
                row.setInt("col1", 4000);
                row.setString("col2", ",,");
                table.addRecord(row, true);



        table.flushDB();
    }


    /* *********** QUERIES CREATION ****************/
    public Operation createTest1(boolean hash) {
        try {
            return DslParser.readQuery(new File("C:\\Users\\ferna\\Dropbox\\dbest\\query trees\\23 - union 4.txt"));
            //return join2;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
    
    public Operation createTest1() {
        try {

            Table table = Directory.getTable(folder, "test", null, cacheSize, pageSize, false);

            Operation scan = new IndexScan("test", table);
            Explode explode = new Explode(scan, "test.col2", ",");
            return explode;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
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


    public static void main(String[] args) throws Exception {

        TestNewOperators main = new TestNewOperators();
        main.createTable();
        Operation op = main.createTest1();
        main.runQuery(op, -1);
        
        
    }
}
