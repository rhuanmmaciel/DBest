package entities.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mxgraph.model.mxICell;

import controller.MainController;

import entities.Tree;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;

public class TreeUtils {

    private TreeUtils() {

    }

    private static final Map<mxICell, Cell> cells = CellUtils.getActiveCells();

    private static final Map<Integer, Tree> trees = MainController.getTrees();

    public static void updateTreesAboveAndBelow(List<Cell> parents, OperationCell child) {
        if (child == null && (parents == null || parents.isEmpty())) return;

        Tree previousTree = null;

        if (child != null) {
            previousTree = child.getTree();

            Tree childTree = new Tree();

            setTreeFromLeavesToRoot(child.getSources(), childTree);

            recalculateContent(child);
        }

        deleteTree(previousTree);

        if (parents != null && !parents.isEmpty()) {
            for (Cell parent : parents) {
                previousTree = parent.getTree();
                Tree parentTree = new Tree();
                setTreeFromLeavesToRoot(parent.getSources(), parentTree);
                deleteTree(previousTree);
            }
        }
    }

    private static void setTreeFromLeavesToRoot(List<Cell> level, Tree tree) {
        while (!level.isEmpty()) {
            Set<Cell> children = new HashSet<>();

            for (Cell cell : level) {
                cell.setTree(tree);

                if (cell.hasChild()) {
                    children.add(cell.getChild());
                }
            }

            level.clear();
            level.addAll(children);
        }
    }

    public static void updateTree(Cell cell) {
        Set<Tree> trees = new HashSet<>();
        Tree tree = cell.getTree();
        List<Cell> level = cell.getSources();

        while (!level.isEmpty()) {
            Set<Cell> children = new HashSet<>();

            for (Cell auxCell : level) {
                trees.add(auxCell.getTree());

                auxCell.setTree(tree);

                if (auxCell.hasChild()) {
                    children.add(auxCell.getChild());
                }
            }

            level.clear();
            level.addAll(children);
        }

        for (Tree auxTree : trees) {
            updateTree(auxTree);
        }
    }

    private static void updateTree(Tree tree) {
        int counter = 0;

        for (Cell cell : cells.values()) {
            if (cell.getTree() != null && cell.getTree().equals(tree)) {
                counter++;
            }
        }

        if (counter <= 0) {
            trees.remove(tree.getIndex());
        }
    }

    public static void deleteTree(Tree tree) {
        if (tree == null) return;

        trees.remove(tree.getIndex());
    }

    public static void recalculateContent(Tree tree) {
        if (tree == null) return;

        List<Cell> level = tree.getLeaves();

        while (!level.isEmpty()) {
            Set<Cell> children = new HashSet<>();

            for (Cell cell : level) {
                if (cell instanceof OperationCell operationCell) {
                    operationCell.updateOperation();
                }

                if (cell.hasChild()) {
                    children.add(cell.getChild());
                }
            }

            level.clear();
            level.addAll(children);
        }
    }

    public static void recalculateContent(OperationCell cell) {
        if (cell == null) return;

        OperationCell currentCell = cell;

        while (currentCell != null) {
            currentCell.updateOperation();
            currentCell = currentCell.getChild();
        }
    }
}
