package dsl.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import controller.MainController;
import entities.Coordinates;
import entities.Tree;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import enums.OperationArity;

public class DslUtils {

	public static Map<String, String> recognizer(String input) {

		int endIndex = input.indexOf('(');

		if (input.contains("[")) {

			endIndex = Math.min(input.indexOf('['), endIndex);

		}

		if (MainController.unaryOperationsName().contains(input.substring(0, endIndex).toLowerCase()))
			return unaryRecognizer(input);

		return binaryRecognizer(input);

	}

	private static Map<String, String> unaryRecognizer(String input) {

		Map<String, String> elements = new HashMap<>();

		int endIndex = input.indexOf('(');

		if (input.contains("[")) {

			endIndex = Math.min(input.indexOf('['), endIndex);
			elements.put("predicate", input.substring(input.indexOf("[") + 1, input.indexOf("]")));

		}

		elements.put("operation", input.substring(0, endIndex).toLowerCase());

		String source = input.substring(input.indexOf("(") + 1, input.lastIndexOf(")"));

		if (!source.contains("(") && source.contains("<")) {

			elements.put("source", source.substring(0, source.indexOf("<")));
			elements.put("sourcePosition", source.substring(source.indexOf("<"), source.length()));

		} else
			elements.put("source", source);

		String afterExpression = input.substring(input.lastIndexOf(")") + 1);

		if (afterExpression.contains("<"))
			elements.put("position",
					afterExpression.substring(afterExpression.indexOf("<"), afterExpression.indexOf(">") + 1));

		elements.put("command", input);

		return elements;

	}

	private static Map<String, String> binaryRecognizer(String input) {

		Map<String, String> elements = new HashMap<>();

		int endIndex = input.indexOf('(');

		String regex = "\\[[^\\[]*\\(";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		if (matcher.find()) {

			endIndex = Math.min(input.indexOf('['), endIndex);
			elements.put("predicate", input.substring(input.indexOf("[") + 1, input.indexOf("]")));

		}

		elements.put("operation", input.substring(0, endIndex).toLowerCase());

		int sourcePosition = input.indexOf("(") + 1;
		int commaPosition = DslUtils.findCommaPosition(input.substring(sourcePosition))
				+ input.substring(0, sourcePosition).length();

		String source1 = input.substring(sourcePosition, commaPosition);
		String source2 = input.substring(commaPosition + 1, input.lastIndexOf(")"));

		if (!source1.contains("(") && source1.contains("<")) {

			elements.put("source1", source1.substring(0, source1.indexOf("<")));
			elements.put("source1Position", source1.substring(source1.indexOf("<"), source1.length()));

		} else
			elements.put("source1", source1);

		if (!source2.contains("(") && source2.contains("<")) {

			elements.put("source2", source2.substring(0, source2.indexOf("<")));
			elements.put("source2Position", source2.substring(source2.indexOf("<"), source2.length()));

		} else
			elements.put("source2", source2);

		String afterExpression = input.substring(input.lastIndexOf(")") + 1);

		if (afterExpression.contains("<"))
			elements.put("position",
					afterExpression.substring(afterExpression.indexOf("<"), afterExpression.indexOf(">") + 1));

		elements.put("command", input);

		return elements;

	}

	public static String clearTableName(String tableName) {
		
		return removeThis(removePosition(tableName));
		
	}
	
	public static String removePosition(String tableName) {

		if (tableName.contains("<"))
			return tableName.substring(0, tableName.indexOf("<"));
		return tableName;

	}
	
	public static String removeThis(String tableName) {
		
		if (tableName.startsWith("this."))
			return tableName.replace("this.", "");
		return tableName;
		
	}

	public static Optional<Coordinates> getPosition(String input) {

		if (input == null)
			return Optional.empty();

		if (input.contains("<")) {

			int x = Integer.parseInt(input.substring(input.indexOf("<") + 1, input.indexOf(",")));
			int y = Integer.parseInt(input.substring(input.indexOf(",") + 1, input.indexOf(">")));

			return Optional.of(new Coordinates(x, y));

		}

		return Optional.empty();

	}

	public static String getPosition(Coordinates coordinates) {

		return String.format("<%d,%d>", coordinates.x(), coordinates.y());

	}

	public static int findCommaPosition(String input) {

		int openingParenthesisAmount = 0;
		int openingSquareBracketsAmount = 0;
		int openingAngleBracketsAmount = 0;

		for (int i = 0; i < input.length(); i++) {

			switch (input.charAt(i)) {

			case '(' -> openingParenthesisAmount++;
			case ')' -> openingParenthesisAmount--;
			case '[' -> openingSquareBracketsAmount++;
			case ']' -> {
				openingSquareBracketsAmount--;
				openingAngleBracketsAmount = 0;
			}
			case '<' -> openingAngleBracketsAmount++;
			case '>' -> openingAngleBracketsAmount--;
			case ',' -> {
				if (openingParenthesisAmount == 0 && openingSquareBracketsAmount == 0
						&& openingAngleBracketsAmount == 0)
					return i;

			}

			}
		}

		throw new RuntimeException("Didn't find comma");

	}

	public static String generateDslTree(Tree tree) {
		
		return putImports(tree)+"\n"+putCommand(tree.getRoot()) + ";";

	}

	private static String putImports(Tree tree) {
	    Set<String> uniqueLines = new HashSet<>();

	    tree.getLeaves().forEach(leaf -> uniqueLines.add("import this." + leaf.getName() + ".head;"));
	    StringBuilder importStatement = new StringBuilder();
	    uniqueLines.forEach(line -> importStatement.append(line).append("\n"));

	    return importStatement.toString();
	}
	
	private static String putCommand(Cell cell) {

		String raw = null;

		if (cell instanceof OperationCell operationCell) {

			raw = operationCell.getType().getDslOperation();

			if (operationCell.getData() != null) {

				raw = raw.replace("[predicate]", operationCell.getData().toString());

			}

			if (operationCell.getArity() == OperationArity.UNARY)
				raw = raw.replace("source",
						putCommand(cell.getParents().get(0)));

			else {

				raw = raw.replace("source1",
						putCommand(cell.getParents().get(0)));
				raw = raw.replace("source2",
						putCommand(cell.getParents().get(1)));

			}

		} else if (cell instanceof TableCell tableCell) {

			raw = tableCell.getName();

		}

		return raw + getPosition(cell.getPosition());

	}

}
