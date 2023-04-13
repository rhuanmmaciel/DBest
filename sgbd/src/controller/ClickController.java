package controller;

import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import controller.CreateAction.CreateCellAction;
import controller.CreateAction.CreateOperationAction;
import controller.CreateAction.CreateTableAction;
import controller.CreateAction.CurrentAction;
import entities.Cell;
import entities.Edge;
import entities.OperationCell;
import entities.TableCell;
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

	public static void clicked(AtomicReference<CurrentAction> currentActionRef, mxCell jCell,
			Edge edge, MouseEvent e, AtomicReference<Boolean> exitRef) {
		
		Cell cell = ActionClass.getCells().get(jCell);

		CurrentAction currentAction = currentActionRef.get();
		
		if(currentAction == null) return;
		
		CurrentAction.ActionType actionType = currentAction.getType();
		boolean createTable = actionType == CurrentAction.ActionType.IMPORT_FILE || actionType == CurrentAction.ActionType.CREATE_TABLE;
		
		if (currentAction != null && (actionType == CurrentAction.ActionType.OPERATOR_CELL
				|| createTable)) {

			String name = ((CreateCellAction) currentAction).getName();
			String style = ((CreateCellAction) currentAction).getStyle();

			mxCell newCell = (mxCell) ActionClass.getGraph().insertVertex((mxCell) ActionClass.getGraph().getDefaultParent(), null, name, e.getX(),
					e.getY(), 80, 30, style);

			if (createTable) {

				TableCell tableCell = ((CreateTableAction) currentAction).getTableCell();

				tableCell.setJGraphCell(newCell);
				ActionClass.getCells().put(newCell, tableCell);

			} else {

				OperationType operationType = ((CreateOperationAction)currentAction).getOperationType();
				
				ActionClass.getCells().put(newCell,
						new OperationCell(name, style, newCell, operationType, null, e.getX(), e.getY(), 80, 30));
			}

			currentActionRef.set(null);

		}

		if (jCell != null) {

			ActionClass.getGraph().getModel().getValue(jCell);
			if (currentAction != null && actionType == CurrentAction.ActionType.EDGE
					&& !edge.hasParent()) {

				edge.addParent(jCell, ActionClass.getCells());

			}

			Cell parentCell = edge.hasParent() != null ? ActionClass.getCells().get(edge.getParent()) : null;

			if (currentAction != null && actionType == CurrentAction.ActionType.EDGE
					&& edge.isDifferent(jCell)) {

				edge.addChild(jCell, ActionClass.getCells());

				if (edge.isReady()) {

					ActionClass.getGraph().insertEdge(edge.getParent(), null, "", edge.getParent(), jCell);

					((OperationCell) cell).addParent(parentCell);

					if (parentCell != null)
						parentCell.setChild((OperationCell)cell);

					if (cell instanceof OperationCell) {

						if (((OperationCell) cell).getType() == OperationType.PROJECTION) {
							
							new FormFrameProjection(jCell, exitRef);
							((OperationCell)cell).setForm(FormFrameProjection.class);
						
						}else if (((OperationCell) cell).getType() == OperationType.SELECTION) {

							new FormFrameSelection(jCell, exitRef);
							((OperationCell)cell).setForm(FormFrameSelection.class);

						}else if (((OperationCell) cell).getType() == OperationType.AGGREGATION) {

							new FormFrameAggregation(jCell, exitRef);
							((OperationCell)cell).setForm(FormFrameAggregation.class);

						}else if (((OperationCell) cell).getType() == OperationType.RENAME) {

							new FormFrameRename(jCell, exitRef);
							((OperationCell)cell).setForm(FormFrameRename.class);

						}else if (((OperationCell) cell).getType() == OperationType.JOIN
								&& ((OperationCell) cell).getParents().size() == 2) {

							new FormFrameJoin(jCell, exitRef);
							((OperationCell)cell).setForm(FormFrameJoin.class);

						}else if (((OperationCell) cell).getType() == OperationType.CARTESIANPRODUCT
								&& ((OperationCell) cell).getParents().size() == 2) {

							new CartesianProduct(jCell, exitRef);
							((OperationCell)cell).setForm(CartesianProduct.class);

						}else if (((OperationCell) cell).getType() == OperationType.UNION
								&& ((OperationCell) cell).getParents().size() == 2) {

							new FormFrameUnion(jCell, exitRef);
							((OperationCell)cell).setForm(FormFrameUnion.class);

						}else if (((OperationCell) cell).getType() == OperationType.LEFTJOIN
								&& ((OperationCell) cell).getParents().size() == 2) {

							new FormFrameLeftJoin(jCell, exitRef);
							((OperationCell)cell).setForm(FormFrameLeftJoin.class);

						}
					}
				}

				edge.reset();
				currentActionRef.set(null);

			}

		}

	}

}
