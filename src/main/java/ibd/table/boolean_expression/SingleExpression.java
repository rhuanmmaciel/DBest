/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.boolean_expression;

/**
 *
 * @author ferna
 */
public class SingleExpression extends Expression{
    public String colName;
    public int comparisonType;
    public Comparable value;
    
    public SingleExpression(String colName, int comparisonType, Comparable value){
        this.colName = colName;
        this.comparisonType = comparisonType;
        this.value = value;
    }

    @Override
    public boolean solve() {
        return true;
    }
    
}
