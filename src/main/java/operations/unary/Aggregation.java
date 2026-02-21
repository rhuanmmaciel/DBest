package operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import ibd.query.unaryop.aggregation.AggregationType;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        }, SUM {
            public String getDisplayName() {
                return ConstantController.getString("operationForm.sum");
            }

            public String getPrefix() {
                return "SUM:";
            }
        }, FIRST {
            public String getDisplayName() {
                return ConstantController.getString("operationForm.first");
            }

            public String getPrefix() {
                return "FIRST:";
            }
        }, LAST {
            public String getDisplayName() {
                return ConstantController.getString("operationForm.last");
            }

            public String getPrefix() {
                return "LAST:";
            }
        }, COUNT {
            public String getDisplayName() {
                return ConstantController.getString("operationForm.count");
            }

            public String getPrefix() {
                return "COUNT:";
            }
        }, COUNT_ALL {
            public String getDisplayName() {
                return ConstantController.getString("operationForm.countAll");
            }

            public String getPrefix() {
                return "COUNT_ALL:";
            }
        }, COUNT_NULL {
            public String getDisplayName() {
                return ConstantController.getString("operationForm.countNull");
            }

            public String getPrefix() {
                return "COUNT_NULL:";
            }
        };

        public abstract String getDisplayName();

        public abstract String getPrefix();
    }

    public static final List<String> PREFIXES = Arrays.stream(Function.values()).map(Function::getPrefix).toList();

    @Override
    public void executeOperation(mxCell jCell, List<String> arguments, String alias) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) {
            return;
        }

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

        if (errorType != null) {
            return;
        }

        Cell parentCell = cell.getParents().get(0);

        ibd.query.Operation operator = parentCell.getOperator();

        String fixedArgument = arguments
                .get(0)
                .substring(0, Utils.getFirstMatchingPrefixIgnoreCase(arguments.get(0), PREFIXES).length()) + Column.composeSourceAndName(parentCell.getSourceNameByColumnName(arguments.get(0).substring(Utils.getFirstMatchingPrefixIgnoreCase(arguments.get(0), PREFIXES).length())), arguments.get(0).substring(Utils.getFirstMatchingPrefixIgnoreCase(arguments.get(0), PREFIXES).length()));

        List<AggregationType> aggregations = AggregationType.getAggregationTypes(fixedArgument);

        //ibd.query.Operation readyOperator = new GroupOperator(operator, Column.removeName(groupBy), Column.removeSource(groupBy), aggregations);
        try {
            //readyOperator = new ibd.query.unaryop.AllAggregation(operator, "aggregate",  aggregateCol, aggregateType);
            ibd.query.unaryop.aggregation.AllAggregation readyOperator = new ibd.query.unaryop.aggregation.AllAggregation(operator, alias, aggregations);
            String formattedAlias = "";
            alias = readyOperator.getDataSourceAlias();
            if (!alias.isBlank()) {
                formattedAlias = ":" + alias;
            }

            String operationName = String.format("%s%s %s", cell.getType().symbol, formattedAlias, arguments);

            Operation.operationSetter(cell, operationName, arguments, readyOperator);
        } catch (Exception ex) {
            Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
