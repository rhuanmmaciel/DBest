package dsl.entities;

import java.util.List;

import dsl.utils.DslUtils;
import enums.OperationType;
import exceptions.dsl.InputException;

public final class UnaryExpression extends OperationExpression {

	public UnaryExpression(String command) throws InputException {

		super(command);
		unaryRecognizer(command);
		
	}

	private void unaryRecognizer(String input) throws InputException {

		int endIndex = input.indexOf('(');

		if (input.contains("[")) {

			endIndex = Math.min(input.indexOf('['), endIndex);
			setArguments(List.of(input.substring(input.indexOf("[") + 1, input.indexOf("]")).split(",")));

		}

		setType(OperationType.fromString(input.substring(0, endIndex).toLowerCase()));

		int beginSourceIndex = 0;

		int bracketsAmount = 0;
		for(int i = 0; i < input.toCharArray().length; i++){

			char c = input.toCharArray()[i];

			if(c == '[') bracketsAmount++;
			if(c == ']') bracketsAmount--;
			if(beginSourceIndex == 0 && bracketsAmount == 0 && c == '(')
				beginSourceIndex = i + 1;

		}

		String source = input.substring(beginSourceIndex, input.lastIndexOf(")"));
		
		setSource(DslUtils.expressionRecognizer(source));

		setCoordinates(input.substring(input.lastIndexOf(")") + 1));

	}

}
