package entities.cells;

import java.io.File;

import java.util.List;

import com.mxgraph.model.mxCell;

import entities.Column;

import sgbd.prototype.Prototype;
import sgbd.table.Table;

public final class FYITableCell extends TableCell {

    public FYITableCell(mxCell jCell, String name, List<Column> columns, Table table, Prototype prototype, File headerFile) {
        super(jCell, name, columns, table, prototype, headerFile);
    }

    public FYITableCell(mxCell jCell, String name, Table table, File headerFile) {
        super(jCell, name, table, headerFile);
    }

    public FYITableCell(String name, Table table, File headerFile) {
        super(null, name, table, headerFile);
    }

    public FYITableCell(FYITableCell tableCell, mxCell jCell) {
        super(jCell, tableCell.getName(), tableCell.getTable(), tableCell.getHeaderFile());
    }
}
