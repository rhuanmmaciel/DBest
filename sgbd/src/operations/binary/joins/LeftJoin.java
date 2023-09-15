package operations.binary.joins;

import lib.booleanexpression.entities.expressions.BooleanExpression;
import sgbd.query.Operator;
import sgbd.query.binaryop.joins.LeftNestedLoopJoin;

public class LeftJoin extends JoinOperators {

    @Override
    public Operator createJoinOperator(Operator operator1, Operator operator2, BooleanExpression booleanExpression) {
        return new LeftNestedLoopJoin(operator1, operator2, booleanExpression);
    }
}
