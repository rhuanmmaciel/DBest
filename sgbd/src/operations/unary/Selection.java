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
import util.Utils;

import java.util.Arrays;
import java.util.List;
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

		String expression = arguments.get(0);
		String[] formattedInput = formatString(expression).split(" ");

		Operator operator = new FilterOperator(parentCell.getOperator(), (Tuple t) -> {

			for (String element : formattedInput) {

				if (isColumn(element)) {

					String column = element.substring(2, element.length()-1);
					boolean hasSource = Column.hasSource(column);

					String source = hasSource ? Column.removeName(element.substring(2))
							: parentCell.getSourceTableNameByColumn(column);
					String columnName = hasSource ? Column.removeSource(element.substring(0, element.length()-1))
							: column;

					ColumnDataType type = parentCell.getColumns().stream()
							.filter(x -> x.getSource().equals(source) && x.getName().equals(columnName))
							.findAny().orElseThrow().getType();

					String inf = switch (type){
						case INTEGER -> String.valueOf(t.getContent(source).getInt(columnName));
						case FLOAT -> String.valueOf(t.getContent(source).getFloat(columnName));
						default -> "'" + t.getContent(source).getString(columnName) + "'";
					};

					System.out.println(inf + " " + source + " " + columnName);

					evaluator.putVariable(hasSource ? source+"."+columnName : column, inf);

				}
			}

			try {

				return evaluator.evaluate(formatString(expression)).equals("1.0");

			} catch (EvaluationException e) {

				return false;

			}

		});

		Operation.operationSetter(cell, "σ  " + expression, arguments, operator);

	}

	public String formatString(String input) {

		input = input.replaceAll("\\bAND\\b", "&&");
		input = input.replaceAll("\\bOR\\b", "||");
		input = input.replaceAll("(?<=\\s|^)([\\w.-]+(?:\\.[\\w.-]+)?)(?=[\\s>=<])", "#{$1}");
		input = input.replaceAll("=", "==");
		input = input.replaceAll("≠", "!=");
		input = input.replaceAll("≥", ">=");
		input = input.replaceAll("≤", "<=");
		return input;

	}

	public boolean isColumn(String input) {

		Pattern pattern = Pattern.compile("#\\{([\\w.-]+(?:\\.[\\w.-]+)?)\\}");
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();

	}

}
