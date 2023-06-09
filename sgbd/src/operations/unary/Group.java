package operations.unary;

import com.mxgraph.model.mxCell;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.query.Operator;
import sgbd.query.agregation.*;
import sgbd.query.unaryop.GroupOperator;
import util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Group implements IOperator {

    public static List<String> PREFIXES = List.of("MIN:", "MAX:", "AVG:", "COUNT:");

    public Group() {

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

            error = OperationErrorType.PARENT_WITHOUT_COLUMN;
            OperationErrorVerifier.parentContainsColumns(cell.getParents().get(0).getColumnSourceNames(),
                    arguments.stream().limit(1).toList());
            OperationErrorVerifier.parentContainsColumns(cell.getParents().get(0).getColumnSourceNames(),
                    arguments.stream().map(x -> Utils.replaceIfStartsWithIgnoreCase(x, PREFIXES, ""))
                            .toList().subList(1, arguments.size()));

            error = OperationErrorType.NO_PREFIX;
            OperationErrorVerifier.everyoneHavePrefix(arguments.subList(1, arguments.size()), PREFIXES);

            error = null;

        } catch (TreeException e) {

            cell.setError(error);

        }

        if(error != null) return;

        Cell parentCell = cell.getParents().get(0);

        List<String> fixedArguments = new ArrayList<>();

        fixedArguments.add(Column.putSource(arguments.get(0), parentCell.getSourceTableNameByColumn(arguments.get(0))));

        for(String argument : arguments.subList(1, arguments.size())){

            String fixedArgument = argument.substring(0, Utils.getLastIndexOfPrefix(Utils.getStartPrefixIgnoreCase(argument, PREFIXES)))
                    +Column.putSource(argument.substring(Utils.getLastIndexOfPrefix(Utils.getStartPrefixIgnoreCase(argument, PREFIXES))),
                    parentCell.getSourceTableNameByColumn(argument.substring(Utils.getLastIndexOfPrefix(Utils.getStartPrefixIgnoreCase(argument, PREFIXES)))));

            fixedArguments.add(fixedArgument);

        }

        String groupBy = fixedArguments.get(0);

        List<AgregationOperation> aggregations = new ArrayList<>();

        for(String argument : fixedArguments.subList(1, arguments.size())){

            String column = argument.substring(Utils.getLastIndexOfPrefix(Utils.getStartPrefixIgnoreCase(argument, PREFIXES)));
            String sourceName = Column.removeName(column);
            String columnName = Column.removeSource(column);

            if(Utils.startsWithIgnoreCase(argument, "MAX:"))
                aggregations.add(new MaxAgregation(sourceName, columnName));
            else if(Utils.startsWithIgnoreCase(argument, "MIN:"))
                aggregations.add(new MinAgregation(sourceName, columnName));
            else if(Utils.startsWithIgnoreCase(argument, "AVG:"))
                aggregations.add(new AvgAgregation(sourceName, columnName));
            else if(Utils.startsWithIgnoreCase(argument, "COUNT:"))
                aggregations.add(new CountAgregation(sourceName, columnName));


        }

        Operator operator = parentCell.getOperator();

        Operator readyOperator = new GroupOperator(operator, Column.removeName(groupBy), Column.removeSource(groupBy), aggregations);

        Operation.operationSetter(cell, cell.getType().SYMBOL + arguments.toString(), arguments, readyOperator);

    }

}
