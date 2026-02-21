package ibd.table.boolean_expression;

import ibd.table.prototype.DataRow;
import ibd.table.ComparisonTypes;
import java.util.List;


public class ExpressionSolverLorenzoST implements ExpressionSolver
{
    @Override
    public boolean solve(Expression exp, DataRow row)
    {
        if (exp instanceof SingleExpression singleExp)
        {
            Comparable columnValue = row.getValue(singleExp.colName);
            Comparable value = singleExp.value;
            int comparisonType = singleExp.comparisonType;
            return ComparisonTypes.match(columnValue, value, comparisonType);
        }
        else if (exp instanceof CompositeExpression compositeExp)
        {
            int boolean_conector = compositeExp.boolean_conector;
            List<Expression> expressions = compositeExp.expressions;
            int AND = CompositeExpression.AND;
            int OR = CompositeExpression.OR;

            if (boolean_conector == AND) {
                for (Expression e : expressions) {
                    if (!solve(e,row)) {
                        return false;
                    }
                }
                return true;
            } else if (boolean_conector == OR) {
                for (Expression e : expressions) {
                    if (solve(e, row)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        else if (exp instanceof NegationExpression negationExp)
        {
            Expression expression = negationExp.getExpression();
            return !solve(expression, row);
        }
        return false;
    }
}
