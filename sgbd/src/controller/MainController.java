package controller;

import java.awt.*;
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
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxEvent;

import dsl.entities.BinaryExpression;
import dsl.entities.OperationExpression;
import dsl.entities.Relation;
import entities.Action.CreateOperationAction;
import entities.Action.CreateTableAction;
import entities.Action.CurrentAction;
import entities.Action.CurrentAction.ActionType;
import entities.Edge;
import entities.Tree;
import entities.buttons.Button;
import entities.buttons.OperationButton;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import entities.utils.CellUtils;
import entities.utils.TreeUtils;
import enums.FileType;
import enums.OperationType;
import files.FileUtils;
import files.ImportFile;
import gui.frames.CellInformationFrame;
import gui.frames.dsl.Console;
import gui.frames.dsl.TextEditor;
import gui.frames.forms.create.FormFrameCreateTable;
import gui.frames.forms.dbconnection.ConfigureDBConnectionForm;
import gui.frames.forms.importexport.ExportAsForm;
import gui.frames.forms.importexport.ImportAsForm;
import gui.frames.main.MainFrame;
import sgbd.table.Table;
import files.ExportFile;

public class MainController extends MainFrame {

    private static final Map<Integer, Tree> trees = new HashMap<>();
    private static File lastDirectory = new File("");

    private final Container textEditor = new TextEditor(this).getContentPane();

    private mxCell jCell;
    private mxCell ghostJCell = null;
    private final AtomicReference<mxCell> invisibleJCellRef = new AtomicReference<>(null);

    private final AtomicReference<CurrentAction> currentActionRef = new AtomicReference<>(ConstantController.NONE_ACTION);
    private final AtomicReference<Edge> edgeRef = new AtomicReference<>(new Edge());

    private static int yTables = 0;
    private static final Map<String, Table> tables = new HashMap<>();
    public boolean clicked = false;

    private static final Set<Button<?>> buttons = new HashSet<>();

