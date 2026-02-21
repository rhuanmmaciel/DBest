package ibd.table.boolean_expression;

import ibd.table.prototype.DataRow;
import ibd.table.ComparisonTypes;

public class ExpressionSolverFrancisco implements ExpressionSolver{
    private ComparisonTypes comparador;
    private int contRetornosOr, contRetornosAnd, tamanhoExpComp;
    @Override
    public boolean solve(Expression exp, DataRow row) {
        if(exp instanceof SingleExpression) {
            if(comparador.match(row.getValue(((SingleExpression) exp).colName),((SingleExpression) exp).value, ((SingleExpression) exp).comparisonType))
                return true;
            else
                return false;
        }
        else if (exp instanceof CompositeExpression) {
            int testaRowAnd = 0, testaRowOr=0;
            for(Expression e: ((CompositeExpression) exp).expressions) {

                if(solve(e, row) && ((CompositeExpression) exp).boolean_conector == CompositeExpression.AND) {
                    testaRowAnd++;
                }
                else if (solve(e, row) && ((CompositeExpression) exp).boolean_conector == CompositeExpression.OR) {
                    testaRowOr++;
                }
            }
            if(testaRowAnd == ((CompositeExpression) exp).expressions.size() && ((CompositeExpression) exp).boolean_conector == CompositeExpression.AND) {
                return true;
            }
            else if (testaRowOr > 0 && ((CompositeExpression) exp).boolean_conector == CompositeExpression.OR) {
                return true;
            }
            else return false;
        }
        else if(exp instanceof NegationExpression) {
            if(((NegationExpression) exp).expression instanceof SingleExpression) {
                SingleExpression se = (SingleExpression) ((NegationExpression) exp).expression;
                if(comparador.match(row.getValue(se.colName), se.value, se.comparisonType))
                    return false;
                else
                    return true;
            }
        }
        return false;
    }
}
