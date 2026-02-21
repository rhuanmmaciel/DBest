package ibd.table.boolean_expression;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;
public class ExpressionSolverFelipe implements ExpressionSolver{
    @Override
    public boolean solve(Expression exp, DataRow row)
    {
        try {
            if (exp instanceof SingleExpression single)
            {

                Comparable c = row.getValue(single.colName);
                return ComparisonTypes.match(c, single.value, single.comparisonType);
            }
            else if (exp instanceof CompositeExpression composite)
            {
                boolean ans = composite.boolean_conector == composite.AND && composite.expressions.size() > 0;
                for (Expression expression : composite.expressions) {
                    if (composite.boolean_conector == composite.OR) {
                        ans = ans || solve(expression, row);
                    } else if (composite.boolean_conector == composite.AND) {
                        ans = ans && solve(expression, row);
                    }
                }
                return ans;
            }
            else if (exp instanceof NegationExpression negation)
            {
                return !solve(negation.expression, row);
            }
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
        catch (StackOverflowError e)
        {
            return false;
        }
    }
}
