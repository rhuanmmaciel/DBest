package operations.unary;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.OperationErrorVerifier;

import java.util.List;

public class Rename implements IOperator {

    public Rename(){

    }

    @Override
    public void executeOperation(mxCell jCell, List<String> arguments) {

        OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

        OperationErrorVerifier.ErrorMessage error = null;

        try {

            error = OperationErrorVerifier.ErrorMessage.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            error = OperationErrorVerifier.ErrorMessage.NO_ONE_PARENT;
            OperationErrorVerifier.oneParent(cell);

            error = OperationErrorVerifier.ErrorMessage.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

            error = OperationErrorVerifier.ErrorMessage.NULL_ARGUMENT;
            OperationErrorVerifier.noNullArgument(arguments);

            error = OperationErrorVerifier.ErrorMessage.PARENT_WITHOUT_COLUMN;
            OperationErrorVerifier.parentContainsColumns(cell.getParents().get(0).getColumnSourceNames(), arguments);
            error = null;

        } catch (TreeException e) {

            cell.setError(error);

        }
    }

}
