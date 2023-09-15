package operations.unary;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.mxgraph.model.mxCell;

import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;

import enums.OperationErrorType;

import exceptions.tree.TreeException;

import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;

import sgbd.query.Operator;
import sgbd.query.unaryop.SortOperator;

import utils.Utils;

public class Sort implements IOperator {

    public static final List<String> PREFIXES = List.of("ASC:", "DESC:");

    @Override
    @SuppressWarnings(value = "deprecation")
    public void executeOperation(mxCell jCell, List<String> arguments) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) return;

        OperationCell cell = (OperationCell) optionalCell.get();
        OperationErrorType errorType = null;

        try {
            errorType = OperationErrorType.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            errorType = OperationErrorType.NO_ONE_PARENT;
            OperationErrorVerifier.oneParent(cell);

            errorType = OperationErrorType.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

            errorType = OperationErrorType.NULL_ARGUMENT;
            OperationErrorVerifier.noNullArgument(arguments);

            errorType = OperationErrorType.PARENT_WITHOUT_COLUMN;
            OperationErrorVerifier.parentContainsColumns(
                cell.getParents().get(0).getColumnSourcesAndNames(),
                Collections.singletonList(
                    Utils.replaceIfStartsWithIgnoreCase(arguments.get(0), PREFIXES, ""))
            );

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell = cell.getParents().get(0);

        Operator operator = parentCell.getOperator();

        String column = arguments.get(0);

        boolean isAscendingOrder = !Utils.startsWithIgnoreCase(column, "DESC:");

        column = Utils.replaceIfStartsWithIgnoreCase(column, PREFIXES, "");

        boolean hasSource = Column.hasSource(column);
        String sourceName = hasSource ? Column.removeName(column) : parentCell.getSourceNameByColumnName(column);
        String columnName = hasSource ? Column.removeSource(column) : column;

        String prefix = isAscendingOrder ? "ASC:" : "DESC:";
        arguments = List.of(prefix + Column.composeSourceAndName(sourceName, columnName));

        Operator readyOperator = new SortOperator(operator, (entries, tuple1) -> {
            switch (Utils.getColumnDataType(tuple1, sourceName, columnName)) {
                case INTEGER -> {
                    Integer integer1 = entries.getContent(sourceName).getInt(columnName);
                    Integer integer2 = tuple1.getContent(sourceName).getInt(columnName);

                    if (isAscendingOrder) {
                        return integer1.compareTo(integer2);
                    } else {
                        return integer2.compareTo(integer1);
                    }
                }
                case LONG -> {
                    Long long1 = entries.getContent(sourceName).getLong(columnName);
                    Long long2 = tuple1.getContent(sourceName).getLong(columnName);

                    if (isAscendingOrder) {
                        return long1.compareTo(long2);
                    } else {
                        return long2.compareTo(long1);
                    }
                }
                case FLOAT -> {
                    Float float1 = entries.getContent(sourceName).getFloat(columnName);
                    Float float2 = tuple1.getContent(sourceName).getFloat(columnName);

                    if (isAscendingOrder) {
                        return float1.compareTo(float2);
                    } else {
                        return float2.compareTo(float1);
                    }
                }
                case DOUBLE -> {
                    Double double1 = entries.getContent(sourceName).getDouble(columnName);
                    Double double2 = tuple1.getContent(sourceName).getDouble(columnName);

                    if (isAscendingOrder) {
                        return double1.compareTo(double2);
                    } else {
                        return double2.compareTo(double1);
                    }
                }
                default -> {
                    String string1 = tuple1.getContent(sourceName).getString(columnName);
                    String string2 = entries.getContent(sourceName).getString(columnName);

                    if (isAscendingOrder) {
                        return string1.compareTo(string2);
                    } else {
                        return string2.compareTo(string1);
                    }
                }
            }
        });

        String operationName = String.format("%s %s", cell.getType().symbol, arguments);

        Operation.operationSetter(cell, operationName, arguments, readyOperator);
    }
}
