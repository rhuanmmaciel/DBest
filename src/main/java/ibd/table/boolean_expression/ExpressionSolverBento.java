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
public class ExpressionSolverBento implements ExpressionSolver {
    @Override
    public boolean solve(Expression exp, DataRow row) {
        try {
            if (exp instanceof NegationExpression ne)
                return !solve(ne.expression, row);
            else if (exp instanceof SingleExpression se)
                try {
                    return ComparisonTypes.match(row.getValue(se.colName), se.value, se.comparisonType);
                } catch (Exception e) {
                    return false;
                }
            else if (exp instanceof CompositeExpression ce) {
                boolean isOr = ce.boolean_conector == CompositeExpression.OR;
                for (int i = 0; i < ce.expressions.size(); i++)
                    if (solve(ce.expressions.get(i), row) == isOr)
                        return isOr;
                return !isOr;
            } else
                return false;
        }
        catch (StackOverflowError e) {
            // O demente botou um Composite nele mesmo.
            return false;
        }
    }
}
