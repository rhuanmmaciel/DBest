package controller;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import entities.Action;
import entities.Action.CreateOperationAction;
import entities.Action.CreateTableAction;
import entities.Action.CurrentAction;
import entities.Edge;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.CellUtils;
import entities.utils.TreeUtils;
import enums.OperationArity;
import enums.OperationType;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.main.MainFrame;
import operations.IOperator;

import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static entities.cells.Cell.getCells;

public class ClickController {

    private final MouseEvent mouseEvent;

	private final AtomicReference<CurrentAction> currentActionRef;
	private final AtomicReference<mxCell> jCellRef;
	private final AtomicReference<Edge> edgeRef;
	private final AtomicReference<mxCell> ghostJCellRef;
	private final AtomicReference<mxCell> invisibleJCellRef;

	private Cell cell;
    private CurrentAction.ActionType actionType;

    public ClickController(AtomicReference<CurrentAction> currentActionRef, AtomicReference<mxCell> jCellRef, AtomicReference<Edge> edgeRef, MouseEvent mouseEvent, AtomicReference<mxCell> ghostJCellRef, AtomicReference<mxCell> invisibleJCellRef) {

		this.currentActionRef = currentActionRef;
		this.jCellRef = jCellRef;
		this.edgeRef = edgeRef;
		this.mouseEvent = mouseEvent;
		this.ghostJCellRef = ghostJCellRef;
		this.invisibleJCellRef = invisibleJCellRef;
		clicked();

	}

	private void clicked() {

		if (currentActionRef.get().getType() == CurrentAction.ActionType.NONE)
			return;

		if(currentActionRef.get().getType() != CurrentAction.ActionType.EDGE) deleteMovableEdge(invisibleJCellRef);

		cell = getCells().get(jCellRef.get());

		CurrentAction currentAction = currentActionRef.get();

		actionType = currentAction.getType();

		if (currentAction instanceof Action.CreateCellAction)
	        createCell();

		if (jCellRef.get() != null && !jCellRef.get().isEdge())
			putCell();

		removeCell(ghostJCellRef);

	}

	private void removeCell(AtomicReference<mxCell> jCell){

		MainController.getGraph().removeCells(new Object[] {jCell.get() }, true);
		jCell.set(null);

	}

	private void putCell(){

		MainController.getGraph().getModel().getValue(jCellRef.get());
		if (actionType == CurrentAction.ActionType.EDGE && !edgeRef.get().hasParent()) {

			edgeRef.get().addParent(jCellRef.get());

			addMovableEdge(mouseEvent, invisibleJCellRef, jCellRef.get());

		}

		if (actionType == CurrentAction.ActionType.EDGE && edgeRef.get().isDifferent(jCellRef.get())) {

			if (!edgeRef.get().isReady())
				edgeRef.get().addChild(jCellRef.get());

			if (edgeRef.get().isReady()) {

				executeOperation();

			}

			edgeRef.get().reset();
			currentActionRef.set(MainController.NONE_ACTION);

		}

	}

	private void executeOperation(){

		Cell parentCell = getCells().get(edgeRef.get().getParent());

		if(!(cell instanceof OperationCell opCell)) return;

		deleteMovableEdge(invisibleJCellRef);

		MainController.getGraph().insertEdge(edgeRef.get().getParent(), null, "", edgeRef.get().getParent(),
				jCellRef.get());

		opCell.addParent(parentCell);

		assert parentCell != null;
		parentCell.setChild(opCell);

		cell.setAllNewTrees();
		TreeUtils.recalculateContent(opCell);

		OperationType type = opCell.getType();

		if((opCell.getArity() == OperationArity.UNARY || opCell.getParents().size() == 2)
				&& !OperationType.OPERATIONS_WITHOUT_FORM.contains(type))
			executeOperationWithForm(type);

		if((opCell.getArity() == OperationArity.UNARY || opCell.getParents().size() == 2)
				&& OperationType.OPERATIONS_WITHOUT_FORM.contains(type))
			executeOperationWithoutForm(type);

	}

	private void executeOperationWithForm(OperationType type){

		try {

			Constructor<? extends IOperationForm> constructor = type.FORM.getDeclaredConstructor(mxCell.class);
			constructor.newInstance(jCellRef.get());

		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
				 | InvocationTargetException e) {

			e.printStackTrace();

		}

	}

	private void executeOperationWithoutForm(OperationType type){

		try {

			Constructor<? extends IOperator> constructor = type.OPERATOR_CLASS.getDeclaredConstructor();
			constructor.newInstance().executeOperation(jCellRef.get(), List.of());

		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
				 | InvocationTargetException e) {

			e.printStackTrace();

		}

	}

    private void createCell(){

        if (currentActionRef.get() instanceof CreateOperationAction createOperationAction) {

            String name = createOperationAction.getName();
            String style = createOperationAction.getStyle();

            mxCell newCell = (mxCell) MainController.getGraph().insertVertex(
                    MainController.getGraph().getDefaultParent(), null, name, mouseEvent.getX(), mouseEvent.getY(), 80, 30,
                    style);

            OperationType operationType = createOperationAction.getOperationType();

            new OperationCell(newCell, operationType);

            if (createOperationAction.hasParent()) {

                edgeRef.get().addParent(createOperationAction.getParent());
                edgeRef.get().addChild(newCell);

                currentActionRef.set(new CurrentAction(CurrentAction.ActionType.EDGE));
                actionType = CurrentAction.ActionType.EDGE;

                cell = getCells().get(newCell);
                jCellRef.set(newCell);

				removeCell(ghostJCellRef);

            }

        } else if (currentActionRef.get() instanceof CreateTableAction createTableAction) {

            mxCell jTableCell = createTableAction.getTableCell().getJGraphCell();

            int currentX = (int) jTableCell.getGeometry().getX();
            int currentY = (int) jTableCell.getGeometry().getY();

            MainFrame.getGraph().moveCells(
                    new Object[] { jTableCell }, currentX - mouseEvent.getX(), currentY - mouseEvent.getY());

        }

        if (currentActionRef.get().getType() != CurrentAction.ActionType.EDGE)
            resetCurrentAction();

    }

    private void resetCurrentAction(){
        currentActionRef.set(MainController.NONE_ACTION);
    }

	private void addMovableEdge(MouseEvent mouseEvent, AtomicReference<mxCell> invisibleJCellRef, mxCell jCell){

		invisibleJCellRef.set((mxCell) MainController.getGraph().insertVertex(MainController.getGraph().getDefaultParent(), "invisible", "",
				mouseEvent.getX(),
				mouseEvent.getY() , 80, 30,
				"invisible"));

		MainController.getGraph().insertEdge(jCell, null, "", jCell,
				invisibleJCellRef.get());

		invisibleJCellRef.get().setGeometry(new mxGeometry(0, 0, 0, 0));

	}

	public static void deleteMovableEdge(AtomicReference<mxCell> invisibleJCellRef){

		CellUtils.deleteCell(invisibleJCellRef.get());
		invisibleJCellRef.set(null);

	}

}
