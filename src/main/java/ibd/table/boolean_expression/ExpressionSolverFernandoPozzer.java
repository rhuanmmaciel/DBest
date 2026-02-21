package ibd.table.boolean_expression;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;

public class ExpressionSolverFernandoPozzer implements ExpressionSolver
{
    @Override
    public boolean solve(Expression exp, DataRow row)
    {
        if(exp instanceof SingleExpression)
        {
            return solveSingleExpression((SingleExpression) exp, row);
        }

        if(exp instanceof NegationExpression)
        {
            return !solve(((NegationExpression) exp).getExpression(), row);
        }

        CompositeExpression comExp = (CompositeExpression) exp;
        boolean comExpValue;

        if(comExp.expressions.isEmpty())
        {
            return true;
        }

        if(comExp.boolean_conector == CompositeExpression.AND)
        {
            comExpValue = true;
            for(Expression aux: comExp.expressions)
            {
                comExpValue &= solve(aux, row);
            }
        }
        else
        {
            comExpValue = false;
            for(Expression aux: comExp.expressions)
            {
                comExpValue |= solve(aux, row);
            }
        }

        return comExpValue;
    }

    private boolean solveSingleExpression(SingleExpression exp, DataRow row)
    {
        return ComparisonTypes.match(row.getValue(exp.colName), exp.value, exp.comparisonType);
    }
}
