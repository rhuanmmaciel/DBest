package gui.frames.main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;
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

import entities.Action.CurrentAction;
import entities.buttons.Button;
import entities.buttons.OperationButton;
import entities.buttons.ToolBarButton;
import enums.OperationType;

public abstract class MainFrame extends JFrame
		implements ActionListener, MouseListener, KeyListener, MouseMotionListener {

	private static Container mainContainer;

	public static final int WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.65);
	public static final int HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.7);

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
	protected JMenuItem menuItemExport = new JMenuItem("Exportar");
	protected JMenuItem menuItemEdit = new JMenuItem("Editar");
	protected JMenuItem menuItemRemove = new JMenuItem("Remover");

	protected JMenu menuItemOperations = new JMenu("Operações");
	protected JMenuItem menuItemSelection = new JMenuItem(OperationType.SELECTION.getDisplayName());
	protected JMenuItem menuItemProjection = new JMenuItem(OperationType.PROJECTION.getDisplayName());
	protected JMenuItem menuItemJoin = new JMenuItem(OperationType.JOIN.getDisplayName());
	protected JMenuItem menuItemLeftJoin = new JMenuItem(OperationType.LEFT_JOIN.getDisplayName());
	protected JMenuItem menuItemRightJoin = new JMenuItem(OperationType.RIGHT_JOIN.getDisplayName());
	protected JMenuItem menuItemCartesianProduct = new JMenuItem(OperationType.CARTESIAN_PRODUCT.getDisplayName());
	protected JMenuItem menuItemUnion = new JMenuItem(OperationType.UNION.getDisplayName());
	protected JMenuItem menuItemIntersection = new JMenuItem(OperationType.INTERSECTION.getDisplayName());
	protected JMenuItem menuItemSort = new JMenuItem(OperationType.SORT.getDisplayName());
	protected JMenuItem menuItemGroup = new JMenuItem(OperationType.GROUP.getDisplayName());

	public MainFrame(Set<Button<?>> buttons) {

		super("DBest: Database Basics for Engaging Students and Teachers");
		this.buttons = buttons;
		initGUI();

	}

	private void initGUI() {

		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);

		operationButtonsPane.setLayout(new BoxLayout(operationButtonsPane, BoxLayout.Y_AXIS));
		tablesPane.add(tablesComponent, BorderLayout.CENTER);
		
		getContentPane().add(graphComponent, BorderLayout.CENTER);
		getContentPane().add(tablesPane, BorderLayout.WEST);
		getContentPane().add(operationButtonsPane, BorderLayout.EAST);
		
		mxStylesheet stylesheet = graph.getStylesheet();
		
		buttons.add(new OperationButton(stylesheet, OperationType.PROJECTION, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.SELECTION, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.JOIN, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.LEFT_JOIN, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.RIGHT_JOIN, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.CARTESIAN_PRODUCT, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.UNION, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.INTERSECTION, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.SORT, this, operationButtonsPane));
		buttons.add(new OperationButton(stylesheet, OperationType.GROUP, this, operationButtonsPane));
		
		buttons.add(new ToolBarButton<JButton>(JButton.class, " Importar tabela(i) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.IMPORT_FILE)));
		buttons.add(new ToolBarButton<JButton>(JButton.class, " Criar tabela(c) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.CREATE_TABLE)));
		buttons.add(new ToolBarButton<JButton>(JButton.class, " Edge(e) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.EDGE)));
		buttons.add(new ToolBarButton<JButton>(JButton.class, " Remover(del) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.DELETE_CELL)));
		buttons.add(new ToolBarButton<JButton>(JButton.class, " Remover tudo ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.DELETE_ALL)));
		buttons.add(new ToolBarButton<JButton>(JButton.class, " Exportar(x) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.SAVE_CELL)));
		buttons.add(new ToolBarButton<JButton>(JButton.class, " Mostrar(s) ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.SHOW_CELL)));
		buttons.add(new ToolBarButton<JButton>(JButton.class, " Console ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.OPEN_CONSOLE)));
		buttons.add(new ToolBarButton<JButton>(JButton.class, " Editor de texto ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.OPEN_TEXT_EDITOR)));
		
		getContentPane().add(toolBar, BorderLayout.SOUTH);

		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.setUseBoundingBox(false);

		tablesComponent.getGraphControl().addMouseListener(this);
		tablesComponent.setConnectable(false);
		tablesGraph.setAutoSizeCells(false);
		tablesGraph.setCellsResizable(false);
		tablesGraph.setAutoOrigin(false);
		tablesGraph.setCellsEditable(false);
		tablesGraph.setAllowDanglingEdges(false);
		tablesGraph.setCellsMovable(false);
		tablesGraph.setCellsDeletable(false);
		
		graphComponent.getGraphControl().addMouseMotionListener(this);
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

		menuItemInformations.addActionListener(this);
		menuItemExport.addActionListener(this);
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
		menuItemIntersection.addActionListener(this);
		menuItemSort.addActionListener(this);
		menuItemGroup.addActionListener(this);

		menuItemOperations.add(menuItemSelection);
		menuItemOperations.add(menuItemProjection);
		menuItemOperations.add(menuItemSort);
		menuItemOperations.add(menuItemGroup);
		menuItemOperations.addSeparator();
		menuItemOperations.add(menuItemJoin);
		menuItemOperations.add(menuItemLeftJoin);
		menuItemOperations.add(menuItemRightJoin);
		menuItemOperations.add(menuItemCartesianProduct);
		menuItemOperations.add(menuItemUnion);
		menuItemOperations.add(menuItemIntersection);

		mainContainer = getContentPane();

		setVisible(true);

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
