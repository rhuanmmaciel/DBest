package entities.util;

import java.util.Map;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import entities.Cell;
import entities.OperationCell;
import entities.Tree;

public class CellUtils {

	public static void deleteCell(mxCell jCell, Map<mxCell, Cell> cells, mxGraph graph, Map<Integer, Tree> trees) {

		if (jCell != null) {

			Cell cell = cells.get(jCell);

			if (cell != null) {

				cell.setChild(null);

				if (cell instanceof OperationCell) {

					for (Cell cellParent : ((OperationCell) cell).getParents()) {

						cellParent.setChild(null);

					}

					((OperationCell) cell).clearParents();

				}
			}

			graph.getModel().beginUpdate();
			try {

				graph.removeCells(new Object[] { jCell }, true);
				cells.remove(jCell);

			} finally {
				graph.getModel().endUpdate();
			}

			graph.refresh();

			TreeUtils.updateTree(trees, cells);
			
		}

	}

	public static void deleteAllGraph(Map<mxCell, Cell> cells, mxGraph graph, Map<Integer, Tree> trees) {

		graph.getModel().beginUpdate();
		try {
			graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
			cells.clear();

		} finally {
			graph.getModel().endUpdate();
		}

		graph.refresh();
		TreeUtils.updateTree(trees, cells);

	}
	
}
