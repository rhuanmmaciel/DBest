package controller;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxEvent;

import commands.CommandController;
import commands.InsertOperationCellCommand;
import commands.RemoveCellCommand;
import commands.UndoableRedoableCommand;
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
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import entities.utils.TreeUtils;
import entities.utils.cells.CellUtils;
import enums.OperationType;
import files.ExportFile;
import files.FileUtils;
import files.ImportFile;
import gui.frames.CellInformationFrame;
import gui.frames.dsl.Console;
import gui.frames.dsl.TextEditor;
import gui.frames.forms.create.FormFrameCreateTable;
import gui.frames.forms.importexport.ExportAsForm;
import gui.frames.forms.importexport.ImportAsForm;
import gui.frames.main.MainFrame;
import sgbd.table.Table;
import utils.RandomUtils;

public class MainController extends MainFrame {

    private final Container textEditor = new TextEditor(this).getContentPane();

    private mxCell cell, ghostCell = null;

    private final AtomicReference<mxCell> invisibleCellReference = new AtomicReference<>(null);

    private final AtomicReference<CurrentAction> currentActionReference = new AtomicReference<>(ConstantController.NONE_ACTION);

    public static final AtomicReference<Edge> currentEdgeReference = new AtomicReference<>(new Edge());

    private static final Map<Integer, Tree> trees = new HashMap<>();

    private static final Map<String, Table> tables = new HashMap<>();

    public static final CommandController commandController = new CommandController();

    private static File lastDirectory = new File("");

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
                CellUtils.verifyCell((mxCell) graph.getSelectionCell(), this.ghostCell);
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
                //case DELETE_CELL -> CellUtils.removeCell(this.cell);
                case DELETE_CELL -> this.executeRemoveCellCommand(this.cell);
                case DELETE_ALL -> CellUtils.deleteGraph();
                case PRINT_SCREEN -> this.printScreen();
                case SHOW_CELL -> CellUtils.showTable(this.cell);
                case IMPORT_FILE -> this.newTable(CurrentAction.ActionType.IMPORT_FILE);
                case CREATE_TABLE_CELL -> this.newTable(CurrentAction.ActionType.CREATE_TABLE_CELL);
                case OPEN_CONSOLE -> new Console();
                case OPEN_TEXT_EDITOR -> this.changeScreen();
            }

            if (clickedButton instanceof OperationButton clickedOperationButton) {
                style = clickedOperationButton.getStyle();
                ((CreateOperationCellAction) this.currentActionReference.get()).setParent(null);
            }
        }

        this.menuItemClicked(event, clickedButton, style);

        currentEdgeReference.set(new Edge());
        // currentEdgeReference.get().reset();
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
            this.executeInsertCellCommand(event);
        }

        if (optionalCell.isPresent() && event.getClickCount() == 2) {
            CellUtils.showTable(this.cell);
        }
    }

    public void executeInsertCellCommand(MouseEvent event) {
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

    public void menuItemClicked(ActionEvent event, Button<?> clickedButton, String style) {
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
                .ifPresent(cell -> new ExportFile(cell.getTree()));
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

    private void changeScreen() {
        this.setContentPane(this.textEditor);
        this.revalidate();
    }

    private void newTable(CurrentAction.ActionType action) {
        AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(false);

        TableCell tableCell = action == CurrentAction.ActionType.CREATE_TABLE_CELL
            ? new FormFrameCreateTable(cancelServiceReference).getResult()
            : new ImportAsForm(cancelServiceReference).getResult();

        if (!cancelServiceReference.get()) {
            saveTable(tableCell);
            CellUtils.removeCell(tableCell.getJCell());
        } else {
            if (tableCell != null) {
                TreeUtils.deleteTree(tableCell.getTree());
            }
        }

        this.setNoneAction();
    }

    private void setNoneAction() {
        this.currentActionReference.set(ConstantController.NONE_ACTION);
    }

    private void export() {
        AtomicReference<Boolean> cancelService = new AtomicReference<>(false);

        new ExportAsForm(this.cell, cancelService);
    }

    public static void saveTable(TableCell tableCell) {
        String tableName = tableCell.getName();
        boolean create = tables.keySet().stream().noneMatch(x -> x.equals(tableName));

        if (!create) return;

        tablesGraph.insertVertex(
            tablesGraph.getDefaultParent(), null, tableName, 0,
            currentTableYPosition, tableCell.getWidth(), tableCell.getHeight(), tableCell.getStyle()
        );

        tables.put(tableName, tableCell.getTable());

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

        this.setNoneAction();
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
                CellUtils.removeCell(this.cell);
                this.setNoneAction();
            }
        } else if (keyCode == KeyEvent.VK_E) {
            graphComponent.getGraphControl().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            this.currentActionReference.set(new CurrentAction(CurrentAction.ActionType.CREATE_EDGE));
        } else if (keyCode == KeyEvent.VK_I) {
            this.newTable(CurrentAction.ActionType.IMPORT_FILE);
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

    public static Map<String, Table> getTables() {
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
            x = (int) (Math.random() * 600);
            y = (int) (Math.random() * 600);
        }

        mxCell jTableCell = (mxCell) MainFrame
            .getGraph()
            .insertVertex(
                graph.getDefaultParent(), null, relation.getName(),
                x, y, 80, 30, "table"
            );

        relation.setCell(new ImportFile(relation.getName() + ".head", jTableCell).getResult());

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
                x, y, 80, 30, type.displayName
            );

        List<Cell> parents = new ArrayList<>(List.of(operationExpression.getSource().getCell()));

        if (operationExpression instanceof BinaryExpression binaryExpression) {
            parents.add(binaryExpression.getSource2().getCell());
        }

        operationExpression.setCell(new OperationCell(jCell, type, parents, operationExpression.getArguments()));

        OperationCell cell = operationExpression.getCell();

        cell.setAllNewTrees();
    }
}
