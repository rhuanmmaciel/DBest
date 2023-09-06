package dsl;

import java.io.File;
import java.util.*;

import controller.ConstantController;
import controller.MainController;
import dsl.entities.BinaryExpression;
import dsl.entities.Expression;
import dsl.entities.OperationExpression;
import dsl.entities.Relation;
import dsl.entities.VariableDeclaration;
import dsl.utils.DslUtils;
import enums.FileType;
import exceptions.dsl.InputException;
import files.FileUtils;
import gui.frames.dsl.TextEditor;

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
		DslErrorListener.clearErrors();

	}

	public static void parser() throws InputException {

		execute();
		reset();

	}

	private static void execute() throws InputException {

		for (String command : commands) {

			switch (DslUtils.commandRecognizer(command)) {

			case IMPORT_STATEMENT -> importTable(command);
			case EXPRESSION ->
				solveExpression(DslUtils.expressionRecognizer(command));
			case VARIABLE_DECLARATION -> solveDeclaration(new VariableDeclaration(command));

			}

		}

	}

	private static void importTable(String importStatement) throws InputException {

		String path = importStatement.substring(6, importStatement.indexOf(FileType.HEADER.EXTENSION) + 5);

		String tableName;

		if (path.startsWith("this.")) {

			tableName = path.substring(path.indexOf("this.") + 5, path.indexOf(FileType.HEADER.EXTENSION));
			path = TextEditor.getLastPath() + "/" + path.substring(path.indexOf("this.") + 5);

		} else {

			tableName = path.substring(path.lastIndexOf("/") + 1, path.indexOf(FileType.HEADER.EXTENSION));

		}

		String unnamedImport = importStatement.substring(0, importStatement.indexOf(FileType.HEADER.EXTENSION) + 5);

		if (!unnamedImport.equals(importStatement)) {

			tableName = importStatement.substring(importStatement.indexOf(FileType.HEADER.EXTENSION) + 7);

		}

		if (MainController.getTables().containsKey(DslUtils.clearTableName(tableName)))

			throw new InputException(ConstantController.getString("dsl.error.sameName") +
							": '" + DslUtils.clearTableName(tableName) + "'");

		else if (!FileUtils.copyToTempDirectory(new File(path)))
			throw new InputException(ConstantController.getString("dsl.error.fileNotFound") +": '" + DslUtils.clearTableName(tableName) + FileType.HEADER.EXTENSION + "' ou '"
					+ DslUtils.clearTableName(tableName) + ".dat'");

		else {

//			MainController.saveTable(new FyiTableCell(null, DslUtils.clearTableName(tableName), "fyi",
//					Table.loadFromHeader(tableName+FileType.HEADER.EXTENSION), new File()));

		}
	}

	private static void solveDeclaration(VariableDeclaration declaration) {

		declarations.put(declaration.getVariable(), declaration);

	}

	private static void solveExpression(Expression<?> expression) throws InputException {

		if (expression instanceof OperationExpression operationExpression) {

			if (operationExpression.getSource() instanceof Relation relation)
				createTable(relation);
			else
				solveExpression(operationExpression.getSource());

			if (operationExpression instanceof BinaryExpression binaryExpression)

				if (binaryExpression.getSource2() instanceof Relation relation2)
					createTable(relation2);
				else
					solveExpression(binaryExpression.getSource2());

		} else if (expression instanceof Relation relation) {

			createTable(relation);
			return;

		} else
			throw new InputException("expression is null");



		MainController.putOperationCell(operationExpression);

	}

	private static void createTable(Relation relation){

		MainController.putTableCell(relation);

	}

}
