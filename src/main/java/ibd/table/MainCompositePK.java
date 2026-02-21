/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table;

import ibd.table.prototype.BasicDataRow;
import java.util.List;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.column.IntegerColumn;

/**
 *
 * @author Sergio
 */
public class MainCompositePK {

    public void addAluno(Table table, int id1, int id2) throws Exception {
        BasicDataRow rowData = new BasicDataRow();
        rowData.setInt("id1", id1);
        rowData.setInt("id2", id2);
        table.addRecord(rowData, true);
        
    }

    public void getAll(Table table) throws Exception{
        List<LinkedDataRow> list = table.getAllRecords();
        for (DataRow rowData : list) {
            System.out.println(rowData.toString());
        }
    }
    
    public void getId1(Table table, Integer id1) throws Exception{
        BasicDataRow query = new BasicDataRow();
        query.setInt("id1", id1);
        List<LinkedDataRow> list = table.getRecords(query);
        for (DataRow rowData : list) {
            System.out.println(rowData.toString());
        }
    }
    
    public void getRows(Table table, String col, Comparable comp, int type) throws Exception{
        List<LinkedDataRow> list = table.getRecords(col, comp, type);
        for (DataRow rowData : list) {
            System.out.println(rowData.toString());
        }
    }
    
    public Prototype createSchema(){
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("id1", true));
        pt.addColumn(new IntegerColumn("id2", true));
        return pt;
    }

    public static void main(String[] args) throws Exception {

        MainCompositePK main = new MainCompositePK();

        Prototype schema = main.createSchema();
            
        Table table = Directory.getTable("c:\\teste\\ibd", "composite",schema, 99999,  4096, true);

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
               main.addAluno(table, i, j);
            }
        }
        
        
        //main.addAluno(table, 2, 5);
        
        //table.flushDB();
        //main.getSalario(table, "financas");
        //main.getId1(table, 3);
        //main.getAll(table);
        
        main.getRows(table, "id1",5, ComparisonTypes.GREATER_THAN);
        


    }

}
