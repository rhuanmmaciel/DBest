package entities.cells;

import java.io.File;

import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

import controllers.ConstantController;

import entities.Column;
import ibd.table.Table;

import ibd.table.prototype.Prototype;

public final class FYITableCell extends TableCell {

    public FYITableCell(mxCell jCell, String name, List<Column> columns, Table table, Prototype prototype, File headerFile) {
        super(jCell, name, columns, table, prototype, headerFile);
    }

    public FYITableCell(mxCell jCell, String name, Table table, File headerFile) {
        super(jCell, name, table, headerFile);
    }

    public FYITableCell(String name, Table table, File headerFile) {
        super(new mxCell(name, new mxGeometry(), ConstantController.J_CELL_FYI_TABLE_STYLE), name, table, headerFile);
    }

    public FYITableCell(FYITableCell tableCell, mxCell jCell) {
        super(jCell, tableCell.getName(), tableCell.getTable(), tableCell.getHeaderFile());
    }
    
    @Override
    public Cell copy() {
        mxCell newCell;
        try {
            newCell = (mxCell) this.jCell.clone();
        } catch (CloneNotSupportedException e) {
            newCell = new mxCell(this.jCell.getValue(), this.jCell.getGeometry(), this.jCell.getStyle());
        }
        
        FYITableCell copy = new FYITableCell(newCell, this.getName(), this.getTable(), this.getHeaderFile());
        copy.alias = this.alias;
        return copy;
    }
}
