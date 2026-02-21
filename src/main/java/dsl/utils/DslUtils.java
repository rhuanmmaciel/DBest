package dsl.utils;

import controllers.ConstantController;
import controllers.MainController;
import dsl.DslController;
import dsl.entities.BinaryExpression;
import dsl.entities.Expression;
import dsl.entities.NullaryExpression;
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
import exceptions.dsl.InputException;
import ibd.table.Table;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DslUtils {

    public static CommandType commandRecognizer(String command) {

        command = command.strip();

        if (command.startsWith("import")) {
            return CommandType.IMPORT_STATEMENT;
        }

        if (command.contains("=")) {
            if (!command.contains("[") || command.indexOf("=") < command.indexOf("[")) {
                return CommandType.VARIABLE_DECLARATION;
            }
        }

        return CommandType.EXPRESSION;

    }

    public static Expression<?> expressionRecognizer(String input, Map<String, Table> tables) throws InputException {
        input = input.trim();
        if (input.contains("(")) {

            int endIndex = input.indexOf('(');

            if (input.contains("[")) {
                endIndex = Math.min(input.indexOf('['), endIndex);
            }

            OperationArity arity = OperationType.fromString(input.substring(0, endIndex).toLowerCase()).arity;
            if (arity == OperationArity.UNARY) {
                return new UnaryExpression(input, tables);
            } else if (arity == OperationArity.NULLARY) {
                return new NullaryExpression(input, tables);
            }

            return new BinaryExpression(input, tables);
        }
        int endIndex = input.length() - 1;

        if (input.contains("<"))
            endIndex = input.indexOf('<');
            
        if (input.contains("[")) {
            endIndex = input.indexOf('[');
        }

        OperationType type = OperationType.fromString(input.substring(0, endIndex).toLowerCase());
        if (type!=null && type.arity == OperationArity.NULLARY) {
            return new NullaryExpression(input, tables);
        }
        if (DslController.containsVariable(clearTableName(input))) {
            return expressionRecognizer(DslController.getVariableContent(input), tables);
        }

        if (MainController.getTables().get(clearTableName(input)) != null) {
            return new Relation(input);
        }

        if (tables != null && tables.get(clearTableName(input)) != null) {
            return new Relation(input);
        }

        throw new InputException(ConstantController.getString("dsl.error.sourceNotFound") + ": " + input);

    }

    public static String clearTableName(String tableName) {

        return removeAs(removeSemicolon(removeThis(removePosition(tableName)))).strip();

    }

    public static String removeAs(String tableName) {

        if (tableName.contains(":")) {
            return tableName.substring(0, tableName.indexOf(":"));
        }

        return tableName;

    }

    public static String getRealName(String tableName) {

        if (tableName.contains(":")) {
            return clearTableName(tableName.substring(tableName.indexOf(":") + 1));
        }

        return clearTableName(tableName);

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

        if (input == null) {
            return Optional.empty();
        }

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

                case '(' ->
                    openingParenthesisAmount++;
                case ')' ->
                    openingParenthesisAmount--;
                case '[' ->
                    openingSquareBracketsAmount++;
                case ']' -> {
                    openingSquareBracketsAmount--;
                    openingAngleBracketsAmount = 0;
                }
                case '<' ->
                    openingAngleBracketsAmount++;
                case '>' ->
                    openingAngleBracketsAmount--;
                case ',' -> {
                    if (openingParenthesisAmount == 0 && openingSquareBracketsAmount == 0
                            && openingAngleBracketsAmount == 0) {
                        return i;
                    }

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

        //tree.getLeaves().forEach(leaf -> uniqueLines.add("import this." + leaf.getName() + ".head;"));
        List<Cell> cells = tree.getLeaves();
        for (Cell cell : cells) {
            if (cell instanceof TableCell tableCell) {
                uniqueLines.add("import " + tableCell.getHeaderFile().getAbsolutePath() + ";");
            }
        }

        StringBuilder importStatement = new StringBuilder();
        uniqueLines.forEach(line -> importStatement.append(line).append("\n"));

        return importStatement.toString();
    }

    private static String generateExpression(Cell cell) {

        String raw = null;

        if (cell instanceof OperationCell operationCell) {

            raw = operationCell.getType().dslSyntax;

            int in = raw.indexOf('[');
            if (in == -1) {
                in = raw.indexOf('(');
            }
            String toBeReplaced = raw.substring(0, in);
            String replacement = toBeReplaced;
            if (!operationCell.getAlias().isBlank()) {
                replacement = replacement + ":" + operationCell.getAlias();
            }

            raw = raw.replace(toBeReplaced, replacement);

            raw = raw.replace("[args]", OperationType.OPERATIONS_WITHOUT_FORM.contains(operationCell.getType()) ? ""
                    : operationCell.getArguments().toString());

            if (operationCell.getArity() == OperationArity.UNARY) {
                raw = raw.replace("source", generateExpression(cell.getParents().get(0)));
            } else if (operationCell.getArity() == OperationArity.BINARY) {

                raw = raw.replace("source1", generateExpression(cell.getParents().get(0)));
                raw = raw.replace("source2", generateExpression(cell.getParents().get(1)));
            } else {

            }

        } else if (cell instanceof TableCell tableCell) {
            String fileName = tableCell.getTable().getHeaderName();
            String alias = tableCell.getAlias();
            if (fileName.equals(alias) || alias.isBlank()) {
                raw = fileName;
            } else {
                raw = fileName + ":" + alias;
            }

        }

        return raw + getPosition(cell.getUpperLeftPosition());

    }

}
