package ibd.table.boolean_expression;
import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;
/**
 *
 * @author gabrielsfranca
 */
public class ExpressionSolverGabrielFranca implements ExpressionSolver{
    @Override
    public boolean solve(Expression exp, DataRow row){
        if (exp instanceof SingleExpression) {
            return solveSingleExpression((SingleExpression) exp, row);
        } else if (exp instanceof CompositeExpression) {
            return solveCompositeExpression((CompositeExpression) exp, row);
        } else {
            throw new IllegalArgumentException("Expressao nao suportada");
        }
    }

    private boolean solveSingleExpression(SingleExpression exp, DataRow row){
        String columnName = exp.colName;
        Comparable value = exp.value;
        int comparisonType = exp.comparisonType;

        Comparable rowValue = row.getValue(columnName);

        return ComparisonTypes.match(rowValue, value, comparisonType);
    }

    private boolean solveCompositeExpression(CompositeExpression exp, DataRow row){
        int booleanConector = exp.boolean_conector;
        boolean result = false;

        for (Expression subExp : exp.expressions){
            boolean subResult = solve(subExp, row);
            if (booleanConector == CompositeExpression.AND && !subResult){
                // Se a conexão booleana for AND e uma subexpressão for falsa, a expressão composta é falsa.
                return false;
            } else if (booleanConector == CompositeExpression.OR && subResult){
                // Se a conexão booleana for OR e uma subexpressão for verdadeira, a expressão composta é verdadeira.
                return true;
            }
            // Para outras subexpressões, mantenha o resultado para avaliar o próximo subexpressão.
            result = subResult;
        }

        // Se a conexão booleana for AND e todas as subexpressões forem verdadeiras ou se a conexão for OR e todas as subexpressões forem falsas.
        return result;
    }
}

