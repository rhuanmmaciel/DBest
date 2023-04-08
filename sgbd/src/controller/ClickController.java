package controller;

import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import controller.CreateAction.CreateCellAction;
import controller.CreateAction.CreateOperationAction;
import controller.CreateAction.CreateTableAction;
import controller.CreateAction.CurrentAction;
import entities.Cell;
import entities.Edge;
import entities.OperationCell;
import entities.TableCell;
import enums.OperationArity;
import enums.OperationType;
import gui.frames.forms.operations.CartesianProduct;
import gui.frames.forms.operations.FormFrameAggregation;
import gui.frames.forms.operations.FormFrameJoin;
import gui.frames.forms.operations.FormFrameLeftJoin;
import gui.frames.forms.operations.FormFrameProjection;
import gui.frames.forms.operations.FormFrameRename;
import gui.frames.forms.operations.FormFrameSelection;
import gui.frames.forms.operations.FormFrameUnion;

public class ClickController {

	public static void clicked(AtomicReference<CurrentAction> currentActionRef, Map<mxCell, Cell> cells, mxCell jCell,
			Edge edge, mxGraph graph, MouseEvent e, AtomicReference<Boolean> exitRef) {
		
		Cell cell = cells.get(jCell);

		CurrentAction currentAction = currentActionRef.get();
		
		if(currentAction == null) return;
		
		CurrentAction.ActionType actionType = currentAction.getType();
		boolean createTable = actionType == CurrentAction.ActionType.IMPORT_FILE || actionType == CurrentAction.ActionType.CREATE_TABLE;
		
		if (currentAction != null && (actionType == CurrentAction.ActionType.OPERATOR_CELL
				|| createTable)) {

			String name = ((CreateCellAction) currentAction).getName();
			String style = ((CreateCellAction) currentAction).getStyle();

			mxCell newCell = (mxCell) graph.insertVertex((mxCell) graph.getDefaultParent(), null, name, e.getX(),
					e.getY(), 80, 30, style);

			if (createTable) {

				TableCell tableCell = ((CreateTableAction) currentAction).getTableCell();

				tableCell.setJGraphCell(newCell);
				cells.put(newCell, tableCell);

			} else {

				OperationType operationType = ((CreateOperationAction)currentAction).getOperationType();
				
				cells.put(newCell,
						new OperationCell(name, style, newCell, operationType, null, e.getX(), e.getY(), 80, 30));
			}

			currentActionRef.set(null);

		}

		if (jCell != null) {

			graph.getModel().getValue(jCell);
			if (currentAction != null && actionType == CurrentAction.ActionType.EDGE
					&& !edge.hasParent()) {

				edge.addParent(jCell, cells);

			}

			Cell parentCell = edge.hasParent() != null ? cells.get(edge.getParent()) : null;

			if (currentAction != null && actionType == CurrentAction.ActionType.EDGE
					&& edge.isDifferent(jCell)) {

				edge.addChild(jCell, cells);

				if (edge.isReady()) {

					graph.insertEdge(edge.getParent(), null, "", edge.getParent(), jCell);

					((OperationCell) cell).addParent(parentCell);

					if (parentCell != null)
						parentCell.setChild(cell);

					if (cell instanceof OperationCell) {

						if (((OperationCell) cell).getType() == OperationType.PROJECTION
								&& ((OperationCell) cell).checkRules(OperationArity.UNARY) == true)

							new FormFrameProjection(jCell, cells, graph, exitRef);

						else if (((OperationCell) cell).getType() == OperationType.SELECTION
								&& ((OperationCell) cell).checkRules(OperationArity.UNARY) == true)

							new FormFrameSelection(jCell, cells, graph, exitRef);

						else if (((OperationCell) cell).getType() == OperationType.AGGREGATION
								&& ((OperationCell) cell).checkRules(OperationArity.UNARY) == true)

							new FormFrameAggregation(jCell, cells, graph);

						else if (((OperationCell) cell).getType() == OperationType.RENAME
								&& ((OperationCell) cell).checkRules(OperationArity.UNARY) == true)

							new FormFrameRename(jCell, cells, graph);

						else if (((OperationCell) cell).getType() == OperationType.JOIN
								&& ((OperationCell) cell).getParents().size() == 2
								&& ((OperationCell) cell).checkRules(OperationArity.BINARY) == true)

							new FormFrameJoin(jCell, cells, graph, exitRef);

						else if (((OperationCell) cell).getType() == OperationType.CARTESIANPRODUCT
								&& ((OperationCell) cell).getParents().size() == 2
								&& ((OperationCell) cell).checkRules(OperationArity.BINARY) == true)

							new CartesianProduct(jCell, cells, graph);

						else if (((OperationCell) cell).getType() == OperationType.UNION
								&& ((OperationCell) cell).getParents().size() == 2
								&& ((OperationCell) cell).checkRules(OperationArity.BINARY) == true)

							new FormFrameUnion(jCell, cells, graph, exitRef);

						else if (((OperationCell) cell).getType() == OperationType.LEFTJOIN
								&& ((OperationCell) cell).getParents().size() == 2
								&& ((OperationCell) cell).checkRules(OperationArity.BINARY) == true)

							new FormFrameLeftJoin(jCell, cells, graph, exitRef);

					}

				}

				edge.reset();
				currentActionRef.set(null);

			}

		}

	}

}
