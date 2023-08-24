package operations.unary;

import booleanexpression.BooleanExpressionRecognizer;
import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import lib.booleanexpression.entities.expressions.AtomicExpression;
import lib.booleanexpression.entities.expressions.BooleanExpression;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.query.Operator;
import sgbd.query.unaryop.FilterOperator;

import java.util.List;

public class Selection implements IOperator {

	public Selection() {

	}

	public void executeOperation(mxCell jCell, List<String> arguments) {

		OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

		OperationErrorType error = null;
		
		try {
			
			error = OperationErrorType.NULL_ARGUMENT;
			OperationErrorVerifier.noNullArgument(arguments);
			
			error = OperationErrorType.NO_ONE_ARGUMENT;
			OperationErrorVerifier.oneArgument(arguments);
			
			error = OperationErrorType.NO_PARENT;
			OperationErrorVerifier.hasParent(cell);
			
			error = OperationErrorType.NO_ONE_PARENT;
			OperationErrorVerifier.oneParent(cell);
			
			error = OperationErrorType.PARENT_ERROR;
			OperationErrorVerifier.noParentError(cell);
			
			error = null;

		} catch (TreeException e) {

			cell.setError(error);
		
		}
		
		if(error != null) return;
		
		Cell parentCell = cell.getParents().get(0);
		String expression = arguments.get(0);

		BooleanExpression booleanExpression = new BooleanExpressionRecognizer().recognizer(expression);

		Operator operator = parentCell.getOperator();
		operator = new FilterOperator(operator, booleanExpression);

		Operation.operationSetter(cell, cell.getType().SYMBOL + "  " + new BooleanExpressionRecognizer().recognizer(booleanExpression),
				arguments, operator);

	}

}
