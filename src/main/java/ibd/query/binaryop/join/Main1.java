/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join;

import ibd.table.Params;
import ibd.query.Operation;
import ibd.query.TreePrinter;
import ibd.query.Tuple;
import ibd.query.sourceop.FullTableScan;
import ibd.table.DataFaker;
import ibd.table.Directory;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;

/**
 *
 * @author Sergio
 */
public class Main1 {

    Table funcTable = null;
    Table phoneTable = null;
    Table addressTable = null;
    Table socialInfoTable = null;
    
    int cacheSize = 0;

    private Table createFuncTable(String folder, String name, int pageSize) throws Exception {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("id", true));
        prototype.addColumn(new StringColumn("name"));
        return Directory.getTable(folder, name, prototype, cacheSize,  pageSize, true);
    }

    private Table createPhoneTable(String folder, String name, int pageSize) throws Exception {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("id", true));
        prototype.addColumn(new StringColumn("phone"));
        return Directory.getTable(folder, name, prototype, cacheSize, pageSize, true);
    }

    private Table createAddressTable(String folder, String name, int pageSize) throws Exception {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("id", true));
        prototype.addColumn(new StringColumn("address"));
        return Directory.getTable(folder, name, prototype, cacheSize, pageSize, true);
    }

    private Table createSocialInfoTable(String folder, String name, int pageSize) throws Exception {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("id", true));
        prototype.addColumn(new StringColumn("socialInfo"));
        return Directory.getTable(folder, name, prototype, cacheSize,  pageSize, true);
    }

    private void addFunc(Table funcTable, int id, String name) throws Exception {
        BasicDataRow row = new BasicDataRow();
        row.setInt("id", id);
        row.setString("name", name);
        funcTable.addRecord(row, true);
    }

    private void addPhone(Table phoneTable, int id, String phone) throws Exception {
        BasicDataRow row = new BasicDataRow();
        row.setInt("id", id);
        row.setString("phone", phone);
        phoneTable.addRecord(row, true);
    }

    private void addAddress(Table addressTable, int id, String address) throws Exception {
        BasicDataRow row = new BasicDataRow();
        row.setInt("id", id);
        row.setString("address", address);
        addressTable.addRecord(row, true);
    }

    private void addSocialInfo(Table socialInfoTable, int id, String socialInfo) throws Exception {
        BasicDataRow row = new BasicDataRow();
        row.setInt("id", id);
        row.setString("socialInfo", socialInfo);
        socialInfoTable.addRecord(row, true);
    }

    public void prepareTables(String folder, int pageSize, int size, boolean create) throws Exception {

        if (!create) {
            funcTable = Directory.getTable(folder, "func",null, cacheSize, pageSize, false);
            phoneTable = Directory.getTable(folder, "phone",null, cacheSize, pageSize, false);
            addressTable = Directory.getTable(folder, "address",null, cacheSize, pageSize, false);
            socialInfoTable = Directory.getTable(folder, "socialInfo",null, cacheSize, pageSize, false);
            return;
        }
        funcTable = createFuncTable(folder, "func", pageSize);
        phoneTable = createPhoneTable(folder, "phone", pageSize);
        addressTable = createAddressTable(folder, "address", pageSize);
        socialInfoTable = createSocialInfoTable(folder, "socialInfo", pageSize);

        String names[] = DataFaker.generateNames(size, size);

        for (int i = 0; i < size; i++) {
            //String text = name + "(" + array1[i] + ")";
            String text = names[i];
            //text = Utils.pad(text, 40);
            addFunc(funcTable, i, text);
            addPhone(phoneTable, i, text + "'s phone");
            if (i % 5 == 0) {
                addAddress(addressTable, i, text + "'s address");
            }
            if (i % 10 == 0) {
                addSocialInfo(socialInfoTable, i, "@" + text);
            }

            //table.addRecord(array1[i], String.valueOf(array1[i]));
            //table.addRecord(array1[i], "0");
        }
        funcTable.flushDB();
        addressTable.flushDB();
        phoneTable.flushDB();
        socialInfoTable.flushDB();
    }

    public Operation createQuery() throws Exception {

        Operation funcScan = new FullTableScan("t1", funcTable);
        //PKFilter scan1 = new PKFilter(scan, "t1", ComparisonTypes.LOWER_THAN, 2L);
        Operation addressScan = new FullTableScan("t2", addressTable);
        Operation phoneScan = new FullTableScan("t3", phoneTable);
        Operation socialInfoScan = new FullTableScan("t4", socialInfoTable);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        //balanced
        //blocks loaded during reorganization 1171629
        //blocks saved during reorganization 0
//        Operation join2 = new NestedLoopJoin(funcScan, addressScan, terms);
//        Operation join3 = new NestedLoopJoin(phoneScan, socialInfoScan, terms);
//        Operation join1 = new NestedLoopJoin(join2, join3, terms);

        //worst
//        Operation join3 = new NestedLoopJoin(funcScan, addressScan,terms);
//        Operation join2 = new NestedLoopJoin(phoneScan, join3,terms);
//        Operation join1 = new NestedLoopJoin(socialInfoScan, join2,terms);

        //best
        Operation join3 = new NestedLoopJoin(funcScan, addressScan,terms);
        Operation join2 = new NestedLoopJoin(join3, phoneScan,terms);
        Operation join1 = new NestedLoopJoin(join2, socialInfoScan,terms);

        //really best
//        Operation join3 = new NestedLoopJoin(socialInfoScan, addressScan,terms);
//        Operation join2 = new NestedLoopJoin(join3, funcScan,terms);
//        Operation join1 = new NestedLoopJoin(join2, phoneScan,terms);
        return join1;

    }

    public static void main(String[] args) {
        try {
            Main1 m = new Main1();

            //create and populate tables, if necessary
            m.prepareTables("c:\\teste\\ibd", 4096, 100, true);
            
            //create the query
            Operation op = m.createQuery();

            //print the query
            TreePrinter printer = new TreePrinter();
            printer.printTree(op);
            
            //execute the query
            Params.BLOCKS_LOADED = 0;
            Iterator<Tuple> tuples = op.run();
            while (tuples.hasNext()) {
                Tuple r = tuples.next();
                System.out.println(r);
            }

            System.out.println("blocks loaded " + Params.BLOCKS_LOADED);

        } catch (Exception ex) {
            Logger.getLogger(Main1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
