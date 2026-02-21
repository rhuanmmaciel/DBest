package operations.unary;

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
import utils.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Sort implements IOperator {

    public static final List<String> PREFIXES = List.of("ASC:", "DESC:");

    @Override
    @SuppressWarnings(value = "deprecation")
    public void executeOperation(mxCell jCell, List<String> arguments, String alias) {
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

        ibd.query.Operation operator = parentCell.getOperator();

        String column = arguments.get(0);

        boolean isAscendingOrder = !Utils.startsWithIgnoreCase(column, "DESC:");

        column = Utils.replaceIfStartsWithIgnoreCase(column, PREFIXES, "");

        boolean hasSource = Column.hasSource(column);
        String sourceName = hasSource ? Column.removeName(column) : parentCell.getSourceNameByColumnName(column);
        String columnName = hasSource ? Column.removeSource(column) : column;

        String prefix = isAscendingOrder ? "ASC:" : "DESC:";
        arguments = List.of(prefix + Column.composeSourceAndName(sourceName, columnName));

        ibd.query.Operation readyOperator = null;
        try {
            readyOperator = new ibd.query.unaryop.sort.Sort(operator, sourceName+"."+columnName, isAscendingOrder);
        } catch (Exception ex) {
        }

        String operationName = String.format("%s %s", cell.getType().symbol, arguments);

        Operation.operationSetter(cell, operationName, arguments, readyOperator);
    }
}
