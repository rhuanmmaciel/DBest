package dsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mxgraph.model.mxCell;

import controller.MainController;
import dsl.utils.DslUtils;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import enums.OperationArity;
import enums.OperationType;
import exceptions.ParentNullException;
import gui.frames.forms.operations.binary.CartesianProduct;
import gui.frames.forms.operations.binary.FormFrameJoin;
import gui.frames.forms.operations.binary.FormFrameLeftJoin;
import gui.frames.forms.operations.binary.FormFrameRightJoin;
import gui.frames.forms.operations.binary.FormFrameUnion;
import gui.frames.forms.operations.unary.FormFrameProjection;
import gui.frames.forms.operations.unary.FormFrameSelection;
import util.FileUtils;
import util.ImportFile;
import util.Utils;

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

	public static void reset() {

		expressions.clear();
		tables.clear();
		imports.clear();
		
	}

	public static void parser() {

		importTables();
		
		execute();

		reset();

	}
	
	private static void importTables(){
		
		for(String importStatement : imports) {
			
			String path = importStatement.substring(6, importStatement.indexOf(".head") + 5);
			
			String unnamedImport = importStatement.substring(0, importStatement.indexOf(".head") + 5);
			
			String tableName = path.substring(path.lastIndexOf("/") + 1, path.indexOf(".head") + 5);
			
			if(!unnamedImport.equals(importStatement)) {
				
				tableName = importStatement.substring(importStatement.indexOf(".head") + 7);
				
			}
			
			System.out.println(path);
			System.out.println(tableName);
			
		}
		
	}

	private static void execute() {

		if (tables.isEmpty() || expressions.isEmpty())
			return;

		boolean everyTableExist = true;

		List<String> tableFileNames = FileUtils.getDatFilesNames();

		for (String tableName : tables)
			if (!tableFileNames.contains(DslUtils.removePosition(tableName))) {

				everyTableExist = false;
				DslErrorListener.addErrors("Table '" + tableName + "' doesn't exist");

			}

		if (!everyTableExist)
			return;

		createTree();

	}

	private static void createTree() {

		Map<String, mxCell> operationsReady = new HashMap<>();

		for (String command : expressions) {

			Map<String, String> elements = DslUtils.recognizer(command);

			solveExpression(elements, operationsReady);

		}

	}

	private static void solveExpression(Map<String, String> elements, Map<String, mxCell> operationsReady) {

		if (MainController.unaryOperationsName().contains(elements.get("operation"))) {

			if (elements.get("source").contains("("))
				solveExpression(DslUtils.recognizer(elements.get("source")), operationsReady);

			else
				createTable(elements.get("source"), elements.get("sourcePosition"));

		} else {

			Map<String, String> tables = new HashMap<>();

			if (elements.get("source1").contains("("))
				solveExpression(DslUtils.recognizer(elements.get("source1")), operationsReady);
			else
				tables.put(elements.get("source1"), elements.get("source1Position"));

			if (elements.get("source2").contains("("))
				solveExpression(DslUtils.recognizer(elements.get("source2")), operationsReady);

			else
				tables.put(elements.get("source2"), elements.get("source2Position"));

			for (Map.Entry<String, String> table : tables.entrySet())
				createTable(table.getKey(), table.getValue());

		}

		boolean replace = false;
		int replaced = 0;

		OperationCell operationCell = null;

		OperationArity arity = null;

		List<Cell> parents = new ArrayList<>();

		if (Utils.containsIgnoreCase(MainController.unaryOperationsName(), elements.get("operation"))
				&& operationsReady.containsKey(elements.get("source"))) {

			elements.put("source", operationsReady.get(elements.get("source")).getId());
			replace = true;

		} else if (Utils.containsIgnoreCase(MainController.binaryOperationsName(), elements.get("operation"))
				&& (operationsReady.containsKey(elements.get("source1"))
						|| operationsReady.containsKey(elements.get("source2")))) {

			if (operationsReady.containsKey(elements.get("source1"))) {
				elements.put("source1", operationsReady.get(elements.get("source1")).getId());
				replaced--;
			}

			if (operationsReady.containsKey(elements.get("source2"))) {
				elements.put("source2", operationsReady.get(elements.get("source2")).getId());
				replaced++;
			}

			replace = true;

		}

		switch (elements.get("operation").toLowerCase()) {

		case "selection" -> {

			operationCell = new OperationCell(OperationType.SELECTION.getDisplayNameAndSymbol(),
					OperationType.SELECTION.getDisplayName(), null, OperationType.SELECTION, null, 80, 30);

			operationCell.setForm(FormFrameSelection.class);

			arity = OperationArity.UNARY;

		}
		case "projection" -> {

			operationCell = new OperationCell(OperationType.PROJECTION.getDisplayNameAndSymbol(),
					OperationType.PROJECTION.getDisplayName(), null, OperationType.PROJECTION, null, 80, 30);

			operationCell.setForm(FormFrameProjection.class);

			arity = OperationArity.UNARY;

		}
		case "cartesianproduct" -> {

			operationCell = new OperationCell(OperationType.CARTESIAN_PRODUCT.getDisplayNameAndSymbol(),
					OperationType.CARTESIAN_PRODUCT.getDisplayName(), null, OperationType.CARTESIAN_PRODUCT, null, 80,
					30);

			operationCell.setForm(CartesianProduct.class);

			arity = OperationArity.BINARY;

		}
		case "join" -> {

			operationCell = new OperationCell(OperationType.JOIN.getDisplayNameAndSymbol(),
					OperationType.JOIN.getDisplayName(), null, OperationType.JOIN, null, 80, 30);

			operationCell.setForm(FormFrameJoin.class);

			arity = OperationArity.BINARY;

		}
		case "leftjoin" -> {

			operationCell = new OperationCell(OperationType.LEFT_JOIN.getDisplayNameAndSymbol(),
					OperationType.LEFT_JOIN.getDisplayName(), null, OperationType.LEFT_JOIN, null, 80, 30);

			operationCell.setForm(FormFrameLeftJoin.class);

			arity = OperationArity.BINARY;

		}
		case "rightjoin" -> {

			operationCell = new OperationCell(OperationType.RIGHT_JOIN.getDisplayNameAndSymbol(),
					OperationType.RIGHT_JOIN.getDisplayName(), null, OperationType.RIGHT_JOIN, null, 80, 30);

			operationCell.setForm(FormFrameRightJoin.class);

			arity = OperationArity.BINARY;

		}
		case "union" -> {

			operationCell = new OperationCell(OperationType.UNION.getDisplayNameAndSymbol(),
					OperationType.UNION.getDisplayName(), null, OperationType.UNION, null, 80, 30);

			operationCell.setForm(FormFrameUnion.class);

			arity = OperationArity.BINARY;

		}
		}

		if (arity == OperationArity.UNARY && !replace) {

			String parentName = elements.get("source");
			parents.add(MainController.getCells().values().stream()
					.filter(cell -> cell.getName().equals(parentName) && !cell.hasChild()).findAny().orElse(null));

		} else if (arity == OperationArity.BINARY && !replace) {

			String parentName1 = elements.get("source1");
			String parentName2 = elements.get("source2");

			parents.add(MainController.getCells().values().stream()
					.filter(cell -> cell.getName().equals(parentName1) && !cell.hasChild()).findAny().orElse(null));
			parents.add(MainController.getCells().values().stream()
					.filter(cell -> cell.getName().equals(parentName2) && !cell.hasChild()).findAny().orElse(null));

		} else if (arity == OperationArity.UNARY) {

			parents.add(MainController.getCells().get(MainController.getCells().keySet().stream()
					.filter(jCell -> jCell.getId().equals(elements.get("source"))).findAny().orElse(null)));

		} else {

			String parentName1 = elements.get("source1");
			String parentName2 = elements.get("source2");

			switch (replaced) {

			case -1 -> {
				parents.add(MainController.getCells().get(MainController.getCells().keySet().stream()
						.filter(jCell -> jCell.getId().equals(elements.get("source1"))).findAny().orElse(null)));
				parents.add(MainController.getCells().values().stream()
						.filter(cell -> cell.getName().equals(parentName2) && !cell.hasChild()).findAny().orElse(null));

			}

			case 1 -> {
				parents.add(MainController.getCells().values().stream()
						.filter(cell -> cell.getName().equals(parentName1) && !cell.hasChild()).findAny().orElse(null));
				parents.add(MainController.getCells().get(MainController.getCells().keySet().stream()
						.filter(jCell -> jCell.getId().equals(elements.get("source2"))).findAny().orElse(null)));

			}
			case 0 -> {
				parents.add(MainController.getCells().get(MainController.getCells().keySet().stream()
						.filter(jCell -> jCell.getId().equals(elements.get("source1"))).findAny().orElse(null)));
				parents.add(MainController.getCells().get(MainController.getCells().keySet().stream()
						.filter(jCell -> jCell.getId().equals(elements.get("source2"))).findAny().orElse(null)));

			}

			}

		}

		if (parents.contains(null))
			throw new ParentNullException("Parent is null");

		mxCell jCell = MainController.putOperationCell(operationCell, parents, elements.get("command"),
				DslUtils.getPosition(elements.get("position")));

		operationsReady.put(elements.get("command"), jCell);

	}

	private static void createTable(String tableName, String position) {

		TableCell table = new ImportFile(DslUtils.removePosition(tableName) + ".head").getResult();

		MainController.putTableCell(table, DslUtils.getPosition(position));

	}

}
