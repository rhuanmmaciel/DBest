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
import ibd.table.prototype.column.StringColumn;

/**
 *
 * @author Sergio
 */
public class MainFuncSolved {

    /**
     * Adds a person row into a table
     * @param table the target table
     * @param id the id of the person
     * @param name the name of the person
     * @param salary the salary of the person
     * @param dept the departament of the person
     * @throws Exception
     */
    public void addFunc(Table table, int id, String name, int salary, String dept) throws Exception {
        BasicDataRow rowData = new BasicDataRow();
        rowData.setInt("id", id);
        rowData.setInt("salary", salary);
        rowData.setString("name", name);
        rowData.setString("dept", dept);
        table.addRecord(rowData, true);
        
    }

    /**
     * Print information of all rows from the table
     * @param table the target table
     * @throws Exception
     */
    public void getAll(Table table) throws Exception{
        List<LinkedDataRow> list = table.getAllRecords();
        for (DataRow rowData : list) {
            System.out.println(rowData.toString());
        }
    }
    
    /**
     * Print information from people of a specific department
     * @param table the target table
     * @param dept the department 
     * @throws Exception
     */
    public void getSalary(Table table, String dept) throws Exception{
        List<LinkedDataRow> list = table.getRecords("dept", dept, ComparisonTypes.EQUAL);
        for (DataRow rowData : list) {
            System.out.println(rowData.toString());
        }
    }
    
    /**
     * Find the average salary considering people that belong to a specific department
     * @param table the target table
     * @param dept the department
     * @return the average salary
     * @throws Exception
     */
    public double getAvgSalary(Table table, String dept) throws Exception{
        List<LinkedDataRow> list = table.getRecords("dept", dept, ComparisonTypes.EQUAL);
        double media = 0;
        int count = 0;
        for (DataRow rowData : list) {
            int salario = rowData.getInt("salary");
            media = media + salario;
            count++;
        }
        return media /count;
    }
    
    /**
     * Prints the average salary of people from a specific department
     * @param table the target table
     * @param dept the department
     * @throws Exception
     */
    public void printAvgSalary(Table table, String dept) throws Exception{
        double avg = getAvgSalary(table, dept);
        System.out.println("The average salary of dept "+ dept +": "+avg);
    }
    
    /**
     * Cretes the schema of the person table
     * @return
     */
    public Prototype createSchema(){
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("id", true));
        pt.addColumn(new StringColumn("name"));
        pt.addColumn(new IntegerColumn("salary"));
        pt.addColumn(new StringColumn("dept"));
        return pt;
    }

    public static void main(String[] args) throws Exception {

        MainFuncSolved main = new MainFuncSolved();

        Prototype schema = main.createSchema();
        
        LinkedDataRow row = new LinkedDataRow(schema, false);
        
        if (1==1) {
        System.out.println(row);return;
        }
            
        Table table = Directory.getTable("c:\\teste\\ibd", "person",schema, 99999,  4096, true);
        
        main.addFunc(table, 1, "Ana", 2000, "finances");
        main.addFunc(table, 2, "Joao", 1500, "sells");
        main.addFunc(table, 3, "Miguel", 4500, "sells");
        main.addFunc(table, 4, "Carlos", 3000, "finances");
        
         table.flushDB();
        //main.getSalario(table, "finances");
        //main.getAll(table);
        main.printAvgSalary(table, "finances");

       

    }

}
