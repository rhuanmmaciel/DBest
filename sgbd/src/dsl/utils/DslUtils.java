package dsl.utils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import dsl.DslController;
import dsl.DslErrorListener;
import dsl.entities.BinaryExpression;
import dsl.entities.Expression;
import dsl.entities.Relation;
import dsl.entities.UnaryExpression;
import dsl.enums.CommandType;
import entities.Coordinates;
import entities.Tree;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import enums.OperationArity;
import enums.OperationType;
import files.FileUtils;
import jdk.swing.interop.SwingInterOpUtils;

public class DslUtils {

	public static CommandType commandRecognizer(String command) {
		
		command = command.strip();
		
		if(command.startsWith("import")) return CommandType.IMPORT_STATEMENT;
		
		if (command.contains("=")) 
			if (!command.contains("[") || command.indexOf("=") < command.indexOf("[")) 
					return CommandType.VARIABLE_DECLARATION;
		
		return CommandType.EXPRESSION;
		
	}
	
	public static Expression<?> expressionRecognizer(String input) {

		if (input.contains("(")) {

			int endIndex = input.indexOf('(');

			if (input.contains("["))
				endIndex = Math.min(input.indexOf('['), endIndex);

			if (OperationType.fromString(input.substring(0, endIndex).toLowerCase()).getArity() == OperationArity.UNARY)
				return new UnaryExpression(input);

			return new BinaryExpression(input);
		}

		if(DslController.containsVariable(clearTableName(input)))
			return expressionRecognizer(DslController.getVariableContent(input));
		
		if(FileUtils.getDatFilesNames().contains(clearTableName(input)))
			return new Relation(input);
		
		DslErrorListener.addErrors("A fonte de dados: '" + input + "' não encontrado");
		return null;
		
	}

	public static String clearTableName(String tableName) {

		return removeSemicolon(removeThis(removePosition(tableName))).strip();

	}

	public static String removePosition(String tableName) {

		return tableName.contains("<") ? tableName.substring(0, tableName.indexOf("<")) : tableName;

	}

	public static String removeThis(String tableName) {

		return tableName.startsWith("this.") ? tableName.replace("this.", "") : tableName;

	}

	public static String removeSemicolon(String tableName) {

		return tableName.endsWith(";") ? tableName.substring(0, tableName.lastIndexOf(";")) : tableName;

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

		return generateImports(tree) + "\n" + generateExpression(tree.getRoot()) + ";";

	}

	private static String generateImports(Tree tree) {
		Set<String> uniqueLines = new HashSet<>();

		tree.getLeaves().forEach(leaf -> uniqueLines.add("import this." + leaf.getName() + ".head;"));
		StringBuilder importStatement = new StringBuilder();
		uniqueLines.forEach(line -> importStatement.append(line).append("\n"));

		return importStatement.toString();
	}

	private static String generateExpression(Cell cell) {

		String raw = null;

		if (cell instanceof OperationCell operationCell) {

			raw = operationCell.getType().getDslOperation();

			if (operationCell.getArguments() != null) {

				raw = raw.replace("[args]", operationCell.getArguments().toString());

			}

			if (operationCell.getArity() == OperationArity.UNARY)
				raw = raw.replace("source", generateExpression(cell.getParents().get(0)));

			else {

				raw = raw.replace("source1", generateExpression(cell.getParents().get(0)));
				raw = raw.replace("source2", generateExpression(cell.getParents().get(1)));

			}

		} else if (cell instanceof TableCell tableCell) {

			raw = tableCell.getName();

		}

		return raw + getPosition(cell.getUpperLeftPosition());

	}

}
