package operations.binary.joins;

import booleanexpression.BooleanExpressionException;
import booleanexpression.BooleanExpressionRecognizer;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import controllers.ConstantController;
import controllers.MainController;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

            errorType = OperationErrorType.NO_ONE_ARGUMENT;
            OperationErrorVerifier.oneArgument(arguments);

            errorType = OperationErrorType.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            errorType = OperationErrorType.NO_TWO_PARENTS;
            OperationErrorVerifier.twoParents(cell);

            errorType = OperationErrorType.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

            errorType = OperationErrorType.SAME_SOURCE;
            OperationErrorVerifier.haveDifferentSources(cell.getParents().getFirst(), cell.getParents().get(1));

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell1 = cell.getParents().getFirst();
        Cell parentCell2 = cell.getParents().get(1);

        Operator operator1 = parentCell1.getOperator();
        Operator operator2 = parentCell2.getOperator();

        try {
            BooleanExpression booleanExpression = new BooleanExpressionRecognizer(jCell).recognizer(arguments.getFirst());
            Operator readyOperator = this.createJoinOperator(operator1, operator2, booleanExpression);
            String operationName = String.format("%s   %s", cell.getType().symbol, new BooleanExpressionRecognizer(jCell).recognizer(booleanExpression));
            Operation.operationSetter(cell, operationName, arguments, readyOperator);

        } catch (BooleanExpressionException exception) {
            cell.setError(exception.getMessage());
        }

        Object[] edges = MainController.getGraph().getEdges(jCell);

        MainController.getGraph().getModel().setValue(edges[0], ConstantController.getString("left"));
        MainController.getGraph().getModel().setValue(edges[1], ConstantController.getString("right"));
    }

    abstract Operator createJoinOperator(Operator operator1, Operator operator2, BooleanExpression booleanExpression);
}
