package dsl.entities;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dsl.utils.DslUtils;
import enums.OperationType;
import exceptions.dsl.InputException;
import ibd.table.Table;
import java.util.Map;

public final class BinaryExpression extends OperationExpression {

	private dsl.entities.Expression<?> source2;
	
	public BinaryExpression(String command, Map<String, Table> tables) throws InputException {
	
		super(command);
		binaryRecognizer(command, tables);
		
	}

    public int findFirstUnbracketedParenthesis(String input) {
        int bracketCount = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '[') {
                bracketCount++;
            } else if (c == ']') {
                bracketCount--;
            } else if (c == '(' && bracketCount == 0) {
                return i;
            }
        }

        return -1;
    }

	private void binaryRecognizer(String input, Map<String, Table> tables) throws InputException {

		int endIndex = findFirstUnbracketedParenthesis(input);

		String regex = "\\[[^\\[]*\\(";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		if (matcher.find()) {

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
		
		int sourcePosition = findFirstUnbracketedParenthesis(input) + 1;
		int commaPosition = DslUtils.findCommaPosition(input.substring(sourcePosition))
				+ input.substring(0, sourcePosition).length();

		String source1 = input.substring(sourcePosition, commaPosition);
		String source2 = input.substring(commaPosition + 1, input.lastIndexOf(")"));

		setSource(DslUtils.expressionRecognizer(source1, tables));
		this.source2 = DslUtils.expressionRecognizer(source2, tables);
		
		setCoordinates(input.substring(input.lastIndexOf(")") + 1));

	}


	public Expression<?> getSource2() {
		return source2;
	}
	
}
