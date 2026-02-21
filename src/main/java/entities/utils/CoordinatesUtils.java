package entities.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.awt.event.MouseEvent;

import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxPoint;
import entities.Area;
import entities.Coordinates;
import entities.Tree;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;
import gui.frames.main.MainFrame;

public class CoordinatesUtils {

    private CoordinatesUtils() {

    }

    public static Coordinates transformScreenToCanvasCoordinates(MouseEvent mouseEvent) {
        return transformScreenToCanvasCoordinates(mouseEvent.getX(), mouseEvent.getY());
    }

    public static Coordinates transformScreenToCanvasCoordinates(double screenX, double screenY) {
        mxGraph graph = MainFrame.getGraph();
        double scale = graph.getView().getScale();
        mxPoint translation = graph.getView().getTranslate();
        
        //  canvasCoord = (screenCoord / scale) - translation
        double canvasX = (screenX / scale) - translation.getX();
        double canvasY = (screenY / scale) - translation.getY();
        
        return new Coordinates((int) Math.round(canvasX), (int) Math.round(canvasY));
    }

    public static Coordinates transformCanvasToScreenCoordinates(double canvasX, double canvasY) {
        mxGraph graph = MainFrame.getGraph();
        double scale = graph.getView().getScale();
        mxPoint translation = graph.getView().getTranslate();
        
        // screenCoord = (canvasCoord + translation) * scale
        double screenX = (canvasX + translation.getX()) * scale;
        double screenY = (canvasY + translation.getY()) * scale;
        
        return new Coordinates((int) Math.round(screenX), (int) Math.round(screenY));
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
