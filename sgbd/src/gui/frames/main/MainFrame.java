package gui.frames.main;

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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public abstract class MainFrame extends JFrame implements ActionListener, MouseListener, KeyListener, MouseMotionListener {

    private static Container mainContainer;

    protected static mxGraph graph;

    protected static mxGraphComponent graphComponent;

    protected JPanel operationsPanel;

    protected static mxGraph tablesGraph;

    protected mxGraphComponent tablesComponent;

    protected static JPanel tablesPanel;

    protected JToolBar toolBar;

    protected Set<Button<?>> buttons;

    protected JPopupMenu popupMenuJCell;

    protected JMenu operationsMenuItem;

    protected JMenuItem showMenuItem, informationsMenuItem, exportTableMenuItem, exportTreeMenuItem, editMenuItem,
        removeMenuItem, selectionMenuItem, projectionMenuItem, sortMenuItem, aggregationMenuItem, groupMenuItem,
        renameMenuItem, indexerMenuItem, joinMenuItem, leftJoinMenuItem, rightJoinMenuItem, cartesianProductMenuItem,
        unionMenuItem, intersectionMenuItem;

    protected MainFrame(Set<Button<?>> buttons) {
        super(ConstantController.APPLICATION_TITLE);

        this.initializeFields(buttons);
        this.initializeGUI();
    }

    private void initializeFields(Set<Button<?>> buttons) {
        graph = new mxGraph();
        graphComponent = new mxGraphComponent(graph);
        this.operationsPanel = new JPanel();
        tablesGraph = new mxGraph();
        this.tablesComponent = new mxGraphComponent(tablesGraph);
        tablesPanel = new JPanel();
        this.toolBar = new JToolBar();
        this.buttons = buttons;
        this.popupMenuJCell = new JPopupMenu();
        this.showMenuItem = new JMenuItem("Mostrar");
        this.informationsMenuItem = new JMenuItem("Informações");
        this.exportTableMenuItem = new JMenuItem("Exportar tabela");
        this.exportTreeMenuItem = new JMenuItem("Exportar árvore");
        this.editMenuItem = new JMenuItem("Editar");
        this.removeMenuItem = new JMenuItem("Remover");
        this.operationsMenuItem = new JMenu("Operações");
        this.selectionMenuItem = new JMenuItem(OperationType.SELECTION.displayName);
        this.projectionMenuItem = new JMenuItem(OperationType.PROJECTION.displayName);
        this.sortMenuItem = new JMenuItem(OperationType.SORT.displayName);
        this.aggregationMenuItem = new JMenuItem(OperationType.AGGREGATION.displayName);
        this.groupMenuItem = new JMenuItem(OperationType.GROUP.displayName);
        this.renameMenuItem = new JMenuItem(OperationType.RENAME.displayName);
        this.indexerMenuItem = new JMenuItem(OperationType.INDEXER.displayName);
        this.joinMenuItem = new JMenuItem(OperationType.JOIN.displayName);
        this.leftJoinMenuItem = new JMenuItem(OperationType.LEFT_JOIN.displayName);
        this.rightJoinMenuItem = new JMenuItem(OperationType.RIGHT_JOIN.displayName);
        this.cartesianProductMenuItem = new JMenuItem(OperationType.CARTESIAN_PRODUCT.displayName);
        this.unionMenuItem = new JMenuItem(OperationType.UNION.displayName);
        this.intersectionMenuItem = new JMenuItem(OperationType.INTERSECTION.displayName);
    }

    private void initializeGUI() {
        this.setSize(ConstantController.UI_SCREEN_WIDTH, ConstantController.UI_SCREEN_HEIGHT);
        this.setLocationRelativeTo(null);

        this.operationsPanel.setLayout(new BoxLayout(this.operationsPanel, BoxLayout.Y_AXIS));
        tablesPanel.add(this.tablesComponent, BorderLayout.CENTER);

        this.getContentPane().add(graphComponent, BorderLayout.CENTER);
        this.getContentPane().add(tablesPanel, BorderLayout.WEST);
        this.getContentPane().add(this.operationsPanel, BorderLayout.EAST);

        this.addOperationButtons();
        this.addBottomButtons();

        this.getContentPane().add(this.toolBar, BorderLayout.SOUTH);

        this.getContentPane().addKeyListener(this);

        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.setUseBoundingBox(false);

        this.setTablesSavedConfig();
        this.setGraphConfig();

        this.setMenuItemsListener();

        this.addMenuItemOperations();

        mainContainer = this.getContentPane();

        this.setVisible(true);
    }

    private void addOperationButtons() {
        mxStylesheet stylesheet = graph.getStylesheet();

        this.buttons.add(new OperationButton(stylesheet, OperationType.PROJECTION, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.SELECTION, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.SORT, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.AGGREGATION, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.GROUP, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.RENAME, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.INDEXER, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.JOIN, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.LEFT_JOIN, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.RIGHT_JOIN, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.CARTESIAN_PRODUCT, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.UNION, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.INTERSECTION, this, this.operationsPanel));
    }

    private void addBottomButtons() {
        this.buttons.add(new ToolBarButton<>(JButton.class, "Importar tabela (i)", this, this.toolBar, new CurrentAction(CurrentAction.ActionType.IMPORT_FILE)));
        // buttons.add(new ToolBarButton<>(JButton.class, " Criar tabela(c) ", this, this.toolBar, new CurrentAction(CurrentAction.ActionType.CREATE_TABLE)));
        this.buttons.add(new ToolBarButton<>(JButton.class, "Edge (e)", this, this.toolBar, new CurrentAction(CurrentAction.ActionType.CREATE_EDGE)));
        this.buttons.add(new ToolBarButton<>(JButton.class, "Remover (del)", this, this.toolBar, new CurrentAction(CurrentAction.ActionType.DELETE_CELL)));
        this.buttons.add(new ToolBarButton<>(JButton.class, "Remover tudo", this, this.toolBar, new CurrentAction(CurrentAction.ActionType.DELETE_ALL)));
        this.buttons.add(new ToolBarButton<>(JButton.class, "Captura de tela", this, this.toolBar, new CurrentAction(CurrentAction.ActionType.PRINT_SCREEN)));
        this.buttons.add(new ToolBarButton<>(JButton.class, "Console", this, this.toolBar, new CurrentAction(CurrentAction.ActionType.OPEN_CONSOLE)));
        this.buttons.add(new ToolBarButton<>(JButton.class, "Editor de texto", this, this.toolBar, new CurrentAction(CurrentAction.ActionType.OPEN_TEXT_EDITOR)));
    }

    private void setTablesSavedConfig() {
        this.tablesComponent.getGraphControl().addMouseListener(this);
        this.tablesComponent.setConnectable(false);

        tablesGraph.setAutoSizeCells(false);
        tablesGraph.setCellsResizable(false);
        tablesGraph.setAutoOrigin(false);
        tablesGraph.setCellsEditable(false);
        tablesGraph.setAllowDanglingEdges(false);
        tablesGraph.setCellsMovable(false);
        tablesGraph.setCellsDeletable(false);
    }

    private void setGraphConfig() {
        graphComponent.getGraphControl().addMouseMotionListener(this);
        graphComponent.getGraphControl().addKeyListener(this);
        graphComponent.setConnectable(false);
        graphComponent.getGraphControl().addMouseListener(this);
        graphComponent.addKeyListener(this);
        graphComponent.setFocusable(true);
        graphComponent.requestFocus();
        graphComponent.setComponentPopupMenu(this.popupMenuJCell);

        graph.setAutoSizeCells(false);
        graph.setCellsResizable(false);
        graph.setAutoOrigin(false);
        graph.setCellsEditable(false);
        graph.setAllowDanglingEdges(false);
    }

    private void setMenuItemsListener() {
        this.informationsMenuItem.addActionListener(this);
        this.exportTableMenuItem.addActionListener(this);
        this.exportTreeMenuItem.addActionListener(this);
        this.showMenuItem.addActionListener(this);
        this.editMenuItem.addActionListener(this);
        this.removeMenuItem.addActionListener(this);
        this.selectionMenuItem.addActionListener(this);
        this.projectionMenuItem.addActionListener(this);
        this.sortMenuItem.addActionListener(this);
        this.aggregationMenuItem.addActionListener(this);
        this.groupMenuItem.addActionListener(this);
        this.renameMenuItem.addActionListener(this);
        this.indexerMenuItem.addActionListener(this);
        this.joinMenuItem.addActionListener(this);
        this.leftJoinMenuItem.addActionListener(this);
        this.rightJoinMenuItem.addActionListener(this);
        this.cartesianProductMenuItem.addActionListener(this);
        this.unionMenuItem.addActionListener(this);
        this.intersectionMenuItem.addActionListener(this);
    }

    private void addMenuItemOperations() {
        this.operationsMenuItem.add(this.selectionMenuItem);
        this.operationsMenuItem.add(this.projectionMenuItem);
        this.operationsMenuItem.add(this.sortMenuItem);
        this.operationsMenuItem.add(this.aggregationMenuItem);
        this.operationsMenuItem.add(this.groupMenuItem);
        this.operationsMenuItem.add(this.renameMenuItem);
        this.operationsMenuItem.add(this.indexerMenuItem);
        this.operationsMenuItem.addSeparator();
        this.operationsMenuItem.add(this.joinMenuItem);
        this.operationsMenuItem.add(this.leftJoinMenuItem);
        this.operationsMenuItem.add(this.rightJoinMenuItem);
        this.operationsMenuItem.add(this.cartesianProductMenuItem);
        this.operationsMenuItem.add(this.unionMenuItem);
        this.operationsMenuItem.add(this.intersectionMenuItem);
    }

    public static mxGraph getGraph() {
        return graph;
    }

    public static mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public void goBackToMain() {
        this.setContentPane(mainContainer);
        this.revalidate();
    }

    @Override
    public void mouseMoved(MouseEvent event) {
    }

    @Override
    public void mousePressed(MouseEvent event) {
    }

    @Override
    public void mouseReleased(MouseEvent event) {
    }

    @Override
    public void mouseEntered(MouseEvent event) {
    }

    @Override
    public void mouseExited(MouseEvent event) {
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }

    @Override
    public void mouseDragged(MouseEvent event) {
    }
}
