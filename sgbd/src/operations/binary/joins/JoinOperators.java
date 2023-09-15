package operations.binary.joins;

import java.util.List;
import java.util.Optional;

import com.mxgraph.model.mxCell;

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

public abstract class JoinOperators implements IOperator {

    @Override
    public void executeOperation(mxCell jCell, List<String> arguments) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) return;

        OperationCell cell = (OperationCell) optionalCell.get();
        OperationErrorType errorType = null;

        try {
            errorType = OperationErrorType.NULL_ARGUMENT;
            OperationErrorVerifier.noNullArgument(arguments);

            errorType = OperationErrorType.NO_TWO_ARGUMENTS;
            OperationErrorVerifier.twoArguments(arguments);

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

        BooleanExpression booleanExpression = new BooleanExpressionRecognizer().recognizer(arguments.get(0));

        Operator readyOperator = this.createJoinOperator(operator1, operator2, booleanExpression);

        String operationName = String.format("%s   %s", cell.getType().symbol, new BooleanExpressionRecognizer().recognizer(booleanExpression));

        Operation.operationSetter(cell, operationName, arguments, readyOperator);
    }

    abstract Operator createJoinOperator(Operator operator1, Operator operator2, BooleanExpression booleanExpression);
}
