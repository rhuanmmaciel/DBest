package operations.unary;

import java.util.List;
import java.util.Optional;

import com.mxgraph.model.mxCell;

import booleanexpression.BooleanExpressionException;
import booleanexpression.BooleanExpressionRecognizer;

import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;

import enums.OperationErrorType;

import exceptions.tree.TreeException;

import lib.booleanexpression.entities.expressions.BooleanExpression;

import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;

import sgbd.query.Operator;
import sgbd.query.unaryop.FilterOperator;

public class Selection implements IOperator {

    @Override
    public void executeOperation(mxCell jCell, List<String> arguments) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) return;

        OperationCell cell = (OperationCell) optionalCell.get();
        OperationErrorType errorType = null;

        try {
            errorType = OperationErrorType.NULL_ARGUMENT;
            OperationErrorVerifier.noNullArgument(arguments);

            errorType = OperationErrorType.NO_ONE_ARGUMENT;
            OperationErrorVerifier.oneArgument(arguments);

            errorType = OperationErrorType.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            errorType = OperationErrorType.NO_ONE_PARENT;
            OperationErrorVerifier.oneParent(cell);

            errorType = OperationErrorType.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell = cell.getParents().get(0);

        String expression = arguments.get(0);

        try {
            BooleanExpression booleanExpression = new BooleanExpressionRecognizer(jCell).recognizer(expression);

            Operator operator = parentCell.getOperator();
            operator = new FilterOperator(operator, booleanExpression);

            String operationName = String.format("%s  %s", cell.getType().symbol, booleanExpression);

            Operation.operationSetter(cell, operationName, arguments, operator);
        } catch (BooleanExpressionException exception) {
            cell.setError(exception.getMessage());
        }
    }
}
