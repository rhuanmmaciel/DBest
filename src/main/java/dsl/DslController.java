package dsl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import controllers.ConstantController;
import controllers.MainController;
import dsl.entities.*;
import dsl.utils.DslUtils;
import entities.cells.CSVTableCell;
import entities.cells.FYITableCell;
import enums.FileType;
import enums.CellType;
import exceptions.dsl.InputException;
import gui.frames.dsl.TextEditor;
import sgbd.source.table.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		String path = importStatement.substring(6, importStatement.indexOf(FileType.HEADER.extension) + 5);

		String tableName;

		if (path.startsWith("this.")) {

			tableName = path.substring(path.indexOf("this.") + 5, path.indexOf(FileType.HEADER.extension));
			path = TextEditor.getLastPath() + "/" + path.substring(path.indexOf("this.") + 5);

		} else {

			tableName = path.substring(path.lastIndexOf("/") + 1, path.indexOf(FileType.HEADER.extension));

		}

		String unnamedImport = importStatement.substring(0, importStatement.indexOf(FileType.HEADER.extension) + 5);

		if (!unnamedImport.equals(importStatement)) {

			tableName = importStatement.substring(importStatement.indexOf(FileType.HEADER.extension) + 7);

		}

		if (MainController.getTables().containsKey(DslUtils.clearTableName(tableName)))
			throw new InputException(ConstantController.getString("dsl.error.sameName") +
							": '" + DslUtils.clearTableName(tableName) + "'");

		CellType cellType;

		try {

			JsonObject headerFile = new Gson().fromJson(new FileReader(path), JsonObject.class);
			cellType = headerFile.getAsJsonObject("information").get("file-path").getAsString()
					.replaceAll("' | \"", "").endsWith(".dat")
					? CellType.FYI_TABLE : CellType.CSV_TABLE;

		}catch (FileNotFoundException e){

			throw new InputException(ConstantController.getString("dsl.error.fileNotFound") +": '" +
					DslUtils.clearTableName(tableName) + FileType.HEADER.extension);

		}


		Table table = Table.loadFromHeader(path);
		table.open();

		switch (cellType){

			case CSV_TABLE -> MainController.saveTable(new CSVTableCell(DslUtils.clearTableName(tableName),
					table, new File(path)));
			case FYI_TABLE -> 		MainController.saveTable(new FYITableCell(DslUtils.clearTableName(tableName),
					table, new File(path)));

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
