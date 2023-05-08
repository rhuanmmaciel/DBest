package entities.utils;

import java.util.Map;

import javax.swing.JOptionPane;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import controller.ActionClass;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import gui.frames.DataFrame;

public class CellUtils {

	public static void deleteCell(mxCell jCell) {

		Map<mxCell, Cell> cells = ActionClass.getCells();
		mxGraph graph = ActionClass.getGraph();
		
		if (jCell != null) {

			Cell cell = cells.get(jCell);
			
			if (cell != null) {
				
				if(cell.hasChild()) {
					
					cell.getChild().removeParent(cell);

					TreeUtils.recalculateContent(cell.getChild());
					
					cell.setChild(null);
					
				}
				
				if (cell instanceof OperationCell operationCell) {

					for (Cell cellParent : operationCell.getParents()) {

						cellParent.setChild(null);

					}

					operationCell.clearParents();

				}
				
			}else {
				
				
				OperationCell child = (OperationCell) cells.get(jCell.getTarget());
				Cell parent = cells.get(jCell.getSource());
				
				if(parent!=null) {
					
					child.removeParent(parent);
					parent.setChild(null);
					TreeUtils.recalculateContent(child);
				
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
		
	}
	
	public static void showTable(mxCell jCell) {
		
		Cell cell = jCell != null ? ActionClass.getCells().get(jCell) : null;
		if (cell != null && (cell instanceof TableCell || ((OperationCell) cell).hasForm())) {
			
			if(!cell.hasError()) {
			
				new DataFrame(cell);
			
			}else {
				
				JOptionPane.showMessageDialog(null, "A operação possui erros", "Erro",
						JOptionPane.ERROR_MESSAGE);
				
			}
				
		}
			
	}
	
}
