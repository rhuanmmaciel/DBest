package operations.binary;

import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxCell;

import entities.cells.Cell;
import entities.cells.OperationCell;
import exceptions.TreeException;
import gui.frames.forms.operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import operations.OperationErrorVerifier.ErrorMessage;
import sgbd.query.Operator;
import sgbd.query.binaryop.UnionOperator;

public class Union implements IOperator {

	public Union() {

	}

	public void executeOperation(mxCell jCell, List<String> arguments) {

		OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

		ErrorMessage error = null;

		try {

			error = ErrorMessage.NULL_ARGUMENT;
			OperationErrorVerifier.noNullArgument(arguments);

			error = ErrorMessage.NO_PARENT;
			OperationErrorVerifier.hasParent(cell);

			error = ErrorMessage.NO_TWO_PARENTS;
			OperationErrorVerifier.twoParents(cell);

			error = ErrorMessage.PARENT_ERROR;
			OperationErrorVerifier.noParentError(cell);

			error = null;
			
		} catch (TreeException e) {

			cell.setError(error);

		}

		if(error != null) return;

		Cell parentCell1 = cell.getParents().get(0);
		Cell parentCell2 = cell.getParents().get(1);

		Operator table1 = parentCell1.getOperator();
		Operator table2 = parentCell2.getOperator();

		List<String> selectedColumns1 = new ArrayList<>(arguments.subList(0, arguments.size() / 2));
		List<String> selectedColumns2 = new ArrayList<>(arguments.subList(arguments.size() / 2, arguments.size()));

		selectedColumns1.replaceAll(s -> parentCell1.getSourceTableName(s) + "." + s);
		selectedColumns2.replaceAll(s -> parentCell2.getSourceTableName(s) + "." + s);

		Operator operator = new UnionOperator(table1, table2, selectedColumns1, selectedColumns2);

		Operation.operationSetter(cell, selectedColumns1.toString() + " U " + selectedColumns2.toString(), arguments,
				operator);

	}

}
