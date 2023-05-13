package controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import entities.Action.CreateOperationAction;
import entities.Action.CreateTableAction;
import entities.Action.CurrentAction;
import entities.Action.CurrentAction.ActionType;
import entities.Edge;
import entities.Tree;
import entities.buttons.Button;
import entities.buttons.OperationButton;
import entities.buttons.ToolBarButton;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import entities.utils.CellUtils;
import entities.utils.TreeUtils;
import enums.OperationType;
import gui.frames.forms.create.FormFrameCreateTable;
import gui.frames.forms.importexport.FormFrameExportTable;
import gui.frames.forms.importexport.FormFrameImportAs;
import gui.frames.forms.operations.binary.CartesianProduct;
import gui.frames.forms.operations.binary.FormFrameJoin;
import gui.frames.forms.operations.unary.FormFrameProjection;
import gui.frames.forms.operations.unary.FormFrameSelection;

@SuppressWarnings("serial")
public class ActionClass extends JFrame implements ActionListener, MouseListener, KeyListener, MouseMotionListener {

	private static mxGraph graph = new mxGraph();;
	private static mxGraphComponent graphComponent = new mxGraphComponent(graph);
	private static Map<mxCell, Cell> cells = new HashMap<>();
	private static Map<Integer, Tree> trees = new HashMap<>();
	private static File lastDirectory = new File("");

	private mxCell jCell;

	private mxCell ghostJCell = null;

	private JPanel containerPanel = new JPanel(new GridLayout(7, 1));;

	private AtomicReference<CurrentAction> currentActionRef = new AtomicReference<>();
	private AtomicReference<Edge> edgeRef = new AtomicReference<>(new Edge());

	private Set<Button> buttons = new HashSet<>();

	private JToolBar toolBar = new JToolBar();

	private JPopupMenu popupMenuJCell = new JPopupMenu();
	private JMenuItem menuItemShow = new JMenuItem("Mostrar");
	private JMenuItem menuItemEdit = new JMenuItem("Editar");
	private JMenuItem menuItemRemove = new JMenuItem("Remover");

	private JMenu menuItemOperations = new JMenu("Operações");
	private JMenuItem menuItemSelection = new JMenuItem(OperationType.SELECTION.getName());
	private JMenuItem menuItemProjection = new JMenuItem(OperationType.PROJECTION.getName());
	private JMenuItem menuItemJoin = new JMenuItem(OperationType.JOIN.getName());
	private JMenuItem menuItemLeftJoin = new JMenuItem(OperationType.LEFT_JOIN.getName());
	private JMenuItem menuItemRightJoin = new JMenuItem(OperationType.RIGHT_JOIN.getName());
	private JMenuItem menuItemCartesianProduct = new JMenuItem(OperationType.CARTESIAN_PRODUCT.getName());
	private JMenuItem menuItemUnion = new JMenuItem(OperationType.UNION.getName());

