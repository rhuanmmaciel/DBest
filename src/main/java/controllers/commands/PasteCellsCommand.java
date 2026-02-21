package controllers.commands;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import javax.swing.SwingUtilities;
import controllers.clipboard.Clipboard;
import controllers.clipboard.Clipboard.CopiedItem;
import controllers.clipboard.Clipboard.CopiedItemType;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.CSVTableCell;
import entities.cells.FYITableCell;
import entities.cells.JDBCTableCell;
import entities.cells.MemoryTableCell;
import gui.frames.main.MainFrame;
import entities.utils.cells.CellUtils;
import entities.utils.CoordinatesUtils;
import entities.Coordinates;

import java.awt.*;
import java.awt.MouseInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Command to paste cells and edges from clipboard
 */
public class PasteCellsCommand extends BaseCommand implements UndoableRedoableCommand {

    private static boolean isPasting = false;
    private final mxGraph graph;
    private final Coordinates pasteLocation;
    private Map<mxCell, mxCell> pastedCells;
    private static final double PASTE_OFFSET = 1.0;

    public PasteCellsCommand(Coordinates pasteLocation) {
        this.graph = MainFrame.getGraph();
        this.pasteLocation = pasteLocation;
        this.pastedCells = new HashMap<>();
    }

    public PasteCellsCommand() {
        this(getDefaultPasteLocation());
    }

    public static boolean getIsPasting() {
        return isPasting;
    }

    private static Coordinates getDefaultPasteLocation() {
        try {
            mxGraphComponent graphComponent = MainFrame.getGraphComponent();
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            Point mouseScreenPos = pointerInfo.getLocation();

            SwingUtilities.convertPointFromScreen(mouseScreenPos, graphComponent);

            Point graphControlPos = SwingUtilities.convertPoint(
                    graphComponent,
                    mouseScreenPos,
                    graphComponent.getGraphControl());

            Coordinates canvasCoords = CoordinatesUtils.transformScreenToCanvasCoordinates(
                    graphControlPos.x,
                    graphControlPos.y);

            return new Coordinates(
                    (int) (canvasCoords.x() + PASTE_OFFSET),
                    (int) (canvasCoords.y() + PASTE_OFFSET));

        } catch (Exception e) {
            System.err.println("Error getting paste location: " + e.getMessage());
            return new Coordinates(100, 100);
        }
    }

    @Override
    public void execute() {
        Clipboard clipboard = Clipboard.getInstance();

        if (!clipboard.hasData()) {
            System.out.println("Clipboard is empty");
            return;
        }

        List<CopiedItem> copiedItems = clipboard.getCopiedItems();
        pastedCells.clear();
        graph.getModel().beginUpdate();
        isPasting = true;
        try {
            double offsetX = pasteLocation.x();
            double offsetY = pasteLocation.y();

            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;

            for (CopiedItem item : copiedItems) {
                if (item.getType() == CopiedItemType.CELL) {
                    mxGeometry geometry = item.getJCell().getGeometry();
                    minX = Math.min(minX, geometry.getX());
                    minY = Math.min(minY, geometry.getY());
                }
            }

            if (minX != Double.MAX_VALUE && minY != Double.MAX_VALUE) {
                offsetX = pasteLocation.x() - minX;
                offsetY = pasteLocation.y() - minY;
            }

            for (CopiedItem item : copiedItems) {
                if (item.getType() == CopiedItemType.CELL) {
                    mxCell pastedCell = pasteCellItem(item, offsetX, offsetY);
                    if (pastedCell != null) {
                        pastedCells.put(item.getJCell(), pastedCell);
                    }
                }
            }
            for (CopiedItem item : copiedItems) {
                if (item.getType() == CopiedItemType.EDGE) {
                    pasteEdgeItem(item);
                }
            }
            for (mxCell pastedCell : pastedCells.values()) {
                Cell activeCell = CellUtils.getActiveCell(pastedCell).orElse(null);
                if (activeCell instanceof OperationCell operationCell) {
                    rebuildParentChildRelationships(operationCell);
                }
            }

            recalculateOperationCellsBottomUp();

            System.out.println("Pasted " + copiedItems.size() + " item(s) from clipboard at (" + pasteLocation.x()
                    + ", " + pasteLocation.y() + ")");

        } finally {
            isPasting = false;
            graph.getModel().endUpdate();
        }
    }

