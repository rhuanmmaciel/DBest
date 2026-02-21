/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.unaryop.aggregation.Aggregation;
import ibd.query.*;
import ibd.query.sourceop.FullTableScan;
import ibd.query.unaryop.aggregation.AggregationType;
import ibd.table.Params;
import ibd.table.DataFaker;
import ibd.table.Directory;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class MainAggregation {

    public Operation testGroupByQuery(Table table, String groupByColumn, String groupedColumn, int type) throws Exception {
        
        FullTableScan scan = new FullTableScan("t1", table);
        Operation groupBy = new Aggregation(scan, "g", groupByColumn, groupedColumn, type, false);
        return groupBy;

    }

    static public Table createPersonTable(String folder, String name, int pageSize, int size, boolean shuffled, int range, int cardinality) throws Exception {

        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("id", true));
        pt.addColumn(new StringColumn("nome"));
        pt.addColumn(new IntegerColumn("idade"));
        pt.addColumn(new StringColumn("cidade"));

        Table table = Directory.getTable(folder, name, pt, 999999, pageSize, true);

        //id values
        Integer[] array1 = new Integer[(int)size];
        for (int i = 0; i < array1.length; i++) {
            array1[i] = i ;
        }

        if (shuffled) {
            DataFaker.shuffleArray(array1);
        }

        //name values
        String names_[] = DataFaker.generateNames(cardinality, array1.length);

        //city values organized by the cities informed
        String cidades[] = DataFaker.generateStrings(new String[]{"Santa Maria", "Porto Alegre", "Sao Paulo"}, array1.length, true);

        //age values are organized in three groups: small, medium and large values.
        //it means Santa Maria is associated with small values, Porto Alegre with medium values and Sao Paulo with the larger values
        Integer idades[] = new Integer[array1.length];
        int groupSize = 1 + array1.length / 3;
        int startValue = 20;
        int gap = 20;
        int offset = 0;
        for (int i = 0; i < 3; i++) {
            Integer idades_[] = DataFaker.generateInts(20, groupSize, startValue, 2, true);
            System.arraycopy(idades_, 0, idades, offset, idades_.length);
            startValue += gap;
            offset += idades_.length;
            if (offset + groupSize > idades.length) {
                groupSize = idades.length - offset;
            }
        }

        //Rows are formed and the table is populated
        for (int i = 0; i < array1.length; i++) {
            //String text = name + "(" + array1[i] + ")";
            String text = names_[i];
            //text = Utils.pad(text, 40);
            BasicDataRow row = new BasicDataRow();
            row.setInt("id", array1[i]);
            row.setString("nome", text);
            row.setInt("idade", idades[i]);
            row.setString("cidade", cidades[i]);
            table.addRecord(row, true);

            //table.addRecord(array1[i], String.valueOf(array1[i]));
            //table.addRecord(array1[i], "0");
        }
        table.flushDB();
        return table;
    }

    public void run(Operation op) throws Exception {

        Params.BLOCKS_LOADED = 0;

        int count = 0;
        Iterator<Tuple> it = op.run();
        while (it.hasNext()) {
            Tuple r = it.next();
            System.out.println(r);
            count++;
        }
        op.close();

        System.out.println("number of records: " + count);
        System.out.println("blocks loaded " + Params.BLOCKS_LOADED);
    }

    public static void main(String[] args) {
        try {
            MainAggregation m = new MainAggregation();
            boolean recreate = false;
            Table table;
            
            //create the table.
            //change the recreate value to false after the first execution 
            //to prevent the table from being created with random values at each execution
            if (recreate)
                table = createPersonTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 10, false, 1, 50);
            else 
                table = Directory.getTable("c:\\teste\\ibd", "tab1",null, 9999, Table.DEFULT_PAGE_SIZE,  false);
        
            Operation op = m.testGroupByQuery(table, "cidade", "idade", AggregationType.COUNT);
            TreePrinter printer = new TreePrinter();
            printer.printTree(op);

            m.run(op);

        } catch (Exception ex) {
            Logger.getLogger(MainAggregation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
