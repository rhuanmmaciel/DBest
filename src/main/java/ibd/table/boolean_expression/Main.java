/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.boolean_expression;

import ibd.table.ComparisonTypes;
import ibd.table.Directory;
import ibd.table.Table;
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
public class Main {

    public Prototype createSchema() {
        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("id", true));
        pt.addColumn(new IntegerColumn("year"));
        pt.addColumn(new StringColumn("title"));
        pt.addColumn(new StringColumn("genre"));
        pt.addColumn(new IntegerColumn("cost"));
        return pt;
    }

    public void addMovie(Table table, int id, String title, int year, int cost, String genre) throws Exception {
        BasicDataRow rowData = new BasicDataRow();
        rowData.setInt("id", id);
        rowData.setInt("year", year);
        rowData.setInt("cost", cost);
        rowData.setString("title", title);
        rowData.setString("genre", genre);
        table.addRecord(rowData, true);

    }

    public void getAll(Table table) throws Exception {
        List<LinkedDataRow> list = table.getAllRecords();
        for (DataRow rowData : list) {
            System.out.println(rowData.toString());
        }
    }

    public void getRows(Table table, String col, Comparable comp, int type) throws Exception {
        List<LinkedDataRow> list = table.getRecords(col, comp, type);
        for (DataRow rowData : list) {
            System.out.println(rowData.toString());
        }
    }

    public void getRows(Table table, Expression expression, ExpressionSolver solver) throws Exception {
        System.out.println("***************** result list:");
        for (DataRow r : table.getAllRecords()) {
            if (solver.solve(expression, r)) {
                System.out.println(r.toString());
            }
        }
    }
    
    public Expression createExpressionSimple1(){
    //defining the expression
        SingleExpression se1 = new SingleExpression("year", ComparisonTypes.EQUAL, 2004);
        return se1;
    }
    
    public Expression createExpressionSimple2(){
    //defining the expression
        SingleExpression se1 = new SingleExpression("cost", ComparisonTypes.DIFF, 110);
        return se1;
    }
    
    public Expression createExpressionOR1(){
    //defining the expression
        SingleExpression se1 = new SingleExpression("year", ComparisonTypes.EQUAL, 2004);
        SingleExpression se2 = new SingleExpression("year", ComparisonTypes.EQUAL, 2014);
        SingleExpression se3 = new SingleExpression("cost", ComparisonTypes.GREATER_EQUAL_THAN, 150);
        CompositeExpression ce = new CompositeExpression(CompositeExpression.OR);
        ce.addExpression(se1);
        ce.addExpression(se2);
        ce.addExpression(se3);
        return ce;
    }
    
    public Expression createExpressionOR2(){
    //defining the expression
        SingleExpression se1 = new SingleExpression("year", ComparisonTypes.EQUAL, 2004);
        SingleExpression se2 = new SingleExpression("year", ComparisonTypes.EQUAL, 2014);
        
        CompositeExpression ce = new CompositeExpression(CompositeExpression.OR);
        ce.addExpression(se1);
        ce.addExpression(se2);
        
        CompositeExpression ce1 = new CompositeExpression(CompositeExpression.OR);
        SingleExpression se3 = new SingleExpression("cost", ComparisonTypes.GREATER_EQUAL_THAN, 150);
        ce1.addExpression(se3);
        ce1.addExpression(ce);
        return ce1;
    }
    
    
    public Expression createExpressionAND1(){
    //defining the expression
        SingleExpression se1 = new SingleExpression("year", ComparisonTypes.GREATER_EQUAL_THAN, 2004);
        SingleExpression se2 = new SingleExpression("year", ComparisonTypes.LOWER_EQUAL_THAN, 2014);
        SingleExpression se3 = new SingleExpression("cost", ComparisonTypes.GREATER_THAN, 110);
//        NegationExpression ne = new NegationExpression(se2);
        CompositeExpression ce = new CompositeExpression(CompositeExpression.AND);
        ce.addExpression(se1);
        ce.addExpression(se2);
        ce.addExpression(se3);
        return ce;
    }
    
    public Expression createExpressionAND2(){
    //defining the expression
        SingleExpression se1 = new SingleExpression("year", ComparisonTypes.GREATER_EQUAL_THAN, 2004);
        SingleExpression se2 = new SingleExpression("year", ComparisonTypes.LOWER_EQUAL_THAN, 2014);
        
//        NegationExpression ne = new NegationExpression(se2);
        CompositeExpression ce = new CompositeExpression(CompositeExpression.AND);
        ce.addExpression(se1);
        ce.addExpression(se2);
        
        CompositeExpression ce1 = new CompositeExpression(CompositeExpression.AND);
        SingleExpression se3 = new SingleExpression("cost", ComparisonTypes.GREATER_THAN, 110);
        ce1.addExpression(se3);
        ce1.addExpression(ce);
        return ce1;
    }
    
    
    
    public Expression createExpressionANDOR1(){
    //defining the expression
        SingleExpression se1 = new SingleExpression("year", ComparisonTypes.EQUAL, 2004);
        SingleExpression se2 = new SingleExpression("year", ComparisonTypes.EQUAL, 2014);
        
        CompositeExpression ce = new CompositeExpression(CompositeExpression.AND);
        ce.addExpression(se1);
        ce.addExpression(se2);
        
        CompositeExpression ce1 = new CompositeExpression(CompositeExpression.OR);
        SingleExpression se3 = new SingleExpression("cost", ComparisonTypes.GREATER_EQUAL_THAN, 150);
        ce1.addExpression(se3);
        ce1.addExpression(ce);
        return ce1;
    }
    public Expression createExpressionANDOR2(){
    //defining the expression
        SingleExpression se1 = new SingleExpression("year", ComparisonTypes.EQUAL, 2004);
        SingleExpression se2 = new SingleExpression("year", ComparisonTypes.EQUAL, 2014);
        
        CompositeExpression ce = new CompositeExpression(CompositeExpression.OR);
        ce.addExpression(se1);
        ce.addExpression(se2);
        
        CompositeExpression ce1 = new CompositeExpression(CompositeExpression.AND);
        SingleExpression se3 = new SingleExpression("cost", ComparisonTypes.GREATER_EQUAL_THAN, 150);
        ce1.addExpression(se3);
        ce1.addExpression(ce);
        return ce1;
    }
    
    public Expression createExpressionNOT1(){
    //defining the expression
        SingleExpression se1 = new SingleExpression("year", ComparisonTypes.EQUAL, 2004);
        SingleExpression se2 = new SingleExpression("year", ComparisonTypes.EQUAL, 2014);
        
        CompositeExpression ce = new CompositeExpression(CompositeExpression.OR);
        ce.addExpression(se1);
        ce.addExpression(se2);
        NegationExpression ne = new NegationExpression(ce);
        
        CompositeExpression ce1 = new CompositeExpression(CompositeExpression.AND);
        SingleExpression se3 = new SingleExpression("cost", ComparisonTypes.GREATER_EQUAL_THAN, 150);
        ce1.addExpression(se3);
        ce1.addExpression(ne);
        return ce1;
    }
    
    
    public Expression createExpressionNOT2(){
    //defining the expression
        SingleExpression se1 = new SingleExpression("year", ComparisonTypes.EQUAL, 2004);
        SingleExpression se2 = new SingleExpression("year", ComparisonTypes.EQUAL, 2014);
        
        CompositeExpression ce = new CompositeExpression(CompositeExpression.OR);
        NegationExpression ne1 = new NegationExpression(se1);
        NegationExpression ne2 = new NegationExpression(se2);
        ce.addExpression(ne1);
        ce.addExpression(ne2);
        
        
        CompositeExpression ce1 = new CompositeExpression(CompositeExpression.AND);
        SingleExpression se3 = new SingleExpression("cost", ComparisonTypes.GREATER_EQUAL_THAN, 150);
        ce1.addExpression(se3);
        ce1.addExpression(ce);
        NegationExpression ne = new NegationExpression(ce1);
        return ne;
    }

    public static void main(String[] args) throws Exception {

        Main main = new Main();

        //defining the table schema
        Prototype schema = main.createSchema();

        //creating of the table’s file
        Table table = Directory.getTable("c:\\teste\\ibd", "movie1",schema, 99999,  4096, false);

        //adding movies
//        main.addMovie(table, 1, "Interstelar", 2014, 165, "Sci-Fi");
//        main.addMovie(table, 2, "Os Infiltrados", 2006, 40, "Drama");
//        main.addMovie(table, 3, "Avatar", 2009, 237, "Sci-Fi");
//        main.addMovie(table, 4, "O Aviador", 2004, 110, "Drama");
//        main.addMovie(table, 5, "O Terminal", 2004, 60, "Drama");
//        main.addMovie(table, 6, "Guerra dos Mundos", 2005, 132, "Sci-Fi");
//        main.addMovie(table, 7, "Minority Report", 2002, 102, "Sci-Fi");
//        table.flushDB();

        //print movies from 2004, without using a boolean expression
        //main.getRows(table, "year", 2004, ComparisonTypes.EQUAL);
        main.getAll(table);

        ExpressionSolver solver = new MyExpressionSolver();
        
        //printing the rows that satisfy the expression
        System.out.println("teste 1");
        main.getRows(table, main.createExpressionSimple1(), solver);
        System.out.println("teste 2");
        main.getRows(table, main.createExpressionSimple2(), solver);
        System.out.println("teste 3");
        main.getRows(table, main.createExpressionAND1(), solver);
        System.out.println("teste 4");
        main.getRows(table, main.createExpressionAND2(), solver);
        System.out.println("teste 5");
        main.getRows(table, main.createExpressionOR1(), solver);
        System.out.println("teste 6");
        main.getRows(table, main.createExpressionOR2(), solver);
        System.out.println("teste 7");
        main.getRows(table, main.createExpressionANDOR1(), solver);
        System.out.println("teste 8");
        main.getRows(table, main.createExpressionANDOR2(), solver);
        System.out.println("teste 9");
        main.getRows(table, main.createExpressionNOT1(), solver);
        System.out.println("teste 10");
        main.getRows(table, main.createExpressionNOT1(), solver);

    }

}
