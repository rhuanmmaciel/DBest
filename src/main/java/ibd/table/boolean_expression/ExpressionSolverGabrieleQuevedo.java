package ibd.table.boolean_expression;

import java.util.List;
import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;

public class ExpressionSolverGabrieleQuevedo implements ExpressionSolver {

    @Override
    public boolean solve(Expression exp, DataRow row) {
        if (exp instanceof SingleExpression) {
            SingleExpression singleExp = (SingleExpression) exp;
            return ComparisonTypes.match(row.getValue(singleExp.colName), singleExp.value, singleExp.comparisonType);
        } else if (exp instanceof CompositeExpression) {
            CompositeExpression compositeExp = (CompositeExpression) exp;
            List<Expression> expressions = compositeExp.expressions;
            boolean result = compositeExp.boolean_conector == 0; // AND

            for (Expression expression : expressions) {
                boolean antResult = solve(expression, row);
                result = compositeExp.boolean_conector == 0 ? result && antResult : result || antResult;
            }
            return result;
        } else if (exp instanceof NegationExpression) {
            NegationExpression negationExp = (NegationExpression) exp;
            return !solve(negationExp.expression, row);
        }
        return false;
    }
}
