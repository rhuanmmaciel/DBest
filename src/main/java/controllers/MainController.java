package controllers;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import controllers.commands.*;
import controllers.commands.RedistributeNodesCommand;
import database.TableCreator;
import dsl.entities.BinaryExpression;
import dsl.entities.OperationExpression;
import dsl.entities.Relation;
import engine.exceptions.DataBaseException;
import entities.Action.CreateOperationCellAction;
import entities.Action.CreateTableCellAction;
import entities.Action.CurrentAction;
import entities.Action.CurrentAction.ActionType;
import entities.Column;
import entities.Edge;
import entities.Tree;
import entities.buttons.Button;
import entities.buttons.OperationButton;
import entities.cells.*;
import entities.utils.TreeUtils;
import entities.utils.cells.CellUtils;
import enums.CellType;
import enums.FileType;
import enums.OperationArity;
import enums.OperationType;
import files.ExportFile;
import files.FileUtils;
import files.ImportFile;
import files.csv.CSVInfo;
import gui.frames.CellInformationFrame;
import gui.frames.ComparatorFrame;
import gui.frames.ErrorFrame;
import gui.frames.dsl.ConsoleFrame;
import gui.frames.dsl.TextEditor;
import gui.frames.forms.create.FormFrameCreateTable;
import gui.frames.forms.create.FormFrameCreateTableImproved;
import gui.frames.forms.importexport.CSVRecognizerForm;
import gui.frames.forms.importexport.ExportAsForm;
import gui.frames.forms.importexport.ImportAsForm;
import gui.frames.forms.importexport.PKAndNameChooserForm;
import gui.frames.forms.operations.unary.AsOperatorForm;
import gui.frames.forms.operations.JoinForm;
import gui.frames.jdbc.ConnectionsFrame;
import gui.frames.main.MainFrame;
import ibd.query.SingleSource;
import utils.RandomUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class MainController extends MainFrame {

    private final Container textEditor = new TextEditor(this).getContentPane();

    private mxCell jCell, ghostCell = null;

    private final AtomicReference<mxCell> invisibleCellReference = new AtomicReference<>(null);

    private final AtomicReference<CurrentAction> currentActionReference = new AtomicReference<>(
            ConstantController.NONE_ACTION);

    public static final AtomicReference<Edge> currentEdgeReference = new AtomicReference<>(new Edge());

    private static final Map<Integer, Tree> trees = new HashMap<>();

    private static final Map<String, TableCell> tables = new HashMap<>();

    public static final CommandController commandController = new CommandController();

    private static File lastDirectory = new File("");

    public static ConsoleFrame consoleFrame = null;

    public static ComparatorFrame comparatorFrame = null;

    public static ConnectionsFrame connectionsFrame = null;

    private static int currentTableYPosition = 0;
    private boolean isTableCellSelected = false;

    public static Rectangle selectionRectangle = null; // Store the last selected rectangle
    private static Point startPoint = null; // Starting point of the rectangle
    private boolean isPanning = false;
    private Point panStartPoint = null;
    private Point initialViewPosition = null;
    private long lastPanUpdateTime = 0;
    private final Map<Object, Object> lastTargets = new HashMap<>();
    private final Map<Object, Object> lastSources = new HashMap<>();

    public static boolean isImporting = false;

    /**
     * Checks if an OperationCell represents a join operation by checking its form
     */
    private boolean isJoinOperation(OperationCell operationCell) {
        if (operationCell == null || operationCell.getType() == null) {
            return false;
        }
        return operationCell.getType().form == gui.frames.forms.operations.JoinForm.class;
    }

    /**
     * Clears edge labels when disconnecting from join operations
     */
    private void clearEdgeLabelsIfNeeded(mxCell edgeCell, OperationCell previousOperationCell) {
        if (edgeCell != null && isJoinOperation(previousOperationCell)) {
            graph.getModel().setValue(edgeCell, "");
        }
    }

    private static boolean isPopupBeingActivatedByCommand = false;

    // Map para armazenar os estilos originais das células
    // private static Map<mxCell, String> originalStyles = new HashMap<>();
    public MainController() {
        super(new HashSet<>());
        this.tablesComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {
                Object cell = MainController.this.tablesComponent.getCellAt(event.getX(), event.getY());

                if (cell != null) {
                    // Handle right-click for context menu
                    if (SwingUtilities.isRightMouseButton(event)) {
                        String tableName = (String) ((mxCell) cell).getValue();
                        TableCell tableCell = tables.get(tableName);
                        if (tableCell != null) {
                            showTableContextMenu(event, tableCell);

                        }
                    } else {
                        // Handle left-click for selection
                        graph.setSelectionCell(new mxCell(((mxCell) cell).getValue()));
                        MainController.this.isTableCellSelected = true;
                    }
                }
            }
        });

        graph.addListener(mxEvent.CELLS_ADDED, (sender, event) -> {
            if (this.isTableCellSelected) {
                this.executeInsertTableCellCommand((mxCell) graph.getSelectionCell(), this.ghostCell);
                this.isTableCellSelected = false;
            }
        });

        graph.addListener(mxEvent.CELL_CONNECTED, (sender, evt) -> {
            if (MainController.isImporting) {
                return;
            }
            if (PasteCellsCommand.getIsPasting()) {
                return;
            }

            Object edge = evt.getProperties().get("edge");
            Object terminal = evt.getProperties().get("terminal");
            Boolean source = (Boolean) evt.getProperties().get("source");
            Boolean shoot = false;

            if (edge instanceof mxCell && terminal instanceof mxCell) {
                mxCell edgeCell = (mxCell) edge;
                mxCell terminalCell = (mxCell) terminal;

                // Verify if the terminal cell can be a child
                if (!source) {
                    Optional<Cell> optionalTerminalCell = CellUtils.getActiveCell(terminalCell);
                    if (optionalTerminalCell.isPresent() && !optionalTerminalCell.get().canBeChild()) {
                        // Prevent connection to table as destination - revert to previous connection
                        graph.getModel().beginUpdate();
                        try {
                            Object previousTarget = lastTargets.get(edgeCell);
                            if (previousTarget != null) {
                                // Revert to previous target
                                graph.getModel().setTerminal(edgeCell, previousTarget, false);
                            } else {
                                // If there is no previous target, remove the edge completely
                                graph.getModel().setTerminal(edgeCell, null, false);
                            }
                        } finally {
                            graph.getModel().endUpdate();
                        }
                        return;
                    } else {
                        // Successful connection
                    }
                }

                if (!source) {
                    Object previousTarget = lastTargets.get(edgeCell);
                    if (previousTarget != null && previousTarget instanceof mxCell prevTargetCell
                            && previousTarget != terminalCell) {
                        Optional<Cell> optionalPrevCell = CellUtils.getActiveCell(prevTargetCell);
                        if (optionalPrevCell.isPresent() && optionalPrevCell.get() instanceof OperationCell) {
                            OperationCell prevOperationCell = (OperationCell) optionalPrevCell.get();

                            clearEdgeLabelsIfNeeded(edgeCell, prevOperationCell);

                            mxCell sourceCell = (mxCell) edgeCell.getSource();
                            if (sourceCell != null) {
                                Optional<Cell> sourceTableCell = CellUtils.getActiveCell(sourceCell);
                                if (sourceTableCell.isPresent()) {
                                    prevOperationCell.removeParent(sourceTableCell.get());
                                }
                            }

                            prevOperationCell.setOperator(null);
                            TreeUtils.recalculateContent(prevOperationCell);
                            graph.refresh();
                        }
                    }

                    lastTargets.put(edgeCell, terminalCell);

                    Optional<Cell> optionalCell = CellUtils.getActiveCell(terminalCell);
                    if (optionalCell.isPresent() && optionalCell.get() instanceof OperationCell) {
                        OperationCell operationCell = (OperationCell) optionalCell.get();

                        mxCell sourceCell = (mxCell) edgeCell.getSource();
                        if (sourceCell != null) {
                            Optional<Cell> sourceTableCell = CellUtils.getActiveCell(sourceCell);
                            if (sourceTableCell.isPresent()) {
                                SwingUtilities.invokeLater(() -> {
                                    boolean addingNewParent = false;

                                    if (!operationCell.getParents().contains(sourceTableCell.get())) {
                                        operationCell.addParent(sourceTableCell.get());
                                        addingNewParent = true;
                                    }

                                    boolean shouldActivatePopup = !isPopupBeingActivatedByCommand;
                                    if (shouldActivatePopup && operationCell.getArity() == OperationArity.BINARY) {
                                        shouldActivatePopup = addingNewParent && operationCell.getParents().size() >= 2;
                                    }

                                    if (shouldActivatePopup) {
                                        operationCell.editOperation(terminalCell);
                                    }
                                    TreeUtils.recalculateContent(operationCell);
                                });
                            }
                        }
                    }
                } else {
                    Object previousSource = lastSources.get(edgeCell);
                    if (previousSource != null && previousSource instanceof mxCell prevSourceCell
                            && previousSource != terminalCell) {
                        Optional<Cell> optionalPrevCell = CellUtils.getActiveCell(prevSourceCell);
                        if (optionalPrevCell.isPresent()) {
                            Cell prevSourceTableCell = optionalPrevCell.get();

                            mxCell targetCell = (mxCell) edgeCell.getTarget();
                            if (targetCell != null) {
                                Optional<Cell> targetCellOpt = CellUtils.getActiveCell(targetCell);
                                if (targetCellOpt.isPresent() && targetCellOpt.get() instanceof OperationCell) {
                                    OperationCell targetOperationCell = (OperationCell) targetCellOpt.get();

                                    clearEdgeLabelsIfNeeded(edgeCell, targetOperationCell);

                                    prevSourceTableCell.removeChild();
                                    targetOperationCell.removeParent(prevSourceTableCell);
                                    TreeUtils.recalculateContent(targetOperationCell);
                                    graph.refresh();
                                }
                            }
                        }
                    }

                    lastSources.put(edgeCell, terminalCell);

                    mxCell targetCell = (mxCell) edgeCell.getTarget();
                    if (targetCell != null) {
                        Optional<Cell> optionalCell = CellUtils.getActiveCell(targetCell);
                        if (optionalCell.isPresent() && optionalCell.get() instanceof OperationCell) {
                            OperationCell operationCell = (OperationCell) optionalCell.get();
                            SwingUtilities.invokeLater(() -> {
                                Optional<Cell> sourceTableCell = CellUtils.getActiveCell(terminalCell);
                                if (sourceTableCell.isPresent()) {
                                    boolean addingNewParent = false;

                                    if (!operationCell.getParents().contains(sourceTableCell.get())) {
                                        operationCell.addParent(sourceTableCell.get());
                                        addingNewParent = true;
                                    }
                                    boolean shouldActivatePopup = !isPopupBeingActivatedByCommand;
                                    if (shouldActivatePopup && operationCell.getArity() == OperationArity.BINARY) {
                                        shouldActivatePopup = addingNewParent && operationCell.getParents().size() >= 2;
                                    }

                                    if (shouldActivatePopup) {
                                        operationCell.editOperation(targetCell);
                                    }
                                    TreeUtils.recalculateContent(operationCell);
                                }
                            });
                        }
                    }
                }
            }
        });

        for (Object edge : graph.getEdges(graph.getDefaultParent())) {
            if (edge instanceof mxCell edgeCell && edgeCell.isEdge()) {
                lastTargets.put(edgeCell, edgeCell.getTarget());
                lastSources.put(edgeCell, edgeCell.getSource());
            }
        }

        // Set the background color of the graph component
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.WHITE);

        new mxRubberband(graphComponent);

        graphComponent.addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                graphComponent.zoomIn();
            } else {
                graphComponent.zoomOut();
            }
        });

        // graph.setAutoSizeCells(true);
        mxSwingConstants.VERTEX_SELECTION_COLOR = Color.BLUE;

        // Set the selection stroke to a solid line
        mxSwingConstants.VERTEX_SELECTION_STROKE = new BasicStroke(2.0f);

        // Add mouse listener for rectangle selection
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Get starting point in screen coordinates
                startPoint = e.getLocationOnScreen(); // Store starting point
                selectionRectangle = null; // Reset rectangle
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (startPoint != null) {
                    // Get end point in screen coordinates
                    Point endPoint = e.getLocationOnScreen(); // Get end point
                    selectionRectangle = new Rectangle(
                            Math.min(startPoint.x, endPoint.x) - getLocationOnScreen().x,
                            Math.min(startPoint.y, endPoint.y) - getLocationOnScreen().y,
                            Math.abs(startPoint.x - endPoint.x),
                            Math.abs(startPoint.y - endPoint.y));
                    graphComponent.getGraphControl().repaint(); // Refresh to show the rectangle

                }
            }
        });

        graphComponent.getGraphControl().addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (startPoint != null) {
                    // Get current point in screen coordinates
                    Point endPoint = e.getLocationOnScreen();
                    selectionRectangle = new Rectangle(
                            Math.min(startPoint.x, endPoint.x) - getLocationOnScreen().x,
                            Math.min(startPoint.y, endPoint.y) - getLocationOnScreen().y,
                            Math.abs(startPoint.x - endPoint.x),
                            Math.abs(startPoint.y - endPoint.y));
                    graphComponent.getGraphControl().repaint(); // Refresh while dragging
                }
            }
        });

        // Add mouse motion listener for real-time rectangle drawing
        graphComponent.getGraphControl().addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (startPoint != null) {
                    Point endPoint = e.getPoint();
                    selectionRectangle = new Rectangle(
                            Math.min(startPoint.x, endPoint.x),
                            Math.min(startPoint.y, endPoint.y),
                            Math.abs(startPoint.x - endPoint.x),
                            Math.abs(startPoint.y - endPoint.y));
                    graphComponent.getGraphControl().repaint(); // Refresh while dragging
                }
            }
        });

        // // Definir um listener para mudanças na seleção
        // graph.getSelectionModel().addListener(mxEvent.CHANGE, new
        // mxEventSource.mxIEventListener() {
        // @Override
        // public void invoke(Object sender, mxEventObject evt) {
        // // Restaurar o estilo original das células que perderam a seleção
        // for (Map.Entry<mxCell, String> entry : originalStyles.entrySet()) {
        // mxCell cell = entry.getKey();
        // String originalStyle = entry.getValue();
        // graph.getModel().setStyle(cell, originalStyle);
        // }
        //
        // // Limpar o map para as células deselecionadas
        // originalStyles.clear();
        //
        // Object[] selectedCells = graph.getSelectionCells();
        //
        // // Para cada célula selecionada, altere o estilo e salve o original
        // for (Object cell : selectedCells) {
        // if (cell instanceof mxCell) {
        // mxCell mxCell = (mxCell) cell;
        // String originalStyle = mxCell.getStyle();
        //
        // // Salvar o estilo original
        // originalStyles.put(mxCell, originalStyle);
        //
        // // Aplicar novo estilo
        // String newStyle = originalStyle + ";"
        // + mxConstants.STYLE_STROKECOLOR + "=blue;"
        // + mxConstants.STYLE_STROKEWIDTH + "=3;";
        // //+ mxConstants.STYLE_FILLCOLOR + "=yellow";
        // graph.getModel().setStyle(mxCell, newStyle);
        // }
        // }
        // }
        // });
        // graphComponent.getGraphControl().setTransferHandler(new
        // FileTransferHandler());
        // graphComponent.setImportEnabled(true);
        // CustomDropTarget customDropTarget = new CustomDropTarget(graphComponent);
        // customDropTarget.addDropTargetListener(new DropTargetListener() {
        // // Add drag-and-drop support
        // public void dragEnter(DropTargetDragEvent dtde) {}
        // public void dragOver(DropTargetDragEvent dtde) {}
        // public void dropActionChanged(DropTargetDragEvent dtde) {}
        // public void dragExit(DropTargetEvent dte) {}
        //
        // public void drop(DropTargetDropEvent dtde) {
        // try {
        // dtde.acceptDrop(DnDConstants.ACTION_COPY);
        // Transferable transferable = dtde.getTransferable();
        // DataFlavor[] flavors = transferable.getTransferDataFlavors();
        // for (DataFlavor flavor : flavors) {
        // if (flavor.isFlavorJavaFileListType()) {
        // java.util.List<File> files = (java.util.List<File>)
        // transferable.getTransferData(flavor);
        // for (File file : files) {
        // ImportFile.importTXT(file);
        // }
        // }
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // });
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                FileUtils.clearMemory();
                System.exit(0);
            }
        });

        FileUtils.verifyExistingFilesToInitialize();

    }

    private void resetAnyAction() {

        graph.removeCells(new Object[] { this.ghostCell }, true);

        this.ghostCell = null;

        if (this.currentActionReference.get().getType() == ActionType.CREATE_EDGE) {
            graphComponent.getGraphControl().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            CellUtils.removeCell(this.invisibleCellReference);
        }

        this.setCurrentActionToNone();

        resetEdge();

    }

    /**
     * Redistribui os nós da árvore a partir do nó selecionado
     * usando layout hierárquico automático
     */
    private void redistributeNodes() {
        if (this.jCell == null) {
            return;
        }

        Optional<Cell> optionalCell = CellUtils.getActiveCell(this.jCell);
        if (optionalCell.isEmpty()) {
            return;
        }

        RedistributeNodesCommand command = new RedistributeNodesCommand(this.jCell);
        commandController.execute(command);
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        resetAnyAction();

        Button<?> clickedButton = this.buttons
                .stream()
                .filter(button -> button.getButton() == event.getSource())
                .findAny()
                .orElse(null);

        String style = "";

        if (clickedButton != null) {

            resetEdge();

            clickedButton.setCurrentAction(this.currentActionReference);

            switch (this.currentActionReference.get().getType()) {
                case DELETE_CELL ->
                    this.executeRemoveCellCommand(this.jCell);
                case DELETE_ALL ->
                    CellUtils.deleteGraph();
                case PRINT_SCREEN ->
                    this.printScreen();
                case SHOW_CELL ->
                    CellUtils.showTable(this.jCell);
                case IMPORT_FILE ->
                    this.createNewTable(CurrentAction.ActionType.IMPORT_FILE);
                case CREATE_TABLE_CELL ->
                    this.createNewTable(CurrentAction.ActionType.CREATE_TABLE_CELL);
                case OPEN_CONSOLE ->
                    this.openConsole();
                case OPEN_TEXT_EDITOR ->
                    this.changeScreen();
                case OPEN_COMPARATOR -> {
                    try {
                        this.openComparator();
                    } catch (Exception ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if (clickedButton instanceof OperationButton clickedOperationButton) {
                style = clickedOperationButton.getStyle();
                ((CreateOperationCellAction) this.currentActionReference.get()).setParent(null);
            }
        }

        try {
            this.onBottomMenuItemClicked(event, clickedButton, style);
            this.onTopMenuBarItemClicked(event);
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        resetEdge();
    }

    private void resetEdge() {

        currentEdgeReference.set(new Edge());

    }

    private void onTopMenuBarItemClicked(ActionEvent event) throws Exception {
        Object source = event.getSource();
        String theme = null;

        // if (source == this.importTableTopMenuBarItem) {
        // this.createNewTable(CurrentAction.ActionType.IMPORT_FILE);
        // } else
        if (source == this.openDatabaseConnectionTopMenuBarItem) {
            openConnections();
        } else if (source == this.openCSVTableTopMenuBarItem) {
            openCSV();
        } else if (source == this.openBTreeTableTopMenuBarItem) {
            new ImportFile(FileType.DAT, new AtomicReference<>(false));
        } else if (source == this.openHeadFileTableTopMenuBarItem) {
            new ImportFile(FileType.HEADER, new AtomicReference<>(false));

        } else if (source == this.openQueryTopMenuBarItem) {
            new ImportFile(FileType.TXT, new AtomicReference<>(false));
        } else if (source == this.gtkThemeTopMenuBarItem) {
            theme = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        } else if (source == this.motifThemeTopMenuBarItem) {
            theme = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        } else if (source == this.nimbusThemeTopMenuBarItem) {
            theme = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
            // } else if (source == this.undoTopMenuBarItem) {
            // commandController.undo();
            // } else if (source == this.redoTopMenuBarItem) {
            // commandController.redo();
        }

        if (theme == null) {
            return;
        }

        try {
            UIManager.setLookAndFeel(theme);
            this.refreshAllComponents();
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException exception) {
            new ErrorFrame(ConstantController.getString("error"));
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {

        this.jCell = (mxCell) MainFrame.getGraphComponent().getCellAt(event.getX(), event.getY());

        // try {
        // Thread.sleep(500);
        // } catch (InterruptedException ex) {
        // }
        Optional<Cell> optionalCell = CellUtils.getActiveCell(this.jCell);

        if (optionalCell.isPresent() && SwingUtilities.isRightMouseButton(event)) {
            Cell cell = optionalCell.get();

            this.popupMenuJCell.add(this.runQueryMenuItem);
            this.popupMenuJCell.add(this.informationsMenuItem);
            this.popupMenuJCell.add(this.exportTableMenuItem);
            // this.popupMenuJCell.add(this.generateFyiTableMenuItem);
            this.popupMenuJCell.add(this.renameOperatorMenuItem);
            this.popupMenuJCell.add(this.saveQueryMenuItem);
            this.popupMenuJCell.add(this.saveQueryAsImageMenuItem);
            this.popupMenuJCell.add(this.editMenuItem);
            this.popupMenuJCell.add(this.operationsMenuItem);
            this.popupMenuJCell.add(this.removeMenuItem);
            this.popupMenuJCell.add(this.copyMenuItem);
            this.popupMenuJCell.add(this.redistributeNodesMenuItem);
            this.popupMenuJCell.add(cell.isMarked() ? this.unmarkCellMenuItem : this.markCellMenuItem);
            this.popupMenuJCell.remove(cell.isMarked() ? this.markCellMenuItem : this.unmarkCellMenuItem);

            if (cell.getTree().getCells().stream().anyMatch(Cell::isMarked) && !cell.isMarked()) {

                this.popupMenuJCell.remove(unmarkCellMenuItem);
                this.popupMenuJCell.remove(markCellMenuItem);

            }

            // if (cell.isOperationCell() && !cell.hasSingleSource()) {
            // this.popupMenuJCell.remove(this.renameOperatorMenuItem);
            // }

            if (cell instanceof OperationCell operationCell && !operationCell.hasBeenInitialized()) {
                this.popupMenuJCell.remove(this.runQueryMenuItem);
                this.popupMenuJCell.remove(this.operationsMenuItem);
                // this.popupMenuJCell.remove(this.editMenuItem);
                this.popupMenuJCell.remove(this.exportTableMenuItem);
                this.popupMenuJCell.remove(this.saveQueryMenuItem);
                this.popupMenuJCell.remove(this.saveQueryAsImageMenuItem);
            }

            if (cell.isTableCell()) {
                this.popupMenuJCell.remove(this.editMenuItem);
            }

            if (cell instanceof OperationCell operationCell
                    && OperationType.OPERATIONS_WITHOUT_FORM.contains((operationCell).getType())) {
                this.popupMenuJCell.remove(this.editMenuItem);
            }

            if (cell.hasChild()) {
                this.popupMenuJCell.remove(this.operationsMenuItem);
            }

            if (cell.hasError()) {
                this.popupMenuJCell.remove(this.runQueryMenuItem);
                this.popupMenuJCell.remove(this.operationsMenuItem);

                if (!cell.hasParents()) {
                    this.popupMenuJCell.remove(this.editMenuItem);
                }
            }

            this.popupMenuJCell.show(MainFrame.getGraphComponent().getGraphControl(), event.getX(), event.getY());
        } else if (SwingUtilities.isRightMouseButton(event) && !optionalCell.isPresent()) {
            if (controllers.clipboard.Clipboard.getInstance().hasData()) {
                this.popupMenuJCell.removeAll();
                this.popupMenuJCell.add(this.pasteMenuItem);
                this.popupMenuJCell.show(MainFrame.getGraphComponent().getGraphControl(), event.getX(), event.getY());
            }
        }

        if (optionalCell.isPresent() || this.ghostCell != null) {
            this.executeInsertOperationCellCommand(event);
        }

        if (optionalCell.isPresent() && event.getClickCount() == 2) {
            CellUtils.showTable(this.jCell);
        }
    }

    public static void executeImportTableCommand(TableCell tableCell) {
        commandController.execute(new ImportTableCommand(tableCell));
    }

    public void executeInsertTableCellCommand(mxCell jCell, mxCell ghostCell) {
        UndoableRedoableCommand command = new InsertTableCellCommand(
                new AtomicReference<>(jCell), new AtomicReference<>(ghostCell));

        commandController.execute(command);
    }

    public void executeInsertOperationCellCommand(MouseEvent event) {
        UndoableRedoableCommand command = new InsertOperationCellCommand(
                event, new AtomicReference<>(this.jCell), this.invisibleCellReference,
                new AtomicReference<>(this.ghostCell), new AtomicReference<>(currentEdgeReference.get()),
                this.currentActionReference);

        if (this.currentActionReference.get().getType() == ActionType.CREATE_EDGE
                && !currentEdgeReference.get().hasParent()
                && this.jCell != null && CellUtils.getActiveCell(jCell).isPresent()
                && !CellUtils.getActiveCell(jCell).get().canBeParent()) {
            return;
        }

        if (this.currentActionReference.get().getType() == ActionType.CREATE_EDGE
                && this.invisibleCellReference.get() == null) {
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

    private void executeAsOperator(mxCell cell) {

        // if (CellUtils.getActiveCell(cell).isEmpty()
        // || !(CellUtils.getActiveCell(cell).get().isTableCell()
        // || CellUtils.getActiveCell(cell).get().hasSingleSource()
        // )
        // ) {
        // return;
        // }

        AtomicReference<Boolean> cancelService = new AtomicReference<>(false);

        Cell cell_ = CellUtils.getActiveCell(cell).get();

        AsOperatorForm form = new AsOperatorForm(cancelService, cell_.getAlias());

        if (!cancelService.get()) {
            executeAsOperator(cell, form.getNewName());
        }
    }

    public static void executeAsOperator(mxCell cell, String text) {
        // if (CellUtils.getActiveCell(cell).isEmpty()
        // || !(CellUtils.getActiveCell(cell).get().isTableCell()
        // || CellUtils.getActiveCell(cell).get().hasSingleSource()
        // )) {
        // return;
        // }
        Cell cell_ = CellUtils.getActiveCell(cell).get();

        if (cell_ instanceof TableCell)
            ((TableCell) cell_).asOperator(text);
        else {
            ((OperationCell) cell_).asOperator(text);
        }
    }

    public void onBottomMenuItemClicked(ActionEvent event, Button<?> clickedButton, String style) throws Exception {
        CreateOperationCellAction createOperationAction = null;

        Object menuItem = event.getSource();

        if (menuItem == this.runQueryMenuItem) {
            CellUtils.showTable(this.jCell);
        } else if (menuItem == this.informationsMenuItem) {
            new CellInformationFrame(this.jCell);
        } else if (menuItem == this.renameOperatorMenuItem) {
            executeAsOperator(jCell);
        } else if (menuItem == this.exportTableMenuItem) {
            this.export();
            // } else if (menuItem == this.generateFyiTableMenuItem) {
            // this.generateFyiTableCell();
        } else if (menuItem == this.saveQueryMenuItem) {
            CellUtils
                    .getActiveCell(this.jCell)
                    .ifPresent(cell -> new ExportFile().exportToDsl(cell.getTree()));
        } else if (menuItem == this.saveQueryAsImageMenuItem) {
            CellUtils
                    .getActiveCell(this.jCell)
                    .ifPresent(cell -> new ExportFile().exportSubtreeToImage(cell.getTree(), cell));
        } else if (menuItem == this.editMenuItem) {
            Optional<Cell> activeCell = CellUtils.getActiveCell(this.jCell);
            if (activeCell.isPresent()) {
                Cell cell = activeCell.get();
                OperationCell operationCell = (OperationCell) cell;
                operationCell.editOperation(this.jCell);
                TreeUtils.recalculateContent(operationCell);
            }
        } else if (menuItem == this.removeMenuItem) {
            if (this.jCell != null) {
                this.executeRemoveCellCommand(this.jCell);
            }
            this.resetAnyAction();
        } else if (menuItem == this.markCellMenuItem) {
            CellUtils.markCell(this.jCell);
        } else if (menuItem == this.unmarkCellMenuItem) {
            CellUtils.unmarkCell(this.jCell);
        } else if (menuItem == this.copyMenuItem) {
            commandController.execute(new CopyCellsCommand());
        } else if (menuItem == this.redistributeNodesMenuItem) {
            this.redistributeNodes();
        } else if (menuItem == this.pasteMenuItem) {
            Point mousePosition = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(mousePosition, MainFrame.getGraphComponent().getGraphControl());
            entities.Coordinates canvasCoords = entities.utils.CoordinatesUtils
                    .transformScreenToCanvasCoordinates(mousePosition.x, mousePosition.y);
            commandController.execute(new PasteCellsCommand(canvasCoords));
        } else if (menuItem == this.selectionMenuItem) {
            createOperationAction = OperationType.FILTER.getAction();
            style = OperationType.FILTER.displayName;
        } else if (menuItem == this.projectionMenuItem) {
            createOperationAction = OperationType.PROJECTION.getAction();
            style = OperationType.PROJECTION.displayName;
        } else if (menuItem == this.joinMenuItem) {
            createOperationAction = OperationType.NESTED_LOOP_JOIN.getAction();
            style = OperationType.NESTED_LOOP_JOIN.displayName;
        } else if (menuItem == this.leftJoinMenuItem) {
            createOperationAction = OperationType.NESTED_LOOP_LEFT_OUTER_JOIN.getAction();
            style = OperationType.NESTED_LOOP_LEFT_OUTER_JOIN.displayName;
        } else if (menuItem == this.rightJoinMenuItem) {
            createOperationAction = OperationType.NESTED_LOOP_RIGHT_OUTER_JOIN.getAction();
            style = OperationType.NESTED_LOOP_RIGHT_OUTER_JOIN.displayName;
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
        }
        // else if (this.indexerMenuItem == menuItem) {
        // createOperationAction = OperationType.INDEXER.getAction();
        // style = OperationType.INDEXER.displayName;
        // }

        if (createOperationAction != null) {
            createOperationAction.setParent(this.jCell);
            this.currentActionReference.set(createOperationAction);
        }

        if (createOperationAction != null || (clickedButton != null
                && this.currentActionReference.get().getType() == ActionType.CREATE_OPERATOR_CELL)) {
            this.ghostCell = (mxCell) graph.insertVertex(
                    graph.getDefaultParent(), "ghost", style,
                    MouseInfo.getPointerInfo().getLocation().getX() - MainFrame.getGraphComponent().getWidth(),
                    MouseInfo.getPointerInfo().getLocation().getY() - MainFrame.getGraphComponent().getHeight(),
                    80, 30, style);
        }
    }

    // not used
    private void generateFyiTableCell() throws Exception {

        AtomicReference<Boolean> cancelService = new AtomicReference<>(false);

        PKAndNameChooserForm pk = new PKAndNameChooserForm(CellUtils.getActiveCell(this.jCell).orElseThrow());
        List<Column> primaryKeyColumns = pk.getSelectedColumns();

        Cell cell = CellUtils.getActiveCell(this.jCell).get();

        if (!cancelService.get() && !primaryKeyColumns.isEmpty()) {

            List<Column> columns = new ArrayList<>();

            for (Column pkColumns : primaryKeyColumns) {
                columns.add(new Column(cell.getColumns().stream()
                        .filter(c -> c.getSourceAndName()
                                .equalsIgnoreCase((primaryKeyColumns.stream()
                                        .filter(x -> x.getSourceAndName()
                                                .equalsIgnoreCase(pkColumns.getSourceAndName()))
                                        .findFirst().orElseThrow()).getSourceAndName()))
                        .findFirst().orElseThrow(), true));
            }

            for (Column c : cell.getColumns()) {

                if (columns.stream().anyMatch(x -> x.getSourceAndName().equalsIgnoreCase(c.getSourceAndName()))) {
                    continue;
                }

                columns.add(new Column(c, false));

            }

            try {

                TableCell tableCell = TableCreator.createIndex(pk.getTableName(), columns, cell, false);
                this.executeImportTableCommand(tableCell);
                CellUtils.deactivateActiveJCell(MainFrame.getGraph(), tableCell.getJCell());

            } catch (DataBaseException e) {
                cancelService.set(true);
                new ErrorFrame(e.getMessage());
            }

        }

    }

    private void openConnections() {
        if (connectionsFrame == null) {
            connectionsFrame = new ConnectionsFrame();
            return;
        }
        connectionsFrame.setLocationRelativeTo(null);
        connectionsFrame.setExtendedState(Frame.NORMAL);
        connectionsFrame.toFront();
    }

    private void openConsole() {

        if (consoleFrame == null) {
            consoleFrame = new ConsoleFrame();
            return;
        }

        consoleFrame.setLocationRelativeTo(null);
        consoleFrame.setExtendedState(Frame.NORMAL);
        consoleFrame.toFront();
    }

    private void openComparator() {
        if (comparatorFrame == null) {
            comparatorFrame = new ComparatorFrame();
            return;
        }

        comparatorFrame.setLocationRelativeTo(null);
        comparatorFrame.setExtendedState(Frame.NORMAL);
        comparatorFrame.toFront();
    }

    private void changeScreen() {
        this.setContentPane(this.textEditor);
        this.revalidate();
    }

    private void createNewTable(CurrentAction.ActionType action) {
        AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(false);

        TableCell tableCell = action == CurrentAction.ActionType.CREATE_TABLE_CELL
                ? new FormFrameCreateTableImproved(cancelServiceReference).getResult()
                : new ImportAsForm(cancelServiceReference).getResult();

        if (!cancelServiceReference.get()) {
            String originalName = tableCell.getName();
            String newName = resolveTableNameConflict(originalName);

            if (newName == null) {
                TreeUtils.deleteTree(tableCell.getTree());
                return;
            }

            if (!originalName.equals(newName)) {
                tableCell.setName(newName);
            }

            this.executeImportTableCommand(tableCell);
            CellUtils.deactivateActiveJCell(MainFrame.getGraph(), tableCell.getJCell());
        } else {
            if (tableCell != null) {
                TreeUtils.deleteTree(tableCell.getTree());
            }
        }

        this.setCurrentActionToNone();
    }

    private void openCSV() {
        AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(false);
        JFileChooser fileUpload = new JFileChooser();
        fileUpload.setFileFilter(ImportFile.getFileNameExtensionFilter(FileType.CSV));
        if (fileUpload.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            MainController.setLastDirectory(new File(fileUpload.getCurrentDirectory().getAbsolutePath()));
            File file = fileUpload.getSelectedFile();
            AtomicReference<Boolean> exitReference = new AtomicReference();
            CSVInfo info = new CSVRecognizerForm(
                    Path.of(file.getAbsolutePath()), exitReference).getCSVInfo();
            TableCell tableCell = TableCreator.createCSVTable(
                    info.tableName(), info.columns(), info, false);
            MainController.executeImportTableCommand(tableCell);
            CellUtils.deactivateActiveJCell(MainFrame.getGraph(), tableCell.getJCell());
        }
        this.setCurrentActionToNone();

    }

    private void setCurrentActionToNone() {
        this.currentActionReference.set(ConstantController.NONE_ACTION);
    }

    private void export() {
        AtomicReference<Boolean> cancelService = new AtomicReference<>(false);

        new ExportAsForm(this.jCell, cancelService);
    }

    public static void saveTable(TableCell tableCell) {
        String tableName = tableCell.getName();
        boolean shouldCreateTable = tables.keySet().stream().noneMatch(x -> x.equals(tableName));

        if (!shouldCreateTable) {
            return;
        }

        tablesGraph.insertVertex(
                tablesGraph.getDefaultParent(), null, tableName, 0,
                currentTableYPosition, tableCell.getWidth(), tableCell.getHeight(), tableCell.getStyle());

        tables.put(tableName, tableCell);

        tablesPanel.revalidate();

        currentTableYPosition += 40;
    }

    private void printScreen() {
        saveSelectedNodesAsImage(graph);// new ExportFile();
    }

    private void saveSelectedNodesAsImage(mxGraph graph) {
        // Get the selected cells (nodes) in the graph
        Object[] selectedCells = graph.getSelectionCells();
        if (selectedCells.length == 0) {
            System.out.println("No selected nodes to save.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle(ConstantController.getString("exportFile.saveFile"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setCurrentDirectory(MainController.getLastDirectory());

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        MainController.setLastDirectory(new File(fileChooser.getCurrentDirectory().getAbsolutePath()));

        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();

        try {
            Robot robot = new Robot();
            Point locationOnScreen = getLocationOnScreen();
            Rectangle screenRect = new Rectangle(locationOnScreen.x + selectionRectangle.x,
                    locationOnScreen.y + selectionRectangle.y,
                    selectionRectangle.width, selectionRectangle.height);
            BufferedImage screenImage = robot.createScreenCapture(screenRect);
            ImageIO.write(screenImage, "png", new File(filePath));
            System.out.println("Screenshot saved successfully.");
        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();

        if (keyCode == KeyEvent.VK_S) {
            if (this.jCell != null) {
                CellUtils.showTable(this.jCell);
            }
            resetAnyAction();
        } else if (keyCode == KeyEvent.VK_DELETE) {

            graph.getModel().beginUpdate();
            try {
                Object[] cells = graph.getSelectionCells();
                if (cells != null && cells.length > 0) {
                    for (Object cell : cells) {
                        if (cell instanceof mxCell) {
                            this.executeRemoveCellCommand((mxCell) cell);
                        }
                    }
                    // graph.removeCells(cells);
                }
            } finally {
                graph.getModel().endUpdate();
            }

            // if (this.jCell != null) {
            // this.executeRemoveCellCommand(this.jCell);
            // }
            this.resetAnyAction();
        } else if (keyCode == KeyEvent.VK_E) {
            edgeAction();
        } else if (keyCode == KeyEvent.VK_I) {
            this.createNewTable(CurrentAction.ActionType.IMPORT_FILE);
            resetAnyAction();
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            this.resetAnyAction();
        } else if (keyCode == KeyEvent.VK_L) {
            System.out.println("--------------------------");
            System.out.println("Árvores: ");

            for (Integer key : trees.keySet()) {
                System.out.println(trees.get(key));
            }

            System.out.print("\n\n");
        } else if (keyCode == KeyEvent.VK_A) {
            if (this.jCell != null && CellUtils.getActiveCell(this.jCell).isPresent()) {
                CellUtils.getActiveCell(this.jCell).get().getTree().getTreeLayer();
            }
        } else if (keyCode == KeyEvent.VK_C && (event.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
            // Ctrl+C: Copy selected cells
            commandController.execute(new CopyCellsCommand());
        } else if (keyCode == KeyEvent.VK_V && (event.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
            // Ctrl+V: Paste cells from clipboard
            commandController.execute(new PasteCellsCommand());
            // } else if (keyCode == KeyEvent.VK_Z && (event.getModifiersEx() &
            // KeyEvent.CTRL_DOWN_MASK) != 0) {
            // commandController.undo();
            // } else if (keyCode == KeyEvent.VK_Y && (event.getModifiersEx() &
            // KeyEvent.CTRL_DOWN_MASK) != 0) {
            // commandController.redo();
        } else if (keyCode == KeyEvent.VK_M) {
        }

    }

    private void edgeAction() {
        resetEdge();
        setEdgeCursor();
        this.currentActionReference.set(new CurrentAction(CurrentAction.ActionType.CREATE_EDGE));
    }

    private void setEdgeCursor() {
        graphComponent.getGraphControl().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    private void moveCell2(MouseEvent event, mxCell cellMoved) {
        entities.Coordinates canvasCoords = entities.utils.CoordinatesUtils.transformScreenToCanvasCoordinates(event);

        graph.getModel().beginUpdate();
        try {
            mxGeometry geometry = cellMoved.getGeometry();
            geometry.setTerminalPoint(new mxPoint(canvasCoords.x(), canvasCoords.y()), false);
            graph.getModel().setGeometry(cellMoved, geometry);
        } finally {
            graph.getModel().endUpdate();
        }
    }

    private void moveCell(MouseEvent event, mxCell cellMoved) {

        entities.Coordinates canvasCoords = entities.utils.CoordinatesUtils.transformScreenToCanvasCoordinates(event);

        double scale = MainFrame.getGraph().getView().getScale();
        int spaceBetweenCursorY = (int) (20 / scale);

        mxGeometry geo = cellMoved.getGeometry();

        if (cellMoved.getEdgeAt(0) != null
                && cellMoved.getEdgeAt(0).getTerminal(true).getGeometry().getCenterY() < canvasCoords.y()) {
            spaceBetweenCursorY *= -1;
        }

        double dx = canvasCoords.x() - geo.getCenterX();
        double dy = canvasCoords.y() - geo.getCenterY() + spaceBetweenCursorY;

        MainFrame.getGraph().moveCells(new Object[] { cellMoved }, dx, dy);
    }

    private void moveInvisibleCell(MouseEvent event, mxCell cellMoved) {
        entities.Coordinates canvasCoords = entities.utils.CoordinatesUtils.transformScreenToCanvasCoordinates(event);

        double transformedX = canvasCoords.x();
        double transformedY = canvasCoords.y();

        int spaceBetweenCursorY = 20;

        mxGeometry geo = cellMoved.getGeometry();

        if (cellMoved.getEdgeAt(0) != null
                && cellMoved.getEdgeAt(0).getTerminal(true).getGeometry().getCenterY() < transformedY) {
            spaceBetweenCursorY *= -1;
        }

        double dx = transformedX - geo.getCenterX();
        double dy = transformedY - geo.getCenterY() + spaceBetweenCursorY;

        MainFrame.getGraph().moveCells(new Object[] { cellMoved }, dx, dy);
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        ActionType currentActionType = this.currentActionReference.get().getType();

        if (currentActionType == ActionType.CREATE_OPERATOR_CELL && this.ghostCell != null) {
            this.moveCell(event, this.ghostCell);
        } else if (currentActionType == ActionType.CREATE_EDGE && this.invisibleCellReference.get() != null) {
            this.moveInvisibleCell(event, this.invisibleCellReference.get());
        } else if (this.currentActionReference.get() instanceof CreateTableCellAction createTable) {
            this.moveCell(event, createTable.getTableCell().getJCell());
        } else if (currentActionType == ActionType.CREATE_EDGE) {
            this.setEdgeCursor();
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        boolean isMiddleButton = SwingUtilities.isMiddleMouseButton(event) ||
                event.getButton() == MouseEvent.BUTTON2 ||
                (event.getModifiersEx() & InputEvent.BUTTON2_DOWN_MASK) != 0;

        if (isMiddleButton) {
            if (isPanning) {
                isPanning = false;
                panStartPoint = null;
                initialViewPosition = null;
            }

            isPanning = true;
            panStartPoint = new Point(event.getX(), event.getY());
            initialViewPosition = new Point(graphComponent.getViewport().getViewPosition());

            graphComponent.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            event.consume();
            return;
        }

        // startX = event.getX();
        // startY = event.getY();
    }

    @Override
    public void mouseDragged(MouseEvent event) {

        if (isPanning && panStartPoint != null) {
            // calculate the difference in position
            int dx = event.getX() - panStartPoint.x;
            int dy = event.getY() - panStartPoint.y;

            // apply a threshold to avoid small movements
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < 3.0) {
                event.consume();
                return;
            }

            // apply throttle
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPanUpdateTime < 16) { // ~60 FPS
                event.consume();
                return;
            }
            lastPanUpdateTime = currentTime;

            // apply smoothing factor to reduce sensitivity
            double smoothingFactor = 0.8; // reduce sensitivity in 2
            dx = (int) (dx * smoothingFactor);
            dy = (int) (dy * smoothingFactor);

            // calculate the new view position
            Point newViewPosition = new Point(
                    initialViewPosition.x - dx,
                    initialViewPosition.y - dy);

            newViewPosition.x = Math.max(0, newViewPosition.x);
            newViewPosition.y = Math.max(0, newViewPosition.y);

            // set the new view position
            graphComponent.getViewport().setViewPosition(newViewPosition);

            event.consume();
            return;
        }

        // int dx = event.getX() - startX;
        // int dy = event.getY() - startY;
        //
        // int viewX = graphComponent.getViewport().getView().getX();
        // int viewY = graphComponent.getViewport().getView().getY();
        //
        // graphComponent.getViewport().setViewPosition(new java.awt.Point(viewX - dx,
        // viewY - dy));
        //
        // startX = event.getX();
        // startY = event.getY();
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (isPanning) {
            isPanning = false;
            panStartPoint = null;
            initialViewPosition = null;

            graphComponent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            event.consume();
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

        TableCell tableCell = MainController.getTables().get(relation.getFirstName());

        CellType cellType = CellType.fromTableCell(tableCell);

        mxCell jTableCell = (mxCell) MainFrame
                .getGraph()
                .insertVertex(
                        graph.getDefaultParent(), null, relation.getFirstName(), x, y,
                        ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT,
                        cellType.id);

        relation.setCell(
                switch (cellType) {
                    case FYI_TABLE ->
                        new FYITableCell((FYITableCell) tableCell, jTableCell);
                    case CSV_TABLE ->
                        new CSVTableCell((CSVTableCell) tableCell, jTableCell);
                    case MEMORY_TABLE ->
                        new MemoryTableCell((MemoryTableCell) tableCell, jTableCell);
                    case XML_TABLE ->
                        new XMLTableCell((XMLTableCell) tableCell, jTableCell);
                    default ->
                        throw new RuntimeException();
                });

        try {
            relation.getCell().getTable().open();
        } catch (Exception ex) {
        }

        saveTable(relation.getCell());

        if (!relation.getFirstName().equals(relation.getName())) {
            executeAsOperator(jTableCell, relation.getName());
        }

    }

    public static void putOperationCell(OperationExpression operationExpression) {
        int x, y;

        if (operationExpression.getCoordinates().isPresent()) {
            x = operationExpression.getCoordinates().get().x();
            y = operationExpression.getCoordinates().get().y();
        } else {
            x = 500; 
            y = 30;  
        }

        OperationType type = operationExpression.getType();

        int width = CellUtils.getCellWidth(type.getFormattedDisplayName());
        mxCell jCell = (mxCell) MainFrame
                .getGraph()
                .insertVertex(
                        graph.getDefaultParent(), null, type.getFormattedDisplayName(),
                        x, y, width, 30, CellType.OPERATION.id);

        List<Cell> parents = new ArrayList<>();

        if (operationExpression.getSource() != null) {
            parents.addAll(List.of(operationExpression.getSource().getCell()));
        }
        if (operationExpression instanceof BinaryExpression binaryExpression) {
            parents.add(binaryExpression.getSource2().getCell());
        }

        operationExpression.setCell(new OperationCell(jCell, type, parents, operationExpression.getArguments(),
                operationExpression.getAlias()));

        OperationCell cell = operationExpression.getCell();

        cell.setAllNewTrees();

        if (!(operationExpression.getAlias().isEmpty()))
            executeAsOperator(jCell, operationExpression.getAlias());

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

    public static void setPopupBeingActivatedByCommand(boolean value) {
        isPopupBeingActivatedByCommand = value;
    }

    public static boolean isPopupBeingActivatedByCommand() {
        return isPopupBeingActivatedByCommand;
    }

    /**
     * Shows a context menu for table operations when right-clicking on a table in the sidebar
     */
    private void showTableContextMenu(MouseEvent event, TableCell tableCell) {
        JPopupMenu contextMenu = new JPopupMenu();
        // Add Rename Table menu item
        JMenuItem renameTableMenuItem = new JMenuItem("Rename Table");
        renameTableMenuItem.addActionListener(e -> {
            String currentName = tableCell.getName();
            while (true) {
                String newName = JOptionPane.showInputDialog(
                    this,
                    "Enter new name for the table:",
                    "Rename Table",
                    JOptionPane.QUESTION_MESSAGE
                );

                if (newName == null) {
                    break;
                }

                if (newName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Table name cannot be empty!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    continue;
                }

                if (!MainController.isValidNewTableName(newName)) {
                    JOptionPane.showMessageDialog(
                        this,
                        "A table with this name already exists!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    continue;
                }

                // Get the mxCell associated with this table
                mxCell cell = null;
                Object[] cells = tablesGraph.getChildVertices(tablesGraph.getDefaultParent());
                for (Object c : cells) {
                    if (c instanceof mxCell) {
                        mxCell currentCell = (mxCell) c;
                        if (currentCell.getValue().equals(currentName)) {
                            cell = currentCell;
                            break;
                        }
                    }
                }

                if (cell != null) {
                    MainController.renameTable(currentName, newName, cell);}
                break;
            }
        });
        contextMenu.add(renameTableMenuItem);

        // Add Remove Table menu item
        JMenuItem removeTableMenuItem = new JMenuItem("Remove Table");
        removeTableMenuItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove table '" + tableCell.getName() + "'?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // Get the mxCell associated with this table
                mxCell cell = null;
                Object[] cells = tablesGraph.getChildVertices(tablesGraph.getDefaultParent());
                for (Object c : cells) {
                    if (c instanceof mxCell) {
                        mxCell currentCell = (mxCell) c;
                        if (currentCell.getValue().equals(tableCell.getName())) {
                            cell = currentCell;
                            break;
                        }
                    }
                }

                if (cell != null) {
                    MainController.removeTable(tableCell.getName(), cell);
                    tablesGraph.getModel().remove(cell);
                    tablesPanel.revalidate();
                    tablesPanel.repaint();
                }
            }
        });
        contextMenu.add(removeTableMenuItem);

        // Add a separator before cache options if any
        if (tableCell.getTable() instanceof ibd.table.btree.BTreeTable) {
            contextMenu.addSeparator();
        }

        // Only show cache management options for B-Tree tables
        if (tableCell.getTable() instanceof ibd.table.btree.BTreeTable) {
            // Menu item for cache information
            JMenuItem cacheInfoMenuItem = new JMenuItem("Cache Info");
            cacheInfoMenuItem.addActionListener(e -> showCacheInfo(tableCell));

            // Menu item for setting cache size
            JMenuItem setCacheSizeMenuItem = new JMenuItem("Set Cache Size");
            setCacheSizeMenuItem.addActionListener(e -> showSetCacheSizeDialog(tableCell));

            // Menu item for resetting cache
            JMenuItem resetCacheMenuItem = new JMenuItem("Reset Cache");
            resetCacheMenuItem.addActionListener(e -> resetTableCache(tableCell));

            contextMenu.add(cacheInfoMenuItem);
            contextMenu.addSeparator();
            contextMenu.add(setCacheSizeMenuItem);
            contextMenu.add(resetCacheMenuItem);

            // Show the context menu at the mouse position
            contextMenu.show(tablesComponent.getGraphControl(), event.getX(), event.getY());
        }
        else {
            //For other table types (CSV, relational database tables)
            contextMenu.show(event.getComponent(), event.getX(), event.getY());
        }
    }

    /**
     * Shows a dialog to set the cache size for a specific table
     */
    private void showSetCacheSizeDialog(TableCell tableCell) {
        try {
            // Get current cache size if available
            String currentCacheSize = "Unknown";
            int newCacheSizeInPages = -1; // <-- Add this variable to store the number of pages
            if (tableCell.getTable() instanceof ibd.table.btree.BTreeTable) {
                ibd.table.btree.BTreeTable btreeTable = (ibd.table.btree.BTreeTable) tableCell.getTable();
                try {
                    // Access the cache field to get actual cache size
                    java.lang.reflect.Field cacheField = btreeTable.getClass().getDeclaredField("cache");
                    cacheField.setAccessible(true);
                    Object cache = cacheField.get(btreeTable);
                    if (cache != null && cache instanceof ibd.persistent.cache.Cache) {
                        ibd.persistent.cache.Cache<?> tableCache = (ibd.persistent.cache.Cache<?>) cache;
                        // Get cacheSizeBytes field
                        java.lang.reflect.Field cacheSizeBytesField = tableCache.getClass().getSuperclass().getDeclaredField("cacheSizeBytes");
                        cacheSizeBytesField.setAccessible(true);
                        int cacheSizeBytes = cacheSizeBytesField.getInt(tableCache);
                        currentCacheSize = String.valueOf(cacheSizeBytes);
                        // Get cacheSize (pages)
                        java.lang.reflect.Field cacheSizeField = tableCache.getClass().getSuperclass().getDeclaredField("cacheSize");
                        cacheSizeField.setAccessible(true);
                        newCacheSizeInPages = cacheSizeField.getInt(tableCache);
                    } else {
                        currentCacheSize = "No cache";
                    }
                } catch (Exception ex) {
                    // Fallback to default cache size
                    currentCacheSize = String.valueOf(TableCreator.cacheSize);
                }
            }
            // Create custom dialog with Reset Default button
            JTextField inputField = new JTextField(15);
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel(String.format("<html>Current cache size: %s bytes<br>Enter new cache size (in bytes):</html>", currentCacheSize)), BorderLayout.NORTH);
            panel.add(inputField, BorderLayout.CENTER);

            String[] options = {"Reset Default", "OK", "Cancel"};
            int result = JOptionPane.showOptionDialog(
                this,
                panel,
                "Set Cache Size for " + tableCell.getName(),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1] // Default to "OK"
            );

            String input = null;
            if (result == 0) { // Reset Default button clicked
                input = "5000000";
            } else if (result == 1) { // OK button clicked
                input = inputField.getText();
            }
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int newCacheSize = Integer.parseInt(input.trim());
                    if (newCacheSize <= 0) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Cache size must be a positive number.",
                            "Invalid Cache Size",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }

                    // Check if cache size is smaller than page size
                    if (tableCell.getTable() instanceof ibd.table.btree.BTreeTable) {
                        ibd.table.btree.BTreeTable btreeTable = (ibd.table.btree.BTreeTable) tableCell.getTable();
                        try {
                            java.lang.reflect.Field cacheField = btreeTable.getClass().getDeclaredField("cache");
                            cacheField.setAccessible(true);
                            Object cache = cacheField.get(btreeTable);
                            if (cache != null && cache instanceof ibd.persistent.cache.Cache) {
                                ibd.persistent.cache.Cache<?> tableCache = (ibd.persistent.cache.Cache<?>) cache;
                                int pageSize = tableCache.getPageSize();
                                if (newCacheSize < pageSize) {
                                    JOptionPane.showMessageDialog(
                                        this,
                                        String.format("Cache size (%d bytes) cannot be smaller than page size (%d bytes).", newCacheSize, pageSize),
                                        "Invalid Cache Size",
                                        JOptionPane.ERROR_MESSAGE
                                    );
                                    return;
                                }
                            }
                        } catch (Exception ex) {
                            // If we can't get page size, continue with validation
                            System.err.println("Warning: Could not validate against page size: " + ex.getMessage());
                        }
                    }
                    setTableCacheSize(tableCell, newCacheSize);
                    // After setting, try to get the new number of pages again
                    if (tableCell.getTable() instanceof ibd.table.btree.BTreeTable) {
                        ibd.table.btree.BTreeTable btreeTable = (ibd.table.btree.BTreeTable) tableCell.getTable();
                        try {
                            java.lang.reflect.Field cacheField = btreeTable.getClass().getDeclaredField("cache");
                            cacheField.setAccessible(true);
                            Object cache = cacheField.get(btreeTable);
                            if (cache != null && cache instanceof ibd.persistent.cache.Cache) {
                                ibd.persistent.cache.Cache<?> tableCache = (ibd.persistent.cache.Cache<?>) cache;
                                java.lang.reflect.Field cacheSizeField = tableCache.getClass().getSuperclass().getDeclaredField("cacheSize");
                                cacheSizeField.setAccessible(true);
                                newCacheSizeInPages = cacheSizeField.getInt(tableCache);
                            }
                        } catch (Exception ex) {
                            // ignore, keep previous value
                        }
                    }
                    String message = String.format("Cache size for table '%s' has been set to %d bytes.\nThe number of pages has been set to %d pages.",
                        tableCell.getName(), newCacheSize, newCacheSizeInPages);
                    if (result == 0) { // If Reset Default was used
                        message += "\n(Set to default size)";
                    }
                    JOptionPane.showMessageDialog(
                        this,
                        message,
                        "Cache Size Updated",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid number.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error setting cache size: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    /**
     * Sets the cache size for a specific table
     */
    private void setTableCacheSize(TableCell tableCell, int newCacheSize) {
        try {
            if (tableCell.getTable() instanceof ibd.table.btree.BTreeTable) {
                ibd.table.btree.BTreeTable btreeTable = (ibd.table.btree.BTreeTable) tableCell.getTable();

                // Access the cache field using reflection
                java.lang.reflect.Field cacheField = btreeTable.getClass().getDeclaredField("cache");
                cacheField.setAccessible(true);
                Object cache = cacheField.get(btreeTable);

                if (cache != null && cache instanceof ibd.persistent.cache.Cache) {
                    ibd.persistent.cache.Cache<?> tableCache = (ibd.persistent.cache.Cache<?>) cache;

                    // Update cacheSizeBytes field
                    java.lang.reflect.Field cacheSizeBytesField = tableCache.getClass().getSuperclass().getDeclaredField("cacheSizeBytes");
                    cacheSizeBytesField.setAccessible(true);
                    cacheSizeBytesField.setInt(tableCache, newCacheSize);

                    // Update cacheSize field (calculated from cacheSizeBytes / pageSize)
                    java.lang.reflect.Field cacheSizeField = tableCache.getClass().getSuperclass().getDeclaredField("cacheSize");
                    cacheSizeField.setAccessible(true);
                    int pageSize = tableCache.getPageSize();
                    int newCacheSizeInPages = newCacheSize / pageSize;
                    if (newCacheSizeInPages <= 0) {
                        newCacheSizeInPages = 1; // Minimum cache size of 1 page
                    }
                    cacheSizeField.setInt(tableCache, newCacheSizeInPages);
                    // Clear the cache to apply the new size settings
                    java.lang.reflect.Method clearCacheMethod = tableCache.getClass().getDeclaredMethod("clearCache");
                    clearCacheMethod.setAccessible(true);
                    clearCacheMethod.invoke(tableCache);

                    // Re-initialize the cache with new settings if it's an LRU cache
                    if (cache instanceof ibd.persistent.cache.LRUCache) {
                        java.lang.reflect.Method initCacheMethod = tableCache.getClass().getDeclaredMethod("initCache");
                        initCacheMethod.setAccessible(true);
                        initCacheMethod.invoke(tableCache);
                    }

                    /*System.out.println(String.format("Updated cache size for table '%s' to %d bytes (%d pages)",
                                     tableCell.getName(), newCacheSize, newCacheSizeInPages));*/
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        String.format("Table '%s' does not have an active cache to modify.", tableCell.getName()),
                        "No Cache Found",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        } catch (Exception ex) {
            System.err.println("Error updating table cache size: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error updating cache size: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Resets the cache for a specific table
     */
    private void resetTableCache(TableCell tableCell) {
        try {
            if (tableCell.getTable() instanceof ibd.table.btree.BTreeTable) {
                ibd.table.btree.BTreeTable btreeTable = (ibd.table.btree.BTreeTable) tableCell.getTable();

                // Access the cache field using reflection
                java.lang.reflect.Field cacheField = btreeTable.getClass().getDeclaredField("cache");
                cacheField.setAccessible(true);
                Object cache = cacheField.get(btreeTable);
                if (cache != null && cache instanceof ibd.persistent.cache.Cache) {
                    ibd.persistent.cache.Cache<?> tableCache = (ibd.persistent.cache.Cache<?>) cache;
                    // Use reflection to access the protected clearCache method
                    java.lang.reflect.Method clearCacheMethod = tableCache.getClass().getDeclaredMethod("clearCache");
                    clearCacheMethod.setAccessible(true);
                    clearCacheMethod.invoke(tableCache);

                    JOptionPane.showMessageDialog(
                        this,
                        String.format("Cache for table '%s' has been reset successfully.", tableCell.getName()),
                        "Cache Reset",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    //System.out.println(String.format("Reset cache for table '%s'", tableCell.getName()));
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        String.format("Table '%s' does not have an active cache to reset.", tableCell.getName()),
                        "No Cache Found",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error resetting cache: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            System.err.println("Error resetting table cache: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    /**
     * Shows information about cache functionality for a specific table
     */
    private void showCacheInfo(TableCell tableCell) {
        try {
            if (tableCell.getTable() instanceof ibd.table.btree.BTreeTable) {
                ibd.table.btree.BTreeTable btreeTable = (ibd.table.btree.BTreeTable) tableCell.getTable();

                // Access the cache field using reflection
                java.lang.reflect.Field cacheField = btreeTable.getClass().getDeclaredField("cache");
                cacheField.setAccessible(true);
                Object cache = cacheField.get(btreeTable);

                if (cache != null && cache instanceof ibd.persistent.cache.Cache) {
                    ibd.persistent.cache.Cache<?> tableCache = (ibd.persistent.cache.Cache<?>) cache;

                    // Get cache information
                    int pageSize = tableCache.getPageSize();

                    // Get current cache size in bytes and pages
                    java.lang.reflect.Field cacheSizeBytesField = tableCache.getClass().getSuperclass().getDeclaredField("cacheSizeBytes");
                    cacheSizeBytesField.setAccessible(true);
                    int cacheSizeBytes = cacheSizeBytesField.getInt(tableCache);

                    java.lang.reflect.Field cacheSizeField = tableCache.getClass().getSuperclass().getDeclaredField("cacheSize");
                    cacheSizeField.setAccessible(true);
                    int cacheSizePages = cacheSizeField.getInt(tableCache);

                    // Calculate how many complete pages fit in the current cache size
                    int calculatedPages = cacheSizeBytes / pageSize;

                    StringBuilder infoMessage = new StringBuilder();
                    infoMessage.append("<html><body style='width: 400px;'>");
                    infoMessage.append("<h3>Cache Information for Table: ").append(tableCell.getName()).append("</h3>");
                    infoMessage.append("<hr>");

                    infoMessage.append("<b>Page Size:</b> ").append(pageSize).append(" bytes<br><br>");

                    infoMessage.append("<b>Current Cache Configuration:</b><br>");
                    infoMessage.append("• Cache Size (bytes): ").append(cacheSizeBytes).append(" bytes<br>");
                    infoMessage.append("• Cache Size (pages): ").append(cacheSizePages).append(" pages<br><br>");

                    infoMessage.append("<b>How Cache Size Calculation Works:</b><br>");
                    infoMessage.append("When you set a cache size in bytes, the system calculates how many complete pages can fit:<br>");
                    infoMessage.append("• Number of pages = Cache size ÷ Page size<br>");
                    infoMessage.append("• Example: ").append(cacheSizeBytes).append(" ÷ ").append(pageSize).append(" = ").append(calculatedPages).append(" pages<br>");
                    infoMessage.append("• The result is rounded down to the nearest whole number<br>");
                    infoMessage.append("• Minimum cache size is 1 page (").append(pageSize).append(" bytes)<br><br>");

                    infoMessage.append("<b>Cache Functions:</b><br>");
                    infoMessage.append("• <i>Set Cache Size:</i> Change the cache size in bytes<br>");
                    infoMessage.append("• <i>Reset Cache:</i> Clear all cached pages from memory<br>");
                    infoMessage.append("• Default cache size: 5,000,000 bytes<br>");

                    infoMessage.append("</body></html>");

                    JOptionPane.showMessageDialog(
                        this,
                        infoMessage.toString(),
                        "Cache Information",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        String.format("Table '%s' does not have an active cache.", tableCell.getName()),
                        "No Cache Found",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error retrieving cache information: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            System.err.println("Error showing cache info: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Removes a table from the tables panel and updates the graph
     */
    public static void removeTable(String tableName, mxCell cell) {
        double yPosOfRemovedCell = cell.getGeometry().getY();
        tables.remove(tableName);

        updateTablesPanel(cell, yPosOfRemovedCell);
        removeTableReferences(tableName);
    }

    /**
     * Updates the tables panel by removing the specified cell and adjusting positions of remaining cells
     */
    private static void updateTablesPanel(mxCell cell, double yPosOfRemovedCell) {
        tablesGraph.getModel().beginUpdate();
        try {
            tablesGraph.removeCells(new Object[]{cell});
            adjustRemainingCellsPosition(yPosOfRemovedCell);
            decrementCurrentTableYPosition(40);
            refreshTablesPanelUI();
        } finally {
            tablesGraph.getModel().endUpdate();
        }
    }

    /**
     * Adjusts the position of remaining cells after a table is removed
     * Moves all cells below the removed cell up by 40 pixels
     */
    private static void adjustRemainingCellsPosition(double yPosOfRemovedCell) {
        for (Object c : tablesGraph.getChildVertices(tablesGraph.getDefaultParent())) {
            if (c instanceof mxCell currentCell &&
                currentCell.getGeometry().getY() > yPosOfRemovedCell) {
                tablesGraph.moveCells(new Object[]{currentCell}, 0, -40);
            }
        }
    }

    /**
     * Refreshes the tables panel UI after a table is removed or modified
     */
    private static void refreshTablesPanelUI() {
        tablesGraph.refresh();
        tablesPanel.revalidate();
        tablesPanel.repaint();
    }

    /**
     * Removes all references to a table in the main graph
     * This method is called when a table is removed from the tables panel
     */
    private static void removeTableReferences(String tableName) {
        graph.getModel().beginUpdate();
        try {
            List<Object> cellsToRemove = findTableCellsToRemove(tableName);
            for (Object cell : cellsToRemove) {
                if (cell instanceof mxCell) {
                    commandController.execute(new RemoveCellCommand(new AtomicReference<>((mxCell)cell)));
                }
            }

            graph.removeCells(cellsToRemove.toArray(new Object[0]));
            graph.refresh();
        } finally {
            graph.getModel().endUpdate();
        }
    }

    /**
     * Finds all cells in the main graph that reference the specified table name
     * and returns them as a list of objects to be removed
     */
    private static List<Object> findTableCellsToRemove(String tableName) {
        List<Object> cellsToRemove = new ArrayList<>();
        for (Object vertex : graph.getChildVertices(graph.getDefaultParent())) {
            if (vertex instanceof mxCell) {
                mxCell mainGraphCell = (mxCell) vertex;
                Optional<Cell> optionalCell = CellUtils.getActiveCell(mainGraphCell);
                if (optionalCell.isPresent() && optionalCell.get() instanceof TableCell) {
                    TableCell tableCell = (TableCell) optionalCell.get();
                    if (tableName.equals(tableCell.getName())) {
                        cellsToRemove.add(mainGraphCell);
                    }
                }
            }
        }
        return cellsToRemove;
    }

    /**
     * Renames a table in the tables map and updates all references in the graph
     * This method is called when a table is renamed in the tables panel
     */
    public static void renameTable(String currentName, String newName, mxCell cell) {
        updateTableName(currentName, newName);
        updateTableCell(newName, cell);
        updateAllReferences(currentName, newName);
    }

    /**
     * Updates the name of a table in the tables map
     * This method is called when a table is renamed
     */
    private static void updateTableName(String currentName, String newName) {
        TableCell tableCell = tables.remove(currentName);
        if (tableCell != null) {
            tableCell.setName(newName);
            tables.put(newName, tableCell);
        }
    }

    /**
     * Updates the name of a table in the tables graph and adjusts its geometry
     * This method is called when a table is renamed
     */
    private static void updateTableCell(String newName, mxCell cell) {
        tablesGraph.getModel().beginUpdate();
        try {
            tablesGraph.getModel().setValue(cell, newName);
            updateCellGeometry(cell, newName);
            refreshUI(cell);
        } finally {
            tablesGraph.getModel().endUpdate();
        }
    }

    /**
     * Updates the geometry of a cell after it has been renamed
     * This method adjusts the width of the cell based on the new name
     */
    private static void updateCellGeometry(mxCell cell, String newName) {
        mxGeometry geometry = cell.getGeometry();
        if (geometry != null) {
            int textWidth = CellUtils.getCellWidth(newName);
            geometry.setWidth(Math.max(ConstantController.TABLE_CELL_WIDTH, textWidth));
        }
    }

    /**
     * Refreshes the UI after a table has been renamed
     * This method updates the tables graph and revalidates the tables panel
     */
    private static void refreshUI(mxCell cell) {
        tablesGraph.refresh();
        tablesGraph.repaint();
        if (cell.getGeometry().getWidth() == ConstantController.TABLE_CELL_WIDTH) {
            tablesPanel.revalidate();
        }
    }

    /**
     * Updates all references to a table after it has been renamed
     * This method updates both visual and structural references in the graph
     */
    private static void updateAllReferences(String oldName, String newName) {
        refreshVisualTableReferences(oldName, newName);
        updateStructuralTableReferences(oldName, newName);
    }

    /**
     * Checks if a new table name is valid
     * A valid name is non-null, non-empty, and not already in use
     */
    public static boolean isValidNewTableName(String newName) {
        return newName != null && !newName.trim().isEmpty() && !tables.containsKey(newName);
    }

    /**
     * Refreshes all visual references to a table after it has been renamed
     * This method updates the display names of TableCells and OperationCells in the graph
     */
    public static void refreshVisualTableReferences(String oldName, String newName) {
        for (Cell cell : CellUtils.getActiveCells().values()) {
            if (cell instanceof TableCell tableCell && tableCell.getName().equals(oldName)) {
                tableCell.asOperator(newName);
                adjustCellWidth(tableCell.getJCell(), newName);
            }
            if (cell instanceof OperationCell opCell) {
                opCell.getColumns().replaceAll(col -> {
                    if (col.SOURCE.equals(oldName)) {
                        return Column.changeSourceColumn(col, newName);
                    }
                    return col;
                });
                String displayName = opCell.getType().symbol;
                if (opCell.getArguments() != null && !opCell.getArguments().isEmpty()) {
                    displayName += "[" + String.join(", ", opCell.getArguments()) + "]";
                }
                if (opCell.getAlias() != null && !opCell.getAlias().isEmpty()) {
                    displayName += " AS " + opCell.getAlias();
                }
                opCell.getJCell().setValue(displayName);
                adjustCellWidth(opCell.getJCell(), displayName);
            }
        }
        MainFrame.getGraph().refresh();
        graph.clearSelection();
    }

    /**
     * Adjusts the width of a cell based on the text it contains
     * This is used to ensure that table names fit within their cells in the graph
     */
    private static void adjustCellWidth(mxCell cell, String text) {
        if (cell != null && cell.getGeometry() != null) {
            int textWidth = CellUtils.getCellWidth(text);
            int finalWidth = Math.max(ConstantController.TABLE_CELL_WIDTH, textWidth);
            cell.getGeometry().setWidth(finalWidth);
        }
    }

    /**
     * Updates all structural references to a table after it has been renamed
     * This method updates TableCells and OperationCells in the main graph
     */
    private static void updateStructuralTableReferences(String oldName, String newName) {
        graph.getModel().beginUpdate();
        try {
            for (Object vertex : graph.getChildVertices(graph.getDefaultParent())) {
                if (vertex instanceof mxCell) {
                    mxCell mainGraphCell = (mxCell) vertex;
                    Optional<Cell> optionalCell = CellUtils.getActiveCell(mainGraphCell);

                    if (optionalCell.isPresent()) {
                        Cell cellToUpdate = optionalCell.get();

                        if (cellToUpdate instanceof TableCell tableCellToRename) {
                            if (oldName.equals(tableCellToRename.getName())) {
                                tableCellToRename.setName(newName);
                                mainGraphCell.setValue(newName);
                            }
                        }
                        else if (cellToUpdate instanceof OperationCell operationCell) {
                            if (operationCell.getArguments() != null) {
                                List<String> updatedArguments = new ArrayList<>();
                                for (String arg : operationCell.getArguments()) {
                                    if (arg != null) {
                                        String updatedArg = arg.replaceAll("\\b" + oldName + "\\.", newName + ".");
                                        updatedArg = updatedArg.replaceAll("\\b" + oldName + "\\b", newName);
                                        updatedArguments.add(updatedArg);
                                    } else {
                                        updatedArguments.add(arg);
                                    }
                                }
                                operationCell.setArguments(updatedArguments);

                                String displayName = operationCell.getType().getFormattedDisplayName();
                                if (!updatedArguments.isEmpty()) {
                                    displayName += "[" + String.join(", ", updatedArguments) + "]";
                                }
                                if (operationCell.getAlias() != null && !operationCell.getAlias().isEmpty()) {
                                    displayName += " AS " + operationCell.getAlias();
                                }
                                mainGraphCell.setValue(displayName);
                            }

                            if (operationCell.getAlias() != null && operationCell.getAlias().equals(oldName)) {
                                operationCell.setAlias(newName);
                            }

                            try {
                                TreeUtils.recalculateContent(operationCell);
                            } catch (Exception e) {
                                System.err.println("Erro ao recalcular conteúdo: " + e.getMessage());
                            }
                        }

                        List<Column> updatedColumns = new ArrayList<>();
                        for (Column column : cellToUpdate.getColumns()) {
                            if (column.SOURCE.equals(oldName)) {
                                updatedColumns.add(new Column(newName, column.NAME));
                            } else {
                                updatedColumns.add(column);
                            }
                        }
                    }
                }
            }
        } finally {
            graph.getModel().endUpdate();
            graph.refresh();
        }
    }

    /*
     * Suggests a new table name based on the original name
     * If the original name already exists, appends a counter to create a unique name
     */
    public static String suggestNewTableName(String originalName) {
        if (!tables.containsKey(originalName)) {
            return originalName;
        }

        int counter = 1;
        String newName;
        do {
            newName = originalName + "_" + counter;
            counter++;
        } while (tables.containsKey(newName));

        return newName;
    }

    /**
     * Shows a dialog to resolve a table name conflict
     * If the original name already exists, suggests a new name or allows the user to choose another name
     */
    public static String showTableNameConflictDialog(String originalName) {
        String suggestedName = suggestNewTableName(originalName);
        Object[] options = {"Use suggested name", "Choose another name", "Cancel"};

        int choice = JOptionPane.showOptionDialog(
            null,
            String.format("A table with the name already exists '%s'.\nSuggested name: %s",
                originalName, suggestedName),
            "Duplicate Table Name",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == JOptionPane.YES_OPTION) {
            return suggestedName;
        } else if (choice == JOptionPane.NO_OPTION) {
            String newName = JOptionPane.showInputDialog(
                null,
                "Enter a new name for the table:",
                "Rename Table",
                JOptionPane.PLAIN_MESSAGE
            );

            if (newName != null && !newName.trim().isEmpty()) {
                while (tables.containsKey(newName)) {
                    newName = JOptionPane.showInputDialog(
                        null,
                        "This name already exists. Enter another name:",
                        "Duplicate Name",
                        JOptionPane.WARNING_MESSAGE
                    );
                    if (newName == null) {
                        return null;
                    }
                }
                return newName;
            }
        }
        return null;
    }

    /**
     * Resolves a table name conflict by checking if the name already exists
     * If it does, shows a dialog to suggest a new name or allow the user to choose another name
     */
    public static String resolveTableNameConflict(String tableName) {
        if (!tables.containsKey(tableName)) {
            return tableName;
        }
        return showTableNameConflictDialog(tableName);
    }
}
