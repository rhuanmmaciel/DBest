/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.boolean_expression;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ferna
 */
public class ExpressionSolverMarianaPillon implements ExpressionSolver {
    @Override
    public boolean solve(Expression exp, DataRow row){
        // para usar a funcao match
        ComparisonTypes ct = new ComparisonTypes();

        if (exp instanceof SingleExpression) {
            SingleExpression se = (SingleExpression) exp;
            Comparable rowValue = row.getValue(se.colName);

            return ct.match(rowValue, se.value, se.comparisonType);
        } else if (exp instanceof NegationExpression) {
            NegationExpression ne = (NegationExpression) exp;
            return !(solve(ne.getExpression(), row));
        } else {
            CompositeExpression ce = (CompositeExpression) exp;
            int connector = ce.boolean_conector;
            List<Expression> exps = ce.expressions;

            List<Boolean> expValue = new ArrayList<>();
            for ( Expression e : exps ) expValue.add(solve(e, row));

            // se o conector eh do tipo OR, so um valor precisa ser true
            // se eh do tipo AND, um false = todos false
            if (connector == 1) {
                return expValue.contains(true);
            } else {
                if (expValue.contains(false))
                    return false;
            }
        }

        return true;
    }
}
