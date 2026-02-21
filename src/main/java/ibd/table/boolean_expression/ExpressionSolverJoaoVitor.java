package ibd.table.boolean_expression;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;

public class ExpressionSolverJoaoVitor implements ExpressionSolver{

    private boolean recursiveSolve(Expression exp, DataRow row){
        if(exp instanceof SingleExpression){
            return singleExpSolve((SingleExpression) exp, row);
        } else if (exp instanceof NegationExpression) {
            return !(recursiveSolve(((NegationExpression) exp).getExpression(), row));
        } else if (exp instanceof CompositeExpression) {
            return compositeExpSolve((CompositeExpression) exp, row);
        } else {
            return false;
        }
    }

    private boolean singleExpSolve(SingleExpression exp, DataRow row){

        String col_name = exp.colName;
        return ComparisonTypes.match(row.getValue(col_name),
                exp.value, exp.comparisonType);
    }

    private boolean compositeExpSolve(CompositeExpression exp, DataRow row){

        if(exp.boolean_conector == CompositeExpression.OR) {
            for (Expression expression : exp.expressions) {
                if(recursiveSolve(expression, row)) return true;
            }
            return false;

        } else {
            for (Expression expression : exp.expressions) {
                if( ! recursiveSolve(expression, row)) return false;
            }

            return true;
        }


    }
    @Override
    public boolean solve(Expression exp, DataRow row) {

        return recursiveSolve(exp, row);

    }


}
