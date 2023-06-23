package operations.binary.joins;

import sgbd.query.Operator;
import sgbd.query.binaryop.joins.RightNestedLoopJoin;

public class RightJoin extends JoinOperators {

	public RightJoin() {

	}
	@Override
	public Operator createJoinOperator(Operator op1, Operator op2, String source1, String source2, String item1, String item2) {

		return new RightNestedLoopJoin(op1, op2, (t1, t2) -> compare(t1, source1, item1, t2, source2, item2));

	}
}
