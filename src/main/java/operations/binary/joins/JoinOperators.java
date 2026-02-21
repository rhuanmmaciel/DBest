package operations.binary.joins;

import com.mxgraph.model.mxCell;

import controllers.ConstantController;
import controllers.MainController;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import ibd.query.binaryop.join.JoinPredicate;
import lib.booleanexpression.entities.expressions.BooleanExpression;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;

import java.util.List;
import java.util.Optional;

public abstract class JoinOperators implements IOperator {

    @Override
    public void executeOperation(mxCell jCell, List<String> arguments, String alias) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) return;

        OperationCell cell = (OperationCell) optionalCell.get();
        OperationErrorType errorType = null;

        try {
            //errorType = OperationErrorType.NULL_ARGUMENT;
            //OperationErrorVerifier.noNullArgument(arguments);

//            errorType = OperationErrorType.NO_ONE_ARGUMENT;
//            OperationErrorVerifier.oneArgument(arguments);

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

        ibd.query.Operation operator1 = parentCell1.getOperator();
        ibd.query.Operation operator2 = parentCell2.getOperator();

        try {
            //BooleanExpression booleanExpression = new BooleanExpressionRecognizer(jCell).recognizer(arguments.get(0));
            JoinPredicate joinPredicate = ibd.query.binaryop.join.Join.createJoinPredicate(arguments);
            ibd.query.Operation readyOperator = this.createJoinOperator(operator1, operator2, joinPredicate);
            //String operationName = String.format("%s   %s", cell.getType().symbol, new BooleanExpressionRecognizer(jCell).recognizer(booleanExpression));
            String operationName = String.format("%s   %s", cell.getType().symbol, getTextualJoinPredicate(arguments));
            Operation.operationSetter(cell, operationName, arguments, readyOperator);

        //} catch (BooleanExpressionException exception) {
        } catch (Exception exception) {
            cell.setError(exception.getMessage());
        }
        Object[] edges = MainController.getGraph().getIncomingEdges(jCell);

        MainController.getGraph().getModel().setValue(edges[0], ConstantController.getString("left"));
        MainController.getGraph().getModel().setValue(edges[1], ConstantController.getString("right"));
    }
    
    
    
    private String getTextualJoinPredicate(List<String> arguments) {
    if (arguments == null || arguments.isEmpty()) {
        return "";
    }
    return String.join(" and ", arguments);
}

    abstract ibd.query.Operation createJoinOperator(ibd.query.Operation operator1, ibd.query.Operation operator2, BooleanExpression booleanExpression);
    abstract ibd.query.Operation createJoinOperator(ibd.query.Operation operator1, ibd.query.Operation operator2, JoinPredicate joinPredicate);
}
