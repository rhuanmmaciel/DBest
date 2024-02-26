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

    private CoordinatesUtils() {

    }

    public static Coordinates searchForCleanArea() {
        Set<Tree> trees = CellUtils.getActiveCellsTrees();

        Map<Tree, Area> areas = new HashMap<>();

//        trees.forEach(tree -> areas.put(tree, getTreeArea(tree)));

        return null;
    }

	/*private static class Area {

		private Coordinates begin;

        private Coordinates end;

        public Area(Coordinates begin, Coordinates end) {
            this.begin = begin;
            this.end = end;
        }

		boolean isFree(Coordinates beginTree, Coordinates endTree) {
			int minX = Math.min(this.begin.x(), this.end.x());
			int maxX = Math.max(this.begin.x(), this.end.x());

			int minY = Math.min(this.begin.y(), this.end.y());
			int maxY = Math.max(this.begin.y(), this.end.y());

			return
                !(
                    (beginTree.x() > minX && beginTree.x() < maxX && beginTree.y() > minY && beginTree.y() < maxY) ||
                    (endTree.x() > minX && endTree.x() < maxX && endTree.y() > minY && endTree.y() < maxY)
                );
		}

        public Coordinates getBegin() {
            return this.begin;
        }

        public void setBegin(Coordinates begin) {
            this.begin = begin;
        }

        public Coordinates getEnd() {
            return this.end;
        }

        public void setEnd(Coordinates end) {
            this.end = end;
        }
    }*/
}
