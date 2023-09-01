package operations.unary;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mxgraph.model.mxCell;

import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.prototype.query.Tuple;
import sgbd.query.Operator;
import sgbd.query.unaryop.FilterOperator;
import utils.Utils;

public class Selection implements IOperator {

    public Selection() {

    }

    public void executeOperation(mxCell jCell, List<String> arguments) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) return;

        OperationCell cell = (OperationCell) optionalCell.get();
        OperationErrorType error = null;

        try {
            error = OperationErrorType.NULL_ARGUMENT;
            OperationErrorVerifier.noNullArgument(arguments);

            error = OperationErrorType.NO_ONE_ARGUMENT;
            OperationErrorVerifier.oneArgument(arguments);

            error = OperationErrorType.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            error = OperationErrorType.NO_ONE_PARENT;
            OperationErrorVerifier.oneParent(cell);

            error = OperationErrorType.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

            error = null;
        } catch (TreeException e) {
            cell.setError(error);
        }

        if (error != null) return;

        Evaluator evaluator = new Evaluator();

        Cell parentCell = cell.getParents().get(0);

        arguments = putSource(arguments, parentCell);

        String expression = arguments.get(0);
        String[] formattedInput = formatString(expression, parentCell).split(" ");

        evaluator.putVariable("null", "86758593");

        Operator operator = parentCell.getOperator();

        operator = new FilterOperator(operator, (Tuple t) -> {
            for (String element : formattedInput) {

                if (isColumn(element, parentCell)) {

                    String source = Column.removeName(element.substring(2));

                    String columnName = Column.removeSource(element.substring(0, element.length() - 1));

                    String inf = switch (Utils.getColumnDataType(t, source, columnName)) {
                        case INTEGER -> String.valueOf(t.getContent(source).getInt(columnName));
                        case LONG -> String.valueOf(t.getContent(source).getLong(columnName));
                        case FLOAT -> String.valueOf(t.getContent(source).getFloat(columnName));
                        case DOUBLE -> String.valueOf(t.getContent(source).getDouble(columnName));
                        default -> "'" + t.getContent(source).getString(columnName) + "'";
                    };

                    if (inf.equals("null") || inf.equals("'null'")) {
                        inf = "86758593";
                    }

                    evaluator.putVariable(source + "." + columnName, inf);

                }

            }

            try {

                return evaluator.evaluate(formatString(expression, parentCell)).equals("1.0");

            } catch (EvaluationException e) {

                return false;

            }

        });

        Operation.operationSetter(cell, cell.getType().symbol + "  " + expression, arguments, operator);

    }

    public String formatString(String input, Cell parent) {

        input = input
            .replaceAll("\\bAND\\b", "&&")
            .replaceAll("\\bOR\\b", "||")
            .replaceAll("=", "==")
            .replaceAll("≠", "!=")
            .replaceAll("≥", ">=")
            .replaceAll("≤", "<=")
            .replaceAll("\\bis not\\b", "!=")
            .replaceAll("\\bis\\b", "==");

        Pattern pattern = Pattern.compile("(?<=\\s|^|\\(|\\))(null|[\\w.-]+(?:\\.[\\w.-]+)+)(?=[\\s>=<]|$|\\(|\\))");
        Matcher matcher = pattern.matcher(input);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {

            String matchValue = matcher.group();
            if (matchValue.equals("null") || parent.getColumnSourcesAndNames().contains(matchValue)) {
                matcher.appendReplacement(result, "#{" + matchValue + "}");
            } else {
                matcher.appendReplacement(result, matchValue);
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }


    public boolean isColumn(String input, Cell parent) {

        if (input.equals("#{null}")) {
            return false;
        }

        Pattern pattern = Pattern.compile("#\\{([\\w.-]+(?:\\.[\\w.-]+)?)\\}");
        Matcher matcher = pattern.matcher(input);

        boolean isColumn;

        if (matcher.matches()) {
            input = input.substring(2, input.length() - 1);
        }

        if (Column.hasSource(input)) {
            isColumn = parent.getColumnSourcesAndNames().contains(input);
        } else {
            isColumn = parent.getColumnNames().contains(input);
        }

        return matcher.matches() && isColumn;

    }

    private List<String> putSource(List<String> expression, Cell parentCell) {

        List<String> splitted = new ArrayList<>(List.of(expression.get(0).split(" ")));
        List<String> splittedFormatted = new ArrayList<>();

        for (String element : splitted) {
            String elementFormatted = element;

            if (parentCell.getColumnNames().contains(element) && !Column.hasSource(element)) {
                elementFormatted = Column.composeSourceAndName(parentCell.getSourceNameByColumnName(element), element);
            }

            splittedFormatted.add(elementFormatted);
        }

        return List.of(splittedFormatted.stream().reduce((x, y) -> x.concat(" " + y)).orElseThrow());

    }

}
