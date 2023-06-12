package operations.binary;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.OperationType;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import operations.OperationErrorVerifier.ErrorMessage;
import sgbd.query.Operator;
import sgbd.query.binaryop.IntersectionOperator;

import java.util.ArrayList;
import java.util.List;

public class Intersection implements IOperator{

	public final OperationType type = OperationType.INTERSECTION;
	
	public Intersection() {

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

		selectedColumns1.replaceAll(s -> parentCell1.getSourceTableNameByColumn(s) + "." + s);
		selectedColumns2.replaceAll(s -> parentCell2.getSourceTableNameByColumn(s) + "." + s);

		Operator operator = new IntersectionOperator(table1, table2, selectedColumns1, selectedColumns2);

		Operation.operationSetter(cell, selectedColumns1.toString() + " " + type.getSymbol() + " " + selectedColumns2.toString(), arguments,
				operator);

	}

	
}
