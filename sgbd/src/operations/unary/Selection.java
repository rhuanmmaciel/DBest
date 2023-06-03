package operations.unary;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mxgraph.model.mxCell;

import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.ColumnDataType;
import exceptions.tree.TreeException;
import gui.frames.forms.operations.IOperator;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import operations.Operation;
import operations.OperationErrorVerifier;
import operations.OperationErrorVerifier.ErrorMessage;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.query.unaryop.FilterOperator;

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

					String columnName = element.substring(2, element.length() - 1);

					ColumnDataType type = parentCell.getColumns().stream()
							.filter(col -> col.getName().equals(columnName)).findFirst().get().getType();

					String inf;

					if (type == ColumnDataType.INTEGER) {

						inf = String
								.valueOf(t.getContent(parentCell.getSourceTableName(columnName)).getInt(columnName));

					} else if (type == ColumnDataType.FLOAT) {

						inf = String
								.valueOf(t.getContent(parentCell.getSourceTableName(columnName)).getFloat(columnName));

					} else {

						inf = "'" + t.getContent(parentCell.getSourceTableName(columnName)).getString(columnName) + "'";

					}

					if (arguments == null)
						return false;

					evaluator.putVariable(columnName, inf);

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

		input = input.replaceAll("(?<=\\s*|^)([\\w.()-<>]+\\_[\\w.()-<>]+)(?=\\s*|$)", "#{$1}");

		input = input.replaceAll("\\bAND\\b", "&&");

		input = input.replaceAll("\\bOR\\b", "||");

		input = input.replaceAll("=", "==");

		input = input.replaceAll("≠", "!=");

		input = input.replaceAll("≥", ">=");

		input = input.replaceAll("≤", "<=");

		return input;
	}

	public boolean isColumn(String input) {

		Pattern pattern = Pattern.compile("#\\{.*?\\}");
		Matcher matcher = pattern.matcher(input);

		return matcher.matches();

	}

}
