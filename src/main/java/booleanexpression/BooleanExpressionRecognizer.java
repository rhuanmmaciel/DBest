package booleanexpression;

import booleanexpression.antlr.BooleanExpressionDSLController;
import booleanexpression.antlr.BooleanExpressionDSLLexer;
import booleanexpression.antlr.BooleanExpressionDSLParser;
import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;
import lib.booleanexpression.entities.elements.Element;
import lib.booleanexpression.entities.elements.Null;
import lib.booleanexpression.entities.elements.Value;
import lib.booleanexpression.entities.expressions.AtomicExpression;
import lib.booleanexpression.entities.expressions.BooleanExpression;
import lib.booleanexpression.entities.expressions.LogicalExpression;
import lib.booleanexpression.enums.LogicalOperator;
import lib.booleanexpression.enums.RelationalOperator;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static booleanexpression.Utils.getElement;
import entities.Column;
import entities.cells.OperationCell;
import gui.frames.forms.operations.OperationForm;
import ibd.table.prototype.query.fields.BooleanField;
import ibd.table.prototype.query.fields.DoubleField;
import ibd.table.prototype.query.fields.FloatField;
import ibd.table.prototype.query.fields.IntegerField;
import ibd.table.prototype.query.fields.LongField;
import ibd.table.prototype.query.fields.StringField;

public class BooleanExpressionRecognizer {

    private final List<Cell> parents;
    private final List<Column> leftSideColumns;

    public BooleanExpressionRecognizer(mxCell jCell) {
        this.parents = CellUtils.getActiveCells().get(jCell).getParents();
        OperationCell cell = (OperationCell)CellUtils.getActiveCells().get(jCell);
        leftSideColumns = OperationForm.getLeftSideCorrelationColumns(cell);
    }

    public String recognizer(BooleanExpression booleanExpression) {
        if (booleanExpression instanceof AtomicExpression atomicExpression) {
            return this.recognizeAtomic(atomicExpression);
        }

        LogicalExpression logicalExpression = (LogicalExpression) booleanExpression;

        int numberOfExpressions = logicalExpression.getExpressions().size();

        LogicalOperator operator = logicalExpression.getLogicalOperator();

        StringBuilder logicalExpressionStringBuilder = new StringBuilder();
        logicalExpressionStringBuilder.append(" ( ");

        boolean first = true;
        for (int i = 0; i < numberOfExpressions; i++) {
            BooleanExpression exp = logicalExpression.getExpressions().get(i);
            if (exp==null) continue;
            if (!first) {
                logicalExpressionStringBuilder.append(operator);
            }
            logicalExpressionStringBuilder.append(this.recognizer(exp));

            
            first = false;
        }

        logicalExpressionStringBuilder.append(" ) ");

        return logicalExpressionStringBuilder.toString();
    }

    private String recognizeAtomic(AtomicExpression atomicExpression) {
        String firstElement = this.getString(atomicExpression.getFirstElement());
        String relationalOperator = atomicExpression.getRelationalOperator().symbols[0];
        String secondElement = this.getString(atomicExpression.getSecondElement());

        return String.format(" ( %s %s %s )", firstElement, relationalOperator, secondElement);
    }

    private String getString(Element element) {
        if (element instanceof Value value) {
            if (value.getField() instanceof IntegerField field) {
                return String.valueOf(field.getInt());
            } else if (value.getField() instanceof FloatField field) {
                return String.valueOf(field.getFloat());
            } else if (value.getField() instanceof LongField field) {
                return String.valueOf(field.getLong());
            } else if (value.getField() instanceof DoubleField field) {
                return String.valueOf(field.getDouble());
            } else if (value.getField() instanceof StringField field) {
                return String.format("'%s'", field.getString());
            } 
            else if (value.getField() instanceof BooleanField field) {
                return String.valueOf(field.getBoolean());
            } 
            else {
                throw new IllegalStateException(String.format("Unexpected value: %s", value.getField()));
            }
        } else if (element instanceof Null) {
            return ConstantController.NULL;
        } else {
            return element.toString();
        }
    }

    public static BooleanExpression recognizer(String text) throws BooleanExpressionException {
        BooleanExpressionDSLParser parser = new BooleanExpressionDSLParser(
                new CommonTokenStream(
                        new BooleanExpressionDSLLexer(CharStreams.fromString(text))
                )
        );

        parser.removeErrorListeners();

        ErrorListener errorListener = new ErrorListener();
        ErrorListener.getErrors().clear();
        parser.addErrorListener(errorListener);

        ParseTreeWalker walker = new ParseTreeWalker();
        BooleanExpressionDSLController listener = new BooleanExpressionDSLController();

        walker.walk(listener, parser.command());

        if (!ErrorListener.getErrors().isEmpty()) {
            //throw new BooleanExpressionException(ConstantController.getString("booleanExpression.error.invalidFormat"));
        }

        return recognize(text);
    }

