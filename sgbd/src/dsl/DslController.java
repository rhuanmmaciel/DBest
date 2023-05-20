package dsl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mxgraph.model.mxCell;

import controller.MainController;
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
import util.ImportFile;
import util.Utils;

public class DslController {

	private static List<String> commands = new LinkedList<>();
	private static List<String> tables = new ArrayList<>();

	public static void addCommand(String command) {

		commands.add(command);

	}

	public static void addTable(String tableName) {

		tables.add(tableName);

	}

	public static void reset() {

		commands.clear();
		tables.clear();

	}

	public static void parser() {
		
		execute();

		reset();

	}

	private static void execute() {

		if (tables.isEmpty() || commands.isEmpty())
			return;

		boolean everyTableExist = true;

		List<String> tableFileNames = getTableFileNames();

		for (String tableName : tables)
			if (!tableFileNames.contains(tableName)) {
			
				everyTableExist = false;
				DslErrorListener.addErrors("Table '" + tableName + "' doesn't exist");
			
			}
		if (!everyTableExist)
			return;

		createTree();

	}

	private static void createTree() {

		Map<String, mxCell> operationsReady = new HashMap<>();

		for (String command : commands) {

			Map<String, String> elements = recognizer(command);

			solveExpression(elements, operationsReady);

		}

	}

	private static void solveExpression(Map<String, String> elements, Map<String, mxCell> operationsReady) {

		if (MainController.unaryOperationsName().contains(elements.get("operation"))) {

			if (elements.get("source").contains("("))
				solveExpression(recognizer(elements.get("source")), operationsReady);

			else {

				String tables[] = elements.get("source").split(",");

				for (String table : tables) {

					createTable(table);

				}

			}

		} else {

			List<String> tables = new ArrayList<>();

			if (elements.get("source1").contains("("))
				solveExpression(recognizer(elements.get("source1")), operationsReady);
			else
				tables.addAll(List.of(elements.get("source1").split(",")));

			if (elements.get("source2").contains("("))
				solveExpression(recognizer(elements.get("source2")), operationsReady);

			else
				tables.addAll(List.of(elements.get("source2").split(",")));

			for (String table : tables)
				createTable(table);

		}

		boolean replace = false;
		int replaced = 0;

		OperationCell operationCell = null;

		OperationType type = null;

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

			type = OperationType.SELECTION;
			arity = OperationArity.UNARY;

		}
		case "projection" -> {

			operationCell = new OperationCell(OperationType.PROJECTION.getDisplayNameAndSymbol(),
					OperationType.PROJECTION.getDisplayName(), null, OperationType.PROJECTION, null, 80, 30);

			operationCell.setForm(FormFrameProjection.class);

			type = OperationType.PROJECTION;
			arity = OperationArity.UNARY;

		}
		case "cartesianproduct" -> {

			operationCell = new OperationCell(OperationType.CARTESIAN_PRODUCT.getDisplayNameAndSymbol(),
					OperationType.CARTESIAN_PRODUCT.getDisplayName(), null, OperationType.CARTESIAN_PRODUCT, null, 80,
					30);

			operationCell.setForm(CartesianProduct.class);

			type = OperationType.CARTESIAN_PRODUCT;
			arity = OperationArity.BINARY;

		}
		case "join" -> {

			operationCell = new OperationCell(OperationType.JOIN.getDisplayNameAndSymbol(),
					OperationType.JOIN.getDisplayName(), null, OperationType.JOIN, null, 80, 30);

			operationCell.setForm(FormFrameJoin.class);

			type = OperationType.JOIN;
			arity = OperationArity.BINARY;

		}
		case "leftjoin" -> {

			operationCell = new OperationCell(OperationType.LEFT_JOIN.getDisplayNameAndSymbol(),
					OperationType.LEFT_JOIN.getDisplayName(), null, OperationType.LEFT_JOIN, null, 80, 30);

			operationCell.setForm(FormFrameLeftJoin.class);

			type = OperationType.LEFT_JOIN;
			arity = OperationArity.BINARY;

		}
		case "rightjoin" -> {

			operationCell = new OperationCell(OperationType.RIGHT_JOIN.getDisplayNameAndSymbol(),
					OperationType.RIGHT_JOIN.getDisplayName(), null, OperationType.RIGHT_JOIN, null, 80, 30);

			operationCell.setForm(FormFrameRightJoin.class);

			type = OperationType.RIGHT_JOIN;
			arity = OperationArity.BINARY;

		}
		case "union" -> {

			operationCell = new OperationCell(OperationType.UNION.getDisplayNameAndSymbol(),
					OperationType.UNION.getDisplayName(), null, OperationType.UNION, null, 80, 30);

			operationCell.setForm(FormFrameUnion.class);

			type = OperationType.UNION;
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
			throw new ParentNullException("parent is null");

		mxCell jCell = MainController.putOperationCell(50, 100, operationCell, parents, elements.get("command"), type);

		operationsReady.put(elements.get("command"), jCell);

	}

	private static void createTable(String tableName) {

		TableCell table = new ImportFile(tableName + ".head").getResult();

		MainController.putTableCell(50, 100, table);

	}

	private static Map<String, String> recognizer(String input) {

		int endIndex = input.indexOf('(');

		if (input.contains("[")) {

			endIndex = Math.min(input.indexOf('['), endIndex);

		}

		if (MainController.unaryOperationsName().contains(input.substring(0, endIndex).toLowerCase()))
			return unaryRecognizer(input);

		return binaryRecognizer(input);

	}

	private static Map<String, String> unaryRecognizer(String input) {

		Map<String, String> elements = new HashMap<>();

		int endIndex = input.indexOf('(');

		if (input.contains("[")) {

			endIndex = Math.min(input.indexOf('['), endIndex);
			elements.put("predicate", input.substring(input.indexOf("[") + 1, input.indexOf("]")));

		}

		elements.put("operation", input.substring(0, endIndex).toLowerCase());

		elements.put("source", input.substring(input.indexOf("(") + 1, input.lastIndexOf(")")));

		elements.put("command", input);

		return elements;

	}

	private static Map<String, String> binaryRecognizer(String input) {

		Map<String, String> elements = new HashMap<>();

		int endIndex = input.indexOf('(');

		String regex = "\\[[^\\[]*\\(";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		if (matcher.find()) {

			endIndex = Math.min(input.indexOf('['), endIndex);
			elements.put("predicate", input.substring(input.indexOf("[") + 1, input.indexOf("]")));

		}

		elements.put("operation", input.substring(0, endIndex).toLowerCase());

		int sourcePosition = input.indexOf("(") + 1;
		int commaPosition = findCommaPosition(input.substring(sourcePosition))
				+ input.substring(0, sourcePosition).length();

		elements.put("source1", input.substring(sourcePosition, commaPosition));
		elements.put("source2", input.substring(commaPosition + 1, input.lastIndexOf(")")));

		elements.put("command", input);

		return elements;

	}

	private static int findCommaPosition(String input) {

		int openingParenthesisAmount = 0;
		int openingBracetsAmount = 0;

		for (int i = 0; i < input.length(); i++) {

			if (input.charAt(i) == '(')

				openingParenthesisAmount++;

			else if (input.charAt(i) == ')')
				openingParenthesisAmount--;

			else if (input.charAt(i) == '[')
				openingBracetsAmount++;

			else if (input.charAt(i) == ']')
				openingBracetsAmount--;

			else if (input.charAt(i) == ',' && openingParenthesisAmount == 0 && openingBracetsAmount == 0)
				return i;

		}

		return -1;

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
