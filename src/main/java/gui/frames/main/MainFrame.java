package gui.frames.main;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import controllers.ConstantController;
import entities.Action.CurrentAction;
import entities.buttons.Button;
import entities.buttons.OperationButton;
import entities.buttons.ToolBarButton;
import enums.CellType;
import enums.OperationType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
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

    protected JMenuItem showMenuItem;

    protected JMenuItem informationsMenuItem;

    protected JMenuItem asOperatorMenuItem;

    protected JMenuItem exportTableMenuItem;

    protected JMenuItem generateFyiTableMenuItem;

    protected JMenuItem exportTreeMenuItem;

    protected JMenuItem editMenuItem;

    protected JMenuItem removeMenuItem;

    protected JMenuItem markCellMenuItem;

    protected JMenuItem unmarkCellMenuItem;

    protected JMenuItem selectionMenuItem;

    protected JMenuItem projectionMenuItem;

    protected JMenuItem filterColumnMenuItem;

    protected JMenuItem sortMenuItem;

//    protected JMenuItem aggregationMenuItem;

//    protected JMenuItem groupMenuItem;

    protected JMenuItem renameMenuItem;

//    protected JMenuItem indexerMenuItem;

    protected JMenuItem joinMenuItem;

    protected JMenuItem leftJoinMenuItem;

    protected JMenuItem rightJoinMenuItem;

    protected JMenuItem cartesianProductMenuItem;

    protected JMenuItem unionMenuItem;

    protected JMenuItem intersectionMenuItem;

    protected JMenuItem importTableMenuItem;

    protected JMenuItem importTreeMenuItem;

    protected JMenuBar topMenuBar = new JMenuBar();

    protected JMenuItem importTableTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.importTable"));

    protected JMenuItem importTreeTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.importTree"));

    protected JMenuItem nimbusThemeTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.appearance.theme.nimbus"));

    protected JMenuItem motifThemeTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.appearance.theme.motif"));

    protected JMenuItem gtkThemeTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.appearance.theme.gtk"));

    protected JMenuItem undoTopMenuBarItem = new JMenuItem(String.format("%s (%s)", ConstantController.getString("menu.edit.undo"), ConstantController.getString("menu.edit.undo.shortcut")));

    protected JMenuItem redoTopMenuBarItem = new JMenuItem(String.format("%s (%s)", ConstantController.getString("menu.edit.redo"), ConstantController.getString("menu.edit.redo.shortcut")));

    private JPanel loadingSomethingPanel = new JPanel(new BorderLayout());

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
        this.topMenuBar = new JMenuBar();
        this.showMenuItem = new JMenuItem(ConstantController.getString("cell.show"));
        this.informationsMenuItem = new JMenuItem(ConstantController.getString("cell.informations"));
        this.asOperatorMenuItem = new JMenuItem(ConstantController.getString("cell.asOperator"));
        this.exportTableMenuItem = new JMenuItem(ConstantController.getString("cell.exportTable"));
        this.generateFyiTableMenuItem = new JMenuItem(ConstantController.getString("cell.generateFyiTable"));
        this.exportTreeMenuItem = new JMenuItem(ConstantController.getString("cell.exportTree"));
        this.editMenuItem = new JMenuItem(ConstantController.getString("cell.edit"));
        this.removeMenuItem = new JMenuItem(ConstantController.getString("cell.remove"));
        this.markCellMenuItem = new JMenuItem(ConstantController.getString("cell.mark"));
        this.unmarkCellMenuItem = new JMenuItem(ConstantController.getString("cell.unmark"));
        this.operationsMenuItem = new JMenu(ConstantController.getString("cell.operations"));
        this.selectionMenuItem = new JMenuItem(OperationType.SELECTION.displayName);
        this.projectionMenuItem = new JMenuItem(OperationType.PROJECTION.displayName);
        this.filterColumnMenuItem = new JMenuItem(OperationType.SELECT_COLUMNS.displayName);
        this.sortMenuItem = new JMenuItem(OperationType.SORT.displayName);
