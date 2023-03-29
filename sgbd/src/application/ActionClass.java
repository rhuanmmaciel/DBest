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
import entities.OperatorCell;
import entities.TableCell;
import enums.OperationArity;
import enums.OperationType;
import gui.buttons.TypesButtons;
import gui.frames.ResultFrame;
import gui.frames.forms.create.FormFrameCreateTable;
import gui.frames.forms.importexport.FormFrameExportTable;
import gui.frames.forms.importexport.FormFrameImportAs;
import gui.frames.forms.operations.CartesianProduct;
import gui.frames.forms.operations.FormFrameAggregation;
import gui.frames.forms.operations.FormFrameJoin;
import gui.frames.forms.operations.FormFrameLeftJoin;
import gui.frames.forms.operations.FormFrameProjection;
import gui.frames.forms.operations.FormFrameRename;
import gui.frames.forms.operations.FormFrameSelection;
import gui.frames.forms.operations.FormFrameUnion;

@SuppressWarnings("serial")
public class ActionClass extends JFrame implements ActionListener, MouseListener, KeyListener {

	private mxGraph graph;
	private mxGraphComponent graphComponent;
	private JPanel containerPanel;
	private mxCell parent;
	private mxCell newCell;
	private Boolean createCell = false;
	private String style;
	private String name;
	private mxCell jCell;
	private Boolean isOperation;
	private OperationType currentType;
	private Map<mxCell, Cell> cells;

	private TypesButtons btnTypeProjection;
	private TypesButtons btnTypeSelection;
	private TypesButtons btnTypeCartesianProduct;
	private TypesButtons btnTypeUnion;
	// private TypesButtons tipoDiferenca;
	// private TypesButtons btnTypeRename;
	// private TypesButtons btnTypeAggregation;
	private TypesButtons btnTypeJoin;
	private TypesButtons btnTypeLeftJoin;

	private mxCell newParent;

	private JButton edgeButton;
	private Boolean createEdge = false;

	private JButton deleteButton;

	private JButton importButton;
	private JToolBar toolBar;

	private JButton saveTableButton;

	private TableCell currentTableCell = null;
	private JButton btnCreateTable;

	private AtomicReference<File> lastDirectoryRef = new AtomicReference<>();

