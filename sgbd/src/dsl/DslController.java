package dsl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mxgraph.model.mxCell;

import controller.MainController;
import dsl.entities.BinaryExpression;
import dsl.entities.Expression;
import dsl.entities.OperationExpression;
import dsl.entities.Relation;
import dsl.utils.DslUtils;
import gui.frames.dsl.TextEditor;
import util.FileUtils;

public class DslController {
	
	private static List<String> expressions = new LinkedList<>();
	private static List<String> imports = new ArrayList<>();
	private static List<String> tables = new ArrayList<>();

	public static void addExpression(String command) {

		expressions.add(command);

	}

	public static void addTable(String tableName) {

		tables.add(tableName);

	}

	public static void addImport(String importStatement) {

		imports.add(importStatement);

	}

	public static void addAloneTable(String table) {

		tables.add(table);
		expressions.add(table);
		
	}

	public static void reset() {

		expressions.clear();
		tables.clear();
		imports.clear();

	}

	public static void parser() {

		importTables();

		if (DslErrorListener.getErrors().isEmpty())
			execute();

		reset();

	}

	private static void importTables() {

		for (String importStatement : imports) {

			String path = importStatement.substring(6, importStatement.indexOf(".head") + 5);

			String tableName = null;

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

			tables.add(tableName);
			if (path == null || !FileUtils.copyDatFilesWithHead(path, tableName, Path.of("")))
				DslErrorListener.addErrors("Files '" + DslUtils.clearTableName(tableName) + ".head" + "' or '"
						+ DslUtils.clearTableName(tableName) + ".dat' not found");

		}

	}

	private static void execute() {

		if (expressions.isEmpty() && imports.isEmpty())
			return;

		boolean everyTableExist = true;

		List<String> tableFileNames = FileUtils.getDatFilesNames();

		for (String tableName : tables)
			if (!tableFileNames.contains(DslUtils.clearTableName(tableName))) {

				everyTableExist = false;
				DslErrorListener.addErrors("Table '" + DslUtils.clearTableName(tableName) + "' doesn't exist");

			}

		if (!everyTableExist)
			return;

		createTree();

	}


	private static void createTree() {

		Map<Expression<?>, mxCell> operationsReady = new HashMap<>();

		for (String command : expressions) {

			solveExpression(DslUtils.expressionRecognizer(command), operationsReady);

		}

	}

	private static void solveExpression(Expression<?> expression, Map<Expression<?>, mxCell> operationsReady) {

		if (expression instanceof OperationExpression operationExpression) {
			
			if (operationExpression.getSource() instanceof Relation relation)
				createTable(relation);
			else
				solveExpression(operationExpression.getSource(), operationsReady);

			if (operationExpression instanceof BinaryExpression binaryExpression) 
				
				if (binaryExpression.getSource2() instanceof Relation relation)
					createTable(relation);
				else
					solveExpression(binaryExpression.getSource2(), operationsReady);
				

		} else if(expression instanceof Relation relation){
		
			createTable(relation);
			return;
			
		}else {
			
			throw new RuntimeException("expression is null");
			
		}
		
		MainController.putOperationCell(operationExpression);
		
	}

	private static void createTable(Relation relation) {

		MainController.putTableCell(relation);

	}

}
