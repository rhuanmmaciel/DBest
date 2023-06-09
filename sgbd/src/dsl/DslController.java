package dsl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.MainController;
import dsl.entities.BinaryExpression;
import dsl.entities.Expression;
import dsl.entities.OperationExpression;
import dsl.entities.Relation;
import dsl.entities.VariableDeclaration;
import dsl.utils.DslUtils;
import files.FileUtils;
import gui.frames.dsl.TextEditor;
import sgbd.table.Table;

public class DslController {

	private static final List<String> commands = new ArrayList<>();
	private static final Map<String, VariableDeclaration> declarations = new HashMap<>();

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

	}

	public static void parser() {

		execute();
		reset();

	}

	private static void execute() {

		for (String command : commands) {

			switch (DslUtils.commandRecognizer(command)) {

			case IMPORT_STATEMENT -> importTable(command);
			case EXPRESSION ->
				solveExpression(DslUtils.expressionRecognizer(command));
			case VARIABLE_DECLARATION -> solveDeclaration(new VariableDeclaration(command));

			}

			if (!DslErrorListener.getErrors().isEmpty())
				return;

		}

	}

	private static void importTable(String importStatement) {

		String path = importStatement.substring(6, importStatement.indexOf(".head") + 5);

		String tableName;

		if (path.startsWith("this.")) {

			tableName = path.substring(path.indexOf("this.") + 5, path.indexOf(".head"));
			path = TextEditor.getLastPath() + "/" + path.substring(path.indexOf("this.") + 5);

		} else {

			tableName = path.substring(path.lastIndexOf("/") + 1, path.indexOf(".head"));

		}

		String unnamedImport = importStatement.substring(0, importStatement.indexOf(".head") + 5);

		if (!unnamedImport.equals(importStatement)) {

			tableName = importStatement.substring(importStatement.indexOf(".head") + 7);

		}

		if (MainController.getTables().containsKey(DslUtils.clearTableName(tableName)))
			DslErrorListener
					.addErrors("Já existe uma tabela com o mesmo nome: '" + DslUtils.clearTableName(tableName) + "'");

		else if (!FileUtils.copyDatFilesWithHead(path, tableName, Path.of("")))
			DslErrorListener.addErrors("Arquivo '" + DslUtils.clearTableName(tableName) + ".head" + "' ou '"
					+ DslUtils.clearTableName(tableName) + ".dat' não encontrado");

		else
			MainController.saveTable(DslUtils.clearTableName(tableName),
					Table.loadFromHeader(DslUtils.clearTableName(tableName) + ".head"));

	}

	private static void solveDeclaration(VariableDeclaration declaration) {

		declarations.put(declaration.getVariable(), declaration);

	}

	private static void solveExpression(Expression<?> expression) {

		if (!DslErrorListener.getErrors().isEmpty())
			return;

		if (expression instanceof OperationExpression operationExpression) {

			if (operationExpression.getSource() instanceof Relation relation)
				createTable(relation);
			else
				solveExpression(operationExpression.getSource());

			if (operationExpression instanceof BinaryExpression binaryExpression)

				if (binaryExpression.getSource2() instanceof Relation relation)
					createTable(relation);
				else
					solveExpression(binaryExpression.getSource2());

		} else if (expression instanceof Relation relation) {

			createTable(relation);
			return;

		} else {

			throw new RuntimeException("expression is null");

		}

		MainController.putOperationCell(operationExpression);

	}

	private static void createTable(Relation relation) {

		MainController.putTableCell(relation);

	}

}
