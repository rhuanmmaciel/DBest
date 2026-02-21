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
public class NegationExpression extends Expression{
    
    Expression expression;
    
    public NegationExpression(Expression expression){
        this.expression = expression;
    }
    
    public Expression getExpression(){
        return expression;
    }
    
    @Override
    public boolean solve() {
        return true;
    }
    
}
