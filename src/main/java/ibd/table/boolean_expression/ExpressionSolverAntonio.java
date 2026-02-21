package ibd.table.boolean_expression;

import ibd.table.prototype.DataRow;
import ibd.table.ComparisonTypes;
import java.util.List;

public class ExpressionSolverAntonio implements ExpressionSolver
{
    public boolean solve(Expression exp, DataRow row)
    {
        // Verifica se a expressão é uma instância de SingleExpression
        if (exp instanceof SingleExpression singleExp)
        {
            Comparable value = row.getValue(singleExp.colName);
            return ComparisonTypes.match(value, singleExp.value, singleExp.comparisonType);
        }

        // Verifica se a expressão é uma instância de NegationExpression
        else if (exp instanceof NegationExpression negExp)
        {
            return !solve(negExp.getExpression(), row); // Negação da expressão interna
        }

        // Verifica se a expressão é uma instância de CompositeExpression
        else if (exp instanceof CompositeExpression compExp)
        {
            List<Expression> expressions = compExp.expressions;
            boolean result = compExp.boolean_conector == CompositeExpression.AND;

            // Itera sobre cada expressão da lista
            for (Expression subExp : expressions)
            {
                boolean subResult = solve(subExp, row);

                if (compExp.boolean_conector == CompositeExpression.AND)
                {
                    result = result && subResult; // AND logic
                    if (!result) {
                        return false;
                    }
                } else if (compExp.boolean_conector == CompositeExpression.OR)
                {
                    result = result || subResult; // OR
                    if (result)
                    {
                        return true;
                    }
                }
            }
            return result;
        }

        // Caso a expressão não seja de nenhum dos tipos conhecidos
        throw new IllegalArgumentException("Tipo de expressão desconhecido: " + exp.getClass());
    }
}
