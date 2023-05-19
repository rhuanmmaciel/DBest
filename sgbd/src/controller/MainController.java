package controller;

import java.awt.Container;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

import entities.Action.CreateOperationAction;
import entities.Action.CreateTableAction;
import entities.Action.CurrentAction;
import entities.Action.CurrentAction.ActionType;
import entities.Edge;
import entities.Tree;
import entities.buttons.Button;
import entities.buttons.OperationButton;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import entities.utils.CellUtils;
import entities.utils.TreeUtils;
import enums.OperationType;
import gui.frames.dsl.Console;
import gui.frames.dsl.TextEditor;
import gui.frames.forms.create.FormFrameCreateTable;
import gui.frames.forms.importexport.FormFrameExportTable;
import gui.frames.forms.importexport.FormFrameImportAs;
import gui.frames.forms.operations.binary.CartesianProduct;
import gui.frames.forms.operations.binary.FormFrameJoin;
import gui.frames.forms.operations.unary.FormFrameProjection;
import gui.frames.forms.operations.unary.FormFrameSelection;
import gui.frames.main.MainFrame;

@SuppressWarnings("serial")
public class MainController extends MainFrame {

	private static Map<mxCell, Cell> cells = new HashMap<>();
	private static Map<Integer, Tree> trees = new HashMap<>();
	private static File lastDirectory = new File("");

	private Container textEditor = new TextEditor(this).getContentPane();

	private mxCell jCell;
	private mxCell ghostJCell = null;

	private AtomicReference<CurrentAction> currentActionRef = new AtomicReference<>();
	private AtomicReference<Edge> edgeRef = new AtomicReference<>(new Edge());

	private static Set<Button> buttons = new HashSet<>();

