package operations.binary;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import operations.OperationErrorVerifier.ErrorMessage;
import sgbd.query.Operator;
import sgbd.query.binaryop.joins.LeftNestedLoopJoin;

import java.util.List;

public class LeftJoin implements IOperator {

	public LeftJoin() {

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
			OperationErrorVerifier.parentContainsColumns(cell.getParents().get(0).getColumnsName(),
					List.of(arguments.get(0)));
			OperationErrorVerifier.parentContainsColumns(cell.getParents().get(1).getColumnsName(),
					List.of(arguments.get(1)));
			
			error = null;
			
		} catch (TreeException e) {

			cell.setError(error);

		}
		
		if(error != null) return;

		Cell parentCell1 = cell.getParents().get(0);
		Cell parentCell2 = cell.getParents().get(1);

		Operator table_1 = parentCell1.getOperator();
		Operator table_2 = parentCell2.getOperator();
		String item1 = arguments.get(0);
		String item2 = arguments.get(1);

		Operator operator = new LeftNestedLoopJoin(table_1, table_2, (t1, t2) -> {

			return t1.getContent(parentCell1.getSourceTableNameByColumn(item1)).getInt(item1) == t2
					.getContent(parentCell2.getSourceTableNameByColumn(item2)).getInt(item2);

		});

		Operation.operationSetter(cell, "⟕   " + item1 + " = " + item2, arguments, operator);

	}

}
