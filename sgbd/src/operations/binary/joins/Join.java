package operations.binary.joins;

import booleanexpression.BooleanExpressionRecognizer;
import lib.booleanexpression.entities.expressions.BooleanExpression;
import sgbd.query.Operator;
import sgbd.query.binaryop.joins.BlockNestedLoopJoin;

public class Join extends JoinOperators {

	public Join() {

	}
	@Override
	public Operator createJoinOperator(Operator op1, Operator op2, BooleanExpression booleanExpression){

		return new BlockNestedLoopJoin(op1, op2, booleanExpression);

	}

}
