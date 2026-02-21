package ibd.table.boolean_expression;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;

import java.sql.SQLOutput;

public class ExpressionSolverRafaelCarneiroPregardier implements ExpressionSolver {
    @Override
    public boolean solve(Expression exp, DataRow row) {
        if (exp instanceof SingleExpression singleExpression) {
            return ComparisonTypes.match(row.getValue(singleExpression.colName), singleExpression.value, singleExpression.comparisonType);
        } else if(exp instanceof CompositeExpression compositeExpression) {
            if(compositeExpression.boolean_conector == CompositeExpression.AND) {
                for(Expression e : compositeExpression.expressions) {
                    if(!solve(e, row)) {
                        return false;
                    }
                }
                return true;
            } else if(compositeExpression.boolean_conector == CompositeExpression.OR) {
                for(Expression e : compositeExpression.expressions) {
                    if(solve(e, row)) {
                        return true;
                    }
                }
                return false;
            }
        } else if(exp instanceof NegationExpression negationExpression) {
            return !solve(negationExpression.expression, row);
        }
        return false;
    }
}
