package entities.cells;

import java.io.File;

import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

import controllers.ConstantController;

import entities.Column;
import ibd.table.Table;

import ibd.table.prototype.Prototype;

public final class JDBCTableCell extends TableCell {
    public JDBCTableCell(mxCell jCell, String name, List<Column> columns, Table table, Prototype prototype, File headerFile) {
        super(jCell, name, columns, table, prototype, headerFile);
    }
    public JDBCTableCell(mxCell jCell, String name, Table table, File headerFile) {
        super(jCell, name, table, headerFile);
    }
    public JDBCTableCell(String name, Table table, File headerFile) {
        super(new mxCell(null, new mxGeometry(), ConstantController.J_CELL_FYI_TABLE_STYLE), name, table, headerFile);
    }
    public JDBCTableCell(JDBCTableCell tableCell, mxCell jCell) {
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
        
        JDBCTableCell copy = new JDBCTableCell(newCell, this.getName(), this.getTable(), this.getHeaderFile());
        copy.alias = this.alias;
        return copy;
    }
}
