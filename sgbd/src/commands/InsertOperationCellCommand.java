package commands;

import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.SerializationUtils;

import com.mxgraph.model.mxCell;

import entities.Action;
import entities.Action.CreateOperationCellAction;
import entities.Action.CurrentAction;
import entities.Action.CurrentAction.ActionType;
import entities.Edge;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import gui.frames.main.MainFrame;

public class InsertOperationCellCommand extends BaseUndoableRedoableCommand {

    private final MouseEvent mouseEvent;

    private mxCell mxCell;

    private final AtomicReference<mxCell> cellReference, invisibleCellReference, ghostCellReference;

    private final AtomicReference<Edge> edgeReference;

    private final AtomicReference<CurrentAction> currentActionReference, currentActionReferenceCopy;

    private ActionType currentActionType;

    public InsertOperationCellCommand(
        MouseEvent mouseEvent,
        AtomicReference<mxCell> cellReference,
        AtomicReference<mxCell> invisibleCellReference,
        AtomicReference<mxCell> ghostCellReference,
        AtomicReference<Edge> edgeReference,
        AtomicReference<CurrentAction> currentActionReference
    ) {
        this.mouseEvent = mouseEvent;
        this.cellReference = cellReference;
        this.invisibleCellReference = invisibleCellReference;
        this.ghostCellReference = ghostCellReference;
        this.edgeReference = edgeReference;
        this.currentActionReference = currentActionReference;

        this.currentActionReferenceCopy = SerializationUtils.clone(currentActionReference);
    }

    @Override
    public void execute() {
        CurrentAction currentAction = this.currentActionReferenceCopy.get();
        this.currentActionType = currentAction.getType();

        if (this.currentActionType == CurrentAction.ActionType.NONE) {
            return;
        }

        if (this.currentActionType != CurrentAction.ActionType.CREATE_EDGE) {
            CellUtils.removeCell(this.invisibleCellReference);
        }

        if (currentAction instanceof Action.CreateCellAction) {
            this.insertCell();
        }

        if (this.cellReference.get() != null && !this.cellReference.get().isEdge()) {
            this.insertEdge();
        }

        this.removeCell(this.ghostCellReference);
    }

    @Override
    public void undo() {
        CurrentAction currentAction = this.currentActionReferenceCopy.get();
        this.currentActionType = currentAction.getType();

        while (this.commandController.canUndo()) {
            this.commandController.undo();
        }

        if (
            this.currentActionType == ActionType.CREATE_TABLE_CELL ||
            this.currentActionType == ActionType.CREATE_OPERATOR_CELL
        ) {
            CellUtils.desactivateActiveJCell(this.cellReference.get());
        }
    }

    @Override
    public void redo() {
        CurrentAction currentAction = this.currentActionReferenceCopy.get();
        this.currentActionType = currentAction.getType();

        CellUtils.removeCell(this.invisibleCellReference);

        if (
            this.currentActionType == ActionType.CREATE_TABLE_CELL ||
            this.currentActionType == ActionType.CREATE_OPERATOR_CELL
        ) {
            CellUtils.activateInactiveJCell(this.cellReference.get());
        }

        this.removeCell(this.ghostCellReference);

        while (this.commandController.canRedo()) {
            this.commandController.redo();
        }
    }

    private void insertCell() {
        if (this.currentActionReferenceCopy.get() instanceof CreateOperationCellAction createOperationAction) {
            this.mxCell = (mxCell) MainFrame
                .getGraph()
                .insertVertex(
                    MainFrame.getGraph().getDefaultParent(), null,
                    createOperationAction.getName(), this.mouseEvent.getX(),
                    this.mouseEvent.getY(), 80, 30, createOperationAction.getStyle()
                );

            this.cellReference.set(this.mxCell);
            new OperationCell(this.mxCell, createOperationAction.getOperationType());

            if (createOperationAction.hasParent() && this.edgeReference != null) {
                this.edgeReference.get().addParent(createOperationAction.getParent());
                this.edgeReference.get().addChild(this.mxCell);

                this.currentActionType = ActionType.CREATE_EDGE;
                this.currentActionReferenceCopy.set(new CurrentAction(this.currentActionType));

                this.cellReference.set(this.mxCell);

                this.removeCell(this.ghostCellReference);
            }
        } else if (this.currentActionReferenceCopy.get() instanceof Action.CreateTableCellAction createTableAction) {
            this.mxCell = createTableAction.getTableCell().getJCell();
            this.cellReference.set(this.mxCell);

            int currentX = (int) this.mxCell.getGeometry().getX();
            int currentY = (int) this.mxCell.getGeometry().getY();

            MainFrame.getGraph().moveCells(
                new Object[]{this.mxCell},
                (double) currentX - this.mouseEvent.getX(),
                (double) currentY - this.mouseEvent.getY()
            );
        }

        this.currentActionReference.set(new CurrentAction(ActionType.NONE));
    }

    private void insertEdge() {
        MainFrame.getGraph().getModel().getValue(this.cellReference.get());

        if (this.currentActionType == ActionType.CREATE_EDGE && !this.edgeReference.get().hasParent()) {
            this.edgeReference.get().addParent(this.cellReference.get());
            CellUtils.addMovableEdge(this.mouseEvent, this.invisibleCellReference, this.cellReference.get());
        }

        if (this.currentActionType == ActionType.CREATE_EDGE) {
            this.commandController.execute(new InsertEdgeCommand(
                this.cellReference, this.invisibleCellReference,
                this.edgeReference, this.currentActionReference
            ));
        }
    }

    private void removeCell(AtomicReference<mxCell> cellReference) {
        MainFrame.getGraph().removeCells(new Object[]{cellReference.get()}, true);
        cellReference.set(null);
    }
}
