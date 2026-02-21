package ibd.table.boolean_expression;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;

public class ExpressionSolverAndriel implements ExpressionSolver {

    //criei esse método pensando na possibilidade de haver valores nulos
    private boolean safeMatch(Comparable<?> c1, Comparable<?> c2, int comparsionType) {
        if (c1 == null || c2 == null) {
            if (c1 == null && c2 == null) {
                return comparsionType == ComparisonTypes.EQUAL || comparsionType == ComparisonTypes.GREATER_EQUAL_THAN || comparsionType == ComparisonTypes.LOWER_EQUAL_THAN;
            } else {
                return false;
            }
        }
        return ComparisonTypes.match(c1, c2, comparsionType);
    }

    @Override
    public boolean solve(Expression exp, DataRow row) {
        boolean valid = false;
        if (exp instanceof CompositeExpression) {
            CompositeExpression compositeExpression = (CompositeExpression) exp;
            if (compositeExpression.boolean_conector == CompositeExpression.OR) {
                valid = compositeExpression.expressions.stream().anyMatch(ce -> solve(ce, row));
            } else if (compositeExpression.boolean_conector == CompositeExpression.AND) {
                valid = compositeExpression.expressions.stream().allMatch(ce -> solve(ce, row));
            }
        } else if (exp instanceof SingleExpression) {
            SingleExpression singleExpression = (SingleExpression) exp;
            valid = safeMatch(row.getValue(singleExpression.colName), singleExpression.value, singleExpression.comparisonType);
        } else if (exp instanceof NegationExpression) {
            NegationExpression negationExpression = (NegationExpression) exp;
            valid = !solve(negationExpression.getExpression(), row);
        }
        return valid;
    }
}
