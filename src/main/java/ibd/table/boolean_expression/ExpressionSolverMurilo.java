package ibd.table.boolean_expression;

import ibd.table.prototype.DataRow;
import ibd.table.ComparisonTypes;

public class ExpressionSolverMurilo implements ExpressionSolver {

    @Override
    public boolean solve(Expression exp, DataRow row) {
        if (exp instanceof SingleExpression) {
            SingleExpression singleExpression = (SingleExpression) exp;
            Comparable columnValue = row.getValue(singleExpression.colName);
            return ComparisonTypes.match(columnValue, singleExpression.value, singleExpression.comparisonType);
        } else if (exp instanceof CompositeExpression) {
            CompositeExpression compositeExpression = (CompositeExpression) exp;
            if (compositeExpression.boolean_conector == CompositeExpression.AND) {
                return compositeExpression.expressions.stream().allMatch(subExp -> solve(subExp, row));
            } else if (compositeExpression.boolean_conector == CompositeExpression.OR) {
                return compositeExpression.expressions.stream().anyMatch(subExp -> solve(subExp, row));
            }
        } else if (exp instanceof NegationExpression) {
            NegationExpression negationExpression = (NegationExpression) exp;
            return !solve(negationExpression.expression, row);
        }
        return false;
    }
}