	public static final CreateOperationAction projectionOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.PROJECTION.getSymbol(),
			OperationType.PROJECTION.getName(), OperationType.PROJECTION);
	public static final CreateOperationAction selectionOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.SELECTION.getSymbol(),
			OperationType.SELECTION.getName(), OperationType.SELECTION);
	public static final CreateOperationAction joinOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.JOIN.getSymbol(), OperationType.JOIN.getName(),
			OperationType.JOIN);
	public static final CreateOperationAction leftJoinOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.LEFT_JOIN.getSymbol(),
			OperationType.LEFT_JOIN.getName(), OperationType.LEFT_JOIN);
	public static final CreateOperationAction rightJoinOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.RIGHT_JOIN.getSymbol(),
			OperationType.RIGHT_JOIN.getName(), OperationType.RIGHT_JOIN);
	public static final CreateOperationAction cartesianProductOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.CARTESIAN_PRODUCT.getSymbol(),
			OperationType.CARTESIAN_PRODUCT.getName(), OperationType.CARTESIAN_PRODUCT);
	public static final CreateOperationAction unionOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.UNION.getSymbol(),
			OperationType.UNION.getName(), OperationType.UNION);

	public MainController() {

		super(buttons);

	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void actionPerformed(ActionEvent e) {

		graph.removeCells(new Object[] { ghostJCell }, true);
		ghostJCell = null;

		Button btnClicked = buttons.stream().filter(x -> x.getButton() == e.getSource()).findAny().orElse(null);
		String style = null;

		if (btnClicked != null) {

			edgeRef.get().reset();
			btnClicked.setCurrentAction(currentActionRef);

			switch (currentActionRef.get().getType()) {

			case DELETE_CELL -> CellUtils.deleteCell(jCell);
			case DELETE_ALL -> CellUtils.deleteAllGraph();
			case SAVE_CELL -> exportTable();
			case SHOW_CELL -> CellUtils.showTable(jCell);
			case IMPORT_FILE -> newTable(CurrentAction.ActionType.IMPORT_FILE);
			case CREATE_TABLE -> newTable(CurrentAction.ActionType.CREATE_TABLE);
			case OPEN_CONSOLE -> new Console();
			case OPEN_TEXT_EDITOR -> changeScreen();

			}

			if (btnClicked instanceof OperationButton btnOpClicked) {

				style = btnOpClicked.getStyle();
				((CreateOperationAction) currentActionRef.get()).setParent(null);

			}

		}

		CreateOperationAction opAction = null;

		if (e.getSource() == menuItemShow) {

			CellUtils.showTable(jCell);

		} else if (e.getSource() == menuItemEdit) {

			((OperationCell) cells.get(jCell)).editOperation(jCell);
			TreeUtils.recalculateContent(cells.get(jCell));

		} else if (e.getSource() == menuItemRemove) {

			CellUtils.deleteCell(jCell);

		} else if (e.getSource() == menuItemSelection) {

			opAction = selectionOperation;
			style = OperationType.SELECTION.getName();

		} else if (e.getSource() == menuItemProjection) {

			opAction = projectionOperation;
			style = OperationType.PROJECTION.getName();

		} else if (e.getSource() == menuItemJoin) {

			opAction = joinOperation;
			style = OperationType.JOIN.getName();

		} else if (e.getSource() == menuItemLeftJoin) {

			opAction = leftJoinOperation;
			style = OperationType.LEFT_JOIN.getName();

		} else if (e.getSource() == menuItemRightJoin) {

			opAction = rightJoinOperation;
			style = OperationType.RIGHT_JOIN.getName();

		} else if (e.getSource() == menuItemCartesianProduct) {

			opAction = cartesianProductOperation;
			style = OperationType.CARTESIAN_PRODUCT.getName();

		} else if (e.getSource() == menuItemUnion) {

			opAction = unionOperation;
			style = OperationType.UNION.getName();

		}

		if (opAction != null) {

			opAction.setParent(jCell);
			currentActionRef.set(opAction);

		}

		if (currentActionRef.get() != null && (opAction != null
				|| (btnClicked != null && currentActionRef.get().getType() == ActionType.CREATE_OPERATOR_CELL))) {

			ghostJCell = (mxCell) graph.insertVertex((mxCell) graph.getDefaultParent(), "ghost", style,
					MouseInfo.getPointerInfo().getLocation().getX() - MainFrame.getGraphComponent().getWidth(),
					MouseInfo.getPointerInfo().getLocation().getY() - MainFrame.getGraphComponent().getHeight(), 80, 30,
					style);
		}
		edgeRef.get().reset();

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		jCell = (mxCell) MainFrame.getGraphComponent().getCellAt(e.getX(), e.getY());

		if (e.getButton() == MouseEvent.BUTTON3 && cells.get(jCell) != null) {

			Cell cell = cells.get(jCell);

			popupMenuJCell.add(menuItemShow);
			popupMenuJCell.add(menuItemEdit);
			popupMenuJCell.add(menuItemOperations);
			popupMenuJCell.add(menuItemRemove);

			if (cell instanceof OperationCell opCell && !opCell.hasForm()) {

				popupMenuJCell.remove(menuItemShow);
				popupMenuJCell.remove(menuItemOperations);
				popupMenuJCell.remove(menuItemEdit);

			}
			if (cell instanceof TableCell || ((OperationCell) cell).getType() == OperationType.CARTESIAN_PRODUCT) {

				popupMenuJCell.remove(menuItemEdit);

			}
			if (cell.hasChild()) {

				popupMenuJCell.remove(menuItemOperations);

			}
			if (cell.hasError()) {

				popupMenuJCell.remove(menuItemShow);
				popupMenuJCell.remove(menuItemOperations);

				if (!cell.hasParents())
					popupMenuJCell.remove(menuItemEdit);

			}
			popupMenuJCell.show(MainFrame.getGraphComponent().getGraphControl(), e.getX(), e.getY());

		}

		ClickController.clicked(currentActionRef, jCell, edgeRef, e, ghostJCell);

		if (cells.get(jCell) != null && e.getClickCount() == 2) {

			CellUtils.showTable(jCell);

		}

	}

	private void changeScreen() {

		setContentPane(textEditor);
		revalidate();

	}

	private void newTable(CurrentAction.ActionType action) {

		AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(false);

		TableCell tableCell = action == CurrentAction.ActionType.CREATE_TABLE
				? new FormFrameCreateTable(cancelServiceReference).getResult()
				: new FormFrameImportAs(cancelServiceReference).getResult();

		if (!cancelServiceReference.get()) {

			currentActionRef.set(new CreateTableAction(action, tableCell.getName(), tableCell.getStyle(), tableCell));

		} else {

			if (tableCell != null)
				TreeUtils.deleteTree(tableCell.getTree());
			currentActionRef.set(null);

		}

	}

	private void exportTable() {

		AtomicReference<Boolean> exitRef = new AtomicReference<>(false);

		if (!cells.isEmpty())
			new FormFrameExportTable(exitRef);
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_S) {

			if (jCell != null)
				CellUtils.showTable(jCell);

		} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {

			if (jCell != null) {

				CellUtils.deleteCell(jCell);
				currentActionRef.set(null);

			}

		} else if (e.getKeyCode() == KeyEvent.VK_E) {

			currentActionRef.set(new CurrentAction(CurrentAction.ActionType.EDGE));

		} else if (e.getKeyCode() == KeyEvent.VK_I) {

			newTable(CurrentAction.ActionType.IMPORT_FILE);

		} else if (e.getKeyCode() == KeyEvent.VK_X) {

			if (cells.size() > 0)
				exportTable();

		} else if (e.getKeyCode() == KeyEvent.VK_C) {

			newTable(CurrentAction.ActionType.CREATE_TABLE);

		} else if (e.getKeyCode() == KeyEvent.VK_L) {

			System.out.println("--------------------------");
			System.out.println("Árvores: ");
			for (Integer i : trees.keySet()) {

				System.out.println(trees.get(i));

			}
			System.out.println();
			System.out.println();

		} else if (e.getKeyCode() == KeyEvent.VK_A) {

			if (jCell != null && cells.get(jCell) != null) {

				System.out.println(((OperationCell) cells.get(jCell)).getParents());

			}

		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		if (currentActionRef != null && currentActionRef.get() != null
				&& currentActionRef.get().getType() == CurrentAction.ActionType.CREATE_OPERATOR_CELL
				&& ghostJCell != null) {

			mxGeometry geo = ghostJCell.getGeometry();
			double dx = e.getX() - geo.getCenterX();
			double dy = e.getY() - geo.getCenterY();
			MainFrame.getGraph().moveCells(new Object[] { ghostJCell }, dx, dy);

		}

	}

	public static Map<mxCell, Cell> getCells() {
		return cells;
	}

	public static Map<Integer, Tree> getTrees() {
		return trees;
	}

	public static File getLastDirectory() {
		return lastDirectory;
	}

	public static void setLastDirectory(File newLastDirectory) {
		lastDirectory = newLastDirectory;
	}

	public static void putTableCell(Integer x, Integer y, TableCell cell) {

		if (MainFrame.getGraphComponent().getCellAt(x, y) != null) {
			putTableCell(x + 100, y, cell);
			return;
		}

		mxCell jCell = (mxCell) MainFrame.getGraph().insertVertex((mxCell) graph.getDefaultParent(), null,
				cell.getName(), x, y, 80, 30, cell.getStyle());

		cell.setJGraphCell(jCell);

		cells.put(jCell, cell);

	}

	public static mxCell putOperationCell(Integer x, Integer y, OperationCell cell, List<Cell> parents, String command,
			OperationType type) {

		x = (int) (Math.random() * 600);
		y = (int) (Math.random() * 600);

		mxCell jCell = (mxCell) MainFrame.getGraph().insertVertex((mxCell) graph.getDefaultParent(), null,
				cell.getName(), x, y, 80, 30, cell.getStyle());

		cell.setJGraphCell(jCell);

		cells.put(jCell, cell);

		for (Cell parent : parents) {

			cell.addParent(parent);
			parent.setChild(cell);

			MainFrame.getGraph().insertEdge(parent.getJGraphCell(), null, "", parent.getJGraphCell(), jCell);

		}

		cell.setAllNewTrees();

		switch (type) {

		case SELECTION ->

			new FormFrameSelection().executeOperation(jCell,
					List.of(command.substring(command.indexOf("[") + 1, command.indexOf("]"))));

		case AGGREGATION -> throw new UnsupportedOperationException("Unimplemented case: " + type);
		case CARTESIAN_PRODUCT ->

			new CartesianProduct().executeOperation(jCell, null);

		case DIFFERENCE -> throw new UnsupportedOperationException("Unimplemented case: " + type);
		case JOIN ->

			new FormFrameJoin().executeOperation(jCell,
					List.of(command.substring(command.indexOf("[") + 1, command.indexOf("]")).split(",")));

		case LEFT_JOIN -> throw new UnsupportedOperationException("Unimplemented case: " + type);
		case PROJECTION -> new FormFrameProjection().executeOperation(jCell,
				List.of(command.substring(command.indexOf("[") + 1, command.indexOf("]")).split(",")));

		case RENAME -> throw new UnsupportedOperationException("Unimplemented case: " + type);
		case RIGHT_JOIN -> throw new UnsupportedOperationException("Unimplemented case: " + type);
		case UNION -> throw new UnsupportedOperationException("Unimplemented case: " + type);
		default -> throw new IllegalArgumentException("Unexpected value: " + type);

		}

		return jCell;

	}

}
