package controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import controller.CreateAction.CreateOperationAction;
import controller.CreateAction.CreateTableAction;
import controller.CreateAction.CurrentAction;
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
public class ActionClass extends JFrame implements ActionListener, MouseListener, KeyListener {

	private mxGraph graph;
	private static mxGraphComponent graphComponent;
	private mxCell jCell;

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

		containerPanel = new JPanel(new GridLayout(6, 1));
		mxStylesheet stylesheet = graph.getStylesheet();

		buttons.add(new OperationButton(stylesheet, "π Projeção", "Projeção", this, containerPanel,
				new CreateOperationAction(CurrentAction.ActionType.OPERATOR_CELL, "π  Projeção", "Projeção",
						OperationType.PROJECTION)));
		buttons.add(
				new OperationButton(stylesheet, "σ Seleção", "Seleção", this, containerPanel, new CreateOperationAction(
						CurrentAction.ActionType.OPERATOR_CELL, "σ  Seleção", "Seleção", OperationType.SELECTION)));
		buttons.add(
				new OperationButton(stylesheet, "|X| Junção", "Junção", this, containerPanel, new CreateOperationAction(
						CurrentAction.ActionType.OPERATOR_CELL, "|X| Junção", "Junção", OperationType.JOIN)));
		buttons.add(new OperationButton(stylesheet, "✕ Produto Cartesiano", "Produto Cartesiano", this, containerPanel,
				new CreateOperationAction(CurrentAction.ActionType.OPERATOR_CELL, "✕  Produto Cartesiano",
						"Produto Cartesiano", OperationType.CARTESIANPRODUCT)));
		buttons.add(new OperationButton(stylesheet, "∪ União", "União", this, containerPanel, new CreateOperationAction(
				CurrentAction.ActionType.OPERATOR_CELL, "∪  União", "União", OperationType.UNION)));
		buttons.add(new OperationButton(stylesheet, "⟕ Junção à esquerda", "Junção à esquerda", this, containerPanel,
				new CreateOperationAction(CurrentAction.ActionType.OPERATOR_CELL, "⟕ Junção à esquerda",
						"Junção à esquerda", OperationType.LEFTJOIN)));
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
		popupMenuJCell.add(menuItemShow);
		popupMenuJCell.add(menuItemEdit);
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

		Button btnClicked = buttons.stream().filter(x -> x.getButton() == e.getSource()).findAny().orElse(null);
		
		if(btnClicked != null) {
			
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
			
		}
		
		if(e.getSource() == menuItemShow) {
			
			CellUtils.showTable(jCell);
			
		}else if(e.getSource() == menuItemEdit) {
			
			AtomicReference<Boolean> exitRef = new AtomicReference<>(false);
			((OperationCell)cells.get(jCell)).editOperation(jCell, exitRef);
			
		}
		
		edge.reset();

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		jCell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());

		if (e.getButton() == MouseEvent.BUTTON3 && cells.get(jCell) != null) {
			
			popupMenuJCell.add(menuItemEdit);
			
			if(cells.get(jCell) instanceof TableCell || ((OperationCell)cells.get(jCell)).getType() == OperationType.CARTESIANPRODUCT) 
				popupMenuJCell.remove(menuItemEdit);
				
			popupMenuJCell.show(graphComponent.getGraphControl(), e.getX(), e.getY());	
			
		}

		AtomicReference<Boolean> exitRef = new AtomicReference<>(false);

		ClickController.clicked(currentActionRef, jCell, edge, e, exitRef);

		if (exitRef.get()) {

			Cell deletedCell = cells.get(jCell);

			if (deletedCell instanceof OperationCell) {

				for (Cell parent : ((OperationCell) deletedCell).getParents()) {
					parent.setChild(null);
				}

				((OperationCell) deletedCell).clearParents();

			}
			cells.remove(jCell);
			CellUtils.deleteCell(jCell);

		}

		TreeUtils.identifyTrees(trees);

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

			currentActionRef.set(null);

		}

		TreeUtils.identifyTrees(trees);

	}

	private void createTable() {

		TableCell tableCell = new TableCell(80, 30);

		AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(false);

		new FormFrameCreateTable(tableCell, cancelServiceReference);

		if (!cancelServiceReference.get()) {

			currentActionRef.set(new CreateTableAction(CurrentAction.ActionType.CREATE_TABLE, tableCell.getName(),
					tableCell.getStyle(), tableCell));

		} else {

			currentActionRef.set(null);

		}
		
		TreeUtils.identifyTrees(trees);

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

			if (jCell != null) 
				CellUtils.deleteCell(jCell);

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

		}else if(e.getKeyCode() == KeyEvent.VK_A) {
			
			
			
			
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
	
}