    public static BooleanExpression recognize(String text) throws BooleanExpressionException {
        boolean isAtomic = !hasAnyLogicalOperator(text);

        if (isAtomic) {
            return (recognizeAtomic(text));
        }

        List<String> tokens = new ArrayList<>(tokenize(text).stream().map(String::strip).toList());

        return consumeTokens(tokens);
    }

    private static BooleanExpression consumeTokens(List<String> tokens) throws BooleanExpressionException {
        Optional<LogicalOperator> operator = whichLogicalOperator(tokens);

        prioritizeAnds(tokens);

        if (operator.isPresent()) {
            return recognizeLogical(operator.get(), tokens);
        }

        return recognizeAtomic(tokens.get(0));
    }

    private static void prioritizeAnds(List<String> tokens) {
        int bracketsController = 0;

        boolean existsOr = false;

        List<Integer> andIndexes = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {
            if (isRightBracket(tokens.get(i))) {
                bracketsController++;
            }
            if (isLeftBracket(tokens.get(i))) {
                bracketsController--;
            }

            if (bracketsController == 0 && isAnd(tokens.get(i))) {
                andIndexes.add(i);
            }
            if (bracketsController == 0 && isOr(tokens.get(i))) {
                existsOr = true;
            }
        }

        if (!existsOr) {
            return;
        }

        for (Integer index : andIndexes) {
            tokens.add(index - 1, "(");
            tokens.add(index + 3, ")");
        }
    }

    private static LogicalExpression recognizeLogical(LogicalOperator logicalOperator, List<String> tokens) throws BooleanExpressionException {
        List<BooleanExpression> expressions = new ArrayList<>();
        List<List<String>> eachExpressionTokens = new ArrayList<>();
        List<Integer> logicalOperatorIndexes = getExternalLogicalOperatorIndexes(tokens);

        int expressionIndex = 0;

        for (int i = 0; i < tokens.size(); i++) {
            String currentToken = tokens.get(i);

            if (eachExpressionTokens.size() < expressionIndex + 1) {
                eachExpressionTokens.add(new ArrayList<>());
            }

            if (isAndOrOr(currentToken)) {
                if (logicalOperatorIndexes.contains(i)
                        && ((isAnd(currentToken) && logicalOperator.equals(LogicalOperator.AND))
                        || (isOr(currentToken) && logicalOperator.equals(LogicalOperator.OR)))) {
                    expressionIndex++;
                } else {
                    eachExpressionTokens.get(expressionIndex).add(currentToken);
                }
            } else {
                eachExpressionTokens.get(expressionIndex).add(currentToken);
            }
        }

        for (List<String> expressionTokens : eachExpressionTokens) {
            expressions.add(consumeTokens(expressionTokens));
        }

        return new LogicalExpression(logicalOperator, expressions);
    }

    private static List<Integer> getExternalLogicalOperatorIndexes(List<String> tokens) {
        List<Integer> orIndexes = new ArrayList<>();
        List<Integer> andIndexes = new ArrayList<>();

        int bracketsController = 0;

        for (int i = 0; i < tokens.size(); i++) {
            if (isRightBracket(tokens.get(i))) {
                bracketsController++;
            }
            if (isLeftBracket(tokens.get(i))) {
                bracketsController--;
            }

            if (bracketsController == 0 && isAnd(tokens.get(i))) {
                andIndexes.add(i);
            }
            if (bracketsController == 0 && isOr(tokens.get(i))) {
                orIndexes.add(i);
            }
        }

        return andIndexes.isEmpty() ? orIndexes : andIndexes;
    }

    private static void removeUnnecessaryExternalBrackets(List<String> tokens) {
        while (hasUnnecessaryExternalBrackets(tokens)) {
            tokens.remove(0);
            tokens.remove(tokens.size() - 1);
        }
    }

    private static boolean hasUnnecessaryExternalBrackets(List<String> tokens) {
        if (isLeftBracket(tokens.get(0)) && isRightBracket(tokens.get(tokens.size() - 1))) {
            int bracketsController = 0;

            for (String token : tokens.stream().limit(tokens.size() - 1L).toList()) {
                if (isRightBracket(token)) {
                    bracketsController--;
                }
                if (isLeftBracket(token)) {
                    bracketsController++;
                }

                if (bracketsController < 1) {
                    return false;
                }
            }

            return bracketsController == 1;
        }

        return false;
    }

