package operations.binary;

import java.util.List;
import java.util.Optional;

import com.mxgraph.model.mxCell;

import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;

import enums.OperationErrorType;

import exceptions.tree.TreeException;

import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;

import sgbd.query.Operator;
import sgbd.query.binaryop.joins.NestedLoopJoin;

public class CartesianProduct implements IOperator {

    @Override
    public void executeOperation(mxCell jCell, List<String> arguments) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) return;

        OperationCell cell = (OperationCell) optionalCell.get();
        OperationErrorType errorType = null;

        try {
            errorType = OperationErrorType.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            errorType = OperationErrorType.NO_TWO_PARENTS;
            OperationErrorVerifier.twoParents(cell);

            errorType = OperationErrorType.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

            errorType = OperationErrorType.SAME_SOURCE;
            OperationErrorVerifier.haveDifferentSources(cell.getParents().get(0), cell.getParents().get(1));

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell1 = cell.getParents().get(0);
        Cell parentCell2 = cell.getParents().get(1);

        Operator operator1 = parentCell1.getOperator();
        Operator operator2 = parentCell2.getOperator();

        Operator readyOperator = new NestedLoopJoin(operator1, operator2);

        Operation.operationSetter(cell, "  X  ", List.of(), readyOperator);
    }
}
