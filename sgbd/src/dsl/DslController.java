package dsl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mxgraph.model.mxCell;

import controller.MainController;
import dsl.entities.BinaryExpression;
import dsl.entities.Expression;
import dsl.entities.OperationExpression;
import dsl.entities.Relation;
import dsl.entities.VariableDeclaration;
import dsl.utils.DslUtils;
import gui.frames.dsl.TextEditor;
import util.FileUtils;

public class DslController {

	private static List<String> commands = new ArrayList<>();
	private static Map<String, VariableDeclaration> declarations = new HashMap<>();
	private static List<String> sources = new ArrayList<>();

	public static void addCommand(String command) {

		commands.add(command);

	}
	
	public static Expression<?> getVariableContent(String input){
		
		return declarations.get(input).getContent();
		
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

		for(String command : commands) {
			
			switch(DslUtils.commandRecognizer(command)) {
			
			case IMPORT_STATEMENT -> importTable(command);
			case EXPRESSION -> solveExpression(DslUtils.expressionRecognizer(command), new HashMap<Expression<?>, mxCell>());
			case VARIABLE_DECLARATION -> solveDeclaration(new VariableDeclaration(command));
			
			}
			
			if(!DslErrorListener.getErrors().isEmpty())
				return;
			
		}

	}

	private static void importTable(String importStatement) {
		
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

		sources.add(tableName);
		if (path == null || !FileUtils.copyDatFilesWithHead(path, tableName, Path.of("")))
			DslErrorListener.addErrors("Files '" + DslUtils.clearTableName(tableName) + ".head" + "' or '"
					+ DslUtils.clearTableName(tableName) + ".dat' not found");


	}
	
	private static void solveDeclaration(VariableDeclaration declaration) {

		declarations.put(declaration.getVariable(), declaration);

	}

	private static void solveExpression(Expression<?> expression, Map<Expression<?>, mxCell> operationsReady) {

		if(!DslErrorListener.getErrors().isEmpty())
			return;
		
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
