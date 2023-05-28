package controller;

import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import entities.Action.CreateCellAction;
import entities.Action.CreateOperationAction;
import entities.Action.CreateTableAction;
import entities.Action.CurrentAction;
import entities.Edge;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.TreeUtils;
import enums.OperationType;
import gui.frames.forms.operations.binary.FormFrameJoin;
import gui.frames.forms.operations.binary.FormFrameLeftJoin;
import gui.frames.forms.operations.binary.FormFrameRightJoin;
import gui.frames.forms.operations.binary.FormFrameUnion;
import gui.frames.forms.operations.unary.FormFrameProjection;
import gui.frames.forms.operations.unary.FormFrameSelection;
import gui.frames.main.MainFrame;
import operations.binary.CartesianProduct;

public class ClickController {

	public static void clicked(AtomicReference<CurrentAction> currentActionRef, mxCell jCell,
			AtomicReference<Edge> edgeRef, MouseEvent e, mxCell ghostJCell) {

		if (currentActionRef.get() == null)
			return;

		Cell cell = Cell.getCells().get(jCell);

		CurrentAction currentAction = currentActionRef.get();

		CurrentAction.ActionType actionType = currentAction.getType();

		boolean createTable = actionType == CurrentAction.ActionType.IMPORT_FILE
				|| actionType == CurrentAction.ActionType.CREATE_TABLE;

		if (actionType == CurrentAction.ActionType.CREATE_OPERATOR_CELL || createTable) {

			if (!createTable) {

				String name = ((CreateCellAction) currentAction).getName();
				String style = ((CreateCellAction) currentAction).getStyle();

				mxCell newCell = (mxCell) MainController.getGraph().insertVertex(
						(mxCell) MainController.getGraph().getDefaultParent(), null, name, e.getX(), e.getY(), 80, 30,
						style);
				
				OperationType operationType = ((CreateOperationAction) currentAction).getOperationType();

				new OperationCell(newCell, operationType);

				if (((CreateOperationAction) currentAction).hasParent()) {

					edgeRef.get().addParent(((CreateOperationAction) currentAction).getParent());
					edgeRef.get().addChild(newCell);

					currentActionRef.set(new CurrentAction(CurrentAction.ActionType.EDGE));
					actionType = CurrentAction.ActionType.EDGE;

					cell = Cell.getCells().get(newCell);
					jCell = newCell;

					MainController.getGraph().removeCells(new Object[] { ghostJCell }, true);
					ghostJCell = null;

				}

			} else if (currentAction instanceof CreateTableAction) {

				mxCell jTableCell = ((CreateTableAction) currentAction).getTableCell().getJGraphCell();
				
				int currentX = (int) jTableCell.getGeometry().getX();
				int currentY = (int) jTableCell.getGeometry().getY();
				
				MainFrame.getGraph().moveCells(
						new Object[] { jTableCell }, currentX - e.getX(), currentY - e.getY());
				
			}

			if (currentAction.getType() != CurrentAction.ActionType.EDGE)
				currentActionRef.set(null);

		}

		if (jCell != null) {

			MainController.getGraph().getModel().getValue(jCell);
			if (currentAction != null && actionType == CurrentAction.ActionType.EDGE && !edgeRef.get().hasParent()) {

				edgeRef.get().addParent(jCell);

			}

			Cell parentCell = edgeRef.get().hasParent() != null ? Cell.getCells().get(edgeRef.get().getParent()) : null;

			if (currentAction != null && actionType == CurrentAction.ActionType.EDGE
					&& edgeRef.get().isDifferent(jCell)) {

				if (!edgeRef.get().isReady())
					edgeRef.get().addChild(jCell);

				if (edgeRef.get().isReady()) {

					MainController.getGraph().insertEdge(edgeRef.get().getParent(), null, "", edgeRef.get().getParent(),
							jCell);

					((OperationCell) cell).addParent(parentCell);

					parentCell.setChild((OperationCell) cell);

					cell.setAllNewTrees();
					TreeUtils.recalculateContent(cell);

					if (cell instanceof OperationCell operationCell) {

						if (operationCell.getType() == OperationType.PROJECTION) {

							new FormFrameProjection(jCell);
							operationCell.setForm();

						} else if (operationCell.getType() == OperationType.SELECTION) {

							new FormFrameSelection(jCell);
							operationCell.setForm();

						} else if (operationCell.getType() == OperationType.AGGREGATION) {

							//new FormFrameAggregation(jCell);
							//operationCell.setForm(FormFrameAggregation.class);

						} else if (operationCell.getType() == OperationType.RENAME) {

							//new FormFrameRename(jCell);
							//operationCell.setForm(FormFrameRename.class);

						} else if (operationCell.getType() == OperationType.JOIN
								&& operationCell.getParents().size() == 2) {

							new FormFrameJoin(jCell);
							operationCell.setForm();

						} else if (operationCell.getType() == OperationType.CARTESIAN_PRODUCT
								&& operationCell.getParents().size() == 2) {

							new CartesianProduct(jCell);
							operationCell.setForm();

						} else if (operationCell.getType() == OperationType.UNION
								&& operationCell.getParents().size() == 2) {

							new FormFrameUnion(jCell);
							operationCell.setForm();

						} else if (operationCell.getType() == OperationType.LEFT_JOIN
								&& operationCell.getParents().size() == 2) {

							new FormFrameLeftJoin(jCell);
							operationCell.setForm();

						} else if (operationCell.getType() == OperationType.RIGHT_JOIN
								&& operationCell.getParents().size() == 2) {

							new FormFrameRightJoin(jCell);
							operationCell.setForm();

						}

					}
				}

				edgeRef.get().reset();
				currentActionRef.set(null);

			}

		}

		MainController.getGraph().removeCells(new Object[] { ghostJCell }, true);

		ghostJCell = null;

	}

}
