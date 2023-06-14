package operations.binary;

import com.mxgraph.model.mxCell;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import operations.OperationErrorVerifier.ErrorMessage;
import sgbd.query.Operator;
import sgbd.query.binaryop.joins.BlockNestedLoopJoin;
import sgbd.util.statics.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Join implements IOperator {

	public Join() {

	}

	public void executeOperation(mxCell jCell, List<String> arguments) {

		OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

		ErrorMessage error = null;
		
		try {

			error = ErrorMessage.NULL_ARGUMENT;
			OperationErrorVerifier.noNullArgument(arguments);
			
			error = ErrorMessage.NO_TWO_ARGUMENTS;
			OperationErrorVerifier.twoArguments(arguments);
			
			error = ErrorMessage.NO_PARENT;
			OperationErrorVerifier.hasParent(cell);
			
			error = ErrorMessage.NO_TWO_PARENTS;
			OperationErrorVerifier.twoParents(cell);
			
			error = ErrorMessage.PARENT_ERROR;
			OperationErrorVerifier.noParentError(cell);
			
			error = ErrorMessage.PARENT_WITHOUT_COLUMN;
			OperationErrorVerifier.parentContainsColumns(Column.sourceAndNameTogether(cell.getParents().get(0).getColumns()), List.of(arguments.get(0)));
			OperationErrorVerifier.parentContainsColumns(Column.sourceAndNameTogether(cell.getParents().get(1).getColumns()), List.of(arguments.get(1)));
			
			error = null;
			
		} catch (TreeException e) {

			cell.setError(error);

		}

		if(error != null) return;

		Cell parentCell1 = cell.getParents().get(0);
		Cell parentCell2 = cell.getParents().get(1);

		List<String> argumentsFixed = new ArrayList<>();

		argumentsFixed.add(Column.putSource(arguments.get(0), parentCell1.getSourceTableNameByColumn(arguments.get(0))));
		argumentsFixed.add(Column.putSource(arguments.get(1), parentCell2.getSourceTableNameByColumn(arguments.get(1))));

		Operator op1 = parentCell1.getOperator();
		Operator op2 = parentCell2.getOperator();

		String source1 = Column.removeName(argumentsFixed.get(0));
		String source2 = Column.removeName(argumentsFixed.get(1));

		String item1 = Column.removeSource(argumentsFixed.get(0));
		String item2 = Column.removeSource(argumentsFixed.get(1));;

		Operator operator = new BlockNestedLoopJoin(op1, op2, (t1, t2) -> Objects.equals(t1.getContent(source1).getInt(item1),
						t2.getContent(source2).getInt(item2))
		);

		Operation.operationSetter(cell, "|X|   " + Column.putSource(item1, source1) + " = " + Column.putSource(item2, source2), argumentsFixed, operator);

	}

}
