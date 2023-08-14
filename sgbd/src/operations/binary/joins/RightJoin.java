package operations.binary.joins;

import lib.booleanexpression.entities.expressions.BooleanExpression;
import sgbd.query.Operator;
import sgbd.query.binaryop.joins.RightNestedLoopJoin;

public class RightJoin extends JoinOperators {

	public RightJoin() {

	}
	@Override
	public Operator createJoinOperator(Operator op1, Operator op2, BooleanExpression booleanExpression) {

		return new RightNestedLoopJoin(op1, op2, booleanExpression);

	}
}
