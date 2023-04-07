package application;

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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import entities.Cell;
import entities.Edge;
import entities.OperatorCell;
import entities.TableCell;
import entities.Tree;
import enums.OperationType;
import gui.buttons.TypesButtons;
import gui.frames.ResultFrame;
import gui.frames.forms.create.FormFrameCreateTable;
import gui.frames.forms.importexport.FormFrameExportTable;
import gui.frames.forms.importexport.FormFrameImportAs;
import util.PrintOperator;

@SuppressWarnings("serial")
public class ActionClass extends JFrame implements ActionListener, MouseListener, KeyListener {

	private mxGraph graph;
	private mxGraphComponent graphComponent;
	private JPanel containerPanel;
	private mxCell parent;
	private String style;
	private String name;
	private mxCell jCell;
	private OperationType currentType;
	private AtomicReference<CurrentAction> currentActionRef;
	private Edge edge;

	private Map<mxCell, Cell> cells;
	private Map<Integer, Tree> trees;

	private TypesButtons btnTypeProjection;
	private TypesButtons btnTypeSelection;
	private TypesButtons btnTypeCartesianProduct;
	private TypesButtons btnTypeUnion;
	// private TypesButtons tipoDiferenca;
	// private TypesButtons btnTypeRename;
	// private TypesButtons btnTypeAggregation;
	private TypesButtons btnTypeJoin;
	private TypesButtons btnTypeLeftJoin;

	private JButton edgeButton;

	private JButton deleteButton;
	private JButton deleteAllButton;

	private JButton importButton;
	private JToolBar toolBar;

	private JButton saveTableButton;
	private JButton btnShowTable;

	private TableCell currentTableCell = null;
	private JButton btnCreateTable;

	private AtomicReference<File> lastDirectoryRef = new AtomicReference<>();

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

		btnTypeProjection = new TypesButtons(stylesheet, "π Projeção", "Projeção");
		btnTypeProjection.getButton().addActionListener(this);
		containerPanel.add(btnTypeProjection.getPanel());

		btnTypeSelection = new TypesButtons(stylesheet, "σ Seleção", "Seleção");
		btnTypeSelection.getButton().addActionListener(this);
		containerPanel.add(btnTypeSelection.getPanel());

		btnTypeJoin = new TypesButtons(stylesheet, "|X| Junção", "Junção");
		btnTypeJoin.getButton().addActionListener(this);
		containerPanel.add(btnTypeJoin.getPanel());

		btnTypeCartesianProduct = new TypesButtons(stylesheet, "✕ Produto Cartesiano", "Produto Cartesiano");
		btnTypeCartesianProduct.getButton().addActionListener(this);
		containerPanel.add(btnTypeCartesianProduct.getPanel());

		btnTypeUnion = new TypesButtons(stylesheet, "∪ União", "União");
		btnTypeUnion.getButton().addActionListener(this);
		containerPanel.add(btnTypeUnion.getPanel());
		/*
		 * btnTypeAggregation = new TypesButtons(stylesheet, "G Agregação",
		 * "Agregação"); btnTypeAggregation.getButton().addActionListener(this);
		 * containerPanel.add(btnTypeAggregation.getPanel());
		 * 
		 * /* tipoDiferenca = new TypesButtons(stylesheet,"- Diferenca","diferenca");
		 * tipoDiferenca.getButton().addActionListener(this);
		 * containerPanel.add(tipoDiferenca.getPanel());
		 * 
		 * btnTypeRename = new TypesButtons(stylesheet, "ρ Renomeação", "Renomeação");
		 * btnTypeRename.getButton().addActionListener(this);
		 * containerPanel.add(btnTypeRename.getPanel());
		 * 
		 */

		btnTypeLeftJoin = new TypesButtons(stylesheet, "⟕ Junção à esquerda", "Junção à esquerda");
		btnTypeLeftJoin.getButton().addActionListener(this);
		containerPanel.add(btnTypeLeftJoin.getPanel());

		toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.SOUTH);

		importButton = new JButton(" Importar tabela(i) ");
		toolBar.add(importButton);
		importButton.setHorizontalAlignment(SwingConstants.LEFT);
		importButton.addActionListener(this);

		btnCreateTable = new JButton(" Criar tabela(c) ");
		toolBar.add(btnCreateTable);
		btnCreateTable.setHorizontalAlignment(SwingConstants.LEFT);
		btnCreateTable.addActionListener(this);

		edgeButton = new JButton(" Edge(e) ");
		toolBar.add(edgeButton);
		edgeButton.setHorizontalAlignment(SwingConstants.LEFT);
		edgeButton.addActionListener(this);

		deleteButton = new JButton(" Remover(del) ");
		toolBar.add(deleteButton);
		deleteButton.setHorizontalAlignment(SwingConstants.LEFT);
		deleteButton.addActionListener(this);

		deleteAllButton = new JButton(" Remover tudo ");
		toolBar.add(deleteAllButton);
		deleteAllButton.setHorizontalAlignment(SwingConstants.LEFT);
		deleteAllButton.addActionListener(this);

		saveTableButton = new JButton(" Exportar(x) ");
		toolBar.add(saveTableButton);
		saveTableButton.setHorizontalAlignment(SwingConstants.LEFT);
		saveTableButton.addActionListener(this);

		btnShowTable = new JButton(" Mostrar(s) ");
		toolBar.add(btnShowTable);
		btnShowTable.setHorizontalAlignment(SwingConstants.LEFT);
		btnShowTable.addActionListener(this);

		this.add(containerPanel, BorderLayout.EAST);

		parent = (mxCell) graph.getDefaultParent();
		graph.getModel().beginUpdate();

		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.setUseBoundingBox(false);
		layout.execute(parent);

		this.cells = new HashMap<>();
		this.trees = new HashMap<>();

		currentActionRef = new AtomicReference<>();
		edge = new Edge();

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

		lastDirectoryRef.set(new File(""));

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

	private void assignVariables(String styleVar, String nameVar, OperationType currentType) {
		
		edge.reset();
		style = styleVar;
		name = nameVar;
		this.currentType = currentType;

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnTypeProjection.getButton()) {

			currentActionRef.set(new CurrentAction(CurrentAction.ActionType.OPERATORCELL));
			assignVariables("Projeção", "π  Projeção", OperationType.PROJECTION);

		} else if (e.getSource() == btnTypeSelection.getButton()) {

			currentActionRef.set(new CurrentAction(CurrentAction.ActionType.OPERATORCELL));
			assignVariables("Seleção", "σ  Seleção", OperationType.SELECTION);

		} else if (e.getSource() == btnTypeCartesianProduct.getButton()) {

			currentActionRef.set(new CurrentAction(CurrentAction.ActionType.OPERATORCELL));
			assignVariables("Produto Cartesiano", "✕  Produto Cartesiano", OperationType.CARTESIANPRODUCT);

		} else if (e.getSource() == btnTypeUnion.getButton()) {

			currentActionRef.set(new CurrentAction(CurrentAction.ActionType.OPERATORCELL));
			assignVariables("União", "∪  União", OperationType.UNION);

		} /*
			 * else if(e.getSource() == tipoDiferenca.getButton()) {
			 * 
			 * assignVariables("diferenca","-  diferenca", true, OperationType.DIFERENCA);
			 * 
			 * } else if (e.getSource() == btnTypeRename.getButton()) {
			 * 
			 * assignVariables("Renomeação", "ρ  Renomeação", true, OperationType.RENAME);
			 * 
			 * } else if (e.getSource() == btnTypeAggregation.getButton()) {
			 * 
			 * assignVariables("Agregação", "G Agregação", true, OperationType.AGGREGATION);
			 * 
			 * }
			 */ else if (e.getSource() == btnTypeLeftJoin.getButton()) {

			assignVariables("Junção à esquerda", "⟕ Junção à esquerda", OperationType.LEFTJOIN);

		} else if (e.getSource() == btnTypeJoin.getButton()) {

			currentActionRef.set(new CurrentAction(CurrentAction.ActionType.OPERATORCELL));
			assignVariables("Junção", "|X| Junção", OperationType.JOIN);

		} else if (e.getSource() == edgeButton) {

			currentActionRef.set(new CurrentAction(CurrentAction.ActionType.EDGE));

		} else if (e.getSource() == deleteButton) {

			deleteCell(jCell);

		} else if (e.getSource() == deleteAllButton) {

			deleteAllGraph();

		} else if (e.getSource() == importButton) {

			currentActionRef.set( new CurrentAction(CurrentAction.ActionType.TABLECELL));
			importFile();

		} else if (e.getSource() == btnCreateTable) {

			currentActionRef.set(new CurrentAction(CurrentAction.ActionType.TABLECELL));
			createTable();

		} else if (e.getSource() == saveTableButton) {

			exportTable();

		} else if (e.getSource() == btnShowTable) {

			showTable();

		}
		
		edge.reset();

	}

	public class CurrentAction {

		enum ActionType {
			EDGE, TABLECELL, OPERATORCELL
		}

		private ActionType action;

		public CurrentAction(ActionType action) {
			this.action = action;
		}

		public ActionType getType() {
			return action;
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		jCell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());

		AtomicReference<Boolean> exitRef = new AtomicReference<>(false);

		CellClickRenderer.clicked(currentActionRef, cells, jCell, edge, graph, parent, name, e,
				currentTableCell, style, currentType, exitRef);

		if (exitRef.get()) {

			Cell deletedCell = cells.get(jCell);

			if (deletedCell instanceof OperatorCell) {

				for (Cell parent : ((OperatorCell) deletedCell).getParents()) {
					parent.setChild(null);
				}

				((OperatorCell) deletedCell).clearParents();

			}
			cells.remove(jCell);
			deleteCell(jCell);

		}

	}

	private void deleteCell(mxCell jCell) {

		if (jCell != null) {

			Cell cell = cells.get(jCell);

			if (cell != null) {

				cell.setChild(null);

				if (cell instanceof OperatorCell) {

					for (Cell cellParent : ((OperatorCell) cell).getParents()) {

						cellParent.setChild(null);

					}

					((OperatorCell) cell).clearParents();

				}
			}

			graph.getModel().beginUpdate();
			try {

				graph.removeCells(new Object[] { jCell }, true);
				cells.remove(jCell);

			} finally {
				graph.getModel().endUpdate();
			}

			graph.refresh();

		}

	}

	private void deleteAllGraph() {

		graph.getModel().beginUpdate();
		try {
			graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
			cells.clear();

		} finally {
			graph.getModel().endUpdate();
		}

		graph.refresh();

	}

	private void importFile() {

		TableCell tableCell = new TableCell(80, 30);

		Boolean cancelService = false;
		AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(cancelService);

		List<String> tablesName = new ArrayList<>();
		cells.values().forEach(x -> tablesName.add(x.getName().toUpperCase()));

		new FormFrameImportAs(tableCell, tablesName, cancelServiceReference, lastDirectoryRef);

		if (!cancelServiceReference.get()) {

			assignVariables(tableCell.getStyle(), tableCell.getName(), null);
			currentTableCell = tableCell;

		} else {

			tableCell = null;

		}

	}

	private void createTable() {

		TableCell tableCell = new TableCell(80, 30);

		Boolean cancelService = false;
		AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(cancelService);

		new FormFrameCreateTable(tableCell, cancelServiceReference);

		if (!cancelServiceReference.get()) {

			assignVariables(tableCell.getStyle(), tableCell.getName(), null);
			currentTableCell = tableCell;

		} else {

			tableCell = null;

		}

	}

	private void exportTable() {

		Boolean exit = false;
		AtomicReference<Boolean> exitRef = new AtomicReference<>(exit);

		if (!cells.isEmpty())
			new FormFrameExportTable(cells, graphComponent, exitRef, lastDirectoryRef);

	}

	private void showTable() {

		Cell cell = jCell != null ? cells.get(jCell) : null;
		if (cell != null)
			new ResultFrame(cell);

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
				showTable();

		} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {

			if (jCell != null)
				deleteCell(jCell);

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

			if (jCell != null)
				PrintOperator.print(cells.get(jCell).getOperator());

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
