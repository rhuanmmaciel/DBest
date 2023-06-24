package operations.binary.set;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.query.Operator;

import java.util.ArrayList;
import java.util.List;

public abstract class SetOperators implements IOperator {


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

            error = null;

        } catch (TreeException e) {

            cell.setError(error);

        }

        if(error != null) return;

        Cell parentCell1 = cell.getParents().get(0);
        Cell parentCell2 = cell.getParents().get(1);

        Operator operator1 = parentCell1.getOperator();
        Operator operator2 = parentCell2.getOperator();

        int numberOfColumns = Math.min(parentCell1.getColumns().size(), parentCell2.getColumns().size());

        List<String> selectedColumns1 = new ArrayList<>(parentCell1.getColumnSourceNames().stream().limit(numberOfColumns).toList());
        List<String> selectedColumns2 = new ArrayList<>(parentCell2.getColumnSourceNames().stream().limit(numberOfColumns).toList());

        Operator readyOperator = createSetOperator(operator1, operator2, selectedColumns1, selectedColumns2);

        Operation.operationSetter(cell, "   "+cell.getType().SYMBOL+"   ", arguments, readyOperator);

    }

    abstract Operator createSetOperator(Operator op1, Operator op2, List<String> columns1, List<String> columns2);

}
