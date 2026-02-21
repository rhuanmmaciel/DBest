package ibd.table.boolean_expression;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;

public class ExpressionSolverAlissonCostaSchmidt implements ExpressionSolver {
    @Override
    public boolean solve(Expression exp, DataRow row) {

        if (exp instanceof SingleExpression) {
            return solveSingleExpression((SingleExpression) exp, row);
        } else if (exp instanceof CompositeExpression) {
            return solveCompositeExpression((CompositeExpression) exp, row);
        } else if (exp instanceof NegationExpression) {
            return solveNegationExpression((NegationExpression) exp, row);
        } else {
            return false;
        }
    }

    private boolean solveSingleExpression(SingleExpression exp, DataRow row) {
        Comparable value1 = row.getValue(exp.colName);
        Comparable value2 = exp.value;
        int comparisonType = exp.comparisonType;

        return ComparisonTypes.match(value1, value2, comparisonType);
    }

    private boolean solveCompositeExpression(CompositeExpression exp, DataRow row) {
        boolean result = exp.boolean_conector == CompositeExpression.AND;
        for (Expression expression : exp.expressions) {
            boolean expressionResult = solve(expression, row);
            if (exp.boolean_conector == CompositeExpression.AND) {  // true && qualquerCoisa = qualquerCoisa
                result = result && expressionResult;
            } else {                                                // false || qualquerCoisa = qualquerCoisa
                result = result || expressionResult;
            }
        }
        return result;
    }

    private boolean solveNegationExpression(NegationExpression exp, DataRow row) {
        return !solve(exp.getExpression(), row);
    }
}
