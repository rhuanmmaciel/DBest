package gui.commands;

import com.mxgraph.model.mxCell;

import controllers.MainController;

import entities.cells.TableCell;
import entities.utils.cells.CellUtils;

import gui.frames.main.MainFrame;

public class ImportTableCommand extends BaseUndoableRedoableCommand {

    private final TableCell tableCell;

    private mxCell tableJCell;

    public ImportTableCommand(TableCell tableCell) {
        this.tableCell = tableCell;
        this.tableJCell = null;
    }

    @Override
    public void execute() {
        boolean shouldCreateTable = MainController
            .getTables()
            .keySet()
            .stream()
            .noneMatch(existingTableName -> existingTableName.equals(this.tableCell.getName()));

        if (!shouldCreateTable) return;

        this.tableJCell = (mxCell) MainFrame
            .getTablesGraph()
            .insertVertex(
                MainFrame.getTablesGraph().getDefaultParent(), null, this.tableCell.getName(),
                0, MainController.getCurrentTableYPosition(), this.tableCell.getWidth(),
                this.tableCell.getHeight(), this.tableCell.getStyle()
            );

        CellUtils.addCell(this.tableJCell, this.tableCell);

        MainController.getTables().put(this.tableCell.getName(), this.tableCell);

        MainFrame.getTablesPanel().revalidate();

        MainController.incrementCurrentTableYPosition(40);
    }

    @Override
    public void undo() {
        CellUtils.deactivateActiveJCell(MainFrame.getTablesGraph(), this.tableJCell);

        MainController.getTables().remove(this.tableCell.getName());

        MainFrame.getTablesPanel().revalidate();

        MainController.decrementCurrentTableYPosition(40);
    }

    @Override
    public void redo() {
        CellUtils.activateInactiveJCell(MainFrame.getTablesGraph(), this.tableJCell);

        MainController.getTables().put(this.tableCell.getName(), this.tableCell);

        MainFrame.getTablesPanel().revalidate();

        MainController.incrementCurrentTableYPosition(40);
    }
}