//        this.aggregationMenuItem = new JMenuItem(OperationType.AGGREGATION.displayName);
//        this.groupMenuItem = new JMenuItem(OperationType.GROUP.displayName);
        this.renameMenuItem = new JMenuItem(OperationType.RENAME.displayName);
//        this.indexerMenuItem = new JMenuItem(OperationType.INDEXER.displayName);
        this.joinMenuItem = new JMenuItem(OperationType.JOIN.displayName);
        this.leftJoinMenuItem = new JMenuItem(OperationType.LEFT_JOIN.displayName);
        this.rightJoinMenuItem = new JMenuItem(OperationType.RIGHT_JOIN.displayName);
        this.cartesianProductMenuItem = new JMenuItem(OperationType.CARTESIAN_PRODUCT.displayName);
        this.unionMenuItem = new JMenuItem(OperationType.UNION.displayName);
        this.intersectionMenuItem = new JMenuItem(OperationType.INTERSECTION.displayName);
        this.importTableMenuItem = new JMenuItem(ConstantController.getString("menu.file.importTable"));
        this.importTreeMenuItem = new JMenuItem(ConstantController.getString("menu.file.importTree"));
    }

    protected void refreshAllComponents(){
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initializeGUI() {
        this.setSize(ConstantController.UI_SCREEN_WIDTH, ConstantController.UI_SCREEN_HEIGHT);
        this.setLocationRelativeTo(null);

        this.operationsPanel.setLayout(new BoxLayout(this.operationsPanel, BoxLayout.Y_AXIS));
        tablesPanel.add(this.tablesComponent, BorderLayout.CENTER);

        Box bottomComponents = Box.createVerticalBox();
        bottomComponents.add(toolBar);
        bottomComponents.add(loadingSomethingPanel);

        this.getContentPane().add(this.topMenuBar, BorderLayout.NORTH);
        this.getContentPane().add(graphComponent, BorderLayout.CENTER);
        this.getContentPane().add(tablesPanel, BorderLayout.WEST);
        this.getContentPane().add(this.operationsPanel, BorderLayout.EAST);
        this.getContentPane().add(bottomComponents, BorderLayout.SOUTH);

        this.addOperationButtons();
        this.addBottomButtons();
        this.addTopMenuBarFileItems();
        this.addTopMenuBarAppearanceItems();
        this.addTopMenuBarEditItems();

        this.getContentPane().addKeyListener(this);

        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.setUseBoundingBox(false);

        this.setTablesSavedGraphConfig();
        this.setGraphConfig();

        this.setMenuItemsListener();

        this.addMenuItemOperations();

        mainContainer = this.getContentPane();

        this.setJCellStyles();
        this.setVisible(true);
    }

    private void addTopMenuBarFileItems() {
        JMenu fileMenu = new JMenu(ConstantController.getString("menu.file"));
        this.topMenuBar.add(fileMenu);

        fileMenu.add(this.importTableTopMenuBarItem);
        fileMenu.add(this.importTreeTopMenuBarItem);

        this.importTableTopMenuBarItem.addActionListener(this);
        this.importTreeTopMenuBarItem.addActionListener(this);
    }

    private void addTopMenuBarAppearanceItems() {
        JMenu appearanceMenu = new JMenu(ConstantController.getString("menu.appearance"));
        this.topMenuBar.add(appearanceMenu);

        JMenu themeMenu = new JMenu(ConstantController.getString("menu.appearance.theme"));
        appearanceMenu.add(themeMenu);

        themeMenu.add(this.gtkThemeTopMenuBarItem);
        themeMenu.add(this.motifThemeTopMenuBarItem);
        themeMenu.add(this.nimbusThemeTopMenuBarItem);

        this.gtkThemeTopMenuBarItem.addActionListener(this);
        this.motifThemeTopMenuBarItem.addActionListener(this);
        this.nimbusThemeTopMenuBarItem.addActionListener(this);
    }

    private void addTopMenuBarEditItems() {
        JMenu editMenu = new JMenu(ConstantController.getString("menu.edit"));

        editMenu.add(this.undoTopMenuBarItem);
        editMenu.add(this.redoTopMenuBarItem);

        this.undoTopMenuBarItem.addActionListener(this);
        this.redoTopMenuBarItem.addActionListener(this);

        this.topMenuBar.add(editMenu);
    }

    private void setJCellStyles() {

        setCSVCellStyle();
        setFYICellStyle();
        setOperationCellStyle();
        setMemoryCellStyle();

    }

    private void setCSVCellStyle(){

        Map<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_FILLCOLOR, "#98FB98");
        style.put(mxConstants.STYLE_SHADOW, String.valueOf(true));

        String customStyle = CellType.CSV_TABLE.id;

        graph.getStylesheet().putCellStyle(customStyle, style);
        tablesGraph.getStylesheet().putCellStyle(customStyle, style);

    }

    private void setFYICellStyle(){

        Map<String, Object> style = new HashMap<>();

        style.put(mxConstants.STYLE_FILLCOLOR, "#EEEE89");
        style.put(mxConstants.STYLE_SHADOW, String.valueOf(true));

        String customStyle = CellType.FYI_TABLE.id;

        graph.getStylesheet().putCellStyle(customStyle, style);
        tablesGraph.getStylesheet().putCellStyle(customStyle, style);

    }

    private void setOperationCellStyle(){

        Map<String, Object> style = new HashMap<>();

        style.put(mxConstants.STYLE_FILLCOLOR, "none");
        style.put(mxConstants.STYLE_STROKECOLOR, "none");

        String customStyle = CellType.OPERATION.id;

        graph.getStylesheet().putCellStyle(customStyle, style);
        tablesGraph.getStylesheet().putCellStyle(customStyle, style);

    }

    private void setMemoryCellStyle(){

        Map<String, Object> style = new HashMap<>();

        style.put(mxConstants.STYLE_FILLCOLOR, "#C3DEE6");
        style.put(mxConstants.STYLE_SHADOW, String.valueOf(true));

        String customStyle = CellType.MEMORY_TABLE.id;

        graph.getStylesheet().putCellStyle(customStyle, style);
        tablesGraph.getStylesheet().putCellStyle(customStyle, style);

    }

    private void addOperationButtons() {
        mxStylesheet stylesheet = graph.getStylesheet();

        this.buttons.add(new OperationButton(stylesheet, OperationType.PROJECTION, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.SELECT_COLUMNS, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.SELECTION, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.SORT, this, this.operationsPanel));
//        this.buttons.add(new OperationButton(stylesheet, OperationType.AGGREGATION, this, this.operationsPanel));
//        this.buttons.add(new OperationButton(stylesheet, OperationType.GROUP, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.RENAME, this, this.operationsPanel));
//        this.buttons.add(new OperationButton(stylesheet, OperationType.INDEXER, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.JOIN, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.LEFT_JOIN, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.RIGHT_JOIN, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.CARTESIAN_PRODUCT, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.UNION, this, this.operationsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.INTERSECTION, this, this.operationsPanel));
    }

    private void addBottomButtons() {
        this.buttons.add(new ToolBarButton<>(JButton.class, String.format("%s (i)", ConstantController.getString("toolBarButtons.importTable")), this, this.toolBar, new CurrentAction(CurrentAction.ActionType.IMPORT_FILE)));
        this.buttons.add(new ToolBarButton<>(JButton.class, String.format("%s (c)", ConstantController.getString("toolBarButtons.createTable")), this, this.toolBar, new CurrentAction(CurrentAction.ActionType.CREATE_TABLE_CELL)));
        this.buttons.add(new ToolBarButton<>(JButton.class, String.format("%s (e)", ConstantController.getString("toolBarButtons.edge")), this, this.toolBar, new CurrentAction(CurrentAction.ActionType.CREATE_EDGE)));
        this.buttons.add(new ToolBarButton<>(JButton.class, String.format("%s (del)", ConstantController.getString("toolBarButtons.remove")), this, this.toolBar, new CurrentAction(CurrentAction.ActionType.DELETE_CELL)));
        this.buttons.add(new ToolBarButton<>(JButton.class, String.format("%s", ConstantController.getString("toolBarButtons.removeAll")), this, this.toolBar, new CurrentAction(CurrentAction.ActionType.DELETE_ALL)));
        this.buttons.add(new ToolBarButton<>(JButton.class, String.format("%s", ConstantController.getString("toolBarButtons.screenshot")), this, this.toolBar, new CurrentAction(CurrentAction.ActionType.PRINT_SCREEN)));
        this.buttons.add(new ToolBarButton<>(JButton.class, String.format("%s", ConstantController.getString("toolBarButtons.console")), this, this.toolBar, new CurrentAction(CurrentAction.ActionType.OPEN_CONSOLE)));
        this.buttons.add(new ToolBarButton<>(JButton.class, String.format("%s", ConstantController.getString("toolBarButtons.textEditor")), this, this.toolBar, new CurrentAction(CurrentAction.ActionType.OPEN_TEXT_EDITOR)));
        this.buttons.add(new ToolBarButton<>(JButton.class, String.format("%s", ConstantController.getString("toolBarButtons.comparator")), this, this.toolBar, new CurrentAction(CurrentAction.ActionType.OPEN_COMPARATOR)));
    }

    private void setTablesSavedGraphConfig() {
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
        graphComponent.setGridVisible(true);
        graphComponent.setDragEnabled(true);
        graphComponent.setGridStyle(1);
        graphComponent.setAutoExtend(true);
        graphComponent.requestFocus();
        graphComponent.setComponentPopupMenu(this.popupMenuJCell);

        graph.setAutoSizeCells(false);
        graph.setCellsResizable(false);
        graph.setAutoOrigin(false);
        graph.setCellsEditable(false);
        graph.setAllowDanglingEdges(false);
        graph.setAllowLoops(false);
        graph.setCellsBendable(false);
        graph.setConnectableEdges(false);
        graph.setEdgeLabelsMovable(false);

    }

    private void setMenuItemsListener() {
        this.informationsMenuItem.addActionListener(this);
        this.asOperatorMenuItem.addActionListener(this);
        this.exportTableMenuItem.addActionListener(this);
        this.generateFyiTableMenuItem.addActionListener(this);
        this.exportTreeMenuItem.addActionListener(this);
        this.showMenuItem.addActionListener(this);
        this.editMenuItem.addActionListener(this);
        this.removeMenuItem.addActionListener(this);
        this.markCellMenuItem.addActionListener(this);
        this.unmarkCellMenuItem.addActionListener(this);
        this.selectionMenuItem.addActionListener(this);
        this.projectionMenuItem.addActionListener(this);
        this.sortMenuItem.addActionListener(this);
//        this.aggregationMenuItem.addActionListener(this);
//        this.groupMenuItem.addActionListener(this);
        this.renameMenuItem.addActionListener(this);
//        this.indexerMenuItem.addActionListener(this);
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
        this.operationsMenuItem.add(this.filterColumnMenuItem);
        this.operationsMenuItem.add(this.sortMenuItem);
//        this.operationsMenuItem.add(this.aggregationMenuItem);
//        this.operationsMenuItem.add(this.groupMenuItem);
        this.operationsMenuItem.add(this.renameMenuItem);
//        this.operationsMenuItem.add(this.indexerMenuItem);
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

    public static mxGraph getTablesGraph() {
        return tablesGraph;
    }

    public static JPanel getTablesPanel() {
        return tablesPanel;
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

//    int startX, startY;

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

}
