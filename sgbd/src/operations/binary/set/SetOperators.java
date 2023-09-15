package operations.binary.set;

import java.util.ArrayList;
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

public abstract class SetOperators implements IOperator {

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

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell1 = cell.getParents().get(0);
        Cell parentCell2 = cell.getParents().get(1);

        Operator operator1 = parentCell1.getOperator();
        Operator operator2 = parentCell2.getOperator();

        int numberOfColumns = Math.min(parentCell1.getColumns().size(), parentCell2.getColumns().size());

        List<String> selectedColumns1 = new ArrayList<>(parentCell1.getColumnSourcesAndNames().stream().limit(numberOfColumns).toList());
        List<String> selectedColumns2 = new ArrayList<>(parentCell2.getColumnSourcesAndNames().stream().limit(numberOfColumns).toList());

        Operator readyOperator = this.createSetOperator(operator1, operator2, selectedColumns1, selectedColumns2);

        String operationName = String.format("   %s   ", cell.getType().symbol);

        Operation.operationSetter(cell, operationName, arguments, readyOperator);
    }

    abstract Operator createSetOperator(Operator operator1, Operator operator2, List<String> columns1, List<String> columns2);
}