    private mxCell pasteCellItem(CopiedItem item, double offsetX, double offsetY) {
        try {
            mxCell originalJCell = item.getJCell();
            Cell originalActiveCell = item.getActiveCell();

            mxGeometry originalGeometry = originalJCell.getGeometry();
            mxGeometry newGeometry = new mxGeometry(
                    originalGeometry.getX() + offsetX,
                    originalGeometry.getY() + offsetY,
                    originalGeometry.getWidth(),
                    originalGeometry.getHeight());

            mxCell pastedJCell;
            Cell newActiveCell;

            if (originalActiveCell instanceof OperationCell) {
                newActiveCell = originalActiveCell.copy();
                pastedJCell = newActiveCell.getJCell();

                pastedJCell.setGeometry(newGeometry);

                graph.addCell(pastedJCell);

            } else {
                pastedJCell = (mxCell) graph.insertVertex(
                        graph.getDefaultParent(),
                        null, // Let graph generate ID
                        originalJCell.getValue(),
                        newGeometry.getX(),
                        newGeometry.getY(),
                        newGeometry.getWidth(),
                        newGeometry.getHeight(),
                        originalJCell.getStyle());

                if (originalActiveCell instanceof CSVTableCell csvTableCell) {
                    newActiveCell = new CSVTableCell(pastedJCell, csvTableCell.getName(), csvTableCell.getTable(),
                            csvTableCell.getHeaderFile());
                    newActiveCell.setAlias(csvTableCell.getAlias());
                } else if (originalActiveCell instanceof FYITableCell fyiTableCell) {
                    newActiveCell = new FYITableCell(pastedJCell, fyiTableCell.getName(), fyiTableCell.getTable(),
                            fyiTableCell.getHeaderFile());
                    newActiveCell.setAlias(fyiTableCell.getAlias());
                } else if (originalActiveCell instanceof JDBCTableCell jdbcTableCell) {
                    newActiveCell = new JDBCTableCell(pastedJCell, jdbcTableCell.getName(), jdbcTableCell.getTable(),
                            jdbcTableCell.getHeaderFile());
                    newActiveCell.setAlias(jdbcTableCell.getAlias());
                } else if (originalActiveCell instanceof MemoryTableCell memoryTableCell) {
                    newActiveCell = new MemoryTableCell(memoryTableCell, pastedJCell);
                    newActiveCell.setAlias(memoryTableCell.getAlias());
                } else {
                    throw new IllegalStateException("Unknown table cell type: " + originalActiveCell.getClass());
                }

                CellUtils.addCell(pastedJCell, newActiveCell);
            }

            return pastedJCell;

        } catch (Exception e) {
            System.err.println("Error pasting cell: " + e.getMessage());
            return null;
        }
    }

    private void pasteEdgeItem(CopiedItem item) {
        try {
            mxCell originalEdge = item.getJCell();
            mxCell originalSource = (mxCell) originalEdge.getSource();
            mxCell originalTarget = (mxCell) originalEdge.getTarget();

            mxCell pastedSource = pastedCells.get(originalSource);
            mxCell pastedTarget = pastedCells.get(originalTarget);

            if (pastedSource != null && pastedTarget != null) {
                Optional<Cell> targetCell = CellUtils.getActiveCell(pastedTarget);
                if (targetCell.isPresent() && !targetCell.get().canBeChild()) {
                    System.err.println("Cannot paste edge: target cell cannot be a child (tables cannot be edge targets)");
                    return;
                }
                
                graph.insertEdge(
                        graph.getDefaultParent(),
                        null, // Let graph generate ID
                        originalEdge.getValue(),
                        pastedSource,
                        pastedTarget,
                        originalEdge.getStyle());
            }

        } catch (Exception e) {
            System.err.println("Error pasting edge: " + e.getMessage());
        }
    }

    /**
     * Rebuilds parent-child relationships for an OperationCell based on the graph
     * edges
     */
    private void rebuildParentChildRelationships(OperationCell operationCell) {
        try {
            mxCell jCell = operationCell.getJCell();

            Object[] incomingEdges = graph.getIncomingEdges(jCell);

            operationCell.removeParents();

            for (Object edgeObj : incomingEdges) {
                if (edgeObj instanceof mxCell edge) {
                    mxCell sourceCell = (mxCell) edge.getSource();

                    Cell parentActiveCell = CellUtils.getActiveCell(sourceCell).orElse(null);
                    if (parentActiveCell != null) {
                        operationCell.addParent(parentActiveCell);
                        parentActiveCell.setChild(operationCell);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error rebuilding parent-child relationships: " + e.getMessage());
        }
    }

    /**
     * Recalculates operation cells in bottom-up order to ensure parent operations
     * exist
     * before building child operations
     */
    private void recalculateOperationCellsBottomUp() {
        // Collect all pasted operation cells
        List<OperationCell> operationCells = new ArrayList<>();
        for (mxCell pastedCell : pastedCells.values()) {
            Cell activeCell = CellUtils.getActiveCell(pastedCell).orElse(null);
            if (activeCell instanceof OperationCell operationCell) {
                operationCells.add(operationCell);
            }
        }

        if (operationCells.isEmpty()) {
            return;
        }

        Set<OperationCell> processed = new HashSet<>();
        boolean madeProgress = true;

        while (madeProgress && processed.size() < operationCells.size()) {
            madeProgress = false;

            for (OperationCell operationCell : operationCells) {
                if (processed.contains(operationCell)) {
                    continue;
                }

                boolean allParentsReady = true;
                for (Cell parent : operationCell.getParents()) {
                    if (parent instanceof OperationCell parentOp) {
                        if (!processed.contains(parentOp)) {
                            allParentsReady = false;
                            break;
                        }
                    }
                }

                if (allParentsReady) {
                    try {
                        operationCell.updateOperation();
                        processed.add(operationCell);
                        madeProgress = true;
                        System.out.println("Updated operation for: " + operationCell.getName());
                    } catch (Exception e) {
                        System.err.println(
                                "Error updating operation for " + operationCell.getName() + ": " + e.getMessage());
                        processed.add(operationCell);
                        madeProgress = true;
                    }
                }
            }
        }

        // Report any unprocessed cells
        for (OperationCell operationCell : operationCells) {
            if (!processed.contains(operationCell)) {
                System.err.println("Warning: Could not process operation cell: " + operationCell.getName());
            }
        }
    }

    @Override
    public void undo() {
        graph.getModel().beginUpdate();
        try {
            // Remove all pasted cells and edges
            Object[] cellsToRemove = pastedCells.values().toArray();
            graph.removeCells(cellsToRemove, true);

            System.out.println("Undid paste operation");

        } finally {
            graph.getModel().endUpdate();
        }
    }

    @Override
    public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Paste Cells Command";
    }
}
