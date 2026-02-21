package operations.unary;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import java.util.List;
import java.util.Optional;

public class HashDuplicateRemoval implements IOperator {


    @Override
    public void executeOperation(mxCell jCell, List<String> arguments, String alias) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) return;

        OperationCell cell = (OperationCell) optionalCell.get();
        OperationErrorType errorType = null;

        try {
            errorType = OperationErrorType.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            errorType = OperationErrorType.NO_ONE_PARENT;
            OperationErrorVerifier.oneParent(cell);

            errorType = OperationErrorType.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

            errorType = OperationErrorType.PARENT_WITHOUT_COLUMN;

            OperationErrorVerifier.parentContainsColumns(
                cell.getParents().get(0).getColumnSourcesAndNames(), arguments.stream().limit(1).toList()
            );

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell = cell.getParents().get(0);

        ibd.query.Operation operator = parentCell.getOperator();
        //ibd.query.Operation readyOperator = new GroupOperator(operator, Column.removeName(groupBy), Column.removeSource(groupBy), aggregations);
        ibd.query.Operation readyOperator = null;
        try {
            readyOperator = new ibd.query.unaryop.HashDuplicateRemoval(operator);
        } catch (Exception ex) {
            cell.setError(ex.getMessage());
            return;
        }

        String operationName = String.format("%s %s", cell.getType().symbol, arguments);

        Operation.operationSetter(cell, operationName, arguments, readyOperator);
    }
}
