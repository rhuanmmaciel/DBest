package gui.commands;

import java.awt.Cursor;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import controllers.ConstantController;
import controllers.MainController;

import entities.Action.CurrentAction;
import entities.Edge;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;
import entities.utils.edges.EdgeUtils;

import gui.frames.main.MainFrame;

public class InsertEdgeCommand extends BaseUndoableRedoableCommand {

    private final AtomicReference<mxCell> cellReference, invisibleCellReference;

    private final AtomicReference<Edge> edgeReference;

    private final AtomicReference<CurrentAction> currentActionReference;

    public InsertEdgeCommand(
        AtomicReference<mxCell> cellReference,
        AtomicReference<mxCell> invisibleCellReference,
        AtomicReference<Edge> edgeReference,
        AtomicReference<CurrentAction> currentActionReference
    ) {
        this.cellReference = cellReference;
        this.invisibleCellReference = invisibleCellReference;
        this.edgeReference = edgeReference;
        this.currentActionReference = currentActionReference;
    }

    @Override
    public void execute() {
        MainFrame.getGraph().getModel().getValue(this.cellReference.get());

        Optional<Cell> optionalCell = CellUtils.getActiveCell(this.cellReference.get());

        if (!this.edgeReference.get().isDifferentFrom(this.cellReference.get())) return;

        if (!this.edgeReference.get().isReady()) {
            this.edgeReference.get().addChild(this.cellReference.get());
        }

        if (this.edgeReference.get().isReady() && optionalCell.isPresent()) {
            this.commandController.execute(new ExecuteOperationCommand(
                this.cellReference, this.invisibleCellReference, this.edgeReference, optionalCell.get()
            ));
        }

        this.currentActionReference.set(ConstantController.NONE_ACTION);
    }

    @Override
    public void undo() {
        while (this.commandController.canUndo()) {
            this.commandController.undo();
        }

        MainFrame.getGraphComponent().getGraphControl().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        CellUtils.desactivateActiveJCell(this.invisibleCellReference.get());
        EdgeUtils.removeEdge(this.edgeReference.get());
        EdgeUtils.removeEdges(this.cellReference.get());
        MainController.resetCurrentEdgeReferenceValue();

        this.currentActionReference.set(ConstantController.NONE_ACTION);
    }

    @Override
    public void redo() {
        CellUtils.activateInactiveJCell(this.invisibleCellReference.get());
        EdgeUtils.addEdge(this.edgeReference.get(), this.cellReference.get());

        while (this.commandController.canRedo()) {
            this.commandController.redo();
        }
    }
}
