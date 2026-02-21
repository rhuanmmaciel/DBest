package dsl;

import booleanexpression.BooleanExpressionRecognizer;
import controllers.MainController;
import database.TableCreator;
import dsl.entities.*;
import dsl.utils.DslUtils;
import enums.FileType;
import enums.OperationType;
import exceptions.dsl.InputException;
import gui.frames.dsl.TextEditor;
import ibd.query.Operation;
import ibd.query.binaryop.join.Join;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.lookup.ExpressionConverter;
import ibd.query.lookup.LookupFilter;
import ibd.query.sourceop.IndexScan;
import ibd.query.unaryop.aggregation.AggregationType;
import ibd.table.Table;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lib.booleanexpression.entities.expressions.BooleanExpression;
import static operations.unary.Sort.PREFIXES;
import utils.Utils;

public class DslParser {

    private static final List<String> commands = new ArrayList<>();
    private static final List<Operation> queries = new ArrayList<>();
    private static final Map<String, Table> tables = new HashMap<>();
    private static final Map<String, VariableDeclaration> declarations = new HashMap<>();

    public static Operation readQuery(File fileName)throws Exception {
        String content = readQuery_(fileName);
        parser(content);
        return queries.get(0);
    }
    
    
    private static String readQuery_(File fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        FileInputStream fis = new FileInputStream(fileName);
        int ch;
        while ((ch = fis.read()) != -1) {
            content.append((char) ch);

            // Add a newline character after each line
            if (ch == '\n') {
                content.append('\n');
            }
        }
        return content.toString();
    }
    
    public static void addCommand(String command) {

        commands.add(command);

    }

    public static String getVariableContent(String input) {

        return declarations.get(input).getExpression();

    }

    public static boolean containsVariable(String input) {

        return declarations.containsKey(input);

    }

    public static void reset() {

        commands.clear();
        declarations.clear();
        DslErrorListener.clearErrors();

    }

    public static void parser(String content) throws Exception {
        //reset what the antlr parser has done. Have to remove  antlr to prevent it from messing with the parse
        commands.clear();

        String[] commands_ = content.split(";");

        // Iterate and print each command
        for (String command : commands_) {
            commands.add(command.trim());  // Use trim() to remove leading/trailing spaces
        }
        execute();
        reset();

    }

    public static void parser() throws Exception {
        execute();
        reset();

    }

    private static void execute() throws Exception {

        for (String command : commands) {

            switch (DslUtils.commandRecognizer(command)) {

                case IMPORT_STATEMENT ->
                    importTable(command);
                case EXPRESSION ->
                    queries.add(solveExpression(DslUtils.expressionRecognizer(command, tables)));
                case VARIABLE_DECLARATION ->
                    solveDeclaration(new VariableDeclaration(command));

            }

        }

    }

    private static void importTable(String importStatement) throws InputException {

        //String path = importStatement.substring(6, importStatement.indexOf(FileType.HEADER.extension) + 5);
        String path = importStatement.substring(6);
        String fileExtension = "";
        String filePath = "";
        String tableName;

        if (path.startsWith("this.")) {

            tableName = path.substring(path.indexOf("this.") + 5, path.indexOf(FileType.HEADER.extension));
            path = TextEditor.getLastPath() + "/" + path.substring(path.indexOf("this.") + 5);

        } else {

            // Extract the file path from the input
            filePath = path.replaceFirst("import\\s+", "");  // Remove 'import '

            filePath = filePath.trim();

            // Create a Path object
            Path path_ = Paths.get(filePath);

            // Extract the file name and extension
            String fileName = path_.getFileName().toString();

            // Extract file extension
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                fileExtension = fileName.substring(dotIndex + 1);
                fileName = fileName.substring(0, dotIndex);  // Remove the extension from the file name
            }
            path = path_.toString();
            tableName = fileName;
            //tableName = path.substring(path.lastIndexOf("/") + 1, path.indexOf(FileType.HEADER.extension));
        }

        String unnamedImport = importStatement.substring(0, importStatement.indexOf(FileType.HEADER.extension) + 5);

        if (!unnamedImport.equals(importStatement)) {

            tableName = importStatement.substring(importStatement.indexOf(FileType.HEADER.extension) + 7);

        }
        String tableKey = DslUtils.clearTableName(tableName);
        if (MainController.getTables().containsKey(tableKey)) {
            return;
//            throw new InputException(ConstantController.getString("dsl.error.sameName")
//                    + ": '" + DslUtils.clearTableName(tableName) + "'");
        }

