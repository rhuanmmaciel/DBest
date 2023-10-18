package controllers;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxEvent;
import dsl.entities.BinaryExpression;
import dsl.entities.OperationExpression;
import dsl.entities.Relation;
import entities.Action.CreateOperationCellAction;
import entities.Action.CreateTableCellAction;
import entities.Action.CurrentAction;
import entities.Action.CurrentAction.ActionType;
import entities.Edge;
import entities.Tree;
import entities.buttons.Button;
import entities.buttons.OperationButton;
import entities.cells.*;
import entities.utils.TreeUtils;
import entities.utils.cells.CellUtils;
import enums.OperationType;
import enums.CellType;
import files.ExportFile;
import files.FileUtils;
import controllers.commands.*;
import gui.frames.CellInformationFrame;
import gui.frames.ErrorFrame;
import gui.frames.dsl.Console;
import gui.frames.dsl.TextEditor;
import gui.frames.forms.create.FormFrameCreateTable;
import gui.frames.forms.importexport.ExportAsForm;
import gui.frames.forms.importexport.ImportAsForm;
import gui.frames.main.MainFrame;
import utils.RandomUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MainController extends MainFrame {

    private final Container textEditor = new TextEditor(this).getContentPane();

    private mxCell cell, ghostCell = null;

    private final AtomicReference<mxCell> invisibleCellReference = new AtomicReference<>(null);

    private final AtomicReference<CurrentAction> currentActionReference = new AtomicReference<>(ConstantController.NONE_ACTION);

    public static final AtomicReference<Edge> currentEdgeReference = new AtomicReference<>(new Edge());

    private static final Map<Integer, Tree> trees = new HashMap<>();

    private static final Map<String, TableCell> tables = new HashMap<>();

    public static final CommandController commandController = new CommandController();

    private static File lastDirectory = new File("");

    public static Console console = null;

    private static int currentTableYPosition = 0;

    private boolean isTableCellSelected = false;

    public MainController() {
        super(new HashSet<>());

        this.tablesComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {
                Object cell = MainController.this.tablesComponent.getCellAt(event.getX(), event.getY());

                if (cell != null) {
                    graph.setSelectionCell(new mxCell(((mxCell) cell).getValue()));
                    MainController.this.isTableCellSelected = true;
                }
            }
        });

        graph.addListener(mxEvent.CELLS_ADDED, (sender, event) -> {
            if (this.isTableCellSelected) {
                this.executeInsertTableCellCommand((mxCell) graph.getSelectionCell(), this.ghostCell);
                this.isTableCellSelected = false;
            }
        });

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                FileUtils.clearMemory();
                System.exit(0);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        graph.removeCells(new Object[]{this.ghostCell}, true);

        this.ghostCell = null;

        Button<?> clickedButton = this.buttons
            .stream()
            .filter(button -> button.getButton() == event.getSource())
            .findAny()
            .orElse(null);

        String style = "";

        if (clickedButton != null) {
            currentEdgeReference.set(new Edge());

            clickedButton.setCurrentAction(this.currentActionReference);

            switch (this.currentActionReference.get().getType()) {
                case DELETE_CELL -> this.executeRemoveCellCommand(this.cell);
                case DELETE_ALL -> CellUtils.deleteGraph();
                case PRINT_SCREEN -> this.printScreen();
                case SHOW_CELL -> CellUtils.showTable(this.cell);
                case IMPORT_FILE -> this.createNewTable(CurrentAction.ActionType.IMPORT_FILE);
                case CREATE_TABLE_CELL -> this.createNewTable(CurrentAction.ActionType.CREATE_TABLE_CELL);
                case OPEN_CONSOLE -> this.openConsole();
                case OPEN_TEXT_EDITOR -> this.changeScreen();
            }

            if (clickedButton instanceof OperationButton clickedOperationButton) {
                style = clickedOperationButton.getStyle();
                ((CreateOperationCellAction) this.currentActionReference.get()).setParent(null);
            }
        }

        this.onBottomMenuItemClicked(event, clickedButton, style);
        this.onTopMenuBarItemClicked(event);

        currentEdgeReference.set(new Edge());
    }

    private void onTopMenuBarItemClicked(ActionEvent event) {
        Object source = event.getSource();
        String theme = null;

        if (source == this.importTableTopMenuBarItem) {
            this.createNewTable(CurrentAction.ActionType.IMPORT_FILE);
        } else if (source == this.importTreeTopMenuBarItem) {

        } else if (source == this.gtkThemeTopMenuBarItem) {
            theme = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        } else if (source == this.motifThemeTopMenuBarItem) {
            theme = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        } else if (source == this.nimbusThemeTopMenuBarItem) {
            theme = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
        } else if (source == this.undoTopMenuBarItem) {
            commandController.undo();
        } else if (source == this.redoTopMenuBarItem) {
            commandController.redo();
        }

        if (theme == null) return;

        try {
            UIManager.setLookAndFeel(theme);
            this.refreshAllComponents();
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (
            ClassNotFoundException | InstantiationException |
            IllegalAccessException | UnsupportedLookAndFeelException exception
        ) {
            new ErrorFrame(ConstantController.getString("error"));
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        this.cell = (mxCell) MainFrame.getGraphComponent().getCellAt(event.getX(), event.getY());

        Optional<Cell> optionalCell = CellUtils.getActiveCell(this.cell);

        if (optionalCell.isPresent() && SwingUtilities.isRightMouseButton(event)) {
            Cell cell = optionalCell.get();

            this.popupMenuJCell.add(this.showMenuItem);
            this.popupMenuJCell.add(this.informationsMenuItem);
            this.popupMenuJCell.add(this.exportTableMenuItem);
            this.popupMenuJCell.add(this.exportTreeMenuItem);
            this.popupMenuJCell.add(this.editMenuItem);
            this.popupMenuJCell.add(this.operationsMenuItem);
            this.popupMenuJCell.add(this.removeMenuItem);
            this.popupMenuJCell.add(cell.isMarked() ? this.unmarkCellMenuItem : this.markCellMenuItem);
            this.popupMenuJCell.remove(cell.isMarked() ? this.markCellMenuItem : this.unmarkCellMenuItem);

            if (cell instanceof OperationCell operationCell && !operationCell.hasBeenInitialized()) {
                this.popupMenuJCell.remove(this.showMenuItem);
                this.popupMenuJCell.remove(this.operationsMenuItem);
                this.popupMenuJCell.remove(this.editMenuItem);
                this.popupMenuJCell.remove(this.exportTableMenuItem);
                this.popupMenuJCell.remove(this.exportTreeMenuItem);
            }

            if (cell instanceof TableCell || OperationType.OPERATIONS_WITHOUT_FORM.contains(((OperationCell) cell).getType())) {
                this.popupMenuJCell.remove(this.editMenuItem);
            }

            if (cell.hasChild()) {
                this.popupMenuJCell.remove(this.operationsMenuItem);
            }

            if (cell.hasError()) {
                this.popupMenuJCell.remove(this.showMenuItem);
                this.popupMenuJCell.remove(this.operationsMenuItem);

                if (!cell.hasParents()) {
                    this.popupMenuJCell.remove(this.editMenuItem);
                }
            }

            this.popupMenuJCell.show(MainFrame.getGraphComponent().getGraphControl(), event.getX(), event.getY());
        }

        if (optionalCell.isPresent() || this.ghostCell != null) {
            this.executeInsertOperationCellCommand(event);
        }

        if (optionalCell.isPresent() && event.getClickCount() == 2) {
            CellUtils.showTable(this.cell);
        }
    }

    public void executeImportTableCommand(TableCell tableCell) {
        commandController.execute(new ImportTableCommand(tableCell));
    }

    public void executeInsertTableCellCommand(mxCell jCell, mxCell ghostCell) {
        UndoableRedoableCommand command = new InsertTableCellCommand(
            new AtomicReference<>(jCell), new AtomicReference<>(ghostCell)
        );

        commandController.execute(command);
    }

    public void executeInsertOperationCellCommand(MouseEvent event) {
        UndoableRedoableCommand command = new InsertOperationCellCommand(
            event, new AtomicReference<>(this.cell), this.invisibleCellReference,
            new AtomicReference<>(this.ghostCell), new AtomicReference<>(currentEdgeReference.get()),
            this.currentActionReference
        );

        if (
            this.currentActionReference.get().getType() == ActionType.CREATE_EDGE &&
            this.invisibleCellReference.get() == null
        ) {
            command.execute();
        } else if (this.currentActionReference.get().getType() != ActionType.NONE) {
            commandController.execute(command);
        }
    }

    public void executeRemoveCellCommand(mxCell jCell) {
        commandController.execute(new RemoveCellCommand(new AtomicReference<>(jCell)));
    }

    public static void resetCurrentEdgeReferenceValue(Edge edge) {
        currentEdgeReference.set(edge);
    }

    public static void resetCurrentEdgeReferenceValue() {
        resetCurrentEdgeReferenceValue(new Edge());
    }

    public void onBottomMenuItemClicked(ActionEvent event, Button<?> clickedButton, String style) {
        CreateOperationCellAction createOperationAction = null;

        Object menuItem = event.getSource();

        if (menuItem == this.showMenuItem) {
            CellUtils.showTable(this.cell);
        } else if (menuItem == this.informationsMenuItem) {
            new CellInformationFrame(this.cell);
        } else if (menuItem == this.exportTableMenuItem) {
            this.export();
        } else if (menuItem == this.exportTreeMenuItem) {
            CellUtils
                .getActiveCell(this.cell)
                .ifPresent(cell -> new ExportFile().exportToDsl(cell.getTree()));
        } else if (menuItem == this.editMenuItem) {
            CellUtils
                .getActiveCell(this.cell)
                .ifPresent(cell -> {
                    OperationCell operationCell = (OperationCell) cell;
                    operationCell.editOperation(this.cell);
                    TreeUtils.recalculateContent(operationCell);
                });
        } else if (menuItem == this.removeMenuItem) {
            CellUtils.removeCell(this.cell);
        } else if (menuItem == this.markCellMenuItem) {
            CellUtils.markCell(this.cell);
        } else if (menuItem == this.unmarkCellMenuItem) {
            CellUtils.unmarkCell(this.cell);
        } else if (menuItem == this.selectionMenuItem) {
            createOperationAction = OperationType.SELECTION.getAction();
            style = OperationType.SELECTION.displayName;
        } else if (menuItem == this.projectionMenuItem) {
            createOperationAction = OperationType.PROJECTION.getAction();
            style = OperationType.PROJECTION.displayName;
        } else if (menuItem == this.joinMenuItem) {
            createOperationAction = OperationType.JOIN.getAction();
            style = OperationType.JOIN.displayName;
        } else if (menuItem == this.leftJoinMenuItem) {
            createOperationAction = OperationType.LEFT_JOIN.getAction();
            style = OperationType.LEFT_JOIN.displayName;
        } else if (menuItem == this.rightJoinMenuItem) {
            createOperationAction = OperationType.RIGHT_JOIN.getAction();
            style = OperationType.RIGHT_JOIN.displayName;
        } else if (menuItem == this.cartesianProductMenuItem) {
            createOperationAction = OperationType.CARTESIAN_PRODUCT.getAction();
            style = OperationType.CARTESIAN_PRODUCT.displayName;
        } else if (menuItem == this.unionMenuItem) {
            createOperationAction = OperationType.UNION.getAction();
            style = OperationType.UNION.displayName;
        } else if (menuItem == this.intersectionMenuItem) {
            createOperationAction = OperationType.INTERSECTION.getAction();
            style = OperationType.INTERSECTION.displayName;
        } else if (menuItem == this.sortMenuItem) {
            createOperationAction = OperationType.SORT.getAction();
            style = OperationType.SORT.displayName;
        } else if (this.indexerMenuItem == menuItem) {
            createOperationAction = OperationType.INDEXER.getAction();
            style = OperationType.INDEXER.displayName;
        }

        if (createOperationAction != null) {
            createOperationAction.setParent(this.cell);
            this.currentActionReference.set(createOperationAction);
        }

        if (createOperationAction != null || (clickedButton != null && this.currentActionReference.get().getType() == ActionType.CREATE_OPERATOR_CELL)) {
            this.ghostCell = (mxCell) graph.insertVertex(
                graph.getDefaultParent(), "ghost", style,
                MouseInfo.getPointerInfo().getLocation().getX() - MainFrame.getGraphComponent().getWidth(),
                MouseInfo.getPointerInfo().getLocation().getY() - MainFrame.getGraphComponent().getHeight(),
                80, 30, style
            );
        }
    }

    private void openConsole() {
        if (console == null) {
             console = new Console();
            return;
        }

        console.setLocationRelativeTo(null);
        console.setExtendedState(Frame.NORMAL);
        console.toFront();
    }

    private void changeScreen() {
        this.setContentPane(this.textEditor);
        this.revalidate();
    }

    private void createNewTable(CurrentAction.ActionType action) {
        AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(false);

        TableCell tableCell = action == CurrentAction.ActionType.CREATE_TABLE_CELL
            ? new FormFrameCreateTable(cancelServiceReference).getResult()
            : new ImportAsForm(cancelServiceReference).getResult();

        if (!cancelServiceReference.get()) {
            this.executeImportTableCommand(tableCell);
            CellUtils.deactivateActiveJCell(MainFrame.getGraph(), tableCell.getJCell());
        } else {
            if (tableCell != null) {
                TreeUtils.deleteTree(tableCell.getTree());
            }
        }

        this.setCurrentActionToNone();
    }

    private void setCurrentActionToNone() {
        this.currentActionReference.set(ConstantController.NONE_ACTION);
    }

    private void export() {
        AtomicReference<Boolean> cancelService = new AtomicReference<>(false);

        new ExportAsForm(this.cell, cancelService);
    }

    public static void saveTable(TableCell tableCell) {
        String tableName = tableCell.getName();
        boolean shouldCreateTable = tables.keySet().stream().noneMatch(x -> x.equals(tableName));

        if (!shouldCreateTable) return;

        tablesGraph.insertVertex(
            tablesGraph.getDefaultParent(), null, tableName, 0,
            currentTableYPosition, tableCell.getWidth(), tableCell.getHeight(), tableCell.getStyle()
        );

        tables.put(tableName, tableCell);

        tablesPanel.revalidate();

        currentTableYPosition += 40;
    }

    private void printScreen() {
        new ExportFile();
    }

    private void deleteAction() {
        if (this.currentActionReference.get().getType() == ActionType.CREATE_EDGE) {
            graphComponent.getGraphControl().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            CellUtils.removeCell(this.invisibleCellReference);
        }

        this.setCurrentActionToNone();
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();

        if (keyCode == KeyEvent.VK_S) {
            if (this.cell != null) {
                CellUtils.showTable(this.cell);
            }
        } else if (keyCode == KeyEvent.VK_DELETE) {
            if (this.cell != null) {
                this.executeRemoveCellCommand(this.cell);
                this.setCurrentActionToNone();
            }
        } else if (keyCode == KeyEvent.VK_E) {
            graphComponent.getGraphControl().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            this.currentActionReference.set(new CurrentAction(CurrentAction.ActionType.CREATE_EDGE));
        } else if (keyCode == KeyEvent.VK_I) {
            this.createNewTable(CurrentAction.ActionType.IMPORT_FILE);
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            this.deleteAction();
        } else if (keyCode == KeyEvent.VK_L) {
            System.out.println("--------------------------");
            System.out.println("Árvores: ");

            for (Integer key : trees.keySet()) {
                System.out.println(trees.get(key));
            }

            System.out.print("\n\n");
        } else if (keyCode == KeyEvent.VK_A) {
            if (this.cell != null && CellUtils.getActiveCell(this.cell).isPresent()) {
                System.out.println(this.cell);
            }
        } else if (keyCode == KeyEvent.VK_Z && (event.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
            commandController.undo();
        } else if (keyCode == KeyEvent.VK_Y && (event.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
            commandController.redo();
        } else if (keyCode == KeyEvent.VK_M) {
            // new GeneralStatsFrame();
        }
    }

    private void setEdgeCursor() {
        graphComponent.getGraphControl().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    private void moveCell(MouseEvent event, mxCell cellMoved) {
        int spaceBetweenCursorY = 20;

        mxGeometry geo = cellMoved.getGeometry();

        if (cellMoved.getEdgeAt(0) != null && cellMoved.getEdgeAt(0).getTerminal(true).getGeometry().getCenterY() < event.getY()) {
            spaceBetweenCursorY *= -1;
        }

        double dx = event.getX() - geo.getCenterX();
        double dy = event.getY() - geo.getCenterY() + spaceBetweenCursorY;

        MainFrame.getGraph().moveCells(new Object[]{cellMoved}, dx, dy);
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        ActionType currentActionType = this.currentActionReference.get().getType();

        if (currentActionType == ActionType.NONE) return;

        if (currentActionType == ActionType.CREATE_OPERATOR_CELL && this.ghostCell != null) {
            this.moveCell(event, this.ghostCell);
        } else if (currentActionType == ActionType.CREATE_EDGE && this.invisibleCellReference.get() != null) {
            this.moveCell(event, this.invisibleCellReference.get());
        } else if (this.currentActionReference.get() instanceof CreateTableCellAction createTable) {
            this.moveCell(event, createTable.getTableCell().getJCell());
        } else if (currentActionType == ActionType.CREATE_EDGE) {
            this.setEdgeCursor();
        }
    }

    public static Map<Integer, Tree> getTrees() {
        return trees;
    }

    public static Map<String, TableCell> getTables() {
        return tables;
    }

    public static File getLastDirectory() {
        return lastDirectory;
    }

    public static void setLastDirectory(File lastDirectory) {
        MainController.lastDirectory = lastDirectory;
    }

    public static void putTableCell(Relation relation) {
        int x, y;

        if (relation.getCoordinates().isPresent()) {
            x = relation.getCoordinates().get().x();
            y = relation.getCoordinates().get().y();
        } else {
            x = (int) RandomUtils.nextDouble() * 600;
            y = (int) RandomUtils.nextDouble() * 600;
        }

        TableCell tableCell = MainController.getTables().get(relation.getName());

        CellType cellType = CellType.fromTableCell(tableCell);

        mxCell jTableCell = (mxCell) MainFrame
            .getGraph()
            .insertVertex(
                graph.getDefaultParent(), null, relation.getName(), x, y,
                ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT,
                cellType.equals(CellType.FYI_TABLE) ? CellType.FYI_TABLE.id : CellType.CSV_TABLE.id);

        relation.setCell(
            cellType.equals(CellType.FYI_TABLE)
                ? new FYITableCell((FYITableCell) tableCell, jTableCell)
                : new CSVTableCell((CSVTableCell) tableCell, jTableCell)
        );

        relation.getCell().getTable().open();

        saveTable(relation.getCell());
    }

    public static void putOperationCell(OperationExpression operationExpression) {
        int x, y;

        if (operationExpression.getCoordinates().isPresent()) {
            x = operationExpression.getCoordinates().get().x();
            y = operationExpression.getCoordinates().get().y();
        } else {
            x = RandomUtils.nextInt(0, 1) * 600;
            y = RandomUtils.nextInt(0, 1) * 600;
        }

        OperationType type = operationExpression.getType();

        mxCell jCell = (mxCell) MainFrame
            .getGraph()
            .insertVertex(
                graph.getDefaultParent(), null, type.getFormattedDisplayName(),
                x, y, 80, 30, CellType.OPERATION.id
            );

        List<Cell> parents = new ArrayList<>(List.of(operationExpression.getSource().getCell()));

        if (operationExpression instanceof BinaryExpression binaryExpression) {
            parents.add(binaryExpression.getSource2().getCell());
        }

        operationExpression.setCell(new OperationCell(jCell, type, parents, operationExpression.getArguments()));

        OperationCell cell = operationExpression.getCell();

        cell.setAllNewTrees();
    }

    public static int getCurrentTableYPosition() {
        return currentTableYPosition;
    }

    public static void incrementCurrentTableYPosition(int offset) {
        currentTableYPosition += offset;
    }

    public static void decrementCurrentTableYPosition(int offset) {
        currentTableYPosition = Math.max(currentTableYPosition - offset, 0);
    }
}
