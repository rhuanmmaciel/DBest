package ibd.table.boolean_expression;
import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;
import java.util.List;

//Mikael


public class ExpressionSolverMikael implements ExpressionSolver {

    @Override
    public boolean solve(Expression exp, DataRow row) {
        if (exp instanceof SingleExpression) {
            return solveSingleExpression((SingleExpression) exp, row);
        } else if (exp instanceof CompositeExpression) {
            return solveCompositeExpression((CompositeExpression) exp, row);
        }
        return false; 
    }

    private boolean solveSingleExpression(SingleExpression exp, DataRow row) {
        String columnName = exp.colName;
        int comparisonType = exp.comparisonType;
        Comparable value = exp.value;

                
        Comparable rowValue = row.getValue(columnName);
        if (rowValue == null) return false;
        return ComparisonTypes.match(rowValue, value, comparisonType);
    }

   private boolean solveCompositeExpression(CompositeExpression exp, DataRow row) {
        List<Expression> expressions = exp.expressions;
        int connector = exp.boolean_conector;

        boolean result = (connector == CompositeExpression.AND);
        for (Expression expression : expressions) {
            boolean expressionResult = solve(expression, row);

            // atualizar resultado de acordo com conector 
            if (connector == CompositeExpression.AND) {
                result = result && expressionResult;
            } else if (connector == CompositeExpression.OR) {
                result = result || expressionResult;
            }
        }

        return result;
    }
}