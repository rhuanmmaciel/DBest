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
import java.util.ArrayList;
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

import controller.CreateAction.CreateOperationAction;
import controller.CreateAction.CreateTableAction;
import controller.CreateAction.CurrentAction;
import controller.CreateAction.CurrentAction.ActionType;
import entities.Cell;
import entities.Edge;
import entities.OperationCell;
import entities.TableCell;
import entities.Tree;
import entities.buttons.Button;
import entities.buttons.OperationButton;
import entities.buttons.ToolBarButton;
import entities.util.CellUtils;
import entities.util.TreeUtils;
import enums.OperationType;
import gui.frames.forms.create.FormFrameCreateTable;
import gui.frames.forms.importexport.FormFrameExportTable;
import gui.frames.forms.importexport.FormFrameImportAs;

@SuppressWarnings("serial")
public class ActionClass extends JFrame implements ActionListener, MouseListener, KeyListener, MouseMotionListener {

	private final String selection = "Seleção", selectionSymbol = "σ Seleção", projection = "Projeção",
			projectionSymbol = "π  Projeção", join = "Junção", joinSymbol = "|X| Junção",
			leftJoin = "Junção à esquerda", leftJoinSymbol = "⟕ Junção à esquerda",
			rightJoin = "Junção à direita", rightJoinSymbol = "Junção à direita",
			cartesianProduct = "Produto Cartesiano", cartesianProductSymbol = "✕  Produto Cartesiano", union = "União",
			unionSymbol = "∪  União";

	private mxGraph graph;
	private static mxGraphComponent graphComponent;
	private mxCell jCell;

	private mxCell ghostJCell = null;

	private JPanel containerPanel;

	private AtomicReference<CurrentAction> currentActionRef = new AtomicReference<>();
	private Edge edge = new Edge();

	private static Map<mxCell, Cell> cells = new HashMap<>();
	private static Map<Integer, Tree> trees = new HashMap<>();
	private Set<Button> buttons = new HashSet<>();

	private AtomicReference<File> lastDirectoryRef = new AtomicReference<>(new File(""));

	private JToolBar toolBar;

	private JPopupMenu popupMenuJCell;
	private JMenuItem menuItemShow;
	private JMenuItem menuItemEdit;
	private JMenuItem menuItemRemove;

	private JMenu menuItemOperations;
	private JMenuItem menuItemSelection;
	private JMenuItem menuItemProjection;
	private JMenuItem menuItemJoin;
	private JMenuItem menuItemLeftJoin;
	private JMenuItem menuItemRightJoin;
	private JMenuItem menuItemCartesianProduct;
	private JMenuItem menuItemUnion;

	private CreateOperationAction projectionOperation = new CreateOperationAction(
			CurrentAction.ActionType.OPERATOR_CELL, projectionSymbol, projection, OperationType.PROJECTION);

	private CreateOperationAction selectionOperation = new CreateOperationAction(CurrentAction.ActionType.OPERATOR_CELL,
			selectionSymbol, selection, OperationType.SELECTION);
	private CreateOperationAction joinOperation = new CreateOperationAction(CurrentAction.ActionType.OPERATOR_CELL,
			joinSymbol, join, OperationType.JOIN);
	private CreateOperationAction leftJoinOperation = new CreateOperationAction(CurrentAction.ActionType.OPERATOR_CELL,
			leftJoinSymbol, leftJoin, OperationType.LEFT_JOIN);
	private CreateOperationAction rightJoinOperation = new CreateOperationAction(CurrentAction.ActionType.OPERATOR_CELL,
			rightJoinSymbol, rightJoin, OperationType.RIGHT_JOIN);
	private CreateOperationAction cartesianProductOperation = new CreateOperationAction(
			CurrentAction.ActionType.OPERATOR_CELL, cartesianProductSymbol, cartesianProduct,
			OperationType.CARTESIAN_PRODUCT);
	private CreateOperationAction unionOperation = new CreateOperationAction(CurrentAction.ActionType.OPERATOR_CELL,
			unionSymbol, union, OperationType.UNION);

	public ActionClass() {
		super("DBest: Database Basics for Engaging Students and Teachers");
		initGUI();
	}