    public MainController() {

        super(buttons);

        tablesComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (tablesComponent.getCellAt(e.getX(), e.getY()) != null) {
                    clicked = true;
                    graph.setSelectionCell(
                            new mxCell(((mxCell) tablesComponent.getCellAt(e.getX(), e.getY())).getValue()));
                }
            }
        });

        graph.addListener(mxEvent.CELLS_ADDED, (sender, evt) -> {
            if (clicked) {
                CellUtils.verifyCell((mxCell) graph.getSelectionCell(), ghostJCell);
                clicked = false;
            }
        });

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {

                FileUtils.clearMemory();

                System.exit(0);

            }

        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        graph.removeCells(new Object[]{ghostJCell}, true);
        ghostJCell = null;

        Button<?> btnClicked = buttons.stream().filter(x -> x.getButton() == e.getSource()).findAny().orElse(null);
        String style = "";

        if (btnClicked != null) {

            edgeRef.get().reset();
            btnClicked.setCurrentAction(currentActionRef);

            switch (currentActionRef.get().getType()) {

                case DELETE_CELL -> CellUtils.deleteCell(jCell);
                case DELETE_ALL -> CellUtils.deleteAllGraph();
                case PRINT_SCREEN -> printScreen();
                case SHOW_CELL -> CellUtils.showTable(jCell);
                case IMPORT_FILE -> newTable(CurrentAction.ActionType.IMPORT_FILE);
                case CREATE_TABLE -> newTable(CurrentAction.ActionType.CREATE_TABLE);
                case OPEN_CONSOLE -> new Console();
                case OPEN_TEXT_EDITOR -> changeScreen();
                case CREATE_DB_CONNECTION -> new ConfigureDBConnectionForm();

            }

            if (btnClicked instanceof OperationButton btnOpClicked) {

                style = btnOpClicked.getStyle();
                ((CreateOperationAction) currentActionRef.get()).setParent(null);

            }

        }

        menuItemClicked(e, btnClicked, style);

        edgeRef.get().reset();

    }

    @Override
    public void mouseClicked(MouseEvent e) {

        jCell = (mxCell) MainFrame.getGraphComponent().getCellAt(e.getX(), e.getY());

        if (e.getButton() == MouseEvent.BUTTON3 && Cell.getCells().get(jCell) != null) {

            Cell cell = Cell.getCells().get(jCell);

            popupMenuJCell.add(menuItemShow);
            popupMenuJCell.add(menuItemInformations);
            popupMenuJCell.add(menuItemExport);
            popupMenuJCell.add(menuItemExportTree);
            popupMenuJCell.add(menuItemEdit);
            popupMenuJCell.add(menuItemOperations);
            popupMenuJCell.add(menuItemRemove);

            if (cell instanceof OperationCell opCell && !opCell.hasBeenInitialized()) {

                popupMenuJCell.remove(menuItemShow);
                popupMenuJCell.remove(menuItemOperations);
                popupMenuJCell.remove(menuItemEdit);
                popupMenuJCell.remove(menuItemExport);
                popupMenuJCell.remove(menuItemExportTree);

            }

            if (cell instanceof TableCell || OperationType.OPERATIONS_WITHOUT_FORM.contains(((OperationCell) cell).getType()))
                popupMenuJCell.remove(menuItemEdit);

            if (cell.hasChild())
                popupMenuJCell.remove(menuItemOperations);


            if (cell.hasError()) {

                popupMenuJCell.remove(menuItemShow);
                popupMenuJCell.remove(menuItemOperations);

                if (!cell.hasParents())
                    popupMenuJCell.remove(menuItemEdit);

            }
            popupMenuJCell.show(MainFrame.getGraphComponent().getGraphControl(), e.getX(), e.getY());

        }

        new ClickController(currentActionRef, new AtomicReference<>(jCell), edgeRef, e, new AtomicReference<>(ghostJCell), invisibleJCellRef);

        if (Cell.getCells().get(jCell) != null && e.getClickCount() == 2)
            CellUtils.showTable(jCell);

    }

    public void menuItemClicked(ActionEvent e, Button<?> btnClicked, String style) {

        CreateOperationAction opAction = null;

        if (e.getSource() == menuItemShow)
            CellUtils.showTable(jCell);

        else if (e.getSource() == menuItemInformations)
            new CellInformationFrame(jCell);

        else if (e.getSource() == menuItemExport)
            export();

        else if (e.getSource() == menuItemExportTree)
            new ExportFile(Cell.getCells().get(jCell).getTree());

        else if (e.getSource() == menuItemEdit) {

            OperationCell opCell = (OperationCell) Cell.getCells().get(jCell);
            opCell.editOperation(jCell);
            TreeUtils.recalculateContent(opCell);

        } else if (e.getSource() == menuItemRemove)
            CellUtils.deleteCell(jCell);

        else if (e.getSource() == menuItemSelection) {

            opAction = OperationType.SELECTION.getAction();
            style = OperationType.SELECTION.DISPLAY_NAME;

        } else if (e.getSource() == menuItemProjection) {

            opAction = OperationType.PROJECTION.getAction();
            style = OperationType.PROJECTION.DISPLAY_NAME;

        } else if (e.getSource() == menuItemJoin) {

            opAction = OperationType.JOIN.getAction();
            style = OperationType.JOIN.DISPLAY_NAME;

        } else if (e.getSource() == menuItemLeftJoin) {

            opAction = OperationType.LEFT_JOIN.getAction();
            style = OperationType.LEFT_JOIN.DISPLAY_NAME;

        } else if (e.getSource() == menuItemRightJoin) {

            opAction = OperationType.RIGHT_JOIN.getAction();
            style = OperationType.RIGHT_JOIN.DISPLAY_NAME;

        } else if (e.getSource() == menuItemCartesianProduct) {

            opAction = OperationType.CARTESIAN_PRODUCT.getAction();
            style = OperationType.CARTESIAN_PRODUCT.DISPLAY_NAME;

        } else if (e.getSource() == menuItemUnion) {

            opAction = OperationType.UNION.getAction();
            style = OperationType.UNION.DISPLAY_NAME;

        } else if (e.getSource() == menuItemIntersection) {

            opAction = OperationType.INTERSECTION.getAction();
            style = OperationType.INTERSECTION.DISPLAY_NAME;

        } else if (e.getSource() == menuItemSort) {

            opAction = OperationType.SORT.getAction();
            style = OperationType.SORT.DISPLAY_NAME;

        } else if (menuItemIndexer == e.getSource()) {

            opAction = OperationType.INDEXER.getAction();
            style = OperationType.INDEXER.DISPLAY_NAME;

        }

        if (opAction != null) {

            opAction.setParent(jCell);
            currentActionRef.set(opAction);

        }

        if (opAction != null
                || (btnClicked != null && currentActionRef.get().getType() == ActionType.CREATE_OPERATOR_CELL)) {
            ghostJCell = (mxCell) graph.insertVertex(graph.getDefaultParent(), "ghost", style,
                    MouseInfo.getPointerInfo().getLocation().getX() - MainFrame.getGraphComponent().getWidth(),
                    MouseInfo.getPointerInfo().getLocation().getY() - MainFrame.getGraphComponent().getHeight(),
                    80, 30, style);

        }

    }

    private void changeScreen() {

        setContentPane(textEditor);
        revalidate();

    }

    private void newTable(CurrentAction.ActionType action) {

        AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(false);

        TableCell tableCell = action == CurrentAction.ActionType.CREATE_TABLE
                ? new FormFrameCreateTable(cancelServiceReference).getResult()
                : new ImportAsForm(cancelServiceReference).getResult();

        if (!cancelServiceReference.get()) {

            saveTable(tableCell);
            CellUtils.deleteCell(tableCell.getJGraphCell());

        } else {

            if (tableCell != null)
                TreeUtils.deleteTree(tableCell.getTree());

        }

        setNoneAction();

    }

    private void setNoneAction() {

        currentActionRef.set(ConstantController.NONE_ACTION);

    }

    private void export() {

        AtomicReference<Boolean> cancelService = new AtomicReference<>(false);

        new ExportAsForm(jCell, cancelService);

    }

    public static void saveTable(TableCell table) {

        boolean create = tables.keySet().stream().noneMatch(x -> x.equals(table.getName()));

        if (create) {

            tablesGraph.insertVertex(tablesGraph.getDefaultParent(), null, table.getName(), 0, yTables,
                    table.getWidth(), table.getHeight(), table.getStyle());

            tables.put(table.getName(), table.getTable());

            tablesPane.revalidate();

            yTables += 40;

        }

    }

    private void printScreen() {

        new ExportFile();
    }

    private void deleteAction() {

        System.out.println(currentActionRef.get());

        if (currentActionRef.get().getType() == ActionType.EDGE) {

            graphComponent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            ClickController.deleteMovableEdge(invisibleJCellRef);

        }
        setNoneAction();

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_S) {

            if (jCell != null)
                CellUtils.showTable(jCell);

        } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {

            if (jCell != null) {

                CellUtils.deleteCell(jCell);
                setNoneAction();

            }

        } else if (e.getKeyCode() == KeyEvent.VK_E) {

            graphComponent.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            currentActionRef.set(new CurrentAction(CurrentAction.ActionType.EDGE));

        } else if (e.getKeyCode() == KeyEvent.VK_I) {

            newTable(CurrentAction.ActionType.IMPORT_FILE);

        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

            deleteAction();

        } else if (e.getKeyCode() == KeyEvent.VK_L) {

            System.out.println("--------------------------");
            System.out.println("Árvores: ");
            for (Integer i : trees.keySet()) {

                System.out.println(trees.get(i));

            }
            System.out.println();
            System.out.println();

        } else if (e.getKeyCode() == KeyEvent.VK_A) {

            if (jCell != null && Cell.getCells().get(jCell) != null) {

                System.out.println(jCell);

            }

        }
    }

    private void setEdgeCursor() {

        Cursor edgeCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);

        graphComponent.getGraphControl().setCursor(edgeCursor);

    }

    private void moveCell(MouseEvent e, mxCell cellMoved) {

        int spaceBetweenCursorY = 20;

        mxGeometry geo = cellMoved.getGeometry();

        if (cellMoved.getEdgeAt(0) != null && cellMoved.getEdgeAt(0).getTerminal(true).getGeometry().getCenterY() < e.getY())
            spaceBetweenCursorY *= -1;

        double dx = e.getX() - geo.getCenterX();
        double dy = e.getY() - geo.getCenterY() + spaceBetweenCursorY;
        MainFrame.getGraph().moveCells(new Object[]{cellMoved}, dx, dy);

    }

    @Override
    public void mouseMoved(MouseEvent e) {

        if (currentActionRef.get().getType() == ActionType.NONE) return;

        if (currentActionRef.get().getType() == CurrentAction.ActionType.CREATE_OPERATOR_CELL
                && ghostJCell != null)
            moveCell(e, ghostJCell);

        else if (currentActionRef.get().getType() == ActionType.EDGE && invisibleJCellRef.get() != null)
            moveCell(e, invisibleJCellRef.get());

        else if (currentActionRef.get() instanceof CreateTableAction createTable)
            moveCell(e, createTable.getTableCell().getJGraphCell());

        else if (currentActionRef.get().getType() == ActionType.EDGE)
            setEdgeCursor();

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

    public static void setLastDirectory(File newLastDirectory) {
        lastDirectory = newLastDirectory;
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

        mxCell jTableCell = (mxCell) MainFrame.getGraph().insertVertex(graph.getDefaultParent(), null,
                relation.getName(), x, y, ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, "table");

        relation.setCell(new ImportFile(relation.getName() + FileType.HEADER.EXTENSION, jTableCell).getResult());

        relation.getCell().getTable().open();

        saveTable(relation.getCell());

    }

    public static void putOperationCell(OperationExpression operationExpression) {

        int x, y;
        if (operationExpression.getCoordinates().isPresent()) {

            x = operationExpression.getCoordinates().get().x();
            y = operationExpression.getCoordinates().get().y();

        } else {

            x = (int) (Math.random() * 600);
            y = (int) (Math.random() * 600);

        }

        OperationType type = operationExpression.getType();

        mxCell jCell = (mxCell) MainFrame.getGraph().insertVertex(graph.getDefaultParent(), null,
                type.getDisplayNameAndSymbol(), x, y, ConstantController.OPERATION_CELL_WIDTH, ConstantController.OPERATION_CELL_HEIGHT, type.DISPLAY_NAME);

        List<Cell> parents = new ArrayList<>();
        parents.add(operationExpression.getSource().getCell());

        if (operationExpression instanceof BinaryExpression binaryExpression)
            parents.add(binaryExpression.getSource2().getCell());

        operationExpression.setCell(new OperationCell(jCell, type, parents, operationExpression.getArguments()));

        OperationCell cell = operationExpression.getCell();

        cell.setAllNewTrees();

    }

}
