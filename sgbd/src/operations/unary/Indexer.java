package operations.unary;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.prototype.metadata.Metadata;
import sgbd.query.Operator;
import sgbd.query.agregation.CountAgregation;
import sgbd.query.binaryop.joins.NestedLoopJoin;
import sgbd.query.sourceop.TableScan;
import sgbd.query.unaryop.GroupOperator;
import sgbd.source.components.Header;
import sgbd.source.table.MemoryTable;
import sgbd.source.table.Table;

import java.util.List;
import java.util.Optional;

public class Indexer implements IOperator {

    private static int i = 1;

    private static int j = 1;

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

            errorType = OperationErrorType.EMPTY_ARGUMENT;
            OperationErrorVerifier.noEmptyArgument(arguments);

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell = cell.getParents().getFirst();

        Operator operator = parentCell.getOperator();

        Prototype prototype1 = new Prototype();
        prototype1.addColumn("column", 4, Metadata.SIGNED_INTEGER_COLUMN | Metadata.PRIMARY_KEY);

        Table table = MemoryTable.openTable(new Header(prototype1, "aux"));
        table.open();

        RowData row = new RowData();
        row.setInt("column", 1);
        table.insert(row);

        Operator count = new NestedLoopJoin(operator, new TableScan(table), (t1, t2) -> true);

        count = new GroupOperator(count, "aux", "column", List.of(new CountAgregation("aux", "column")));
        count.open();

        int numberOfRows = count.next().getContent("aux").getInt("count(column)");
        count.close();

        Prototype prototype2 = new Prototype();
        prototype2.addColumn(arguments.getFirst(), 4, Metadata.SIGNED_INTEGER_COLUMN | Metadata.PRIMARY_KEY);

        Table table2 = Table.openTable(new Header(prototype2, "aux"));
        table2.open();

        for (int n = 1; n <= numberOfRows; n++) {
            RowData rowData = new RowData();
            rowData.setInt(arguments.getFirst(), n);
            table2.insert(rowData);
        }

        Operator readyOperator = new NestedLoopJoin(new TableScan(table2), operator, (tuple1, tuple2) -> {
            boolean foundIndex = i == tuple1.getContent("aux").getInt(arguments.getFirst());
            boolean rightTuple = j == i;

            if (foundIndex) {
                j++;
            }

            if (foundIndex && rightTuple) {
                i++;
                j = 1;

                if (i > numberOfRows) {
                    i = j = 1;
                }

                return true;
            }

            return false;
        });

        String operationName = String.format("%s %s", cell.getType().symbol, arguments);

        Operation.operationSetter(cell, operationName, arguments, readyOperator);
    }
}
