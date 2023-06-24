package operations.unary;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.query.Operator;
import sgbd.query.unaryop.RenameSourceOperator;

import java.util.List;

public class Rename implements IOperator {

    public Rename(){

    }

    @Override
    public void executeOperation(mxCell jCell, List<String> arguments) {

        OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

        OperationErrorType error = null;

        try {

            error = OperationErrorType.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            error = OperationErrorType.NO_ONE_PARENT;
            OperationErrorVerifier.oneParent(cell);

            error = OperationErrorType.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

            error = OperationErrorType.NULL_ARGUMENT;
            OperationErrorVerifier.noNullArgument(arguments);

            error = OperationErrorType.EMPTY_ARGUMENT;
            OperationErrorVerifier.noEmptyArgument(arguments);

            error = null;

        } catch (TreeException e) {

            cell.setError(error);

        }

        if(error != null) return;

        Cell parentCell = cell.getParents().get(0);

        Operator operator = parentCell.getOperator();

        for(String name : arguments)
            operator = new RenameSourceOperator(operator, name.substring(0, name.indexOf(":")), name.substring(name.indexOf(":") + 1));

        Operation.operationSetter(cell, cell.getType().SYMBOL+" " + arguments, arguments, operator);

    }

}
