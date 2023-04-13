package entities.util;

import java.util.Map;

import javax.swing.JOptionPane;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import controller.ActionClass;
import entities.Cell;
import entities.OperationCell;
import gui.frames.ResultFrame;

public class CellUtils {

	public static void deleteCell(mxCell jCell) {

		Map<mxCell, Cell> cells = ActionClass.getCells();
		mxGraph graph = ActionClass.getGraph();
		
		if (jCell != null) {

			Cell cell = cells.get(jCell);
			
			if (cell != null) {

				if(cell.hasChild()) {

					cell.getChild().removeParent(cell);
					cell.setChild(null);
					
				}
				
				if (cell instanceof OperationCell) {

					for (Cell cellParent : ((OperationCell) cell).getParents()) {

						cellParent.setChild(null);

					}

					((OperationCell) cell).clearParents();

				}
			}else {
				
				OperationCell child = (OperationCell) cells.get(jCell.getTarget());
				Cell parent = cells.get(jCell.getSource());
				
				child.removeParent(parent);
				parent.setChild(null);
				
			}

			graph.getModel().beginUpdate();
			try {

				graph.removeCells(new Object[] { jCell }, true);
				cells.remove(jCell);

			} finally {
				graph.getModel().endUpdate();
			}

			graph.refresh();

			TreeUtils.identifyTrees(ActionClass.getTrees());
			
		}

	}

	public static void deleteAllGraph() {

		Map<mxCell, Cell> cells = ActionClass.getCells();
		mxGraph graph = ActionClass.getGraph();
		
		graph.getModel().beginUpdate();
		try {
			graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
			cells.clear();

		} finally {
			graph.getModel().endUpdate();
		}

		graph.refresh();
		TreeUtils.identifyTrees(ActionClass.getTrees());

	}
	
	public static void showTable(mxCell jCell) {
		
		Cell cell = jCell != null ? ActionClass.getCells().get(jCell) : null;
		if (cell != null) {
			
			if(!cell.hasError()) {
			
				new ResultFrame(cell);
			
			}else {
				
				JOptionPane.showMessageDialog(null, "A operação possui erros", "Erro",
						JOptionPane.ERROR_MESSAGE);
				
			}
				
		}
			
	}
	
}
