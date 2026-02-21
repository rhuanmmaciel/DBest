/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.boolean_expression;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;

/**
 *
 * @author ferna
 */
public class MyExpressionSolver implements ExpressionSolver{

    @Override
    public boolean solve(Expression exp, DataRow rowData) {

        if (exp instanceof SingleExpression) {
            SingleExpression se = (SingleExpression) exp;
            Comparable value1 = rowData.getValue(se.colName);
            boolean match = ComparisonTypes.match(value1, se.value, se.comparisonType);
            if (!match) {
                return false;
            }
        } else if (exp instanceof CompositeExpression) {
            CompositeExpression ce = (CompositeExpression) exp;
            if (ce.boolean_conector == CompositeExpression.AND) {
                for (Expression expression : ce.expressions) {
                    boolean match = solve(expression, rowData);
                    if (!match) {
                        return false;
                    }
                }
                return true;
            } else {
                boolean match = false;
                for (Expression expression : ce.expressions) {
                    boolean match1 = solve(expression, rowData);
                    match = match || match1;

                }
                return match;
            }
        }
        else if (exp instanceof NegationExpression) {
            NegationExpression ne = (NegationExpression)exp;
            return !solve(ne.getExpression(), rowData);
        }
        return true;
    }
}
