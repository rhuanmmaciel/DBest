package commands;

import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import entities.utils.cells.CellUtils;

public class RemoveCellCommand extends BaseUndoableRedoableCommand {

    private final AtomicReference<mxCell> cellReference;

    public RemoveCellCommand(AtomicReference<mxCell> cellReference) {
        this.cellReference = cellReference;
    }

    @Override
    public void execute() {
        CellUtils.desactivateActiveJCell(this.cellReference.get());
    }

    @Override
    public void undo() {
        CellUtils.activateInactiveJCell(this.cellReference.get());
    }

    @Override
    public void redo() {
        CellUtils.desactivateActiveJCell(this.cellReference.get());
    }
}
