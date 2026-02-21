/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join;

import ibd.table.Params;
import ibd.query.Operation;
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
public class Main {

    Table funcTable = null;
    Table phoneTable = null;
    Table addressTable = null;
    Table socialInfoTable = null;

    private Table createFuncTable(String folder, String name, int pageSize, boolean override) throws Exception {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("id", true));
        prototype.addColumn(new StringColumn("name"));
        return Directory.getTable(folder, name, prototype, 0, pageSize, override);
    }

    private Table createPhoneTable(String folder, String name, int pageSize, boolean override) throws Exception {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("id", true));
        prototype.addColumn(new StringColumn("phone"));
        return Directory.getTable(folder, name, prototype, 0, pageSize, override);
    }

    private Table createAddressTable(String folder, String name, int pageSize, boolean override) throws Exception {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("id", true));
        prototype.addColumn(new StringColumn("address"));
        return Directory.getTable(folder, name, prototype, 0, pageSize, override);
    }

    private Table createSocialInfoTable(String folder, String name, int pageSize, boolean override) throws Exception {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("id", true));
        prototype.addColumn(new StringColumn("socialInfo"));
        return Directory.getTable(folder, name, prototype, 0, pageSize, override);
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

    public void prepareTables(String folder, int pageSize, int size, boolean override) throws Exception {

        funcTable = createFuncTable(folder, "func", pageSize, override);
        phoneTable = createPhoneTable(folder, "phone", pageSize, override);
        addressTable = createAddressTable(folder, "address", pageSize, override);
        socialInfoTable = createSocialInfoTable(folder, "socialInfo", pageSize, override);

        if (override) {
            String names[] = DataFaker.generateNames(size, size);

            for (int i = 0; i < size; i++) {
                //String text = name + "(" + array1[i] + ")";
                String text = names[i];
                //text = Utils.pad(text, 40);
                addFunc(funcTable, i, text);
                addPhone(phoneTable, i, text + "'s phone");
                addAddress(addressTable, i, text + "'s address");
                addSocialInfo(socialInfoTable, i, "@" + text);

                //table.addRecord(array1[i], String.valueOf(array1[i]));
                //table.addRecord(array1[i], "0");
            }
            funcTable.flushDB();
            addressTable.flushDB();
            phoneTable.flushDB();
            socialInfoTable.flushDB();
        }
    }

    public Operation createQuery() throws Exception {

        Operation funcScan = new FullTableScan("t1", funcTable);
        //PKFilter scan1 = new PKFilter(scan, "t1", ComparisonTypes.LOWER_THAN, 2L);
        Operation addressScan = new FullTableScan("t2", addressTable);
        Operation phoneScan = new FullTableScan("t3", phoneTable);
        Operation socialInfoScan = new FullTableScan("t4", socialInfoTable);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");

        Operation join = null;
        //construa aqui as junções 

        Operation join3 = new BlockNestedLoopJoin(funcScan, addressScan, terms, 100);
        Operation join2 = new BlockNestedLoopJoin(join3, phoneScan, terms, 200);
        join = new BlockNestedLoopJoin(join2, socialInfoScan, terms, 200);

        return join;

    }

    public static void main(String[] args) {
        try {
            Main m = new Main();

            boolean override = false;
            m.prepareTables("c:\\teste\\ibd", 4096, 50, override);
            Operation op = m.createQuery();

            Params.BLOCKS_LOADED = 0;
            Params.BLOCKS_SAVED = 0;

            Iterator<Tuple> tuples = op.run();
            while (tuples.hasNext()) {
                Tuple r = tuples.next();
                System.out.println(r);
            }

            System.out.println("blocks loaded during reorganization " + Params.BLOCKS_LOADED);
            System.out.println("blocks saved during reorganization " + Params.BLOCKS_SAVED);

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
