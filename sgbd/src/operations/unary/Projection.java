package operations.unary;

import com.mxgraph.model.mxCell;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.query.Operator;
import sgbd.query.unaryop.SelectColumnsOperator;

import java.util.List;

public class Projection implements IOperator {

	public Projection() {

	}

	public void executeOperation(mxCell jCell, List<String> arguments) {

		OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

		OperationErrorType error = null;

		try {

			error = OperationErrorType.NO_PARENT;
			OperationErrorVerifier.hasParent(cell);
			
			error = OperationErrorType.NO_ONE_PARENT;
			OperationErrorVerifier.oneParent(cell);
			
			error = OperationErrorType.PARENT_ERROR;
			OperationErrorVerifier.noParentError(cell);
			
			error = OperationErrorType.NULL_ARGUMENT;
			OperationErrorVerifier.noNullArgument(arguments);

			error = OperationErrorType.EMPTY_ARGUMENT;
			OperationErrorVerifier.noEmptyArgument(arguments);

			error = OperationErrorType.PARENT_WITHOUT_COLUMN;
			OperationErrorVerifier.parentContainsColumns(cell.getParents().get(0).getColumnSourceNames(), arguments);
			error = null;

		} catch (TreeException e) {

			cell.setError(error);

		}
		
		if(error != null) return;

		Cell parentCell = cell.getParents().get(0);

		List<String> argumentsFixed = Column.putSource(arguments, parentCell);

		Operator operator = parentCell.getOperator();

		Operator readyOperator = new SelectColumnsOperator(operator, argumentsFixed);

		Operation.operationSetter(cell, "π  " + argumentsFixed, argumentsFixed, readyOperator);

	}

}
