package operations.binary.joins;

import com.mxgraph.model.mxCell;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.query.Operator;

import java.util.ArrayList;
import java.util.List;

public abstract class Joins implements IOperator {

    public void executeOperation(mxCell jCell, List<String> arguments) {

        OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

        OperationErrorVerifier.ErrorMessage error = null;

        try {

            error = OperationErrorVerifier.ErrorMessage.NULL_ARGUMENT;
            OperationErrorVerifier.noNullArgument(arguments);

            error = OperationErrorVerifier.ErrorMessage.NO_TWO_ARGUMENTS;
            OperationErrorVerifier.twoArguments(arguments);

            error = OperationErrorVerifier.ErrorMessage.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            error = OperationErrorVerifier.ErrorMessage.NO_TWO_PARENTS;
            OperationErrorVerifier.twoParents(cell);

            error = OperationErrorVerifier.ErrorMessage.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

            error = OperationErrorVerifier.ErrorMessage.PARENT_WITHOUT_COLUMN;
            OperationErrorVerifier.parentContainsColumns(Column.sourceAndNameTogether(cell.getParents().get(0).getColumns()), List.of(arguments.get(0)));
            OperationErrorVerifier.parentContainsColumns(Column.sourceAndNameTogether(cell.getParents().get(1).getColumns()), List.of(arguments.get(1)));

            error = null;

        } catch (TreeException e) {

            cell.setError(error);

        }

        if(error != null) return;

        Cell parentCell1 = cell.getParents().get(0);
        Cell parentCell2 = cell.getParents().get(1);

        List<String> argumentsFixed = new ArrayList<>();

        argumentsFixed.add(Column.putSource(arguments.get(0), parentCell1.getSourceTableNameByColumn(arguments.get(0))));
        argumentsFixed.add(Column.putSource(arguments.get(1), parentCell2.getSourceTableNameByColumn(arguments.get(1))));

        Operator op1 = parentCell1.getOperator();
        Operator op2 = parentCell2.getOperator();

        String source1 = Column.removeName(argumentsFixed.get(0));
        String source2 = Column.removeName(argumentsFixed.get(1));

        String item1 = Column.removeSource(argumentsFixed.get(0));
        String item2 = Column.removeSource(argumentsFixed.get(1));;

        Operator operator = createJoinOperator(op1, op2, source1, source2, item1, item2);

        Operation.operationSetter(cell, "|X|   " + Column.putSource(item1, source1) + " = " + Column.putSource(item2, source2), argumentsFixed, operator);

    }

    abstract Operator createJoinOperator(Operator op1, Operator op2, String source1,
                                         String source2, String item1, String item2);

}
