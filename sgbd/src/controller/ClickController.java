package controller;

import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import enums.OperationArity;
import enums.OperationType;
import gui.frames.forms.operations.IFormFrameOperation;
import gui.frames.main.MainFrame;

public class ClickController {

	public static void clicked(AtomicReference<CurrentAction> currentActionRef, mxCell jCell,
			AtomicReference<Edge> edgeRef, MouseEvent mouseEvent, mxCell ghostJCell) {

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
						MainController.getGraph().getDefaultParent(), null, name, mouseEvent.getX(), mouseEvent.getY(), 80, 30,
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
						new Object[] { jTableCell }, currentX - mouseEvent.getX(), currentY - mouseEvent.getY());
				
			}

			if (currentAction.getType() != CurrentAction.ActionType.EDGE)
				currentActionRef.set(null);

		}

		if (jCell != null) {

			MainController.getGraph().getModel().getValue(jCell);
			if (actionType == CurrentAction.ActionType.EDGE && !edgeRef.get().hasParent()) {

				edgeRef.get().addParent(jCell);

			}

			Cell parentCell = edgeRef.get().hasParent() != null ? Cell.getCells().get(edgeRef.get().getParent()) : null;

			if (actionType == CurrentAction.ActionType.EDGE && edgeRef.get().isDifferent(jCell)) {

				if (!edgeRef.get().isReady())
					edgeRef.get().addChild(jCell);

				if (edgeRef.get().isReady()) {

					MainController.getGraph().insertEdge(edgeRef.get().getParent(), null, "", edgeRef.get().getParent(),
							jCell);

					((OperationCell) cell).addParent(parentCell);

					assert parentCell != null;
					parentCell.setChild((OperationCell) cell);

					cell.setAllNewTrees();
					TreeUtils.recalculateContent(cell);

					OperationCell operationCell = (OperationCell) cell;

					OperationType type = operationCell.getType();

					if(operationCell.getArity() == OperationArity.UNARY || operationCell.getParents().size() == 2){

						try {

							Constructor<? extends IFormFrameOperation> constructor = type.getForm().getDeclaredConstructor(mxCell.class);
							constructor.newInstance(jCell);

						} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
								 | InvocationTargetException e) {

							e.printStackTrace();

						}

					}

				}

				edgeRef.get().reset();
				currentActionRef.set(null);

			}

		}

		MainController.getGraph().removeCells(new Object[] { ghostJCell }, true);

	}

}
