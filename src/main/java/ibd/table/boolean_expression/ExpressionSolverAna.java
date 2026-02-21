package ibd.table.boolean_expression;

import ibd.table.prototype.DataRow;
import ibd.table.ComparisonTypes;
import java.util.ArrayList;
import java.util.List;

public class ExpressionSolverAna implements ExpressionSolver {

    private boolean SolveSE(Expression exp, DataRow row)
    {
        SingleExpression singleExp = (SingleExpression) exp;
        String columnName = singleExp.colName;
        Comparable value = singleExp.value;
        Comparable columnValue = row.getValue(columnName);

        return ComparisonTypes.match(columnValue, value, singleExp.comparisonType);
    }

    @Override
    public boolean solve(Expression exp, DataRow row) {

        if (exp instanceof SingleExpression) {
            return SolveSE(exp, row);
        }
        else if (exp instanceof NegationExpression) {
            NegationExpression negExp = (NegationExpression) exp;
            return !solve(negExp.getExpression(), row);
        }
        else if (exp instanceof CompositeExpression) {
            CompositeExpression compExp = (CompositeExpression) exp;

            List<Boolean> subExpressions = new ArrayList<Boolean>();
            for(int i = 0; i < compExp.expressions.size(); i++)
            {
                subExpressions.add(solve(compExp.expressions.get(i), row));
            }

            if(compExp.boolean_conector == CompositeExpression.OR)
            {
                for(int i = 0; i < subExpressions.size(); i++)
                {
                    if(subExpressions.get(i)) return true;
                }
                return false;
            }

            else if (compExp.boolean_conector == CompositeExpression.AND)
            {
                for(int i = 0; i < subExpressions.size(); i++)
                {
                    if(!subExpressions.get(i)) return false;
                }
                return true;
            }
        }

        return false;
    }
}
