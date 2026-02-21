package operations.unary;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import ibd.query.unaryop.SourceRename;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Rename implements IOperator {

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

            errorType = OperationErrorType.NULL_ARGUMENT;
            OperationErrorVerifier.noNullArgument(arguments);

            errorType = OperationErrorType.EMPTY_ARGUMENT;
            OperationErrorVerifier.noEmptyArgument(arguments);

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell = cell.getParents().get(0);

        ibd.query.Operation operator = parentCell.getOperator();

        for (String name : arguments) {
            try {
                //operator = new RenameSourceOperator(operator, name.substring(0, name.indexOf(":")), name.substring(name.indexOf(":") + 1));
                operator = new SourceRename(operator, name.substring(0, name.indexOf(":")), name.substring(name.indexOf(":") + 1));
            } catch (Exception ex) {
                Logger.getLogger(Rename.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String operationName = String.format("%s %s", cell.getType().symbol, arguments);

        Operation.operationSetter(cell, operationName, arguments, operator);
    }
}
