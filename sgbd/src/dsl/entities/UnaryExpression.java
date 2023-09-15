package dsl.entities;

import java.util.List;

import dsl.utils.DslUtils;
import enums.OperationType;

public final class UnaryExpression extends OperationExpression {

    public UnaryExpression(String command) {
        super(command);

        this.unaryRecognizer(command);
    }

    private void unaryRecognizer(String input) {
        int endIndex = input.indexOf('(');

        if (input.contains("[")) {
            endIndex = Math.min(input.indexOf('['), endIndex);
            this.setArguments(List.of(input.substring(input.indexOf("[") + 1, input.indexOf("]")).split(",")));
        }

        this.setType(OperationType.fromString(input.substring(0, endIndex).toLowerCase()));

        int beginSourceIndex = 0;

        int bracketsAmount = 0;

        for (int i = 0; i < input.toCharArray().length; i++) {
            char c = input.toCharArray()[i];

            if (c == '[') {
                bracketsAmount++;
            } else if (c == ']') {
                bracketsAmount--;
            }

            if (beginSourceIndex == 0 && bracketsAmount == 0 && c == '(') {
                beginSourceIndex = i + 1;
            }
        }

        String source = input.substring(beginSourceIndex, input.lastIndexOf(")"));

        this.setSource(DslUtils.expressionRecognizer(source));

        this.setCoordinates(input.substring(input.lastIndexOf(")") + 1));
    }
}
