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

public class AutoIncrement implements IOperator {


    @Override
    @SuppressWarnings(value = "deprecation")
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

            errorType = OperationErrorType.NULL_ARGUMENT;
            OperationErrorVerifier.noNullArgument(arguments);

            
            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell = cell.getParents().get(0);

        ibd.query.Operation operator = parentCell.getOperator();

        String column = arguments.get(0);
        
        int increment = 1;
        
        if (arguments.size()>1)
            increment = Integer.parseInt(arguments.get(1));

        ibd.query.Operation readyOperator = null;
        try {
            readyOperator = new ibd.query.unaryop.AutoIncrement(operator, "autoIncrement", column, increment);
        } catch (Exception ex) {
        }

        String operationName = String.format("%s %s", cell.getType().symbol, arguments);

        Operation.operationSetter(cell, operationName, arguments, readyOperator);
    }
}
