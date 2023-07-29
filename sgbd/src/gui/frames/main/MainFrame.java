package gui.frames.main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import controller.ConstantController;
import entities.Action.CurrentAction;
import entities.buttons.Button;
import entities.buttons.OperationButton;
import entities.buttons.ToolBarButton;
import enums.OperationType;

public abstract class MainFrame extends JFrame
		implements ActionListener, MouseListener, KeyListener, MouseMotionListener {

	private static Container mainContainer;

	protected static mxGraph graph = new mxGraph();
	protected static mxGraphComponent graphComponent = new mxGraphComponent(graph);
	
	protected JPanel operationButtonsPane = new JPanel();
	protected static mxGraph tablesGraph = new mxGraph();
	protected mxGraphComponent tablesComponent = new mxGraphComponent(tablesGraph);
	protected static JPanel tablesPane = new JPanel();
	
	protected JToolBar toolBar = new JToolBar();

	protected Set<Button<?>> buttons;

	protected JPopupMenu popupMenuJCell = new JPopupMenu();
	protected JMenuItem menuItemShow = new JMenuItem("Mostrar");
	protected JMenuItem menuItemInformations = new JMenuItem("Informações");
	protected JMenuItem menuItemExport = new JMenuItem("Exportar tabela");
	protected JMenuItem menuItemExportTree = new JMenuItem("Exportar árvore");
	protected JMenuItem menuItemEdit = new JMenuItem("Editar");
	protected JMenuItem menuItemRemove = new JMenuItem("Remover");

	protected JMenu menuItemOperations = new JMenu("Operações");
	protected JMenuItem menuItemSelection = new JMenuItem(OperationType.SELECTION.DISPLAY_NAME);
	protected JMenuItem menuItemProjection = new JMenuItem(OperationType.PROJECTION.DISPLAY_NAME);
	protected JMenuItem menuItemSort = new JMenuItem(OperationType.SORT.DISPLAY_NAME);
	protected JMenuItem menuItemAggregation = new JMenuItem(OperationType.AGGREGATION.DISPLAY_NAME);
	protected JMenuItem menuItemGroup = new JMenuItem(OperationType.GROUP.DISPLAY_NAME);
	protected JMenuItem menuItemRename = new JMenuItem(OperationType.RENAME.DISPLAY_NAME);
	protected JMenuItem menuItemIndexer = new JMenuItem(OperationType.INDEXER.DISPLAY_NAME);

	protected JMenuItem menuItemJoin = new JMenuItem(OperationType.JOIN.DISPLAY_NAME);
	protected JMenuItem menuItemLeftJoin = new JMenuItem(OperationType.LEFT_JOIN.DISPLAY_NAME);
	protected JMenuItem menuItemRightJoin = new JMenuItem(OperationType.RIGHT_JOIN.DISPLAY_NAME);
	protected JMenuItem menuItemCartesianProduct = new JMenuItem(OperationType.CARTESIAN_PRODUCT.DISPLAY_NAME);
	protected JMenuItem menuItemUnion = new JMenuItem(OperationType.UNION.DISPLAY_NAME);
	protected JMenuItem menuItemIntersection = new JMenuItem(OperationType.INTERSECTION.DISPLAY_NAME);


	public MainFrame(Set<Button<?>> buttons) {

		super("DBest: Database Basics for Engaging Students and Teachers");
		this.buttons = buttons;
		initGUI();

	}

	private void initGUI() {

		setSize(ConstantController.UI_WIDTH, ConstantController.UI_HEIGHT);
		setLocationRelativeTo(null);

		operationButtonsPane.setLayout(new BoxLayout(operationButtonsPane, BoxLayout.Y_AXIS));
		tablesPane.add(tablesComponent, BorderLayout.CENTER);
		
		getContentPane().add(graphComponent, BorderLayout.CENTER);
		getContentPane().add(tablesPane, BorderLayout.WEST);
		getContentPane().add(operationButtonsPane, BorderLayout.EAST);
		
		addOperationButtons();
		addBottomButtons();

		getContentPane().add(toolBar, BorderLayout.SOUTH);

		getContentPane().addKeyListener(this);

		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.setUseBoundingBox(false);

		setTablesSavedConfig();
		setGraphConfig();

		setMenuItemsListener();

		addMenuItemOperations();

		mainContainer = getContentPane();

		setVisible(true);

	}

	private void addOperationButtons(){

		mxStylesheet stylesheet = graph.getStylesheet();

		buttons.add(new OperationButton(stylesheet, OperationType.PROJECTION, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.SELECTION, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.SORT, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.AGGREGATION, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.GROUP, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.RENAME, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.INDEXER, this, operationButtonsPane));

		buttons.add(new OperationButton(stylesheet, OperationType.JOIN, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.LEFT_JOIN, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.RIGHT_JOIN, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.CARTESIAN_PRODUCT, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.UNION, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.INTERSECTION, this, operationButtonsPane));

	}

	private void addBottomButtons(){

		buttons.add(new ToolBarButton<>(JButton.class, " Importar tabela(i) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.IMPORT_FILE)));
//		buttons.add(new ToolBarButton<>(JButton.class, " Criar tabela(c) ", this, toolBar,
//				new CurrentAction(CurrentAction.ActionType.CREATE_TABLE)));
		buttons.add(new ToolBarButton<>(JButton.class, " Edge(e) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.EDGE)));
		buttons.add(new ToolBarButton<>(JButton.class, " Remover(del) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.DELETE_CELL)));
		buttons.add(new ToolBarButton<>(JButton.class, " Remover tudo ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.DELETE_ALL)));
		buttons.add(new ToolBarButton<>(JButton.class, " Captura de tela ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.PRINT_SCREEN)));
		buttons.add(new ToolBarButton<>(JButton.class, " Console ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.OPEN_CONSOLE)));
		buttons.add(new ToolBarButton<>(JButton.class, " Editor de texto ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.OPEN_TEXT_EDITOR)));

	}

	private void setTablesSavedConfig(){

		tablesComponent.getGraphControl().addMouseListener(this);
		tablesComponent.setConnectable(false);
		tablesGraph.setAutoSizeCells(false);
		tablesGraph.setCellsResizable(false);
		tablesGraph.setAutoOrigin(false);
		tablesGraph.setCellsEditable(false);
		tablesGraph.setAllowDanglingEdges(false);
		tablesGraph.setCellsMovable(false);
		tablesGraph.setCellsDeletable(false);

	}

	private void setGraphConfig(){

		graphComponent.getGraphControl().addMouseMotionListener(this);
		graphComponent.getGraphControl().addKeyListener(this);
		graphComponent.setConnectable(false);
		graphComponent.getGraphControl().addMouseListener(this);
		graphComponent.addKeyListener(this);
		graphComponent.setFocusable(true);
		graphComponent.requestFocus();
		graphComponent.setComponentPopupMenu(popupMenuJCell);

		graph.setAutoSizeCells(false);
		graph.setCellsResizable(false);
		graph.setAutoOrigin(false);
		graph.setCellsEditable(false);
		graph.setAllowDanglingEdges(false);

	}

	private void setMenuItemsListener(){

		menuItemInformations.addActionListener(this);
		menuItemExport.addActionListener(this);
		menuItemExportTree.addActionListener(this);
		menuItemShow.addActionListener(this);
		menuItemEdit.addActionListener(this);
		menuItemRemove.addActionListener(this);

		menuItemSelection.addActionListener(this);
		menuItemProjection.addActionListener(this);
		menuItemSort.addActionListener(this);
		menuItemAggregation.addActionListener(this);
		menuItemGroup.addActionListener(this);
		menuItemRename.addActionListener(this);
		menuItemIndexer.addActionListener(this);

		menuItemJoin.addActionListener(this);
		menuItemLeftJoin.addActionListener(this);
		menuItemRightJoin.addActionListener(this);
		menuItemCartesianProduct.addActionListener(this);
		menuItemUnion.addActionListener(this);
		menuItemIntersection.addActionListener(this);

	}

	private void addMenuItemOperations(){

		menuItemOperations.add(menuItemSelection);
		menuItemOperations.add(menuItemProjection);
		menuItemOperations.add(menuItemSort);
		menuItemOperations.add(menuItemAggregation);
		menuItemOperations.add(menuItemGroup);
		menuItemOperations.add(menuItemRename);
		menuItemOperations.add(menuItemIndexer);
		menuItemOperations.addSeparator();
		menuItemOperations.add(menuItemJoin);
		menuItemOperations.add(menuItemLeftJoin);
		menuItemOperations.add(menuItemRightJoin);
		menuItemOperations.add(menuItemCartesianProduct);
		menuItemOperations.add(menuItemUnion);
		menuItemOperations.add(menuItemIntersection);

	}
	
	public static mxGraph getGraph() {
		return graph;
	}

	public static mxGraphComponent getGraphComponent() {
		return graphComponent;
	}

	public void getBackToMain() {
		setContentPane(mainContainer);
		revalidate();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
