package gui.commands;

import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import entities.utils.cells.CellUtils;

import gui.frames.main.MainFrame;

public class RemoveCellCommand extends BaseUndoableRedoableCommand {

    private final AtomicReference<mxCell> cellReference;

    public RemoveCellCommand(AtomicReference<mxCell> cellReference) {
        this.cellReference = cellReference;
    }

    @Override
    public void execute() {
        CellUtils.desactivateActiveJCell(MainFrame.getGraph(), this.cellReference.get());
    }

    @Override
    public void undo() {
        CellUtils.activateInactiveJCell(MainFrame.getGraph(), this.cellReference.get());
    }

    @Override
    public void redo() {
        CellUtils.desactivateActiveJCell(MainFrame.getGraph(), this.cellReference.get());
    }
}
