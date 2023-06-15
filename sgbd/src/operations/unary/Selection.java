package operations.unary;

import com.mxgraph.model.mxCell;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.ColumnDataType;
import exceptions.tree.TreeException;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import operations.OperationErrorVerifier.ErrorMessage;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.query.unaryop.FilterOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Selection implements IOperator {

	public Selection() {

	}

	public void executeOperation(mxCell jCell, List<String> arguments) {

		OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

		ErrorMessage error = null;
		
		try {
			
			error = ErrorMessage.NULL_ARGUMENT;
			OperationErrorVerifier.noNullArgument(arguments);
			
			error = ErrorMessage.NO_ONE_ARGUMENT;
			OperationErrorVerifier.oneArgument(arguments);
			
			error = ErrorMessage.NO_PARENT;
			OperationErrorVerifier.hasParent(cell);
			
			error = ErrorMessage.NO_ONE_PARENT;
			OperationErrorVerifier.oneParent(cell);
			
			error = ErrorMessage.PARENT_ERROR;
			OperationErrorVerifier.noParentError(cell);
			
			error = null;

		} catch (TreeException e) {

			cell.setError(error);
		
		}
		
		if(error != null) return;
		
		Evaluator evaluator = new Evaluator();

		Cell parentCell = cell.getParents().get(0);

		arguments = putSource(arguments, parentCell);

		String expression = arguments.get(0);
		String[] formattedInput = formatString(expression, parentCell).split(" ");

		Operator operator = parentCell.getOperator();
		AtomicInteger i = new AtomicInteger(1);
		operator = new FilterOperator(operator, (Tuple t) -> {

			for (String element : formattedInput) {

				if (isColumn(element, parentCell)) {

					String source =  Column.removeName(element.substring(2));

					String columnName = Column.removeSource(element.substring(0, element.length()-1));

					ColumnDataType type = parentCell.getColumns().stream()
							.filter(x -> x.getSource().equals(source) && x.getName().equals(columnName))
							.findAny().orElseThrow().getType();

					String inf = switch (type){
						case INTEGER -> String.valueOf(t.getContent(source).getInt(columnName));
						case FLOAT -> String.valueOf(t.getContent(source).getFloat(columnName));
						default -> "'" + t.getContent(source).getString(columnName) + "'";
					};

					evaluator.putVariable(source+"."+columnName, inf);

				}
			}

			try {

				return evaluator.evaluate(formatString(expression, parentCell)).equals("1.0");

			} catch (EvaluationException e) {

				return false;

			}

		});

		Operation.operationSetter(cell, "σ  " + expression, arguments, operator);

	}

	public String formatString(String input, Cell parent) {

		Pattern pattern = Pattern.compile("(?<=\\s|^)([\\w.-]+(?:\\.[\\w.-]+)?)(?=[\\s>=<])");
		Matcher matcher = pattern.matcher(input);

		StringBuilder result = new StringBuilder();
		while (matcher.find()) {

			String match = matcher.group(1);

			if (parent.getColumnNames().contains(Column.removeSource(match)))
				matcher.appendReplacement(result, "#{" + match + "}");

		}
		matcher.appendTail(result);

		input = result.toString();
		input = input.replaceAll("\\bAND\\b", "&&");
		input = input.replaceAll("\\bOR\\b", "||");
		input = input.replaceAll("=", "==");
		input = input.replaceAll("≠", "!=");
		input = input.replaceAll("≥", ">=");
		input = input.replaceAll("≤", "<=");

		return input;

	}


	public boolean isColumn(String input, Cell parent) {

		Pattern pattern = Pattern.compile("#\\{([\\w.-]+(?:\\.[\\w.-]+)?)\\}");
		Matcher matcher = pattern.matcher(input);

		boolean isColumn;

		if(matcher.matches()) input = input.substring(2, input.length()-1);

		if(Column.hasSource(input)) isColumn = parent.getColumnSourceNames().contains(input);
		else isColumn = parent.getColumnNames().contains(input);

		return matcher.matches() && isColumn;

	}

	private List<String> putSource(List<String> expression, Cell parentCell) {

		List<String> splitted = new ArrayList<>(List.of(expression.get(0).split(" ")));
		List<String> splittedFormatted = new ArrayList<>();

		for (String element : splitted) {
			String elementFormatted = element;

			if (parentCell.getColumnNames().contains(element) && !Column.hasSource(element)) {
				elementFormatted = Column.putSource(element, parentCell.getSourceTableNameByColumn(element));
			}

			splittedFormatted.add(elementFormatted);
		}

		return List.of(splittedFormatted.stream().reduce((x, y) -> x.concat(" " + y)).orElseThrow());

	}

}
