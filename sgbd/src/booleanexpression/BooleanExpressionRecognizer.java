package booleanexpression;

import booleanexpression.antlr.BooleanExpressionDSLController;
import booleanexpression.antlr.BooleanExpressionDSLLexer;
import booleanexpression.antlr.BooleanExpressionDSLParser;
import com.mxgraph.model.mxCell;
import controller.ConstantController;
import entities.cells.Cell;
import lib.booleanexpression.entities.elements.Element;
import lib.booleanexpression.entities.elements.Null;
import lib.booleanexpression.entities.elements.Value;
import lib.booleanexpression.entities.elements.Variable;
import lib.booleanexpression.entities.expressions.AtomicExpression;
import lib.booleanexpression.entities.expressions.BooleanExpression;
import lib.booleanexpression.entities.expressions.LogicalExpression;
import lib.booleanexpression.enums.LogicalOperator;
import lib.booleanexpression.enums.RelationalOperator;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import sgbd.prototype.query.fields.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static booleanexpression.Utils.getElement;

public class BooleanExpressionRecognizer {

    private final List<Cell> parents;

    public BooleanExpressionRecognizer(mxCell jCell){
        this.parents = Cell.getCells().get(jCell).getParents();
    }

    public String recognizer(BooleanExpression booleanExpression){

        if (booleanExpression instanceof AtomicExpression atomicExpression) return recognizeAtomic(atomicExpression);

        LogicalExpression logicalExpression = (LogicalExpression) booleanExpression;

        StringBuilder logicalExpressionSB = new StringBuilder();
        int amountOfExpressions = logicalExpression.getExpressions().size();
        LogicalOperator operator = logicalExpression.getLogicalOperator();

        logicalExpressionSB.append(" ( ");

        for (int i = 0; i < logicalExpression.getExpressions().size(); i++){

            logicalExpressionSB.append(recognizer(logicalExpression.getExpressions().get(i)));
            if(i != amountOfExpressions - 1) logicalExpressionSB.append(operator);

        }

        logicalExpressionSB.append(" ) ");

        return logicalExpressionSB.toString();

    }

    private String recognizeAtomic(AtomicExpression atomicExpression){

        return " ( "+getString(atomicExpression.getFirstElement()) + " " +
                atomicExpression.getRelationalOperator().symbols[0] + " " +
                getString(atomicExpression.getSecondElement()) +
                " ) ";

    }

    private String getString(Element element){

        return switch (element){

            case Value value -> switch (value.getField()){

                case IntegerField field -> String.valueOf(field.getInt());
                case FloatField field -> String.valueOf(field.getFloat());
                case LongField field -> String.valueOf(field.getLong());
                case DoubleField field -> String.valueOf(field.getDouble());
                case StringField field -> "'"+field.getString()+"'";

                default -> throw new IllegalStateException("Unexpected value: " + value.getField());

            };

            case Null ignored -> ConstantController.NULL;

            default -> element.toString();

        };

    }

    public BooleanExpression recognizer(String txt) throws BooleanExpressionException {

        BooleanExpressionDSLParser parser = new BooleanExpressionDSLParser(
                new CommonTokenStream(new BooleanExpressionDSLLexer(CharStreams.fromString(txt))));

        parser.removeErrorListeners();

        ErrorListener errorListener = new ErrorListener();
        parser.addErrorListener(errorListener);

        ParseTreeWalker walker = new ParseTreeWalker();
        BooleanExpressionDSLController listener = new BooleanExpressionDSLController();

        walker.walk(listener, parser.command());

        if(!ErrorListener.getErrors().isEmpty())
            throw new BooleanExpressionException(ConstantController.getString("booleanExpression.error.invalidFormat"));

        return recognize(txt);

    }

    private BooleanExpression recognize(String txt) throws BooleanExpressionException {

        boolean isAtomic = !hasAnyLogicalOperator(txt);

        if(isAtomic) return (recognizeAtomic(txt));

        List<String> tokens = new ArrayList<>(tokenize(txt).stream().map(String::strip).toList());

        return consumeTokens(tokens);

    }

