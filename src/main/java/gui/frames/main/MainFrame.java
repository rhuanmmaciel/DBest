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
import files.FileUtils;
import gui.frames.CustomGraphComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class MainFrame extends JFrame implements ActionListener, MouseListener, KeyListener, MouseMotionListener {

    private static Container mainContainer;

    protected static mxGraph graph;

    protected static CustomGraphComponent graphComponent;

    protected JPanel  operationsPanel;

    protected JPanel  indexOperatorsPanel;
    protected JPanel  aggregationOperatorsPanel;
    protected JPanel  innerJoinOperatorsPanel;
    protected JPanel  outerJoinOperatorsPanel;
    protected JPanel  semiJoinOperatorsPanel;
    protected JPanel  antiJoinOperatorsPanel;
    protected JPanel  setOperatorsPanel;
    protected JPanel  logicalOperatorsPanel;
    protected JPanel  ETLOperatorsPanel;
    protected JPanel  algebraOperatorsPanel;
    protected JPanel  removeOperatorsPanel;
    protected JPanel  otherOperatorsPanel;

    protected static mxGraph tablesGraph;

    protected mxGraphComponent tablesComponent;

    protected static JPanel tablesPanel;

    protected JToolBar toolBar;

    protected Set<Button<?>> buttons;

    protected JPopupMenu tablesPopupMenu;

    protected JMenuItem removeTableMenuItem;

    protected JMenuItem renameTableMenuItem;

    protected JPopupMenu popupMenuJCell;

    protected JMenu operationsMenuItem;

    protected JMenuItem runQueryMenuItem;

    protected JMenuItem informationsMenuItem;

    protected JMenuItem renameOperatorMenuItem;

    protected JMenuItem exportTableMenuItem;

    //protected JMenuItem generateFyiTableMenuItem;

    protected JMenuItem saveQueryMenuItem;

    protected JMenuItem saveQueryAsImageMenuItem;

    protected JMenuItem editMenuItem;

    protected JMenuItem removeMenuItem;

    protected JMenuItem markCellMenuItem;

    protected JMenuItem unmarkCellMenuItem;

    protected JMenuItem copyMenuItem;

    protected JMenuItem pasteMenuItem;

    protected JMenuItem redistributeNodesMenuItem;

    protected JMenuItem selectionMenuItem;

    protected JMenuItem projectionMenuItem;

    protected JMenuItem filterColumnMenuItem;

    protected JMenuItem sortMenuItem;

    protected JMenuItem aggregationMenuItem;

    protected JMenuItem groupMenuItem;

    protected JMenuItem renameMenuItem;

//    protected JMenuItem indexerMenuItem;

    protected JMenuItem joinMenuItem;

    protected JMenuItem semiJoinMenuItem;

    protected JMenuItem leftJoinMenuItem;

    protected JMenuItem rightJoinMenuItem;

    protected JMenuItem cartesianProductMenuItem;

    protected JMenuItem unionMenuItem;

    protected JMenuItem intersectionMenuItem;

    protected JMenuItem differenceMenuItem;

    //protected JMenuItem importTableMenuItem;

    //protected JMenuItem openCSVTableMenuItem;

    //protected JMenuItem openHeadFileMenuItem;

    //protected JMenuItem openBTreeTableMenuItem;

    // protected JMenuItem importTreeMenuItem;

    protected JMenuBar topMenuBar = new JMenuBar();

    //protected JMenuItem importTableTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.importTable"));

    protected JMenuItem openDatabaseConnectionTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.openDatabaseConnection"));

    protected JMenuItem openCSVTableTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.openCSVTable"));

    protected JMenuItem openBTreeTableTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.openBTreeTable"));

    protected JMenuItem openHeadFileTableTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.openHeadFileTable"));

    protected JMenuItem openQueryTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.file.openQuery"));

    protected JMenuItem nimbusThemeTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.appearance.theme.nimbus"));

    protected JMenuItem motifThemeTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.appearance.theme.motif"));

    protected JMenuItem gtkThemeTopMenuBarItem = new JMenuItem(ConstantController.getString("menu.appearance.theme.gtk"));

    //protected JMenuItem undoTopMenuBarItem = new JMenuItem(String.format("%s (%s)", ConstantController.getString("menu.edit.undo"), ConstantController.getString("menu.edit.undo.shortcut")));

    //protected JMenuItem redoTopMenuBarItem = new JMenuItem(String.format("%s (%s)", ConstantController.getString("menu.edit.redo"), ConstantController.getString("menu.edit.redo.shortcut")));

    protected MainFrame(Set<Button<?>> buttons) {
        super(ConstantController.APPLICATION_TITLE);

        try {
            this.setIconImage(new ImageIcon(String.valueOf(FileUtils.getDBestLogo())).getImage());
        }catch (Exception ignored){
        }

        this.initializeFields(buttons);
        this.initializeGUI();
    }

    private void initializeFields(Set<Button<?>> buttons) {
        graph = new mxGraph();
        graphComponent = new CustomGraphComponent(graph);
        this.operationsPanel = new JPanel();
        this.indexOperatorsPanel = new JPanel();
        this.aggregationOperatorsPanel = new JPanel();
        this.innerJoinOperatorsPanel = new JPanel();
        this.outerJoinOperatorsPanel = new JPanel();
        this.semiJoinOperatorsPanel = new JPanel();
        this.antiJoinOperatorsPanel = new JPanel();
        this.setOperatorsPanel = new JPanel();
        this.logicalOperatorsPanel = new JPanel();
        this.ETLOperatorsPanel = new JPanel();
        this.algebraOperatorsPanel = new JPanel();
        this.removeOperatorsPanel = new JPanel();
        this.otherOperatorsPanel = new JPanel();;
        tablesGraph = new mxGraph();
        this.tablesComponent = new mxGraphComponent(tablesGraph);
        tablesPanel = new JPanel();
        this.toolBar = new JToolBar();
        this.buttons = buttons;
        this.popupMenuJCell = new JPopupMenu();
        this.tablesPopupMenu = new JPopupMenu();

        this.topMenuBar = new JMenuBar();
        this.runQueryMenuItem = new JMenuItem(ConstantController.getString("cell.runQuery"));
        this.informationsMenuItem = new JMenuItem(ConstantController.getString("cell.informations"));
        this.renameOperatorMenuItem = new JMenuItem(ConstantController.getString("cell.rename"));
        this.exportTableMenuItem = new JMenuItem(ConstantController.getString("cell.exportTable"));
        //this.generateFyiTableMenuItem = new JMenuItem(ConstantController.getString("cell.generateFyiTable"));
        this.saveQueryMenuItem = new JMenuItem(ConstantController.getString("cell.saveQuery"));
        this.saveQueryAsImageMenuItem = new JMenuItem(ConstantController.getString("cell.saveQueryAsImage"));
        this.editMenuItem = new JMenuItem(ConstantController.getString("cell.edit"));
        this.removeMenuItem = new JMenuItem(ConstantController.getString("cell.remove"));
        this.markCellMenuItem = new JMenuItem(ConstantController.getString("cell.mark"));
        this.unmarkCellMenuItem = new JMenuItem(ConstantController.getString("cell.unmark"));
        this.copyMenuItem = new JMenuItem("Copy");
        this.pasteMenuItem = new JMenuItem("Paste");
        this.redistributeNodesMenuItem = new JMenuItem("Redistribute Nodes");
        this.removeTableMenuItem = new JMenuItem("Remove Table");
        this.renameTableMenuItem = new JMenuItem("Rename Table");
        this.operationsMenuItem = new JMenu(ConstantController.getString("cell.operations"));
        this.selectionMenuItem = new JMenuItem(OperationType.FILTER.displayName);
        this.projectionMenuItem = new JMenuItem(OperationType.PROJECTION.displayName);
        this.filterColumnMenuItem = new JMenuItem(OperationType.SELECT_COLUMNS.displayName);
        this.sortMenuItem = new JMenuItem(OperationType.SORT.displayName);
        this.aggregationMenuItem = new JMenuItem(OperationType.AGGREGATION.displayName);
        this.groupMenuItem = new JMenuItem(OperationType.GROUP.displayName);
        this.renameMenuItem = new JMenuItem(OperationType.RENAME.displayName);
//        this.indexerMenuItem = new JMenuItem(OperationType.INDEXER.displayName);
        this.joinMenuItem = new JMenuItem(OperationType.NESTED_LOOP_JOIN.displayName);
        this.semiJoinMenuItem = new JMenuItem(OperationType.NESTED_LOOP_LEFT_SEMI_JOIN.displayName);
        this.leftJoinMenuItem = new JMenuItem(OperationType.NESTED_LOOP_LEFT_OUTER_JOIN.displayName);
        this.rightJoinMenuItem = new JMenuItem(OperationType.NESTED_LOOP_RIGHT_OUTER_JOIN.displayName);
        this.cartesianProductMenuItem = new JMenuItem(OperationType.CARTESIAN_PRODUCT.displayName);
        this.unionMenuItem = new JMenuItem(OperationType.UNION.displayName);
        this.intersectionMenuItem = new JMenuItem(OperationType.INTERSECTION.displayName);
        this.differenceMenuItem = new JMenuItem(OperationType.DIFFERENCE.displayName);
//        this.importTableMenuItem = new JMenuItem(ConstantController.getString("menu.file.importTable"));
//        this.openCSVTableMenuItem = new JMenuItem(ConstantController.getString("menu.file.openCSVTable"));
//        this.openBTreeTableMenuItem = new JMenuItem(ConstantController.getString("menu.file.openBTreeTable"));
//        this.openHeadFileMenuItem = new JMenuItem(ConstantController.getString("menu.file.openHeadFile"));
          //this.importTreeMenuItem = new JMenuItem(ConstantController.getString("menu.file.importTree"));
    }

    protected void refreshAllComponents(){
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initializeGUI() {
        this.setSize(ConstantController.UI_SCREEN_WIDTH, ConstantController.UI_SCREEN_HEIGHT);
        this.setLocationRelativeTo(null);

        this.operationsPanel.setLayout(new BoxLayout(this.operationsPanel, BoxLayout.Y_AXIS));



        tablesPanel.add(this.tablesComponent, BorderLayout.CENTER);

        this.getContentPane().add(this.topMenuBar, BorderLayout.NORTH);
        this.getContentPane().add(graphComponent, BorderLayout.CENTER);
        this.getContentPane().add(tablesPanel, BorderLayout.WEST);
        JScrollPane scrollPane = new JScrollPane(operationsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.getContentPane().add(scrollPane, BorderLayout.EAST);
        this.getContentPane().add(this.toolBar, BorderLayout.SOUTH);



        this.addOperationButtons();
        this.addBottomButtons();
        this.addTopMenuBarFileItems();
        this.addTopMenuBarAppearanceItems();
        //this.addTopMenuBarEditItems();

        this.getContentPane().addKeyListener(this);

        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.setUseBoundingBox(false);

        this.setTablesSavedGraphConfig();
        this.setGraphConfig();

        this.setMenuItemsListener();

        this.tablesPopupMenu.add(this.removeTableMenuItem);
        this.tablesPopupMenu.add(this.renameTableMenuItem);

        this.addMenuItemOperations();

        mainContainer = this.getContentPane();

        this.setJCellStyles();
        this.setVisible(true);
    }

    private void addGroupButton(JPanel panel, String buttonName){
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // Add an empty border to the panel to shift all buttons to the right
        int leftPadding = 20; // Amount of pixels to shift to the right
        panel.setBorder(BorderFactory.createEmptyBorder(0, leftPadding, 0, 0));

        JButton groupedButton = new JButton("+" + buttonName);
        groupedButton.setBackground(new Color(173, 216, 230)); // Light blue color (RGB)
        groupedButton.setBounds(600, 50, 200, 50);
        groupedButton.setBounds(600, 300, 100, 50);
        groupedButton.setMaximumSize(new Dimension(200, 50));
        this.operationsPanel.add(groupedButton);
        this.operationsPanel.add(panel);
        panel.setVisible(false);
        // Add action listener to toggle visibility of the first panel
        groupedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (panel.isVisible()) {
                    panel.setVisible(false);
                    groupedButton.setText("+" +buttonName);
                }
                else {
                    panel.setVisible(true);
                    groupedButton.setText("-" + buttonName);
                }
            }
        });

    }

    private void addTopMenuBarFileItems() {
        JMenu fileMenu = new JMenu(ConstantController.getString("menu.file"));
        this.topMenuBar.add(fileMenu);

        //fileMenu.add(this.importTableTopMenuBarItem);
        fileMenu.add(this.openDatabaseConnectionTopMenuBarItem);
        fileMenu.add(this.openCSVTableTopMenuBarItem);
        fileMenu.add(this.openBTreeTableTopMenuBarItem);
        fileMenu.add(this.openHeadFileTableTopMenuBarItem);

        fileMenu.add(this.openQueryTopMenuBarItem);

        //this.importTableTopMenuBarItem.addActionListener(this);
        this.openDatabaseConnectionTopMenuBarItem.addActionListener(this);
        this.openCSVTableTopMenuBarItem.addActionListener(this);
        this.openBTreeTableTopMenuBarItem.addActionListener(this);
        this.openHeadFileTableTopMenuBarItem.addActionListener(this);
        this.openQueryTopMenuBarItem.addActionListener(this);
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
//        JMenu editMenu = new JMenu(ConstantController.getString("menu.edit"));
//
//        editMenu.add(this.undoTopMenuBarItem);
//        editMenu.add(this.redoTopMenuBarItem);
//
//        this.undoTopMenuBarItem.addActionListener(this);
//        this.redoTopMenuBarItem.addActionListener(this);
//
//        this.topMenuBar.add(editMenu);
    }

    private void setJCellStyles() {

        setCSVCellStyle();
        setFYICellStyle();
        setXMLCellStyle();
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

    private void setXMLCellStyle(){

        Map<String, Object> style = new HashMap<>();

        style.put(mxConstants.STYLE_FILLCOLOR, "#FFB6C1");
        style.put(mxConstants.STYLE_SHADOW, String.valueOf(true));

        String customStyle = CellType.XML_TABLE.id;

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




        this.buttons.add(new OperationButton(stylesheet, OperationType.SORT, this, this.operationsPanel));

        addGroupButton(this.algebraOperatorsPanel, "Rel. Algebra Operators");
        this.buttons.add(new OperationButton(stylesheet, OperationType.PROJECTION, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.FILTER, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.AGGREGATION, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_GROUP, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.CARTESIAN_PRODUCT, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.NESTED_LOOP_JOIN, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.NESTED_LOOP_LEFT_OUTER_JOIN, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.NESTED_LOOP_RIGHT_OUTER_JOIN, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.NESTED_LOOP_LEFT_SEMI_JOIN, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_RIGHT_SEMI_JOIN, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.NESTED_LOOP_LEFT_ANTI_JOIN, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_RIGHT_ANTI_JOIN, this, this.algebraOperatorsPanel));


        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_UNION, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_INTERSECTION, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_DIFFERENCE, this, this.algebraOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.CARTESIAN_PRODUCT, this, this.algebraOperatorsPanel));
        //this.buttons.add(new OperationButton(stylesheet, OperationType.RENAME, this, this.algebraOperatorsPanel));
        addGroupButton(this.removeOperatorsPanel, "Remove Operators");
        this.buttons.add(new OperationButton(stylesheet, OperationType.PROJECTION, this, this.removeOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.SELECT_COLUMNS, this, this.removeOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.FILTER, this, this.removeOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.DUPLICATE_REMOVAL, this, this.removeOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_DUPLICATE_REMOVAL, this, this.removeOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.LIMIT, this, this.removeOperatorsPanel));


        addGroupButton(this.ETLOperatorsPanel, "ETL Operators");
        this.buttons.add(new OperationButton(stylesheet, OperationType.EXPLODE, this, this.ETLOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.AUTO_INCREMENT, this, this.ETLOperatorsPanel));
