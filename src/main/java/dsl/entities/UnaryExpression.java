package dsl.entities;

import java.util.List;

import dsl.utils.DslUtils;
import enums.OperationType;
import exceptions.dsl.InputException;
import ibd.table.Table;
import java.util.Map;

public final class UnaryExpression extends OperationExpression {

    public UnaryExpression(String command, Map<String, Table> tables) throws InputException {

        super(command);
        unaryRecognizer(command, tables);

    }

    public boolean hasSquareBracketBeforeParenthesis(String input) {
        int indexOfSquareBracket = input.indexOf('[');
        int indexOfParenthesis = input.indexOf('(');

        // Check if both symbols are present and if '[' appears before '('
        return indexOfSquareBracket != -1 && indexOfParenthesis != -1 && indexOfSquareBracket < indexOfParenthesis;
    }

    private void unaryRecognizer(String input, Map<String, Table> tables) throws InputException {

        int endIndex = input.indexOf('(');

        //if (input.contains("[")) 
        if (hasSquareBracketBeforeParenthesis(input)){

            endIndex = Math.min(input.indexOf('['), endIndex);
            setArguments(List.of(input.substring(input.indexOf("[") + 1, input.indexOf("]")).split(",")));

        }
        
        String type = input.substring(0, endIndex);
        int index = type.indexOf(":");
        if (index!=-1){
            String alias = type.substring(index+1, type.length()).trim();
            setAlias(alias);
            type = type.substring(0, index);
        }

        setType(OperationType.fromString(type));

        int beginSourceIndex = 0;

        int bracketsAmount = 0;
        for (int i = 0; i < input.toCharArray().length; i++) {

            char c = input.toCharArray()[i];

            if (c == '[') {
                bracketsAmount++;
            }
            if (c == ']') {
                bracketsAmount--;
            }
            if (beginSourceIndex == 0 && bracketsAmount == 0 && c == '(') {
                beginSourceIndex = i + 1;
            }

        }

        String source = input.substring(beginSourceIndex, input.lastIndexOf(")"));

        setSource(DslUtils.expressionRecognizer(source, tables));

        setCoordinates(input.substring(input.lastIndexOf(")") + 1));

    }

}
