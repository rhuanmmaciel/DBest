package dsl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import controllers.ConstantController;
import controllers.MainController;
import dsl.entities.BinaryExpression;
import dsl.entities.Expression;
import dsl.entities.OperationExpression;
import dsl.entities.Relation;
import dsl.entities.VariableDeclaration;
import dsl.utils.DslUtils;
import entities.cells.CSVTableCell;
import entities.cells.FYITableCell;
import enums.FileType;
import enums.TableType;
import exceptions.dsl.InputException;
import gui.frames.dsl.TextEditor;
import sgbd.table.Table;

public class DslController {

    private DslController() {

    }

    private static final List<String> COMMANDS = new ArrayList<>();

    private static final Map<String, VariableDeclaration> DECLARATIONS = new HashMap<>();

    public static void addCommand(String command) {
        COMMANDS.add(command);
    }

    public static String getVariableContent(String input) {
        return DECLARATIONS.get(input).getExpression();
    }

    public static boolean containsVariable(String input) {
        return DECLARATIONS.containsKey(input);
    }

    public static void reset() {
        COMMANDS.clear();
        DECLARATIONS.clear();
        DslErrorListener.clearErrors();
    }

    public static void parser() throws InputException {
        execute();
        reset();
    }

    private static void execute() throws InputException {
        for (String command : COMMANDS) {
            switch (DslUtils.commandRecognizer(command)) {
                case IMPORT_STATEMENT -> importTable(command);
                case EXPRESSION -> solveExpression(DslUtils.expressionRecognizer(command));
                case VARIABLE_DECLARATION -> solveDeclaration(new VariableDeclaration(command));
            }

            if (!DslErrorListener.getErrors().isEmpty()) {
                return;
            }
        }
    }

    private static void importTable(String importStatement) throws InputException {
        String path = importStatement.substring(6, importStatement.indexOf(FileType.HEADER.extension) + 5);

        String tableName;

        if (path.startsWith("this.")) {
            tableName = path.substring(path.indexOf("this.") + 5, path.indexOf(FileType.HEADER.extension));
            path = String.format("%s/%s", TextEditor.getLastPath(), path.substring(path.indexOf("this.") + 5));
        } else {
            tableName = path.substring(path.lastIndexOf("/") + 1, path.indexOf(FileType.HEADER.extension));
        }

        String unnamedImport = importStatement.substring(0, importStatement.indexOf(FileType.HEADER.extension) + 5);

        if (!unnamedImport.equals(importStatement)) {
            tableName = importStatement.substring(importStatement.indexOf(FileType.HEADER.extension) + 7);
        }

        if (MainController.getTables().containsKey(DslUtils.clearTableName(tableName))) {
            String message = String.format("%s: '%s'", ConstantController.getString("dsl.error.sameName"), DslUtils.clearTableName(tableName));
            throw new InputException(message);
        }

        TableType tableType;

        try {
            JsonObject headerFile = new Gson().fromJson(new FileReader(path), JsonObject.class);
            tableType = headerFile
                .getAsJsonObject("information")
                .get("file-path")
                .getAsString()
                .replaceAll("' | \"", "")
                .endsWith(".dat") ? TableType.FYI_TABLE : TableType.CSV_TABLE;
        } catch (FileNotFoundException exception) {
            String message = String.format("%s: '%s%s'", ConstantController.getString("dsl.error.fileNotFound"), DslUtils.clearTableName(tableName), FileType.HEADER.extension);
            throw new InputException(message);
        }

        Table table = Table.loadFromHeader(path);
        table.open();

        switch (tableType) {
            case CSV_TABLE -> MainController.saveTable(new CSVTableCell(DslUtils.clearTableName(tableName), table, new File(path)));
            case FYI_TABLE -> MainController.saveTable(new FYITableCell(DslUtils.clearTableName(tableName), table, new File(path)));
        }
    }

    private static void solveDeclaration(VariableDeclaration declaration) {
        DECLARATIONS.put(declaration.getVariable(), declaration);
    }

    private static void solveExpression(Expression<?> expression) {
        if (!DslErrorListener.getErrors().isEmpty()) {
            return;
        }

        if (expression instanceof OperationExpression operationExpression) {
            if (operationExpression.getSource() instanceof Relation relation) {
                createTable(relation);
            } else {
                solveExpression(operationExpression.getSource());
            }

            if (operationExpression instanceof BinaryExpression binaryExpression) {
                if (binaryExpression.getSource2() instanceof Relation relation) {
                    createTable(relation);
                } else {
                    solveExpression(binaryExpression.getSource2());
                }
            }
        } else if (expression instanceof Relation relation) {
            createTable(relation);
            return;
        } else {
            throw new RuntimeException("Expression is null");
        }

        MainController.putOperationCell(operationExpression);
    }

    private static void createTable(Relation relation) {
        MainController.putTableCell(relation);
    }
}
