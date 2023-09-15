package entities;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import controllers.MainController;

import entities.cells.Cell;
import entities.utils.cells.CellUtils;

public class Tree {

    private int index = 0;

    {
        while (MainController.getTrees().containsKey(this.index)) {
            this.index++;
        }

        MainController.getTrees().put(this.index, this);
    }

    public Tree() {

    }

    public List<Cell> getLeaves() {
        return this
            .getCells()
            .stream()
            .filter(cell -> !cell.hasParents())
            .toList();
    }

    public Cell getRoot() {
        return this
            .getCells()
            .stream()
            .filter(cell -> !cell.hasChild())
            .findAny()
            .orElseThrow();
    }

    public Set<Cell> getCells() {
        return CellUtils
            .getActiveCells()
            .values()
            .stream()
            .filter(cell -> cell.getTree() == this)
            .collect(Collectors.toSet());
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        string.append(this.index).append(": ");

        for (Cell cell : this.getCells()) {
            string.append(cell.getName()).append(", ");
        }

        return string.toString();
    }
}
