package operations.unary;

import java.util.ArrayList;
import java.util.Arrays;
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
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.query.Operator;
import sgbd.query.agregation.AgregationOperation;
import sgbd.query.agregation.AvgAgregation;
import sgbd.query.agregation.CountAgregation;
import sgbd.query.agregation.MaxAgregation;
import sgbd.query.agregation.MinAgregation;
import sgbd.query.binaryop.joins.NestedLoopJoin;
import sgbd.query.sourceop.TableScan;
import sgbd.query.unaryop.FilterColumnsOperator;
import sgbd.query.unaryop.GroupOperator;
import sgbd.table.MemoryTable;
import sgbd.table.Table;
import sgbd.table.components.Header;
import utils.Utils;

public class Aggregation implements IOperator {

    public enum Function {
        MAX {
            public String getDisplayName() {
                return "Máximo";
            }

            public String getPrefix() {
                return "MAX:";
            }
        }, MIN {
            public String getDisplayName() {
                return "Mínimo";
            }

            public String getPrefix() {
                return "MIN:";
            }
        }, AVG {
            public String getDisplayName() {
                return "Média";
            }

            public String getPrefix() {
                return "AVG:";
            }
        }, COUNT {
            public String getDisplayName() {
                return "Contagem";
            }

            public String getPrefix() {
                return "COUNT:";
            }
        };

        public abstract String getDisplayName();

        public abstract String getPrefix();
    }

    public static List<String> PREFIXES = Arrays.stream(Function.values()).map(Function::getPrefix).toList();

    public Aggregation() {

    }

    @Override
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

            error = OperationErrorType.EMPTY_ARGUMENT;
            OperationErrorVerifier.noEmptyArgument(arguments);

            error = OperationErrorType.NO_ONE_ARGUMENT;
            OperationErrorVerifier.oneArgument(arguments);

            error = OperationErrorType.PARENT_WITHOUT_COLUMN;
            OperationErrorVerifier.parentContainsColumns(cell.getParents().get(0).getColumnSourcesAndNames(),
                arguments.stream().map(x -> Utils.replaceIfStartsWithIgnoreCase(x, PREFIXES, ""))
                    .toList(), List.of("*"));

            error = OperationErrorType.NO_PREFIX;
            OperationErrorVerifier.everyoneHavePrefix(arguments, PREFIXES);

            error = null;

        } catch (TreeException e) {

            cell.setError(error);

        }

        if (error != null) return;

        Cell parentCell = cell.getParents().get(0);

        Operator operator = parentCell.getOperator();

        String fixedArgument = arguments.get(0).substring(0, Utils.getFirstMatchingPrefixIgnoreCase(arguments.get(0), PREFIXES).length())
            + Column.composeSourceAndName(parentCell.getSourceNameByColumnName(arguments.get(0).substring(Utils.getFirstMatchingPrefixIgnoreCase(arguments.get(0), PREFIXES).length())), arguments.get(0).substring(Utils.getFirstMatchingPrefixIgnoreCase(arguments.get(0), PREFIXES).length())
        );

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

        Prototype p = new Prototype();
        p.addColumn("madeUp", 4, sgbd.prototype.column.Column.SIGNED_INTEGER_COLUMN | sgbd.prototype.column.Column.PRIMARY_KEY);

        Table table = MemoryTable.openTable(new Header(p, "Aux"));
        table.open();

        RowData row = new RowData();
        row.setInt("madeUp", 1);
        table.insert(row);

        Operator readyOperator = new NestedLoopJoin(operator, new TableScan(table), (t1, t2) -> true);
        readyOperator = new GroupOperator(readyOperator, "Aux", "madeUp", aggregations);
        readyOperator = new FilterColumnsOperator(readyOperator, List.of("Aux.madeUp"));

        Operation.operationSetter(cell, arguments.get(0), List.of(fixedArgument), readyOperator);

    }
}