    private BooleanExpression consumeTokens(List<String> tokens) throws BooleanExpressionException {

        Optional<LogicalOperator> operator = whichLogicalOperator(tokens);
        prioritizeAnds(tokens);

        if (operator.isPresent())
            return recognizeLogical(operator.get(), tokens);

        return recognizeAtomic(tokens.get(0));

    }

    private void prioritizeAnds(List<String> tokens){

        int bracketsController = 0;

        boolean existsOr = false;

        List<Integer> andIndexes = new ArrayList<>();

        for(int i = 0; i < tokens.size(); i++){

            if(isRightBracket(tokens.get(i))) bracketsController++;
            if(isLeftBracket(tokens.get(i))) bracketsController--;

            if(bracketsController == 0 && isAnd(tokens.get(i))) andIndexes.add(i);

            if(bracketsController == 0 && isOr(tokens.get(i))) existsOr = true;

        }

        if(existsOr)
            for(Integer i : andIndexes) {
                tokens.add(i -1, "(");
                tokens.add(i+3, ")");
            }

    }

    private LogicalExpression recognizeLogical(LogicalOperator logicalOperator, List<String> tokens) throws BooleanExpressionException {

        List<BooleanExpression> expressions = new ArrayList<>();

        List<List<String>> eachExpressionTokens = new ArrayList<>();

        List<Integer> logicalOperatorIndexes = getExternalLogicalOperatorIndexes(tokens);

        int expressionIndex = 0;

        for(int i = 0; i < tokens.size(); i++){

            String currentToken = tokens.get(i);

            if(eachExpressionTokens.size() < expressionIndex + 1)
                eachExpressionTokens.add(new ArrayList<>());

            if(isAndOrOr(currentToken)){

                if(logicalOperatorIndexes.contains(i) &&
                        (
                                (isAnd(currentToken) && logicalOperator.equals(LogicalOperator.AND)) ||
                                (isOr(currentToken) && logicalOperator.equals(LogicalOperator.OR))
                        )
                )
                    expressionIndex++;
                else eachExpressionTokens.get(expressionIndex).add(currentToken);

            }else eachExpressionTokens.get(expressionIndex).add(currentToken);

        }

        for(List<String> expressionTokens : eachExpressionTokens)
            expressions.add(consumeTokens(expressionTokens));

        return new LogicalExpression(logicalOperator, expressions);

    }

    private List<Integer> getExternalLogicalOperatorIndexes(List<String> tokens){

        List<Integer> orIndexes = new ArrayList<>();
        List<Integer> andIndexes = new ArrayList<>();

        int bracketsController = 0;

        for(int i = 0; i < tokens.size(); i++){

            if(isRightBracket(tokens.get(i))) bracketsController++;
            if(isLeftBracket(tokens.get(i))) bracketsController--;

            if(bracketsController == 0 && isAnd(tokens.get(i))) andIndexes.add(i);

            if(bracketsController == 0 && isOr(tokens.get(i))) orIndexes.add(i);

        }

        return andIndexes.isEmpty() ? orIndexes : andIndexes;

    }

    private void removeUnnecessaryExternalBrackets(List<String> tokens){

        while(hasUnnecessaryExternalBrackets(tokens)){

            tokens.remove(0);
            tokens.remove(tokens.size()-1);

        }

    }

    private boolean hasUnnecessaryExternalBrackets(List<String> tokens){

        if(isLeftBracket(tokens.get(0)) && isRightBracket(tokens.get(tokens.size()-1))){

            int bracketsController = 0;
            for(String token : tokens.stream().limit(tokens.size()-1).toList()){

                if(isRightBracket(token)) bracketsController--;
                if(isLeftBracket(token)) bracketsController++;

                if(bracketsController < 1) return false;

            }

            return bracketsController == 1;

        }

        return false;

    }