//        this.buttons.add(new OperationButton(stylesheet, OperationType.INDEXER, this, this.operationsPanel));

        addGroupButton(this.indexOperatorsPanel, "Index Operators");
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH, this, this.indexOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.MEMOIZE, this, this.indexOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.MATERIALIZATION, this, this.indexOperatorsPanel));


        addGroupButton(this.aggregationOperatorsPanel, "Aggregation Operators");
        this.buttons.add(new OperationButton(stylesheet, OperationType.AGGREGATION, this, this.aggregationOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.GROUP, this, this.aggregationOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_GROUP, this, this.aggregationOperatorsPanel));


        addGroupButton(this.innerJoinOperatorsPanel, "Inner Join Operators");
        this.buttons.add(new OperationButton(stylesheet, OperationType.NESTED_LOOP_JOIN, this, this.innerJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.MERGE_JOIN, this, this.innerJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_JOIN, this, this.innerJoinOperatorsPanel));

        addGroupButton(this.outerJoinOperatorsPanel, "Outer Join Operators");
        this.buttons.add(new OperationButton(stylesheet, OperationType.NESTED_LOOP_LEFT_OUTER_JOIN, this, this.outerJoinOperatorsPanel));
        //this.buttons.add(new OperationButton(stylesheet, OperationType.RIGHT_OUTER_JOIN, this, this.outerJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.MERGE_LEFT_OUTER_JOIN, this, this.outerJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_LEFT_OUTER_JOIN, this, this.outerJoinOperatorsPanel));

        this.buttons.add(new OperationButton(stylesheet, OperationType.MERGE_RIGHT_OUTER_JOIN, this, this.outerJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_RIGHT_OUTER_JOIN, this, this.outerJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.MERGE_FULL_OUTER_JOIN, this, this.outerJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_FULL_OUTER_JOIN, this, this.outerJoinOperatorsPanel));

        addGroupButton(this.semiJoinOperatorsPanel, "Semi Join Operators");
        this.buttons.add(new OperationButton(stylesheet, OperationType.NESTED_LOOP_LEFT_SEMI_JOIN, this, this.semiJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.MERGE_LEFT_SEMI_JOIN, this, this.semiJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.MERGE_RIGHT_SEMI_JOIN, this, this.semiJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_LEFT_SEMI_JOIN, this, this.semiJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_RIGHT_SEMI_JOIN, this, this.semiJoinOperatorsPanel));


        addGroupButton(this.antiJoinOperatorsPanel, "Anti Join Operators");
        this.buttons.add(new OperationButton(stylesheet, OperationType.NESTED_LOOP_LEFT_ANTI_JOIN, this, this.antiJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.MERGE_LEFT_ANTI_JOIN, this, this.antiJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.MERGE_RIGHT_ANTI_JOIN, this, this.antiJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_LEFT_ANTI_JOIN, this, this.antiJoinOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_RIGHT_ANTI_JOIN, this, this.antiJoinOperatorsPanel));


        addGroupButton(this.setOperatorsPanel, "Set Operators");
        this.buttons.add(new OperationButton(stylesheet, OperationType.APPEND, this, this.setOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.UNION, this, this.setOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_UNION, this, this.setOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.INTERSECTION, this, this.setOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_INTERSECTION, this, this.setOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.DIFFERENCE, this, this.setOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.HASH_DIFFERENCE, this, this.setOperatorsPanel));

        addGroupButton(this.logicalOperatorsPanel, "Logical Operators");
        this.buttons.add(new OperationButton(stylesheet, OperationType.AND, this, this.logicalOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.OR, this, this.logicalOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.XOR, this, this.logicalOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.CONDITION, this, this.logicalOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.IF, this, this.logicalOperatorsPanel));

        addGroupButton(this.otherOperatorsPanel, "Other Operators");
        this.buttons.add(new OperationButton(stylesheet, OperationType.SCAN, this, this.otherOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.CARTESIAN_PRODUCT, this, this.otherOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.RENAME, this, this.otherOperatorsPanel));
        this.buttons.add(new OperationButton(stylesheet, OperationType.REFERENCE, this, this.otherOperatorsPanel));

        //this.buttons.add(new OperationButton(stylesheet, OperationType.UNILATERAL_EXISTENCE, this, this.setOperatorsPanel));
        //this.buttons.add(new OperationButton(stylesheet, OperationType.BILATERAL_EXISTENCE, this, this.setOperatorsPanel));

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
        //graphComponent.setGridVisible(true);
        graphComponent.setDragEnabled(true);
        //graphComponent.setGridStyle(1);
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
        this.renameOperatorMenuItem.addActionListener(this);
        this.exportTableMenuItem.addActionListener(this);
        //this.generateFyiTableMenuItem.addActionListener(this);
        this.saveQueryMenuItem.addActionListener(this);
        this.saveQueryAsImageMenuItem.addActionListener(this);
        this.runQueryMenuItem.addActionListener(this);
        this.editMenuItem.addActionListener(this);
        this.removeMenuItem.addActionListener(this);
        this.markCellMenuItem.addActionListener(this);
        this.unmarkCellMenuItem.addActionListener(this);
        this.removeTableMenuItem.addActionListener(this);
        this.renameTableMenuItem.addActionListener(this);
        this.copyMenuItem.addActionListener(this);
        this.pasteMenuItem.addActionListener(this);
        this.redistributeNodesMenuItem.addActionListener(this);
        this.selectionMenuItem.addActionListener(this);
        this.projectionMenuItem.addActionListener(this);
        this.sortMenuItem.addActionListener(this);
        this.aggregationMenuItem.addActionListener(this);
        this.groupMenuItem.addActionListener(this);
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
        this.operationsMenuItem.add(this.aggregationMenuItem);
        this.operationsMenuItem.add(this.groupMenuItem);
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
