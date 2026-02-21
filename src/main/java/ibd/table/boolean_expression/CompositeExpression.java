/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.boolean_expression;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ferna
 */
public class CompositeExpression extends Expression{
    
    public static final int AND = 0;
    public static final int OR = 1;
    
    List<Expression> expressions = new ArrayList<>();
    int boolean_conector;
    
    public CompositeExpression(int boolean_conector){
        this.boolean_conector = boolean_conector;
    }
    
    public void addExpression(Expression e){
        expressions.add(e);
    }
    
    @Override
    public boolean solve() {
        return true;
    }
    
}
