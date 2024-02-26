package controllers.commands;

import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;

import controllers.MainController;

import entities.cells.CSVTableCell;
import entities.cells.FYITableCell;
import entities.cells.MemoryTableCell;
import entities.cells.TableCell;
import entities.utils.cells.CellUtils;

import enums.FileType;

import gui.frames.main.MainFrame;

public class InsertTableCellCommand extends BaseUndoableRedoableCommand {

    private final AtomicReference<mxCell> jCellReference, ghostJCellReference;

    public InsertTableCellCommand(AtomicReference<mxCell> jCellReference, AtomicReference<mxCell> ghostJCellReference) {
        this.jCellReference = jCellReference;
        this.ghostJCellReference = ghostJCellReference;
    }

    @Override
    public void execute() {
        if (
            this.jCellReference.get() == null ||
            this.jCellReference.get() == this.ghostJCellReference.get() ||
            CellUtils.activeCellsContainsKey(this.jCellReference.get())
        ) return;

        mxCell parent = (mxCell) MainFrame.getGraph().getDefaultParent();
        Object[] vertices = mxGraphModel.getChildVertices(MainFrame.getGraph().getModel(), parent);

        for (Object vertex : vertices) {
            if (!CellUtils.activeCellsContainsKey((mxCell) vertex)) {
                this.jCellReference.set((mxCell) vertex);
            }
        }

        TableCell tableCell = MainController.getTables().get(this.jCellReference.get().getValue());

        switch (tableCell){

            case FYITableCell fyiTableCell -> new FYITableCell(fyiTableCell, this.jCellReference.get());
            case CSVTableCell csvTableCell -> new CSVTableCell(csvTableCell, this.jCellReference.get());
            case MemoryTableCell memoryTableCell -> new MemoryTableCell(memoryTableCell, this.jCellReference.get());

        }

    }

    @Override
    public void undo() {
        CellUtils.deactivateActiveJCell(MainFrame.getGraph(), this.jCellReference.get());
    }

    @Override
    public void redo() {
        CellUtils.activateInactiveJCell(MainFrame.getGraph(), this.jCellReference.get());
    }
}