	private CreateOperationAction projectionOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.PROJECTION.getSymbol(),
			OperationType.PROJECTION.getName(), OperationType.PROJECTION);
	private CreateOperationAction selectionOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.SELECTION.getSymbol(),
			OperationType.SELECTION.getName(), OperationType.SELECTION);
	private CreateOperationAction joinOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.JOIN.getSymbol(), OperationType.JOIN.getName(),
			OperationType.JOIN);
	private CreateOperationAction leftJoinOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.LEFT_JOIN.getSymbol(),
			OperationType.LEFT_JOIN.getName(), OperationType.LEFT_JOIN);
	private CreateOperationAction rightJoinOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.RIGHT_JOIN.getSymbol(),
			OperationType.RIGHT_JOIN.getName(), OperationType.RIGHT_JOIN);
	private CreateOperationAction cartesianProductOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.CARTESIAN_PRODUCT.getSymbol(),
			OperationType.CARTESIAN_PRODUCT.getName(), OperationType.CARTESIAN_PRODUCT);
	private CreateOperationAction unionOperation = new CreateOperationAction(
			CurrentAction.ActionType.CREATE_OPERATOR_CELL, OperationType.UNION.getSymbol(),
			OperationType.UNION.getName(), OperationType.UNION);

	public ActionClass() {
		super("DBest: Database Basics for Engaging Students and Teachers");
		initGUI();
	}

	private void initGUI() {

		setSize(1000, 800);
		setLocationRelativeTo(null);

		graphComponent.setConnectable(false);

		graphComponent.setPreferredSize(new Dimension(400, 400));
		getContentPane().add(graphComponent, BorderLayout.CENTER);
		graphComponent.getGraphControl().addMouseMotionListener(this);

		mxStylesheet stylesheet = graph.getStylesheet();

		buttons.add(new OperationButton(stylesheet, OperationType.PROJECTION.getSymbol(),
				OperationType.PROJECTION.getName(), this, containerPanel, projectionOperation));
		buttons.add(new OperationButton(stylesheet, OperationType.SELECTION.getSymbol(),
				OperationType.SELECTION.getName(), this, containerPanel, selectionOperation));
		buttons.add(new OperationButton(stylesheet, OperationType.JOIN.getSymbol(), OperationType.JOIN.getName(), this,
				containerPanel, joinOperation));
		buttons.add(new OperationButton(stylesheet, OperationType.CARTESIAN_PRODUCT.getSymbol(),
				OperationType.CARTESIAN_PRODUCT.getName(), this, containerPanel, cartesianProductOperation));
		buttons.add(new OperationButton(stylesheet, OperationType.UNION.getSymbol(), OperationType.UNION.getName(),
				this, containerPanel, unionOperation));
		buttons.add(new OperationButton(stylesheet, OperationType.LEFT_JOIN.getSymbol(),
				OperationType.LEFT_JOIN.getName(), this, containerPanel, leftJoinOperation));
		buttons.add(new OperationButton(stylesheet, OperationType.RIGHT_JOIN.getSymbol(),
				OperationType.RIGHT_JOIN.getName(), this, containerPanel, rightJoinOperation));
		/*
		 * btnTypeAggregation = new TypesButtons(stylesheet, "G Agregação",
		 * "Agregação");
		 * 
		 * /* tipoDiferenca = new TypesButtons(stylesheet,"- Diferenca","diferenca");
		 * 
		 * btnTypeRename = new TypesButtons(stylesheet, "ρ Renomeação", "Renomeação");
		 *
		 */

		getContentPane().add(toolBar, BorderLayout.SOUTH);

		buttons.add(new ToolBarButton(" Importar tabela(i) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.IMPORT_FILE)));
		buttons.add(new ToolBarButton(" Criar tabela(c) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.CREATE_TABLE)));
		buttons.add(new ToolBarButton(" Edge(e) ", this, toolBar, new CurrentAction(CurrentAction.ActionType.EDGE)));
		buttons.add(new ToolBarButton(" Remover(del) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.DELETE_CELL)));
		buttons.add(new ToolBarButton(" Remover tudo ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.DELETE_ALL)));
		buttons.add(new ToolBarButton(" Exportar(x) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.SAVE_CELL)));
		buttons.add(new ToolBarButton(" Mostrar(s) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.SHOW_CELL)));
		buttons.add(new ToolBarButton(" Console ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.OPEN_CONSOLE)));

		this.add(containerPanel, BorderLayout.EAST);

		graph.getModel().beginUpdate();

		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.setUseBoundingBox(false);

		graphComponent.getGraphControl().addMouseListener(this);
		graphComponent.addKeyListener(this);

		graphComponent.setFocusable(true);

		graphComponent.requestFocus();

		graph.setAutoSizeCells(false);
		graph.setCellsResizable(false);
		graph.setAutoOrigin(false);
		graph.setCellsEditable(false);
		graph.setAllowDanglingEdges(false);

		graph.getModel().endUpdate();

		menuItemShow.addActionListener(this);
		menuItemEdit.addActionListener(this);
		menuItemRemove.addActionListener(this);
		menuItemSelection.addActionListener(this);
		menuItemProjection.addActionListener(this);
		menuItemJoin.addActionListener(this);
		menuItemLeftJoin.addActionListener(this);
		menuItemRightJoin.addActionListener(this);
		menuItemCartesianProduct.addActionListener(this);
		menuItemUnion.addActionListener(this);

		menuItemOperations.add(menuItemSelection);
		menuItemOperations.add(menuItemProjection);
		menuItemOperations.addSeparator();
		menuItemOperations.add(menuItemJoin);
		menuItemOperations.add(menuItemLeftJoin);
		menuItemOperations.add(menuItemRightJoin);
		menuItemOperations.add(menuItemCartesianProduct);
		menuItemOperations.add(menuItemUnion);

		graphComponent.setComponentPopupMenu(popupMenuJCell);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {

				File directory = new File(".");
				File[] filesList = directory.listFiles();
				for (File file : filesList) {
					if (file.isFile() && (file.getName().endsWith(".dat") || file.getName().endsWith(".head"))) {
						file.delete();
					}
				}

				System.exit(0);

			}

		});

		setVisible(true);

	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void actionPerformed(ActionEvent e) {

		ActionClass.getGraph().removeCells(new Object[] { ghostJCell }, true);
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

			ghostJCell = (mxCell) ActionClass.getGraph().insertVertex(
					(mxCell) ActionClass.getGraph().getDefaultParent(), "ghost", style,
					MouseInfo.getPointerInfo().getLocation().getX() - graphComponent.getWidth(),
					MouseInfo.getPointerInfo().getLocation().getY() - graphComponent.getHeight(), 80, 30, style);
		}
		edgeRef.get().reset();

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		jCell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());

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
			popupMenuJCell.show(graphComponent.getGraphControl(), e.getX(), e.getY());

		}

		ClickController.clicked(currentActionRef, jCell, edgeRef, e, ghostJCell);

		if (cells.get(jCell) != null && e.getClickCount() == 2) {

			CellUtils.showTable(jCell);

		}

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


			if(jCell != null && cells.get(jCell) != null) {
				
				System.out.println(((OperationCell)cells.get(jCell)).getParents());
				
			}
			
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		if (currentActionRef.get() != null
				&& currentActionRef.get().getType() == CurrentAction.ActionType.CREATE_OPERATOR_CELL
				&& ghostJCell != null) {

			mxGeometry geo = ghostJCell.getGeometry();
			double dx = e.getX() - geo.getCenterX();
			double dy = e.getY() - geo.getCenterY();
			graph.moveCells(new Object[] { ghostJCell }, dx, dy);

		}

	}

	public static mxGraph getGraph() {
		return graph;
	}

	public static mxGraphComponent getGraphComponent() {
		return graphComponent;
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

		if (graphComponent.getCellAt(x, y) != null) {
			putTableCell(x + 100, y, cell);
			return;
		}

		mxCell jCell = (mxCell) graph.insertVertex((mxCell) ActionClass.getGraph().getDefaultParent(), null,
				cell.getName(), x, y, 80, 30, cell.getStyle());

		cell.setJGraphCell(jCell);

		cells.put(jCell, cell);

	}

	public static mxCell putOperationCell(Integer x, Integer y, OperationCell cell, List<Cell> parents, String command,
			OperationType type) {

		x = (int) (Math.random() * 600);
		y = (int) (Math.random() * 600);

		mxCell jCell = (mxCell) graph.insertVertex((mxCell) ActionClass.getGraph().getDefaultParent(), null,
				cell.getName(), x, y, 80, 30, cell.getStyle());

		cell.setJGraphCell(jCell);

		cells.put(jCell, cell);

		for(Cell parent : parents) {
		
			cell.addParent(parent);
			parent.setChild(cell);
			
			graph.insertEdge(parent.getJGraphCell(), null, "", parent.getJGraphCell(), jCell);
		
		}

		cell.setAllNewTrees();
		
		switch (type) {

		case SELECTION ->{ 
			
			new FormFrameSelection().executeOperation(jCell, List.of(command.substring(command.indexOf("[") + 1, command.indexOf("]"))));
		
		}
		case AGGREGATION -> throw new UnsupportedOperationException("Unimplemented case: " + type);
		case CARTESIAN_PRODUCT -> {
			
			new CartesianProduct().executeOperation(jCell, null);
			
		}
		case DIFFERENCE -> throw new UnsupportedOperationException("Unimplemented case: " + type);
		case JOIN -> {
			
			new FormFrameJoin().executeOperation(jCell, List.of(command.substring(command.indexOf("[") + 1, command.indexOf("]")).split(",")));
			
		}
		case LEFT_JOIN -> throw new UnsupportedOperationException("Unimplemented case: " + type);
		case PROJECTION -> {
			
			new FormFrameProjection().executeOperation(jCell, List.of(command.substring(command.indexOf("[") + 1, command.indexOf("]")).split(",")));
			
		}
		case RENAME -> throw new UnsupportedOperationException("Unimplemented case: " + type);
		case RIGHT_JOIN -> throw new UnsupportedOperationException("Unimplemented case: " + type);
		case UNION -> throw new UnsupportedOperationException("Unimplemented case: " + type);
		default -> throw new IllegalArgumentException("Unexpected value: " + type);

		}
		
		return jCell;
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
