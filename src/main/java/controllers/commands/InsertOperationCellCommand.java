package controllers.commands;

import com.mxgraph.model.mxCell;
import entities.Action;
import entities.Action.CreateOperationCellAction;
import entities.Action.CurrentAction;
import entities.Action.CurrentAction.ActionType;
import entities.Coordinates;
import entities.Edge;
import entities.cells.OperationCell;
import entities.utils.CoordinatesUtils;
import entities.utils.cells.CellRepository;
import entities.utils.cells.CellUtils;
import enums.CellType;
import enums.OperationType;
import gui.frames.main.MainFrame;
import ibd.query.lookup.NoLookupFilter;
import ibd.query.unaryop.Reference;
import ibd.query.unaryop.filter.Condition;
import org.apache.commons.lang3.SerializationUtils;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

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
        CurrentAction currentAction = this.currentActionReference.get();
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
            CellUtils.deactivateActiveJCell(MainFrame.getGraph(), this.cellReference.get());
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
            CellUtils.activateInactiveJCell(MainFrame.getGraph(), this.cellReference.get());
        }

        this.removeCell(this.ghostCellReference);

        while (this.commandController.canRedo()) {
            this.commandController.redo();
        }
    }    private void insertCell() {
        if (this.currentActionReference.get() instanceof CreateOperationCellAction createOperationAction) {
            Coordinates canvasCoords = CoordinatesUtils.transformScreenToCanvasCoordinates(this.mouseEvent);
            
            int width = CellUtils.getCellWidth(createOperationAction.getName());
            int height = 30;
     
            double centeredX = canvasCoords.x() - (width / 2.0);
            double centeredY = canvasCoords.y() - (height / 2.0);
            
            this.mxCell = (mxCell) MainFrame
                .getGraph()
                .insertVertex(
                    MainFrame.getGraph().getDefaultParent(), null,
                    createOperationAction.getName(), centeredX,
                    centeredY, width, height, CellType.OPERATION.id
                );

            this.cellReference.set(this.mxCell);
            OperationCell operationCell = new OperationCell(this.mxCell, createOperationAction.getOperationType());
            if (createOperationAction.getOperationType()==OperationType.CONDITION)
            {
                try {
                    Condition condition = new Condition(new NoLookupFilter());
                    operationCell.setOperator(condition);
                } catch (Exception ex) {
                }
            }
            else if (createOperationAction.getOperationType()==OperationType.REFERENCE)
            {
                try {
                    Reference reference = new Reference(new ArrayList<String>().toArray(new String[0]));
                    operationCell.setOperator(reference);
                } catch (Exception ex) {
                }
            }
            
            if (createOperationAction.hasParent() && this.edgeReference != null) {
                this.edgeReference.get().addParent(createOperationAction.getParent());
                this.edgeReference.get().addChild(this.mxCell);

                this.currentActionType = ActionType.CREATE_EDGE;
                this.currentActionReference.set(new CurrentAction(this.currentActionType));

                this.cellReference.set(this.mxCell);

                this.removeCell(this.ghostCellReference);
            }        } else if (this.currentActionReference.get() instanceof Action.CreateTableCellAction createTableAction) {
            this.mxCell = createTableAction.getTableCell().getJCell();
            this.cellReference.set(this.mxCell);
 
            Coordinates canvasCoords = CoordinatesUtils.transformScreenToCanvasCoordinates(this.mouseEvent);
            
            double tableWidth = this.mxCell.getGeometry().getWidth();
            double tableHeight = this.mxCell.getGeometry().getHeight();
            
            double centeredX = canvasCoords.x() - (tableWidth / 2.0);
            double centeredY = canvasCoords.y() - (tableHeight / 2.0);
            
            int currentX = (int) this.mxCell.getGeometry().getX();
            int currentY = (int) this.mxCell.getGeometry().getY();

            MainFrame.getGraph().moveCells(
                new Object[]{this.mxCell},
                centeredX - currentX,
                centeredY - currentY
            );
        }

        this.currentActionReference.set(new CurrentAction(ActionType.NONE));
    }

    private void insertEdge() {

        MainFrame.getGraph().getModel().getValue(this.cellReference.get());        if (this.currentActionType == ActionType.CREATE_EDGE && !this.edgeReference.get().hasParent() &&
            this.cellReference.get() != null && CellRepository.getActiveCell(cellReference.get()).isPresent() &&
            !CellRepository.getActiveCell(cellReference.get()).get().hasChild()) {
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