	private void initGUI() {

		setSize(1000, 800);
		setLocationRelativeTo(null);

		graph = new mxGraph();
		graphComponent = new mxGraphComponent(graph);
		graphComponent.setConnectable(false);

		graphComponent.setPreferredSize(new Dimension(400, 400));
		getContentPane().add(graphComponent);
		graphComponent.getGraphControl().addMouseMotionListener(this);

		containerPanel = new JPanel(new GridLayout(7, 1));
		mxStylesheet stylesheet = graph.getStylesheet();

		buttons.add(new OperationButton(stylesheet, projectionSymbol, projection, this, containerPanel,
				projectionOperation));
		buttons.add(
				new OperationButton(stylesheet, selectionSymbol, selection, this, containerPanel, selectionOperation));
		buttons.add(new OperationButton(stylesheet, joinSymbol, join, this, containerPanel, joinOperation));
		buttons.add(new OperationButton(stylesheet, cartesianProductSymbol, cartesianProduct, this, containerPanel,
				cartesianProductOperation));
		buttons.add(new OperationButton(stylesheet, unionSymbol, union, this, containerPanel, unionOperation));
		buttons.add(new OperationButton(stylesheet, leftJoinSymbol, leftJoin, this, containerPanel, leftJoinOperation));
		buttons.add(new OperationButton(stylesheet, rightJoinSymbol, rightJoin, this, containerPanel, rightJoinOperation));
		/*
		 * btnTypeAggregation = new TypesButtons(stylesheet, "G Agregação",
		 * "Agregação");
		 * 
		 * /* tipoDiferenca = new TypesButtons(stylesheet,"- Diferenca","diferenca");
		 * 
		 * btnTypeRename = new TypesButtons(stylesheet, "ρ Renomeação", "Renomeação");
		 *
		 */

		toolBar = new JToolBar();
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

		popupMenuJCell = new JPopupMenu();

		menuItemShow = new JMenuItem("Mostrar");
		menuItemShow.addActionListener(this);

		menuItemEdit = new JMenuItem("Editar");
		menuItemEdit.addActionListener(this);

		menuItemRemove = new JMenuItem("Remover");
		menuItemRemove.addActionListener(this);

		menuItemOperations = new JMenu("Operações");
		menuItemSelection = new JMenuItem(selection);
		menuItemSelection.addActionListener(this);
		menuItemProjection = new JMenuItem(projection);
		menuItemProjection.addActionListener(this);
		menuItemJoin = new JMenuItem(join);
		menuItemJoin.addActionListener(this);
		menuItemLeftJoin = new JMenuItem(leftJoin);
		menuItemLeftJoin.addActionListener(this);
		menuItemRightJoin = new JMenuItem(rightJoin);
		menuItemRightJoin.addActionListener(this);		
		menuItemCartesianProduct = new JMenuItem(cartesianProduct);
		menuItemCartesianProduct.addActionListener(this);
		menuItemUnion = new JMenuItem(union);
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
					if (file.isFile() && file.getName().endsWith(".dat")) {
						file.delete();
					}
				}

