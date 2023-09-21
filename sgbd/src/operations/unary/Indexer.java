package operations.unary;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.query.Operator;
import sgbd.query.agregation.CountAgregation;
import sgbd.query.binaryop.joins.NestedLoopJoin;
import sgbd.query.sourceop.TableScan;
import sgbd.query.unaryop.GroupOperator;
import sgbd.source.components.Header;
import sgbd.source.table.MemoryTable;
import sgbd.source.table.Table;

import java.util.List;

public class Indexer implements IOperator {

    private static int i = 1;
    private static int j = 1;

    public Indexer(){
    }

    @Override
    public void executeOperation(mxCell jCell, List<String> arguments) {

        OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

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

            error = OperationErrorType.EMPTY_ARGUMENT;
            OperationErrorVerifier.noEmptyArgument(arguments);

            error = null;

        } catch (TreeException e) {

            cell.setError(error);

        }

        if(error != null) return;

        Cell parentCell = cell.getParents().get(0);

        Operator operator = parentCell.getOperator();

        Prototype p = new Prototype();
        p.addColumn("column", 4, sgbd.prototype.column.Column.SIGNED_INTEGER_COLUMN | sgbd.prototype.column.Column.PRIMARY_KEY);

        Table table = MemoryTable.openTable(new Header(p, "aux"));
        table.open();

        RowData row = new RowData();
        row.setInt("column", 1);
        table.insert(row);

        Operator count = new NestedLoopJoin(operator, new TableScan(table), (t1, t2) -> true);

        count = new GroupOperator(count, "aux", "column", List.of(new CountAgregation("aux", "column")));
        count.open();

        int numberOfRows = count.next().getContent("aux").getInt("count(column)");
        count.close();

        Prototype p2 = new Prototype();
        p2.addColumn(arguments.get(0), 4, sgbd.prototype.column.Column.SIGNED_INTEGER_COLUMN | sgbd.prototype.column.Column.PRIMARY_KEY);

        Table table2 = MemoryTable.openTable(new Header(p2, "aux"));
        table2.open();
        for(int n = 1; n <= numberOfRows; n++) {
            RowData rowData = new RowData();
            rowData.setInt(arguments.get(0), n);
            table2.insert(rowData);
        }

        Operator readyOperator = new NestedLoopJoin(new TableScan(table2), operator, (t1, t2) -> {

            boolean foundIndex = i == t1.getContent("aux").getInt(arguments.get(0));
            boolean rightTuple = j == i;
            if(foundIndex) j++;

            if(foundIndex && rightTuple){

                i++;
                j = 1;

                if(i > numberOfRows)
                    i = j = 1;

                return true;

            }

            return false;

        });

        Operation.operationSetter(cell, cell.getType().SYMBOL + arguments, arguments, readyOperator);


    }


}