    private Optional<LogicalOperator> whichLogicalOperator(List<String> tokens){

        removeUnnecessaryExternalBrackets(tokens);

        int bracketsController = 0;

        Optional<LogicalOperator> logicalOperator = Optional.empty();

        for(String token : tokens){

            if(isRightBracket(token)) bracketsController++;
            if(isLeftBracket(token)) bracketsController--;

            if(bracketsController == 0 && isAnd(token)) return Optional.of(LogicalOperator.AND);

            if(bracketsController == 0 && isOr(token)) logicalOperator = Optional.of(LogicalOperator.OR);

        }

        return logicalOperator;

    }

    private boolean isAnd(String txt){return txt.replace(" ", "").equalsIgnoreCase("and");}
    private boolean isOr(String txt){return txt.replace(" ", "").equalsIgnoreCase("or");}
    private boolean isAndOrOr(String txt){return isOr(txt) || isAnd(txt);}
    private boolean isRightBracket(String txt){return txt.replace(" ", "").equals(")");}
    private boolean isLeftBracket(String txt){return txt.replace(" ", "").equals("(");}

    private List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();

        String regex = "((?i)(?<=[\\s\\(\\)])+AND(?=[\\s\\(\\)])+)|((?i)(?<=[\\s\\(\\)])+OR(?=[\\s\\(\\)])+)|(\\()|(\\))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        int lastEnd = 0;
        while (matcher.find()) {

            if (matcher.start() > lastEnd)
                tokens.add(input.substring(lastEnd, matcher.start()).strip());

            tokens.add(matcher.group());
            lastEnd = matcher.end();

        }

        if (lastEnd < input.length())
            tokens.add(input.substring(lastEnd).strip());

        tokens.removeIf(String::isEmpty);

        return tokens;
    }


    private AtomicExpression recognizeAtomic(String txt) throws BooleanExpressionException {

        txt = txt.replace(")", "").replace("(","");

        String finalTxt = txt;

        String relationalOperator = ConstantController.RELATIONAL_OPERATORS.stream().filter(finalTxt::contains)
                .findFirst().orElseThrow(() -> new BooleanExpressionException(ConstantController.
                        getString("booleanExpression.error.relationalOperatorUnknown")));

        String[] elements = txt.split(relationalOperator);

        if(elements.length != 2)
            throw new BooleanExpressionException(ConstantController.
                    getString("booleanExpression.error.notTwoElementsInAtomicExpression"));

        Element firstElement = recognizeElement(elements[0]);
        Element secondElement = recognizeElement(elements[1]);

        if(firstElement instanceof Variable)
            parents.stream().map(Cell::getColumns).flatMap(Collection::stream)
                    .filter(x -> x.getName().equals(elements[0].strip()) || x.getSourceAndName().equals(elements[0].strip()))
                    .findAny()
                    .orElseThrow(() -> new BooleanExpressionException(ConstantController.
                            getString("booleanExpression.error.elementIsNotAColumn")));

        if(secondElement instanceof Variable)
            parents.stream().map(Cell::getColumns).flatMap(Collection::stream)
                    .filter(x -> x.getName().equals(elements[1].strip()) || x.getSourceAndName().equals(elements[1].strip()))
                    .findAny()
                    .orElseThrow(() -> new BooleanExpressionException(ConstantController.
                            getString("booleanExpression.error.elementIsNotAColumn")));

        return new AtomicExpression(firstElement, secondElement, RelationalOperator.getOperator(relationalOperator));

    }

    private Element recognizeElement(String txt){

        return getElement(txt.strip());

    }

    private boolean hasAnyLogicalOperator(String txt){

        return hasLogicalOperator(txt, LogicalOperator.AND) || hasLogicalOperator(txt, LogicalOperator.OR);

    }

    private boolean hasLogicalOperator(String txt, LogicalOperator logicalOperator){

        if(LogicalOperator.AND.equals(logicalOperator))
            return Pattern.compile("(?i)[() ]\\s*and[() ]\\s*").matcher(txt).find();

        return Pattern.compile("(?i)[() ]\\s*or[() ]\\s*").matcher(txt).find();

    }

}