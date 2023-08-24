package operations.binary.joins;

import lib.booleanexpression.entities.expressions.BooleanExpression;
import sgbd.query.Operator;
import sgbd.query.binaryop.joins.LeftNestedLoopJoin;

public class LeftJoin extends JoinOperators {

	public LeftJoin() {

	}
	@Override
	public Operator createJoinOperator(Operator op1, Operator op2, BooleanExpression booleanExpression) {

		return new LeftNestedLoopJoin(op1, op2, booleanExpression);

	}
}
