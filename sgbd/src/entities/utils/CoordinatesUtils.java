package entities.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import entities.Area;
import entities.Coordinates;
import entities.Tree;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;

public class CoordinatesUtils {

    public static Coordinates searchForCleanArea() {
        Set<Tree> trees = CellUtils.getActiveCellsTrees();

        Map<Tree, Area> areas = new HashMap<>();

        trees.forEach(tree -> areas.put(tree, getTreeArea(tree)));

        return null;
    }

    public static Area getTreeArea(Tree tree) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;

        for (Cell cell : tree.getCells()) {
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
	/*
	private class Area {

		Coordinates begin;
		Coordinates end;

		boolean isFree(Coordinates beginTree, Coordinates endTree) {

			int minX = Math.min(begin.x(), end.x());
			int maxX = Math.max(begin.x(), end.x());

			int minY = Math.min(begin.y(), end.y());
			int maxY = Math.max(begin.y(), end.y());

			return !((beginTree.x() > minX && beginTree.x() < maxX && beginTree.y() > minY && beginTree.y() < maxY)
					|| (endTree.x() > minX && endTree.x() < maxX && endTree.y() > minY && endTree.y() < maxY));

		}

	}*/

}
