package dsl.entities;

import java.util.List;

import dsl.utils.DslUtils;
import enums.OperationType;

public final class UnaryExpression extends OperationExpression {

	public UnaryExpression(String command) {

		super(command);
		unaryRecognizer(command);
		
	}

	private void unaryRecognizer(String input) {

		int endIndex = input.indexOf('(');

		if (input.contains("[")) {

			endIndex = Math.min(input.indexOf('['), endIndex);
			setArguments(List.of(input.substring(input.indexOf("[") + 1, input.indexOf("]")).split(",")));

		}

		setType(OperationType.fromString(input.substring(0, endIndex).toLowerCase()));
		
		String source = input.substring(input.indexOf("(") + 1, input.lastIndexOf(")"));
		
		setSource(DslUtils.expressionRecognizer(source));

		setCoordinates(input.substring(input.lastIndexOf(")") + 1));

	}

}
