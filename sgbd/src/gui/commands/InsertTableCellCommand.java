package gui.commands;

import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;

import controllers.MainController;

import entities.cells.CSVTableCell;
import entities.cells.FYITableCell;
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
        String cellStyle = this.jCellReference.get().getStyle();

        if (cellStyle.equals(FileType.FYI.id)) {
            new FYITableCell((FYITableCell) tableCell, this.jCellReference.get());
        } else if (cellStyle.equals(FileType.CSV.id)) {
            new CSVTableCell((CSVTableCell) tableCell, this.jCellReference.get());
        }
    }

    @Override
    public void undo() {
        CellUtils.desactivateActiveJCell(MainFrame.getGraph(), this.jCellReference.get());
    }

    @Override
    public void redo() {
        CellUtils.activateInactiveJCell(MainFrame.getGraph(), this.jCellReference.get());
    }
}
