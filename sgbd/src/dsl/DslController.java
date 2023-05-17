package dsl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mxgraph.model.mxCell;

import controller.MainController;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import enums.OperationArity;
import enums.OperationType;
import gui.frames.forms.operations.binary.CartesianProduct;
import gui.frames.forms.operations.binary.FormFrameJoin;
import gui.frames.forms.operations.unary.FormFrameProjection;
import gui.frames.forms.operations.unary.FormFrameSelection;
import util.ImportFile;

public class DslController {

	private static List<String> commands = new LinkedList<>();
	private static List<String> tables = new ArrayList<>();

	public static void addCommand(String command) {

		commands.add(command);

	}

	public static void addTable(String tableName) {

		tables.add(tableName);

	}

	public static void parser() {

		a();

		commands.clear();
		tables.clear();

	}

	private static void a() {

		if (tables.isEmpty() || commands.isEmpty())
			return;

		boolean everyTableExist = true;

		List<String> tableFileNames = getTableFileNames();

		for (String tableName : tables)
			if (!tableFileNames.contains(tableName))
				everyTableExist = false;

		if (!everyTableExist) {
			return;
		}

		createTree();

	}

	private static void createTree() {

		for (String tableName : tables) {

			if (!MainController.getCells().values().stream()
					.anyMatch(cell -> cell.getName().equals(tableName) && !cell.hasChild())) {

				TableCell table = new ImportFile(tableName + ".head").getResult();

				MainController.putTableCell(50, 100, table);

			}

		}

		Map<String, mxCell> operationsReady = new HashMap<>();

		for (String command : commands) {

			boolean replace = false;

			OperationCell operationCell = null;

			OperationType type = null;

			OperationArity arity = null;

			List<Cell> parents = new ArrayList<>();

			Map<String, String> elements = recognizer(command);

			if ((elements.get("operation").equals("selection") || elements.get("operation").equals("projection")) && operationsReady.containsKey(elements.get("source"))) {

				command.replace(elements.get("source"), operationsReady.get(elements.get("source")).getId());
				elements.put("source", operationsReady.get(elements.get("source")).getId());
				replace = true;

			}else {
				
				
			}

			if (elements.get("operation").equals("selection")) {

				operationCell = new OperationCell(OperationType.SELECTION.getSymbol(),
						OperationType.SELECTION.getName(), null, OperationType.SELECTION, null, 80, 30);

				operationCell.setForm(FormFrameSelection.class);

				type = OperationType.SELECTION;
				arity = OperationArity.UNARY;

			} else if (elements.get("operation").equals("projection")) {

				operationCell = new OperationCell(OperationType.PROJECTION.getSymbol(),
						OperationType.PROJECTION.getName(), null, OperationType.PROJECTION, null, 80, 30);

				operationCell.setForm(FormFrameProjection.class);

				type = OperationType.PROJECTION;
				arity = OperationArity.UNARY;

			} else if (elements.get("operation").equals("cartesian")) {

				operationCell = new OperationCell(OperationType.CARTESIAN_PRODUCT.getSymbol(),
						OperationType.CARTESIAN_PRODUCT.getName(), null, OperationType.CARTESIAN_PRODUCT, null, 80, 30);

				operationCell.setForm(CartesianProduct.class);

				type = OperationType.CARTESIAN_PRODUCT;
				arity = OperationArity.BINARY;

			} else if (elements.get("operation").equals("join")) {

				operationCell = new OperationCell(OperationType.JOIN.getSymbol(), OperationType.JOIN.getName(), null,
						OperationType.JOIN, null, 80, 30);

				operationCell.setForm(FormFrameJoin.class);

				type = OperationType.JOIN;
				arity = OperationArity.BINARY;

			}

			if (arity == OperationArity.UNARY && !replace) {

				String parentName = elements.get("source");
				parents.add(MainController.getCells().values().stream()
						.filter(cell -> cell.getName().equals(parentName) && !cell.hasChild()).findAny().orElse(null));

			} else if (arity == OperationArity.BINARY && !replace) {

				String parentName1 = elements.get("source").split(",")[0];
				String parentName2 = elements.get("source").split(",")[1];

				parents.add(MainController.getCells().values().stream()
						.filter(cell -> cell.getName().equals(parentName1) && !cell.hasChild()).findAny().orElse(null));
				parents.add(MainController.getCells().values().stream()
						.filter(cell -> cell.getName().equals(parentName2) && !cell.hasChild()).findAny().orElse(null));

			} else if (arity == OperationArity.UNARY) {
				
				parents.add(MainController.getCells().get(MainController.getCells().keySet().stream()
						.filter(jCell -> jCell.getId().equals(elements.get("source"))).findAny().orElse(null)));
				
			}else {
				
				//String parentName1 = elements.get("source").split(",")[0];
				//String parentName2 = elements.get("source").split(",")[1];
				
			}

			mxCell jCell = MainController.putOperationCell(50, 100, operationCell, parents, command, type);

			operationsReady.put(command, jCell);

		}

	}

	private static Map<String, String> recognizer(String input) {

		Map<String, String> elements = new HashMap<>();

		int endIndex = input.indexOf('(');

		if (input.contains("[")) {

			endIndex = Math.min(input.indexOf('['), endIndex);
			elements.put("predicate", input.substring(input.indexOf("[") + 1, input.indexOf("]")));

		}

		elements.put("operation", input.substring(0, endIndex).toLowerCase());

		elements.put("source", input.substring(input.indexOf("(") + 1, input.lastIndexOf(")")));

		return elements;

	}

	private static List<String> getTableFileNames() {

		List<String> tableFileNames = new ArrayList<>();
		File directory = new File(".");
		File[] filesList = directory.listFiles();

		for (File file : filesList) {

			if (file.isFile() && file.getName().endsWith(".dat"))
				tableFileNames.add(file.getName().substring(0, file.getName().length() - 4));

		}

		return tableFileNames;

	}

}
