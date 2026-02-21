/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table;

import java.util.List;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.LinkedDataRow;

/**
 *
 * @author Sergio
 */
public class MainDBest {

    /**
     * Cretes the schema of the person table
     * @return
     */
    public Prototype createSchema(){
        Prototype pt = new Prototype();
        return pt;
    }
    
    /**
     * Adds a person row into a table
     * @param table the target table
     * @param id the id of the person
     * @param name the name of the person
     * @param salary the salary of the person
     * @param dept the departament of the person
     * @throws Exception
     */
    public void addPerson(Table table, int id, String name, int salary, String dept) throws Exception {
    }

    /**
     * Find the average salary considering people that belong to a specific department
     * @param table the target table
     * @param dept the department
     * @return the average salary
     * @throws Exception
     */
    public double getAvgSalary(Table table, String dept) throws Exception{
        return -1;
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
    
    

    public static void main(String[] args) throws Exception {

        MainDBest main = new MainDBest();

        Table table = Directory.getTable("C:\\Users\\ferna\\Documents\\dbest\\oficina\\oficina-profs", "xxx.dat",null, 99999,  4096, false);

        main.getAll(table);

    }

}
