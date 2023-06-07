package operations.unary;

import java.util.List;

import com.mxgraph.model.mxCell;

import entities.cells.Cell;
import entities.cells.OperationCell;
import exceptions.tree.TreeException;
import gui.frames.forms.operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import operations.OperationErrorVerifier.ErrorMessage;
import sgbd.query.Operator;
import sgbd.query.unaryop.FilterColumnsOperator;
import sgbd.table.Table;

public class Projection implements IOperator {

	public Projection() {

	}

	public void executeOperation(mxCell jCell, List<String> arguments) {

		OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

		ErrorMessage error = null; 
		
		try {
			
			error = ErrorMessage.NO_PARENT;
			OperationErrorVerifier.hasParent(cell);
			
			error = ErrorMessage.NO_ONE_PARENT;
			OperationErrorVerifier.oneParent(cell);
			
			error = ErrorMessage.PARENT_ERROR;
			OperationErrorVerifier.noParentError(cell);
			
			error = ErrorMessage.NULL_ARGUMENT;
			OperationErrorVerifier.noNullArgument(arguments);
			
			error = ErrorMessage.PARENT_WITHOUT_COLUMN;
			OperationErrorVerifier.parentContainsColumns(cell.getParents().get(0).getColumnsName(), arguments);
			error = null;

		} catch (TreeException e) {

			cell.setError(error);

		}
		
		if(error != null) return;

		Cell parentCell = cell.getParents().get(0);

		List<String> aux = parentCell.getColumnsName();
		aux.removeAll(arguments);

		Operator operator = parentCell.getOperator();

		for (Table table : parentCell.getOperator().getSources()) {

			operator = new FilterColumnsOperator(operator, table.getTableName(), aux);

		}

		Operation.operationSetter(cell, "π  " + arguments.toString(), arguments, operator);

	}

}