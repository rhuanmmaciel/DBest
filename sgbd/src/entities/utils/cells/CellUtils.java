package entities.utils.cells;

import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

import controllers.MainController;

import entities.Tree;
import entities.cells.CSVTableCell;
import entities.cells.Cell;
import entities.cells.FYITableCell;
import entities.cells.OperationCell;
import entities.cells.TableCell;

import entities.utils.TreeUtils;

import enums.FileType;

import gui.frames.DataFrame;
import gui.frames.main.MainFrame;

import sgbd.table.Table;

public class CellUtils extends MainController {

    public static void activateInactiveJCell(mxCell inactiveJCell) {
        if (inactiveJCell == null) return;

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

        mxGraph graph = MainFrame.getGraph();

        graph.getModel().beginUpdate();

        try {
            graph.addCell(inactiveJCell);
            optionalInactiveCell.ifPresent(cell -> CellRepository.addCell(inactiveJCell, cell));
        } finally {
            graph.getModel().endUpdate();
        }

        graph.refresh();
    }

    public static void desactivateActiveJCell(mxCell activeJCell) {
        if (activeJCell == null) return;

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

        mxGraph graph = MainFrame.getGraph();

        graph.getModel().beginUpdate();

        try {
            graph.removeCells(new Object[]{activeJCell}, true);
            CellRepository.removeCell(activeJCell);
        } finally {
            graph.getModel().endUpdate();
        }

        graph.refresh();
    }

    public static void removeCell(mxCell jCell) {
        if (jCell == null) return;

        mxGraph graph = MainFrame.getGraph();
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

        graph.getModel().beginUpdate();

        try {
            graph.removeCells(new Object[]{jCell}, true);
            CellRepository.removeCell(jCell);
        } finally {
            graph.getModel().endUpdate();
        }

        graph.refresh();
    }

    public static void removeCell(AtomicReference<mxCell> jCell) {
        removeCell(jCell.get());
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
        int answer = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja deletar todas as árvores?", "Confirmação", JOptionPane.YES_NO_OPTION);

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
            JOptionPane.showMessageDialog(null, "A operação possui erros", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void verifyCell(mxCell cell, mxCell ghostCell) {
        if (cell == null || cell == ghostCell || CellRepository.activeCellsContainsKey(cell)) return;

        Object parent = graph.getDefaultParent();
        Object[] vertices = mxGraphModel.getChildVertices(graph.getModel(), parent);

        for (Object vertex : vertices) {
            if (!CellRepository.activeCellsContainsKey((mxCell) vertex)) {
                cell = (mxCell) vertex;
            }
        }

        TableCell tableCell = MainController.getTables().get(cell.getValue());
        String cellStyle = cell.getStyle();

        if (cellStyle.equals(FileType.FYI.id)) {
            new FYITableCell((FYITableCell) tableCell, cell);
        } else if (cellStyle.equals(FileType.CSV.id)) {
            new CSVTableCell((CSVTableCell) tableCell, cell);
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
            .map(Cell::getTree)
            .collect(Collectors.toSet());
    }
}
