package operations.binary.joins;

import com.mxgraph.model.mxCell;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.ColumnDataType;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.util.statitcs.Util;
import util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class JoinOperators implements IOperator {

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
            OperationErrorVerifier.parentContainsColumns(cell.getParents().get(0).getColumnSourceNames(), List.of(arguments.get(0)));
            OperationErrorVerifier.parentContainsColumns(cell.getParents().get(1).getColumnSourceNames(), List.of(arguments.get(1)));

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
        String item2 = Column.removeSource(argumentsFixed.get(1));

        Operator readyoperator = createJoinOperator(op1, op2, source1, source2, item1, item2);

        Operation.operationSetter(cell, cell.getType().getSymbol()+"   " + Column.putSource(item1, source1) + " = " + Column.putSource(item2, source2), argumentsFixed, readyoperator);

    }

    public boolean compare(Tuple t1, String source1, String item1, Tuple t2, String source2, String item2) {

        ColumnDataType type1 = Utils.getType(t1, source1, item1);
        ColumnDataType type2 = Utils.getType(t2, source2, item2);

        ComplexRowData row1 = t1.getContent(source1);
        ComplexRowData row2 = t2.getContent(source2);

        switch (type1){
            case INTEGER -> {

                Number n1 = row1.getInt(item1);

                Object inf2 = type2.equals(ColumnDataType.INTEGER) ? row2.getInt(item2)
                        : type2.equals(ColumnDataType.FLOAT) ? row2.getFloat(item2)
                        : row2.getString(item2);

                return inf2 instanceof Number n2 && Utils.compareNumbers(n1, n2);

            }
            case FLOAT -> {

                Number n1 = row1.getFloat(item1);

                Object inf2 = type2.equals(ColumnDataType.INTEGER) ? row2.getInt(item2)
                        : type2.equals(ColumnDataType.FLOAT) ? row2.getFloat(item2)
                        : row2.getString(item2);

                return inf2 instanceof Number n2 && Utils.compareNumbers(n1, n2);

            }
            default -> {

                return Objects.equals(row1.getString(item1), row2.getString(item2));

            }

        }

    }

    abstract Operator createJoinOperator(Operator op1, Operator op2, String source1,
                                         String source2, String item1, String item2);

}
