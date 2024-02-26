package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import controllers.MainController;

import entities.cells.Cell;
import entities.utils.CoordinatesUtils;
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

    public Area getTreeArea() {

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;

        for (Cell cell : this.getCells()) {
            minX = Math.min(cell.getUpperLeftPosition().x(), minX);
            minX = Math.min(cell.getLowerRightPosition().x(), minX);

            maxX = Math.max(cell.getUpperLeftPosition().x(), maxX);
            maxX = Math.max(cell.getLowerRightPosition().x(), maxX);

            minY = Math.min(cell.getUpperLeftPosition().y(), minY);
            minY = Math.min(cell.getLowerRightPosition().y(), minY);

            maxY = Math.max(cell.getUpperLeftPosition().y(), maxY);
            maxY = Math.max(cell.getLowerRightPosition().y(), maxY);
        }

        return new Area(new Coordinates(minX, minY), new Coordinates(maxX, maxY));

    }

    public Coordinates getTreeLayer(){

        List<Cell> currentLayer = new ArrayList<>(List.of(getRoot()));

        int yLayer = 0;
        int xLayer = 1;

        while (!currentLayer.isEmpty()){

            List<Cell> newLayer = new ArrayList<>();

            for(List<Cell> parents : currentLayer.stream().filter(Cell::hasParents).map(Cell::getParents).toList()){

                if(parents.size() == 2) xLayer += 2;
                newLayer.addAll(parents);

            }

            yLayer++;

            currentLayer.clear();
            currentLayer.addAll(newLayer);

        }

        return new Coordinates(xLayer, yLayer);

    }

    public Coordinates getCellLayer(Cell cell){

        List<Cell> currentLayer = new ArrayList<>(List.of(getRoot()));

        int yLayer = 0;
        int xLayer = 1;

        while (!currentLayer.isEmpty()){

            List<Cell> newLayer = new ArrayList<>();

            for(List<Cell> parents : currentLayer.stream().filter(Cell::hasParents).map(Cell::getParents).toList()){

                if(parents.size() == 2) xLayer += 2;
                newLayer.addAll(parents);

            }

            yLayer++;

            currentLayer.clear();
            currentLayer.addAll(newLayer);

        }

        return new Coordinates(xLayer, yLayer);

    }

    public void adjustTreePosition(){



    }

    public void moveTreeToAnEmptySpot(){

        System.out.println(this.hasPositionConflict());

        System.out.println();

    }

    public boolean hasPositionConflict(){

        for(Area currentTreeArea : CellUtils.getActiveCellsTrees().stream().filter(x -> x != this).map(Tree::getTreeArea).toList()){

            if (getTreeArea().coordinates2().x() < currentTreeArea.coordinates1().x() ||
                currentTreeArea.coordinates2().x() < getTreeArea().coordinates1().x())
                continue;

            if (getTreeArea().coordinates2().y() < currentTreeArea.coordinates1().y() ||
                currentTreeArea.coordinates2().y() < getTreeArea().coordinates1().y())
                continue;

            return true;

        }

        return false;

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
