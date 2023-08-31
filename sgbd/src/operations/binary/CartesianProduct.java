package operations.binary;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.query.Operator;
import sgbd.query.binaryop.joins.NestedLoopJoin;

import java.util.List;

public class CartesianProduct implements IOperator {

	public CartesianProduct() {

	}

	public void executeOperation(mxCell jCell, List<String> arguments) {

		OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

		OperationErrorType error = null;

		try {

			error = OperationErrorType.NO_PARENT;
			OperationErrorVerifier.hasParent(cell);
			
			error = OperationErrorType.NO_TWO_PARENTS;
			OperationErrorVerifier.twoParents(cell);
			
			error = OperationErrorType.PARENT_ERROR;
			OperationErrorVerifier.noParentError(cell);

			error = OperationErrorType.SAME_SOURCE;
			OperationErrorVerifier.haveDifferentSources(cell.getParents().get(0), cell.getParents().get(1));

			error = null;
			
		} catch (TreeException e) {

			cell.setError(error);

		}
		
		if(error != null) return;
		
		Cell parentCell1 = cell.getParents().get(0);
		Cell parentCell2 = cell.getParents().get(1);

		Operator operator1 = parentCell1.getOperator();
		Operator operator2 = parentCell2.getOperator();

		Operator readyOperator = new NestedLoopJoin(operator1, operator2);

		Operation.operationSetter(cell, "  X  ", List.of(), readyOperator);

	}
}
