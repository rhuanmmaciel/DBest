package operations.binary.joins;

import booleanexpression.BooleanExpressionException;
import booleanexpression.BooleanExpressionRecognizer;
import com.mxgraph.model.mxCell;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.ColumnDataType;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import lib.booleanexpression.entities.expressions.BooleanExpression;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.prototype.RowData;
import sgbd.prototype.query.Tuple;
import sgbd.query.Operator;
import util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class JoinOperators implements IOperator {

    public void executeOperation(mxCell jCell, List<String> arguments) {

        OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

        OperationErrorType error = null;

        try {

            error = OperationErrorType.NULL_ARGUMENT;
            OperationErrorVerifier.noNullArgument(arguments);

            error = OperationErrorType.NO_ONE_ARGUMENT;
            OperationErrorVerifier.oneArgument(arguments);

            error = OperationErrorType.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            error = OperationErrorType.NO_TWO_PARENTS;
            OperationErrorVerifier.twoParents(cell);

            error = OperationErrorType.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

            error = OperationErrorType.SAME_SOURCE;
            OperationErrorVerifier.haveDifferentSources(cell.getParents().get(0), cell.getParents().get(1));

            error = null;

        } catch (TreeException e) {

            cell.setError(error);

        }

        if(error != null) return;

        Cell parentCell1 = cell.getParents().get(0);
        Cell parentCell2 = cell.getParents().get(1);

        Operator op1 = parentCell1.getOperator();
        Operator op2 = parentCell2.getOperator();

        try {

            BooleanExpression booleanExpression = new BooleanExpressionRecognizer(jCell).recognizer(arguments.get(0));

            Operator readyoperator = createJoinOperator(op1, op2, booleanExpression);

            Operation.operationSetter(cell, cell.getType().SYMBOL + "   " + new BooleanExpressionRecognizer(jCell).recognizer(booleanExpression),
                    arguments, readyoperator);

        }catch (BooleanExpressionException e){

            cell.setError(e.getMessage());

        }
    }

    abstract Operator createJoinOperator(Operator op1, Operator op2, BooleanExpression booleanExpression);

}
