package operations.binary.set;

import sgbd.query.Operator;
import sgbd.query.binaryop.IntersectionOperator;
import sgbd.query.binaryop.UnionOperator;

import java.util.List;

public class Union extends SetOperators {

	public Union() {

	}

	@Override
	public Operator createSetOperator(Operator op1, Operator op2, List<String> columns1, List<String> columns2) {
		return new UnionOperator(op1, op2, columns1, columns2);
	}
}