				System.exit(0);

			}

		});

		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		ActionClass.getGraph().removeCells(new Object[] { ghostJCell }, true);
		ghostJCell = null;
		
		Button btnClicked = buttons.stream().filter(x -> x.getButton() == e.getSource()).findAny().orElse(null);
		String style = null;
		
		if (btnClicked != null) {
			
			edge.reset();
			btnClicked.setCurrentAction(currentActionRef);

			switch (currentActionRef.get().getType()) {

			case DELETE_CELL:
				CellUtils.deleteCell(jCell);
				break;
			case DELETE_ALL:
				CellUtils.deleteAllGraph();
				break;
			case SAVE_CELL:
				exportTable();
				break;
			case SHOW_CELL:
				CellUtils.showTable(jCell);
				break;
			case IMPORT_FILE:
				importFile();
				break;
			case CREATE_TABLE:
				createTable();
			default:
				break;

			}

			if (btnClicked instanceof OperationButton)
				style = ((OperationButton) btnClicked).getStyle();

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
			style = selection;

		} else if (e.getSource() == menuItemProjection) {

			opAction = projectionOperation;
			style = projection;

		} else if (e.getSource() == menuItemJoin) {

			opAction = joinOperation;
			style = join;

		} else if (e.getSource() == menuItemLeftJoin) {

			opAction = leftJoinOperation;
			style = leftJoin;

		} else if (e.getSource() == menuItemRightJoin) {

			opAction = rightJoinOperation;
			style = rightJoin;

		} else if (e.getSource() == menuItemCartesianProduct) {

			opAction = cartesianProductOperation;
			style = cartesianProduct;

		} else if (e.getSource() == menuItemUnion) {

			opAction = unionOperation;
			style = union;

		}

		if (opAction != null) {

			opAction.setParent(jCell);
			currentActionRef.set(opAction);

		}

		if (currentActionRef.get() != null && (opAction != null
				|| (btnClicked != null && currentActionRef.get().getType() == ActionType.OPERATOR_CELL)))
			ghostJCell = (mxCell) ActionClass.getGraph().insertVertex(
					(mxCell) ActionClass.getGraph().getDefaultParent(), "ghost", style,
					MouseInfo.getPointerInfo().getLocation().getX() - graphComponent.getWidth(),
					MouseInfo.getPointerInfo().getLocation().getY() - graphComponent.getHeight(), 80, 30, style);

		edge.reset();

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

			if (cell instanceof OperationCell && !((OperationCell) cell).hasForm()) {

				popupMenuJCell.remove(menuItemShow);
				popupMenuJCell.remove(menuItemOperations);
				popupMenuJCell.remove(menuItemEdit);

			}
			if (cell instanceof TableCell
					|| ((OperationCell) cell).getType() == OperationType.CARTESIAN_PRODUCT) {

				popupMenuJCell.remove(menuItemEdit);

			}
			if(cell.hasChild()) {
				
				popupMenuJCell.remove(menuItemOperations);
				
			}
			if(cell.hasError()){
				
				popupMenuJCell.remove(menuItemShow);
				popupMenuJCell.remove(menuItemOperations);
				
				if(!cell.hasParents())
					popupMenuJCell.remove(menuItemEdit);
				
			}
			popupMenuJCell.show(graphComponent.getGraphControl(), e.getX(), e.getY());

		}

		ClickController.clicked(currentActionRef, jCell, edge, e, ghostJCell);

		if (cells.get(jCell) != null && e.getClickCount() == 2) {

			CellUtils.showTable(jCell);

		}

	}

	private void importFile() {

		TableCell tableCell = new TableCell(80, 30);

		Boolean cancelService = false;
		AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(cancelService);

		List<String> tablesName = new ArrayList<>();
		cells.values().forEach(x -> tablesName.add(x.getName().toUpperCase()));

		new FormFrameImportAs(tableCell, tablesName, cancelServiceReference, lastDirectoryRef);

		if (!cancelServiceReference.get()) {

			currentActionRef.set(new CreateTableAction(CurrentAction.ActionType.IMPORT_FILE, tableCell.getName(),
					tableCell.getStyle(), tableCell));

		} else {
			
			TreeUtils.deleteTree(tableCell.getTree());
			currentActionRef.set(null);

		}

	}

	private void createTable() {

		TableCell tableCell = new TableCell(80, 30);

		AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(false);

		new FormFrameCreateTable(tableCell, cancelServiceReference);

		if (!cancelServiceReference.get()) {

			currentActionRef.set(new CreateTableAction(CurrentAction.ActionType.CREATE_TABLE, tableCell.getName(),
					tableCell.getStyle(), tableCell));

		} else {
			
			TreeUtils.deleteTree(tableCell.getTree());
			currentActionRef.set(null);

		}

	}

	private void exportTable() {

		Boolean exit = false;
		AtomicReference<Boolean> exitRef = new AtomicReference<>(exit);

		if (!cells.isEmpty())
			new FormFrameExportTable(exitRef, lastDirectoryRef);
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

			importFile();

		} else if (e.getKeyCode() == KeyEvent.VK_X) {

			if (cells.size() > 0)
				exportTable();

		} else if (e.getKeyCode() == KeyEvent.VK_C) {

			createTable();

		} else if (e.getKeyCode() == KeyEvent.VK_L) {

			System.out.println("--------------------------");
			System.out.println("Árvores: ");
			for (Integer i : trees.keySet()) {

				System.out.println(trees.get(i));

			}
			System.out.println();
			System.out.println();

		} else if (e.getKeyCode() == KeyEvent.VK_A) {
	
			System.out.println();
		
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public static mxGraph getGraph() {
		return graphComponent.getGraph();
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

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

		if (currentActionRef.get() != null && currentActionRef.get().getType() == CurrentAction.ActionType.OPERATOR_CELL
				&& ghostJCell != null) {

			mxGeometry geo = ghostJCell.getGeometry();
			double dx = e.getX() - geo.getCenterX();
			double dy = e.getY() - geo.getCenterY();
			graph.moveCells(new Object[] { ghostJCell }, dx, dy);

		}

	}

}
