package entities.cells;

import com.mxgraph.model.mxCell;
import entities.Column;
import sgbd.prototype.Prototype;
import sgbd.query.Operator;
import sgbd.table.Table;

import java.io.File;
import java.util.List;

public final class FyiTableCell extends TableCell {
    public FyiTableCell(mxCell jCell, String name, String style, List<Column> columns, Table table, Prototype prototype,
                        File headerFile ) {
        super(jCell, name, style, columns, table, prototype, headerFile);
    }

    public FyiTableCell(mxCell jCell, String name, String style, Table table, File headerFile) {

        super(jCell, name, style, table, headerFile);

    }

    public FyiTableCell(FyiTableCell tableCell, mxCell jCell){

        super(jCell, tableCell.getName(), tableCell.getStyle(), tableCell.getTable(),
                tableCell.getHeaderFile());

    }

    @Override
    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}
