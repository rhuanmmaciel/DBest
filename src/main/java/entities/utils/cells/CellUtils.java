package entities.utils.cells;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;
import com.mxgraph.view.mxGraph;
import controllers.ConstantController;
import controllers.MainController;
import entities.Tree;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import entities.utils.TreeUtils;
import gui.frames.DataFrame;
import gui.frames.main.MainFrame;

import javax.swing.*;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CellUtils extends MainController {

    public static int getCellWidth(mxCell jCell){

        JLabel label = new JLabel();
        Font font = new Font("Arial", Font.PLAIN, 12);
        label.setFont(font);

        String text = (String) jCell.getValue();
        FontMetrics fontMetrics = label.getFontMetrics(font);

        return fontMetrics.stringWidth(text);

    }

    public static void activateInactiveJCell(mxGraph jGraph, mxCell inactiveJCell) {
        if (jGraph == null || inactiveJCell == null) return;

        Optional<Cell> optionalInactiveCell = CellRepository.getInactiveCell(inactiveJCell);
        List<Cell> parents = new ArrayList<>();
        OperationCell child = null;

        if (optionalInactiveCell.isPresent()) {
            Cell cell = optionalInactiveCell.get();

            if (cell.hasChild()) {
                cell.getChild().addParent(cell);
            }

            if (cell instanceof OperationCell operationCell) {
                parents.addAll(operationCell.getParents());

                for (Cell parent : operationCell.getParents()) {
                    parent.setChild(operationCell);
                }
            }
        } else {
            Optional<Cell> optionalChild = CellRepository.getInactiveCell(inactiveJCell.getTarget());

            if (optionalChild.isPresent()) {
                child = (OperationCell) optionalChild.get();
            }

            Optional<Cell> optionalParent = CellRepository.getInactiveCell(inactiveJCell.getSource());

            if (optionalParent.isPresent()) {
                Cell parent = optionalParent.get();

                parents.add(parent);
                parent.setChild(child);
            }
        }

        TreeUtils.updateTreesAboveAndBelow(parents, child);

        jGraph.getModel().beginUpdate();

        try {
            jGraph.addCell(inactiveJCell);
            optionalInactiveCell.ifPresent(cell -> CellRepository.addCell(inactiveJCell, cell));
        } finally {
            jGraph.getModel().endUpdate();
        }

        jGraph.refresh();
    }

    public static void markCell(mxCell cell){

        String style = cell.getStyle();

        style = mxStyleUtils.setStyle(style, mxConstants.STYLE_STROKECOLOR, "#FF0000");
        style = mxStyleUtils.setStyle(style, mxConstants.STYLE_STROKEWIDTH, "1");
        MainFrame.getGraph().getModel().setStyle(cell, style);
        CellUtils.getActiveCell(cell).get().markCell();

    }

    public static void unmarkCell(mxCell jCell){

        Cell cell = CellUtils.getActiveCell(jCell).get();
        MainFrame.getGraph().getModel().setStyle(jCell, cell.getStyle());
        cell.unmarkCell();

    }

    public static void deactivateActiveJCell(mxGraph jGraph, mxCell activeJCell) {
        if (jGraph == null || activeJCell == null) return;

        Optional<Cell> optionalActiveCell = CellRepository.getActiveCell(activeJCell);
        List<Cell> parents = new ArrayList<>();
        OperationCell child = null;

        if (optionalActiveCell.isPresent()) {
            Cell cell = optionalActiveCell.get();

            if (cell.hasChild()) {
                child = cell.getChild();
                child.removeParent(cell);
            }

            if (cell instanceof OperationCell operationCell) {
                parents.addAll(operationCell.getParents());

                for (Cell parent : operationCell.getParents()) {
                    parent.setChild(null);
                }
            }
        } else {
            Optional<Cell> optionalChild = CellRepository.getActiveCell(activeJCell.getTarget());

            if (optionalChild.isPresent()) {
                child = (OperationCell) optionalChild.get();
            }

            Optional<Cell> optionalParent = CellRepository.getActiveCell(activeJCell.getSource());

            if (optionalParent.isPresent()) {
                Cell parent = optionalParent.get();

                parents.add(parent);
                parent.setChild(null);
            }
        }

        TreeUtils.updateTreesAboveAndBelow(parents, child);

        jGraph.getModel().beginUpdate();

        try {
            jGraph.removeCells(new Object[]{activeJCell}, true);
            CellRepository.removeCell(activeJCell);
        } finally {
            jGraph.getModel().endUpdate();
        }

        jGraph.refresh();
    }

    public static void removeCell(mxGraph jGraph, mxCell jCell) {
        if (jGraph == null || jCell == null) return;

        Optional<Cell> optionalCell = CellRepository.getActiveCell(jCell);
        List<Cell> parents = new ArrayList<>();
        OperationCell child = null;

        if (optionalCell.isPresent()) {
            Cell cell = optionalCell.get();

            if (cell.hasChild()) {
                child = cell.getChild();
                child.removeParent(cell);
                cell.setChild(null);
            }

            if (cell instanceof OperationCell operationCell) {
                parents.addAll(operationCell.getParents());

                for (Cell parent : operationCell.getParents()) {
                    parent.setChild(null);
                }

                operationCell.removeParents();
            }
        } else {
            Optional<Cell> optionalChild = CellRepository.getActiveCell(jCell.getTarget());

            if (optionalChild.isPresent()) {
                child = (OperationCell) optionalChild.get();
            }

            Optional<Cell> optionalParent = CellRepository.getActiveCell(jCell.getSource());

            if (optionalParent.isPresent()) {
                Cell parent = optionalParent.get();
                parents.add(parent);

                if (child != null) {
                    child.removeParent(parent);
                }

                parent.setChild(null);
            }
        }

        TreeUtils.updateTreesAboveAndBelow(parents, child);

        jGraph.getModel().beginUpdate();

        try {
            jGraph.removeCells(new Object[]{jCell}, true);
            CellRepository.removeCell(jCell);
        } finally {
            jGraph.getModel().endUpdate();
        }

        jGraph.refresh();
    }

    public static void removeCell(AtomicReference<mxCell> jCell) {
        removeCell(MainFrame.getGraph(), jCell.get());
        jCell.set(null);
    }

    public static void addMovableEdge(MouseEvent mouseEvent, AtomicReference<mxCell> invisibleCellReference, mxCell jCell) {
        invisibleCellReference
            .set((mxCell) MainFrame
                .getGraph()
                .insertVertex(
                    MainFrame.getGraph().getDefaultParent(), "invisible", "",
                    mouseEvent.getX(), mouseEvent.getY(), 80, 30, "invisible"
                )
            );

        MainFrame.getGraph().insertEdge(jCell, null, "", jCell, invisibleCellReference.get());

        invisibleCellReference.get().setGeometry(new mxGeometry(0, 0, 0, 0));
    }

    public static void deleteGraph() {
        int answer = JOptionPane.showConfirmDialog(null, ConstantController.getString("cell.deleteAll"), ConstantController.getString("confirmation"), JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        mxGraph graph = MainFrame.getGraph();

        graph.getModel().beginUpdate();

        try {
            graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
            CellRepository.removeCells();
        } finally {
            graph.getModel().endUpdate();
        }

        graph.refresh();
    }

    public static void showTable(mxCell jCell) {

        Optional<Cell> optionalCell = CellRepository.getActiveCell(jCell);

        if (optionalCell.isEmpty()) return;

        Cell cell = optionalCell.orElse(null);

        if (!(cell instanceof TableCell || ((OperationCell) cell).hasBeenInitialized())) return;

        if (!cell.hasError()) {
            new DataFrame(cell);
        } else {
            JOptionPane.showMessageDialog(null, ConstantController.getString("cell.operationCell.error"), ConstantController.getString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void addCell(mxICell jCell, Cell cell) {
        CellRepository.addCell(jCell, cell);
    }

    public static Optional<Cell> removeCell(mxICell jCell) {
        return CellRepository.removeCell(jCell);
    }

    public static Map<mxICell, Cell> removeCells() {
        return CellRepository.removeCells();
    }

    public static boolean activeCellsContainsKey(mxICell jCell) {
        return CellRepository.activeCellsContainsKey(jCell);
    }

    public static Optional<Cell> getActiveCell(mxICell jCell) {
        return CellRepository.getActiveCell(jCell);
    }

    public static Map<mxICell, Cell> getActiveCells() {
        return CellRepository.getActiveCells();
    }

    public static Optional<Cell> getInactiveCell(mxICell jCell) {
        return CellRepository.getInactiveCell(jCell);
    }

    public static Map<mxICell, Cell> getInactiveCells() {
        return CellRepository.getInactiveCells();
    }

    public static Set<Tree> getActiveCellsTrees() {
        return CellRepository
            .getActiveCells()
            .values()
            .stream()
            .filter(cell -> graph.getModel().contains(cell.getJCell()))
            .map(Cell::getTree)
            .collect(Collectors.toSet());
    }
}