        try {
            if (fileExtension.equals("dat")) {
                Table table = TableCreator.openBTreeTable(filePath);
                tables.put(tableKey, table);

                return;
            } else if (fileExtension.equals("head")) {
                Table table = TableCreator.loadFromHeader(filePath);
                tables.put(tableKey, table);
                return;
            }
        } catch (Exception ex) {
            throw new InputException(ex.getMessage());
        }

    }

    private static void solveDeclaration(VariableDeclaration declaration) {

        declarations.put(declaration.getVariable(), declaration);

    }

    private static Operation solveExpression(Expression<?> expression) throws Exception {

        Operation child1 = null;
        Operation child2 = null;

        if (expression instanceof OperationExpression operationExpression) {

            if (operationExpression.getSource() instanceof Relation relation) {
                child1 = createTable(relation);
            } else {
                child1 = solveExpression(operationExpression.getSource());
            }

            if (operationExpression instanceof BinaryExpression binaryExpression) {
                if (binaryExpression.getSource2() instanceof Relation relation2) {
                    child2 = createTable(relation2);
                } else {
                    child2 = solveExpression(binaryExpression.getSource2());
                }
            }

        } else if (expression instanceof Relation relation) {

            return createTable(relation);

        } else {
            throw new InputException("expression is null");
        }

        System.out.println("query parser: expression: " + operationExpression.getType().getFormattedDisplayName());
        return setOperation(operationExpression, child1, child2);
        
    }

    private static Operation setOperation(OperationExpression operationExpression, Operation child1, Operation child2) throws Exception {
        List<String> arguments = operationExpression.getArguments();
        
        if (operationExpression.getType() == OperationType.DUPLICATE_REMOVAL) {
            return new ibd.query.unaryop.DuplicateRemoval(child1);
        }
        if (operationExpression.getType() == OperationType.HASH_DUPLICATE_REMOVAL) {
            return new ibd.query.unaryop.HashDuplicateRemoval(child1);
        }
        if (operationExpression.getType() == OperationType.MATERIALIZATION) {
            return new ibd.query.unaryop.Materialization(child1);
        }
        if (operationExpression.getType() == OperationType.MEMOIZE) {
            return new ibd.query.unaryop.Memoize(child1);
        }
        if (operationExpression.getType() == OperationType.HASH) {
            return new ibd.query.unaryop.HashIndex(child1);
        }
        if (operationExpression.getType() == OperationType.MATERIALIZATION) {
            return new ibd.query.unaryop.Materialization(child1);
        }
        if (operationExpression.getType() == OperationType.SCAN) {
            return new ibd.query.unaryop.Scan(child1);
        }
        if (operationExpression.getType() == OperationType.EXPLODE) {
            return new ibd.query.unaryop.Explode(child1, arguments.get(0), arguments.get(1));
        }
        if (operationExpression.getType() == OperationType.AUTO_INCREMENT) {
            return new ibd.query.unaryop.AutoIncrement(child1, "autoIncrement",arguments.get(0), Integer.parseInt(arguments.get(1)));
        }
        if (operationExpression.getType() == OperationType.PROJECTION) {
            //return new ibd.query.unaryop.Projection(child1, "projection", arguments.toArray(new String[0]));
            return new ibd.query.unaryop.Projection(child1,  arguments.toArray(new String[0]));
        }
        if (operationExpression.getType() == OperationType.FILTER) {
            String expression = arguments.get(0);
            BooleanExpression booleanExpression = BooleanExpressionRecognizer.recognize(expression);
            LookupFilter filter = ExpressionConverter.convert(booleanExpression);
            return new ibd.query.unaryop.filter.Filter(child1, filter);
        }
        if (operationExpression.getType() == OperationType.SORT) {
        String column = arguments.get(0);

        boolean isAscendingOrder = !Utils.startsWithIgnoreCase(column, "DESC:");

        column = Utils.replaceIfStartsWithIgnoreCase(column, PREFIXES, "");
        return new ibd.query.unaryop.sort.Sort(child1, column, isAscendingOrder);
        }
        
        if (operationExpression.getType() == OperationType.AGGREGATION) {
//            String fixedArgument = arguments
//                    .get(0)
//                    .substring(0, Utils.getFirstMatchingPrefixIgnoreCase(arguments.get(0), PREFIXES).length()) + Column.composeSourceAndName(parentCell.getSourceNameByColumnName(arguments.get(0).substring(Utils.getFirstMatchingPrefixIgnoreCase(arguments.get(0), PREFIXES).length())), arguments.get(0).substring(Utils.getFirstMatchingPrefixIgnoreCase(arguments.get(0), PREFIXES).length()));
//
//            List<AggregationType> aggregations = AggregationType.getAggregationTypes(fixedArgument);
//            return new ibd.query.unaryop.aggregation.AllAggregation(child1, "aggregate",  aggregations);
        }
        if (operationExpression.getType() == OperationType.GROUP) {
            String groupBy = arguments.get(0);
            List<AggregationType> aggregations = AggregationType.getAggregationTypes1(arguments);
            return new ibd.query.unaryop.aggregation.Aggregation(child1, "aggregate", groupBy, aggregations,true);
        }
        if (operationExpression.getType() == OperationType.HASH_GROUP) {
            String groupBy = arguments.get(0);
            List<AggregationType> aggregations = AggregationType.getAggregationTypes1(arguments);
            return new ibd.query.unaryop.aggregation.Aggregation(child1, "aggregate", groupBy, aggregations,false);
        }
        
        
        if (operationExpression.getType() == OperationType.CARTESIAN_PRODUCT) {
            return new ibd.query.binaryop.join.CrossJoin(child1, child2);
        }
        if (operationExpression.getType() == OperationType.NESTED_LOOP_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.NestedLoopJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.HASH_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.HashInnerJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.MERGE_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.MergeJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.NESTED_LOOP_LEFT_OUTER_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.outer.NestedLoopLeftJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.NESTED_LOOP_RIGHT_OUTER_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.outer.NestedLoopRightJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.HASH_LEFT_OUTER_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.outer.HashLeftJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.HASH_RIGHT_OUTER_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.outer.HashRightJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.HASH_FULL_OUTER_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.outer.HashFullOuterJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.MERGE_LEFT_OUTER_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.outer.MergeLeftOuterJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.MERGE_RIGHT_OUTER_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.outer.MergeRightOuterJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.MERGE_FULL_OUTER_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.outer.MergeFullOuterJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.NESTED_LOOP_LEFT_SEMI_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.semi.NestedLoopSemiJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.HASH_LEFT_SEMI_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.semi.HashLeftSemiJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.HASH_RIGHT_ANTI_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.semi.HashRightSemiJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.MERGE_LEFT_ANTI_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.semi.MergeLeftSemiJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.MERGE_RIGHT_ANTI_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.semi.MergeRightSemiJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.NESTED_LOOP_LEFT_ANTI_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.anti.NestedLoopLeftAntiJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.HASH_LEFT_ANTI_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.anti.HashLeftAntiJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.HASH_RIGHT_ANTI_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.anti.HashRightAntiJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.MERGE_LEFT_ANTI_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.anti.MergeLeftAntiJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.MERGE_RIGHT_ANTI_JOIN) {
            JoinPredicate joinPredicate = Join.createJoinPredicate(arguments);
            return new ibd.query.binaryop.join.anti.MergeRightAntiJoin(child1, child2, joinPredicate);
        }
        if (operationExpression.getType() == OperationType.APPEND) {
            return new ibd.query.binaryop.set.Append(child1, child2);
        }
        if (operationExpression.getType() == OperationType.UNION) {
            return new ibd.query.binaryop.set.Union(child1, child2);
        }
        if (operationExpression.getType() == OperationType.HASH_UNION) {
            return new ibd.query.binaryop.set.HashUnion(child1, child2);
        }
        if (operationExpression.getType() == OperationType.INTERSECTION) {
            return new ibd.query.binaryop.set.Intersection(child1, child2);
        }
        if (operationExpression.getType() == OperationType.HASH_INTERSECTION) {
            return new ibd.query.binaryop.set.HashIntersection(child1, child2);
        }
        if (operationExpression.getType() == OperationType.DIFFERENCE) {
            return new ibd.query.binaryop.set.Difference(child1, child2);
        }
        if (operationExpression.getType() == OperationType.HASH_DIFFERENCE) {
            return new ibd.query.binaryop.set.HashDifference(child1, child2);
        }
        
        return null;
    }

    private static Operation createTable(Relation relation) throws Exception {

        Table table = tables.get(relation.getFirstName());
        table.open();
        Operation scan = new IndexScan(relation.getName(), table);
        return scan;

    }

}