    private static Optional<LogicalOperator> whichLogicalOperator(List<String> tokens) {
        removeUnnecessaryExternalBrackets(tokens);

        int bracketsController = 0;

        Optional<LogicalOperator> logicalOperator = Optional.empty();

        for (String token : tokens) {
            if (isRightBracket(token)) {
                bracketsController++;
            }
            if (isLeftBracket(token)) {
                bracketsController--;
            }

            if (bracketsController == 0 && isAnd(token)) {
                return Optional.of(LogicalOperator.AND);
            }
            if (bracketsController == 0 && isOr(token)) {
                logicalOperator = Optional.of(LogicalOperator.OR);
            }
        }

        return logicalOperator;
    }

    private static boolean isAnd(String text) {
        return text.replace(" ", "").equalsIgnoreCase("and");
    }

    private static boolean isOr(String text) {
        return text.replace(" ", "").equalsIgnoreCase("or");
    }

    private static boolean isAndOrOr(String text) {
        return isOr(text) || isAnd(text);
    }

    private static boolean isRightBracket(String text) {
        return text.replace(" ", "").equals(")");
    }

    private static boolean isLeftBracket(String text) {
        return text.replace(" ", "").equals("(");
    }

    private static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();

        String regex = "((?i)(?<=[\\s\\(\\)])+AND(?=[\\s\\(\\)])+)|((?i)(?<=[\\s\\(\\)])+OR(?=[\\s\\(\\)])+)|(\\()|(\\))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                tokens.add(input.substring(lastEnd, matcher.start()).strip());
            }

            tokens.add(matcher.group());
            lastEnd = matcher.end();
        }

        if (lastEnd < input.length()) {
            tokens.add(input.substring(lastEnd).strip());
        }

        tokens.removeIf(String::isEmpty);

        return tokens;
    }

    private static AtomicExpression recognizeAtomic(String text) throws BooleanExpressionException {
        text = text.replace(")", "").replace("(", "");

        String finalText = text;

        String relationalOperator = ConstantController.RELATIONAL_OPERATORS
                .stream()
                .filter(finalText::contains)
                .findFirst()
                .orElseThrow(() -> new BooleanExpressionException(
                ConstantController.getString("booleanExpression.error.relationalOperatorUnknown")
        ));

        String[] elements = text.split(relationalOperator);

        if (elements.length != 2) {
            throw new BooleanExpressionException(ConstantController.getString("booleanExpression.error.notTwoElementsInAtomicExpression"));
        }

        Element firstElement = recognizeElement(elements[0]);
        Element secondElement = recognizeElement(elements[1]);

//        if (firstElement instanceof Variable) {
//            this.parents
//                    .stream()
//                    .map(Cell::getColumns)
//                    .flatMap(Collection::stream)
//                    .filter(column -> column.NAME.equals(elements[0].strip()) || column.getSourceAndName().equals(elements[0].strip()))
//                    .findAny()
//                    .orElseThrow(() -> new BooleanExpressionException(
//                    ConstantController.getString("booleanExpression.error.elementIsNotAColumn")
//            ));
//        }

//        if (secondElement instanceof Variable) {
//            this.parents
//                .stream()
//                .map(Cell::getColumns)
//                .flatMap(Collection::stream)
//                .filter(column -> column.NAME.equals(elements[1].strip()) || column.getSourceAndName().equals(elements[1].strip()))
//                .findAny()
//                .orElseThrow(() -> new BooleanExpressionException(
//                    ConstantController.getString("booleanExpression.error.elementIsNotAColumn")
//                ));
//        }
//        if (secondElement instanceof Variable) {
//            boolean found = false;
//            outerLoop:
//            for (Cell parent : this.parents) {
//                List<Column> columns = parent.getColumns();
//                for (Column column : columns) {
//                    if (column.NAME.equals(elements[1].strip()) || column.getSourceAndName().equals(elements[1].strip())) {
//                        found = true;
//                        break outerLoop;
//                    }
//                }
//            }
//                for (Column column : leftSideColumns) {
//                    if (column.NAME.equals(elements[1].strip()) || column.getSourceAndName().equals(elements[1].strip())) {
//                        found = true;
//                    }
//                }
//            if (!found) {
//                throw new BooleanExpressionException(
//                        ConstantController.getString("booleanExpression.error.elementIsNotAColumn")
//                );
//            }
//        }

        return new AtomicExpression(firstElement, secondElement, RelationalOperator.getOperator(relationalOperator));
    }

    private static Element recognizeElement(String text) {
        return getElement(text.strip());
    }

    private static boolean hasAnyLogicalOperator(String text) {
        return hasLogicalOperator(text, LogicalOperator.AND) || hasLogicalOperator(text, LogicalOperator.OR);
    }

    private static boolean hasLogicalOperator(String text, LogicalOperator logicalOperator) {
        if (LogicalOperator.AND.equals(logicalOperator)) {
            return Pattern.compile("(?i)[() ]\\s*and[() ]\\s*").matcher(text).find();
        }

        return Pattern.compile("(?i)[() ]\\s*or[() ]\\s*").matcher(text).find();
    }
}
