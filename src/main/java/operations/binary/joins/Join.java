package operations.binary.joins;

import lib.booleanexpression.entities.expressions.BooleanExpression;

import sgbd.query.Operator;
import sgbd.query.binaryop.joins.NestedLoopJoin;

public class Join extends JoinOperators {

    @Override
    public Operator createJoinOperator(Operator operator1, Operator operator2, BooleanExpression booleanExpression) {
        return new NestedLoopJoin(operator1, operator2, booleanExpression);
    }
}
