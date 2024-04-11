package operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
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
import sgbd.query.agregation.*;
import sgbd.query.binaryop.joins.NestedLoopJoin;
import sgbd.query.sourceop.TableScan;
import sgbd.query.unaryop.FilterColumnsOperator;
import sgbd.query.unaryop.GroupOperator;
import sgbd.source.components.Header;
import sgbd.source.table.MemoryTable;
import sgbd.source.table.Table;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Aggregation implements IOperator {

    public enum Function {
        MAX {
            public String getDisplayName() {
                return ConstantController.getString("operationForm.maximum");
            }

            public String getPrefix() {
                return "MAX:";
            }
        }, MIN {
            public String getDisplayName() {
                return ConstantController.getString("operationForm.minimum");
            }

            public String getPrefix() {
                return "MIN:";
            }
        }, AVG {
            public String getDisplayName() {
                return ConstantController.getString("operationForm.average");
            }

            public String getPrefix() {
                return "AVG:";
            }
        }, COUNT {
            public String getDisplayName() {
                return ConstantController.getString("operationForm.count");
            }

            public String getPrefix() {
                return "COUNT:";
            }
        };

        public abstract String getDisplayName();

        public abstract String getPrefix();
    }

    public static final List<String> PREFIXES = Arrays.stream(Function.values()).map(Function::getPrefix).toList();

    @Override
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

            errorType = OperationErrorType.NO_ONE_ARGUMENT;
            OperationErrorVerifier.oneArgument(arguments);

            errorType = OperationErrorType.PARENT_WITHOUT_COLUMN;
            OperationErrorVerifier.parentContainsColumns(
                cell.getParents().get(0).getColumnSourcesAndNames(),
                arguments.stream().map(x -> Utils.replaceIfStartsWithIgnoreCase(x, PREFIXES, "")).toList(),
                List.of("*")
            );

            errorType = OperationErrorType.NO_PREFIX;
            OperationErrorVerifier.everyoneHavePrefix(arguments, PREFIXES);

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell = cell.getParents().get(0);

        Operator operator = parentCell.getOperator();

        String fixedArgument = arguments
            .get(0)
            .substring(0, Utils.getFirstMatchingPrefixIgnoreCase(arguments.get(0), PREFIXES).length()) + Column.composeSourceAndName(parentCell.getSourceNameByColumnName(arguments.get(0).substring(Utils.getFirstMatchingPrefixIgnoreCase(arguments.get(0), PREFIXES).length())), arguments.get(0).substring(Utils.getFirstMatchingPrefixIgnoreCase(arguments.get(0), PREFIXES).length()));

        String sourceName = Column.removeName(fixedArgument).substring(Utils.getFirstMatchingPrefixIgnoreCase(fixedArgument, PREFIXES).length());
        String columnName = Column.removeSource(fixedArgument);

        List<AgregationOperation> aggregations = new ArrayList<>();

        if (Utils.startsWithIgnoreCase(fixedArgument, "MAX:")) {
            aggregations.add(new MaxAgregation(sourceName, columnName));
        } else if (Utils.startsWithIgnoreCase(fixedArgument, "MIN:")) {
            aggregations.add(new MinAgregation(sourceName, columnName));
        } else if (Utils.startsWithIgnoreCase(fixedArgument, "AVG:")) {
            aggregations.add(new AvgAgregation(sourceName, columnName));
        } else if (Utils.startsWithIgnoreCase(fixedArgument, "COUNT:")) {
            aggregations.add(new CountAgregation(sourceName, columnName));
        }

        Prototype prototype = new Prototype();
        prototype.addColumn("madeUp", 4, Metadata.SIGNED_INTEGER_COLUMN | Metadata.PRIMARY_KEY);

        Table table = MemoryTable.openTable(new Header(prototype, "Aux"));
        table.open();

        RowData row = new RowData();
        row.setInt("madeUp", 1);
        table.insert(row);

        Operator readyOperator = new NestedLoopJoin(operator, new TableScan(table));
        readyOperator = new GroupOperator(readyOperator, "Aux", "madeUp", aggregations);
        readyOperator = new FilterColumnsOperator(readyOperator, List.of("Aux.madeUp"));

        Operation.operationSetter(cell, arguments.get(0), List.of(fixedArgument), readyOperator);
    }
}
