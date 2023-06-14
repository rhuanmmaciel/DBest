package operations.binary.joins;

import sgbd.query.Operator;
import sgbd.query.binaryop.joins.LeftNestedLoopJoin;

import java.util.Objects;

public class LeftJoin extends JoinOperators {

	public LeftJoin() {

	}
	@Override
	public Operator createJoinOperator(Operator op1, Operator op2, String source1, String source2, String item1, String item2) {

		return new LeftNestedLoopJoin(op1, op2, (t1, t2) -> Objects.equals(t1.getContent(source1).getInt(item1),
				t2.getContent(source2).getInt(item2)));

	}
}