	public ActionClass() {
		super("DBest");
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

		importButton = new JButton("Importar tabela");
		toolBar.add(importButton);
		importButton.setHorizontalAlignment(SwingConstants.LEFT);
		importButton.addActionListener(this);

		btnCreateTable = new JButton("Criar tabela");
		toolBar.add(btnCreateTable);
		btnCreateTable.setHorizontalAlignment(SwingConstants.LEFT);
		btnCreateTable.addActionListener(this);

		edgeButton = new JButton("Edge");
		toolBar.add(edgeButton);
		edgeButton.setHorizontalAlignment(SwingConstants.LEFT);
		edgeButton.addActionListener(this);

		deleteButton = new JButton("Delete Cell");
		toolBar.add(deleteButton);
		deleteButton.setHorizontalAlignment(SwingConstants.LEFT);
		deleteButton.addActionListener(this);

		saveTableButton = new JButton("Exportar");
		toolBar.add(saveTableButton);
		saveTableButton.setHorizontalAlignment(SwingConstants.LEFT);
		saveTableButton.addActionListener(this);

		this.add(containerPanel, BorderLayout.EAST);

		setVisible(true);

		parent = (mxCell) graph.getDefaultParent();
		graph.getModel().beginUpdate();

		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.setUseBoundingBox(false);
		layout.execute(parent);

		this.cells = new HashMap<>();

		graphComponent.getGraphControl().addMouseListener(this);
		graphComponent.addKeyListener(this);

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

			}

		});

	}

	private void assignVariables(String styleVar, String nameVar, boolean isOperation, OperationType currentType) {

		createCell = true;
		newCell = null;
		newParent = null;
		style = styleVar;
		name = nameVar;
		this.isOperation = isOperation;
		this.currentType = currentType;

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnTypeProjection.getButton()) {

			assignVariables("Projeção", "π  Projeção", true, OperationType.PROJECTION);

		} else if (e.getSource() == btnTypeSelection.getButton()) {

			assignVariables("Seleção", "σ  Seleção", true, OperationType.SELECTION);

		} else if (e.getSource() == btnTypeCartesianProduct.getButton()) {

			assignVariables("Produto Cartesiano", "✕  Produto Cartesiano", true, OperationType.CARTESIANPRODUCT);

		} else if (e.getSource() == btnTypeUnion.getButton()) {

			assignVariables("União", "∪  União", true, OperationType.UNION);

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

			assignVariables("Junção à esquerda", "⟕ Junção à esquerda", true, OperationType.LEFTJOIN);

		} else if (e.getSource() == btnTypeJoin.getButton()) {

			assignVariables("Junção", "|X| Junção", true, OperationType.JOIN);

		} else if (e.getSource() == edgeButton) {

			createEdge = true;

		} else if (e.getSource() == deleteButton) {

			deleteCell();

		} else if (e.getSource() == importButton) {

			TableCell tableCell = new TableCell(80, 30);

			Boolean cancelService = false;
			AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(cancelService);

			List<String> tablesName = new ArrayList<>();
			cells.values().forEach(x -> tablesName.add(x.getName().toUpperCase()));

			new FormFrameImportAs(tableCell, tablesName, cancelServiceReference, lastDirectoryRef);

			if (!cancelServiceReference.get()) {

				assignVariables(tableCell.getStyle(), tableCell.getName(), false, null);
				currentTableCell = tableCell;

			} else {

				tableCell = null;

			}

		} else if (e.getSource() == btnCreateTable) {

			TableCell tableCell = new TableCell(80, 30);

			Boolean cancelService = false;
			AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(cancelService);

			new FormFrameCreateTable(tableCell, cancelServiceReference);

			if (!cancelServiceReference.get()) {

				assignVariables(tableCell.getStyle(), tableCell.getName(), false, null);
				currentTableCell = tableCell;

			} else {

				tableCell = null;

			}

		} else if (e.getSource() == saveTableButton) {

			Boolean cancelService = false;
			AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(cancelService);

			if (!cells.isEmpty())
				new FormFrameExportTable(cells, graphComponent, cancelServiceReference, lastDirectoryRef);

		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		if (currentTableCell == null) {
			return;
		}

		jCell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
		Cell cell = cells.get(jCell);

		if (createCell == true) {

			newCell = (mxCell) graph.insertVertex(parent, null, name, e.getX(), e.getY(), 80, 30, style);

			if (!isOperation) {

				currentTableCell.setJGraphCell(newCell);
				cells.put(newCell, currentTableCell);
				currentTableCell.setX(e.getX());
				currentTableCell.setY(e.getY());

			} else {

				cells.put(newCell, new OperatorCell(name, style, newCell, currentType, e.getX(), e.getY(), 80, 30));
			}

			createCell = false;

		}

		if (jCell != null) {

			graph.getModel().getValue(jCell);
			if (createEdge == true && newParent == null) {
				newParent = jCell;
			}

			Cell parentCell = newParent != null ? cells.get(newParent) : null;

			if (createEdge == true && jCell != newParent) {

				graph.insertEdge(newParent, null, "", newParent, jCell);

				cell.addParent(parentCell);

				if (parentCell != null)
					parentCell.setChild(cell);

				AtomicReference<Boolean> exitRef = new AtomicReference<>(false);

				if (cell instanceof OperatorCell) {

					if (((OperatorCell) cell).getType() == OperationType.PROJECTION
							&& cell.checkRules(OperationArity.UNARY) == true)

						new FormFrameProjection(jCell, cells, graph, exitRef);

					else if (((OperatorCell) cell).getType() == OperationType.SELECTION
							&& cell.checkRules(OperationArity.UNARY) == true)

						new FormFrameSelection(jCell, cells, graph, exitRef);

					else if (((OperatorCell) cell).getType() == OperationType.AGGREGATION &&
					// tem q ver se eh unario
							cell.checkRules(OperationArity.UNARY) == true)

						new FormFrameAggregation(jCell, cells, graph);

					else if (((OperatorCell) cell).getType() == OperationType.RENAME
							&& cell.checkRules(OperationArity.UNARY) == true)

						new FormFrameRename(jCell, cells, graph);

					else if (((OperatorCell) cell).getType() == OperationType.JOIN && cell.getParents().size() == 2
							&& cell.checkRules(OperationArity.BINARY) == true)

						new FormFrameJoin(jCell, cells, graph, exitRef);

					else if (((OperatorCell) cell).getType() == OperationType.CARTESIANPRODUCT
							&& cell.getParents().size() == 2 && cell.checkRules(OperationArity.BINARY) == true)

						new CartesianProduct(jCell, cells, graph);

					else if (((OperatorCell) cell).getType() == OperationType.UNION && cell.getParents().size() == 2
							&& cell.checkRules(OperationArity.BINARY) == true)

						new FormFrameUnion(jCell, cells, graph, exitRef);

					else if (((OperatorCell) cell).getType() == OperationType.LEFTJOIN && cell.getParents().size() == 2
							&& cell.checkRules(OperationArity.BINARY) == true)

						new FormFrameLeftJoin(jCell, cells, graph, exitRef);

				}

				if (exitRef.get()) {

					

				} else {

				}

				newParent = null;
				createEdge = false;

			}

		}

		if (jCell != null && e.getClickCount() == 2) {

			new ResultFrame(cells.get(jCell));

		}

	}
	
	public void deleteCell() {
		
		if (jCell != null) {

			graph.getModel().beginUpdate();
			try {

				graph.removeCells(new Object[] {jCell}, true);
				cells.remove(jCell);
				
			} finally {
				graph.getModel().endUpdate();
			}

			graph.refresh();

		}
		
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

		Cell cell = jCell != null ? cells.get(jCell) : null;

		if (cell != null) {

			if (e.getKeyCode() == KeyEvent.VK_S) {

				new ResultFrame(cell);

			} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {

				deleteCell();
				
			} else if (e.getKeyCode() == KeyEvent.VK_J) {

				graphComponent.getGraph().selectAll();

			}

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
