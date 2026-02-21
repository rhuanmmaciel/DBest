package ibd.table.boolean_expression;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;

/**
 * It provides methods to solve SingleExpression,
 * NegationExpression, and CompositeExpression expressions types.
 * It traverses through the expression tree recursively,
 * solving each sub-expression and combining the results based on the boolean operator.
 *
 * @author Daniel Seitenfus
 */
public class ExpressionSolverDanielSeitenfus implements ExpressionSolver {
    @Override
    public boolean solve(Expression exp, DataRow row) {
        if (exp instanceof SingleExpression singleExpression) {
            return solveSingleExpression(singleExpression, row);
        }

        if (exp instanceof NegationExpression negationExpression) {
            return solveNegationExpression(negationExpression, row);
        }

        if (exp instanceof CompositeExpression compositeExpression) {
            return solveCompositeExpression(compositeExpression, row);
        }

        return false;
    }

    private boolean solveSingleExpression(SingleExpression singleExpression, DataRow row) {
        Comparable value = row.getValue(singleExpression.colName);
        return ComparisonTypes.match(value, singleExpression.value, singleExpression.comparisonType);
    }

    private boolean solveNegationExpression(NegationExpression negationExpression, DataRow row) {
        return !solve(negationExpression.getExpression(), row);
    }

    private boolean solveCompositeExpression(CompositeExpression compositeExpression, DataRow row) {
        boolean finalResult = compositeExpression.boolean_conector == CompositeExpression.AND;

        for (Expression expression: compositeExpression.expressions) {
            boolean result = solve(expression, row);
            if (compositeExpression.boolean_conector == CompositeExpression.AND) {
                finalResult = finalResult && result;
            } else if (compositeExpression.boolean_conector == CompositeExpression.OR) {
                finalResult = finalResult || result;
            }
        }

        return finalResult;
    }

}
