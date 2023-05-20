package gui.frames.main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Set;

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

import controller.MainController;
import entities.Action.CurrentAction;
import entities.buttons.Button;
import entities.buttons.OperationButton;
import entities.buttons.ToolBarButton;
import enums.OperationType;

@SuppressWarnings("serial")
public abstract class MainFrame extends JFrame implements ActionListener, MouseListener, KeyListener, MouseMotionListener{

	private static Container mainContainer;
	
	public static final int WIDTH = (int)(Toolkit.getDefaultToolkit().getScreenSize().width * 0.65);
	public static final int HEIGHT = (int)(Toolkit.getDefaultToolkit().getScreenSize().height * 0.7);

	
	protected static mxGraph graph = new mxGraph();;
	protected static mxGraphComponent graphComponent = new mxGraphComponent(graph);
	protected JPanel containerPanel = new JPanel(new GridLayout(7, 1));
	protected JToolBar toolBar = new JToolBar();

	protected Set<Button> buttons;
	
	protected JPopupMenu popupMenuJCell = new JPopupMenu();
	protected JMenuItem menuItemShow = new JMenuItem("Mostrar");
	protected JMenuItem menuItemInformations = new JMenuItem("Informações");
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
	
	public MainFrame(Set<Button> buttons) {
		
		super("DBest: Database Basics for Engaging Students and Teachers");
		this.buttons = buttons;
		initGUI();
		
	}
	
	private void initGUI() {

		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);

		graphComponent.setConnectable(false);

		graphComponent.setPreferredSize(new Dimension(400, 400));
		getContentPane().add(graphComponent, BorderLayout.CENTER);
		graphComponent.getGraphControl().addMouseMotionListener(this);

		mxStylesheet stylesheet = graph.getStylesheet();

		buttons.add(new OperationButton(stylesheet, OperationType.PROJECTION.getDisplayNameAndSymbol(),
				OperationType.PROJECTION.getDisplayName(), this, containerPanel, MainController.projectionOperation));
		buttons.add(new OperationButton(stylesheet, OperationType.SELECTION.getDisplayNameAndSymbol(),
				OperationType.SELECTION.getDisplayName(), this, containerPanel, MainController.selectionOperation));
		buttons.add(new OperationButton(stylesheet, OperationType.JOIN.getDisplayNameAndSymbol(), OperationType.JOIN.getDisplayName(), this,
				containerPanel, MainController.joinOperation));
		buttons.add(new OperationButton(stylesheet, OperationType.CARTESIAN_PRODUCT.getDisplayNameAndSymbol(),
				OperationType.CARTESIAN_PRODUCT.getDisplayName(), this, containerPanel, MainController.cartesianProductOperation));
		buttons.add(new OperationButton(stylesheet, OperationType.UNION.getDisplayNameAndSymbol(), OperationType.UNION.getDisplayName(),
				this, containerPanel, MainController.unionOperation));
		buttons.add(new OperationButton(stylesheet, OperationType.LEFT_JOIN.getDisplayNameAndSymbol(),
				OperationType.LEFT_JOIN.getDisplayName(), this, containerPanel, MainController.leftJoinOperation));
		buttons.add(new OperationButton(stylesheet, OperationType.RIGHT_JOIN.getDisplayNameAndSymbol(),
				OperationType.RIGHT_JOIN.getDisplayName(), this, containerPanel, MainController.rightJoinOperation));
		
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
		buttons.add(new ToolBarButton(" Query tool ", this, toolBar,
				new CurrentAction(CurrentAction.ActionType.OPEN_TEXT_EDITOR)));

		getContentPane().add(toolBar, BorderLayout.SOUTH);

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

		menuItemInformations.addActionListener(this);
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
