/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index;

import ibd.table.ComparisonTypes;
import ibd.table.Directory;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author Sergio
 */
public class Main {

    
    public static void addRecord(Table table, int pk, String content) throws Exception{
        BasicDataRow row = new BasicDataRow();
        row.setInt("id", pk);
        row.setString("nome", content);
        table.addRecord(row, true);
    } 
    
    public static void removeRecord(Table table, int pk) throws Exception{
        BasicDataRow row = new BasicDataRow();
        row.setInt("id", pk);
        table.removeRecord(row);
    } 
    
    public static DataRow getRecord(Table table, int pk) throws Exception{
        BasicDataRow row = new BasicDataRow();
        row.setInt("id", pk);
        return table.getRecord(row);
    } 
    
    public static void main(String[] args) {
        try {
            Main m = new Main();
            Table table = Directory.getTable("c:\\teste\\ibd", "table", null, 99999, Table.DEFULT_PAGE_SIZE, true);

            Main.addRecord(table, 9, "reg 9");
            Main.addRecord(table,7, "reg 7");
            Main.addRecord(table,8, "reg 8");
            Main.addRecord(table,5, "reg 5");
            Main.addRecord(table,6, "reg 6");
            Main.addRecord(table,3, "reg 3");
            Main.addRecord(table,4, "reg 4");
            Main.addRecord(table,1, "reg 1");
            Main.removeRecord(table, 5);
            table.flushDB();
            DataRow rec = Main.getRecord(table, 3);
            if (rec == null) {
                System.out.println("não tem");
            } else {
                System.out.println(rec.toString());
            }
            List<? extends DataRow> recs;
            System.out.println(">=3 ************");
            recs = table.getRecords("id", 3, ComparisonTypes.GREATER_EQUAL_THAN);
            for (int i = 0; i < recs.size(); i++) {
                rec = recs.get(i);
                System.out.println("found " + rec.toString());
            }
            System.out.println(">3 ************");
            recs = table.getRecords("id",3, ComparisonTypes.GREATER_THAN);
            for (int i = 0; i < recs.size(); i++) {
                rec = recs.get(i);
                System.out.println("found " + rec.toString());
            }
            System.out.println("<=3 ************");
            recs = table.getRecords("id",3, ComparisonTypes.LOWER_EQUAL_THAN);
            for (int i = 0; i < recs.size(); i++) {
                rec = recs.get(i);
                System.out.println("found " + rec.toString());
            }
            System.out.println("<3 ************");
            recs = table.getRecords("id",3, ComparisonTypes.LOWER_THAN);
            for (int i = 0; i < recs.size(); i++) {
                rec = recs.get(i);
                System.out.println("found " + rec.toString());
            }
            System.out.println("diff 3************");
            recs = table.getRecords("id",3, ComparisonTypes.DIFF);
            for (int i = 0; i < recs.size(); i++) {
                rec = recs.get(i);
                System.out.println("found " + rec.toString());
            }
            System.out.println("equal 3************");
            recs = table.getRecords("id",3, ComparisonTypes.EQUAL);
            for (int i = 0; i < recs.size(); i++) {
                rec = recs.get(i);
                System.out.println("found " + rec.toString());
            }

            System.out.println(">=2 ************");
            recs = table.getRecords("id",2, ComparisonTypes.GREATER_EQUAL_THAN);
            for (int i = 0; i < recs.size(); i++) {
                rec = recs.get(i);
                System.out.println("found " + rec.toString());
            }
            System.out.println(">2 ************");
            recs = table.getRecords("id",2, ComparisonTypes.GREATER_THAN);
            for (int i = 0; i < recs.size(); i++) {
                rec = recs.get(i);
                System.out.println("found " + rec.toString());
            }
            System.out.println("<=2 ************");
            recs = table.getRecords("id",2, ComparisonTypes.LOWER_EQUAL_THAN);
            for (int i = 0; i < recs.size(); i++) {
                rec = recs.get(i);
                System.out.println("found " + rec.toString());
            }
            System.out.println("<2 ************");
            recs = table.getRecords("id",2, ComparisonTypes.LOWER_THAN);
            for (int i = 0; i < recs.size(); i++) {
                rec = recs.get(i);
                System.out.println("found " + rec.toString());
            }
            System.out.println("diff 2************");
            recs = table.getRecords("id",2, ComparisonTypes.DIFF);
            for (int i = 0; i < recs.size(); i++) {
                rec = recs.get(i);
                System.out.println("found " + rec.toString());
            }
            System.out.println("equal 2************");
            recs = table.getRecords("id",2, ComparisonTypes.EQUAL);
            for (int i = 0; i < recs.size(); i++) {
                rec = recs.get(i);
                System.out.println("found " + rec.toString());
            }

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
