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

    public static List<String> PREFIXES = List.of("ASC:", "DESC:");

    public Sort() {

    }

    public void executeOperation(mxCell jCell, List<String> arguments) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) return;

        OperationCell cell = (OperationCell) optionalCell.get();
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

            error = OperationErrorType.PARENT_WITHOUT_COLUMN;
            OperationErrorVerifier.parentContainsColumns(cell.getParents().get(0).getColumnSourcesAndNames(),
                Collections.singletonList(
                    Utils.replaceIfStartsWithIgnoreCase(arguments.get(0), PREFIXES, "")));
            error = null;

        } catch (TreeException e) {

            cell.setError(error);

        }

        if (error != null) return;

        Cell parentCell = cell.getParents().get(0);

        Operator operator = parentCell.getOperator();

        String column = arguments.get(0);

        boolean ascendingOrder = !Utils.startsWithIgnoreCase(column, "DESC:");

        column = Utils.replaceIfStartsWithIgnoreCase(column, PREFIXES, "");

        boolean hasSource = Column.hasSource(column);
        String sourceName = hasSource ? Column.removeName(column)
            : parentCell.getSourceNameByColumnName(column);
        String columnName = hasSource ? Column.removeSource(column)
            : column;

        String prefix = ascendingOrder ? "ASC:" : "DESC:";
        arguments = List.of(prefix + Column.composeSourceAndName(sourceName, columnName));

        Operator readyOperator = new SortOperator(operator, (entries, t1) -> {

            switch (Utils.getColumnDataType(t1, sourceName, columnName)) {
                case INTEGER -> {

                    Integer n1 = entries.getContent(sourceName).getInt(columnName);
                    Integer n2 = t1.getContent(sourceName).getInt(columnName);

                    if (ascendingOrder) return n1.compareTo(n2);

                    return n2.compareTo(n1);

                }
                case LONG -> {

                    Long n1 = entries.getContent(sourceName).getLong(columnName);
                    Long n2 = t1.getContent(sourceName).getLong(columnName);

                    if (ascendingOrder) return n1.compareTo(n2);

                    return n2.compareTo(n1);

                }
                case FLOAT -> {

                    Float n1 = entries.getContent(sourceName).getFloat(columnName);
                    Float n2 = t1.getContent(sourceName).getFloat(columnName);

                    if (ascendingOrder) return n1.compareTo(n2);

                    return n2.compareTo(n1);

                }
                case DOUBLE -> {

                    Double n1 = entries.getContent(sourceName).getDouble(columnName);
                    Double n2 = t1.getContent(sourceName).getDouble(columnName);

                    if (ascendingOrder) return n1.compareTo(n2);

                    return n2.compareTo(n1);

                }
                default -> {

                    String w1 = t1.getContent(sourceName).getString(columnName);
                    String w2 = entries.getContent(sourceName).getString(columnName);

                    if (ascendingOrder) return w2.compareTo(w1);

                    return w1.compareTo(w2);

                }
            }

        });

        Operation.operationSetter(cell, cell.getType().symbol + arguments.toString(), arguments, readyOperator);

    }

}
