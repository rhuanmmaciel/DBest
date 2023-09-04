package entities.utils;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

import controller.ConstantController;
import controller.MainController;
import entities.cells.*;
import enums.FileType;
import enums.TableType;
import gui.frames.DataFrame;
import sgbd.table.Table;

public class CellUtils extends MainController {

	public static void deleteCell(mxCell jCell) {

		mxGraph graph = MainController.getGraph();

		if (jCell != null) {

			Cell cell = Cell.getCells().get(jCell);
			OperationCell child = null;
			List<Cell> parents = new ArrayList<>();

			if (cell != null) {

				if (cell.hasChild()) {

					child = cell.getChild();

					child.removeParent(cell);

					cell.setChild(null);

				}

				if (cell instanceof OperationCell operationCell) {

					parents.addAll(operationCell.getParents());

					for (Cell cellParent : operationCell.getParents())
						cellParent.setChild(null);

					operationCell.clearParents();

				}

			} else {

				child = (OperationCell) Cell.getCells().get(jCell.getTarget());
				Cell parent = Cell.getCells().get(jCell.getSource());

				if (parent != null) {

					parents.add(parent);
					child.removeParent(parent);
					parent.setChild(null);

				}

			}

			TreeUtils.updateTreesAboveAndBelow(parents, child);

			graph.getModel().beginUpdate();
			try {

				graph.removeCells(new Object[] { jCell }, true);
				Cell.removeFromCells(jCell);

			} finally {
				graph.getModel().endUpdate();
			}

			graph.refresh();

		}

	}

	public static void deleteAllGraph() {

		int answer = JOptionPane.showConfirmDialog(null, ConstantController.getString("cell.deleteAll"),
				ConstantController.getString("confirmation"), JOptionPane.YES_NO_OPTION);

		if(answer == JOptionPane.YES_OPTION) {

			mxGraph graph = MainController.getGraph();

			graph.getModel().beginUpdate();
			try {
				graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
				Cell.clearCells();

			} finally {
				graph.getModel().endUpdate();
			}

			graph.refresh();

		}

	}

	public static void showTable(mxCell jCell) {

		Cell cell = jCell != null ? Cell.getCells().get(jCell) : null;
		if (cell != null && (cell instanceof TableCell || ((OperationCell) cell).hasBeenInitialized())) {

			if (!cell.hasError()) {

				new DataFrame(cell);

			} else {

				JOptionPane.showMessageDialog(null, ConstantController.getString("cell.operationCell.error"),
						ConstantController.getString("error"), JOptionPane.ERROR_MESSAGE);

			}

		}

	}

	public static void verifyCell(mxCell cell, mxCell ghostCell) {

		if (!Cell.getCells().containsKey(cell) && cell != ghostCell && cell != null) {

			TableCell tableCell = MainController.getTables().get(cell.getValue());

			Object parent = graph.getDefaultParent();
			Object[] vertices = mxGraphModel.getChildVertices(graph.getModel(), parent);

			for (Object jCell : vertices)
				if (!Cell.getCells().containsKey(jCell))
					cell = (mxCell) jCell;

			if(cell.getStyle().equals(TableType.FYI_TABLE.ID))
				new FyiTableCell((FyiTableCell) tableCell, cell);

			if(cell.getStyle().equals(TableType.CSV_TABLE.ID))
				new CsvTableCell((CsvTableCell) tableCell, cell);

		}

	}

}
