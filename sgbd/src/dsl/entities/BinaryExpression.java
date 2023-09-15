package dsl.entities;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dsl.utils.DslUtils;

import enums.OperationType;

public final class BinaryExpression extends OperationExpression {

    private Expression<?> source2;

    public BinaryExpression(String command) {
        super(command);

        this.binaryRecognizer(command);
    }

    private void binaryRecognizer(String input) {
        int endIndex = input.indexOf('(');

        String regex = "\\[[^\\[]*\\(";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            endIndex = Math.min(input.indexOf('['), endIndex);
            this.setArguments(List.of(input.substring(input.indexOf("[") + 1, input.indexOf("]")).split(",")));
        }

        this.setType(OperationType.fromString(input.substring(0, endIndex).toLowerCase()));

        int sourcePosition = input.indexOf("(") + 1;
        int commaPosition = DslUtils.findCommaPosition(input.substring(sourcePosition)) + input.substring(0, sourcePosition).length();

        String source1 = input.substring(sourcePosition, commaPosition);
        String source2 = input.substring(commaPosition + 1, input.lastIndexOf(")"));

        this.setSource(DslUtils.expressionRecognizer(source1));
        this.source2 = DslUtils.expressionRecognizer(source2);

        this.setCoordinates(input.substring(input.lastIndexOf(")") + 1));
    }

    public Expression<?> getSource2() {
        return this.source2;
    }
}
