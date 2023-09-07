package entities.cells;

import com.mxgraph.model.mxCell;
import entities.Column;
import sgbd.prototype.Prototype;
import sgbd.query.Operator;
import sgbd.table.Table;

import java.io.File;
import java.util.List;

public final class FyiTableCell extends TableCell {
    public FyiTableCell(mxCell jCell, String name, List<Column> columns, Table table, Prototype prototype,
                        File headerFile ) {
        super(jCell, name, columns, table, prototype, headerFile);
    }

    public FyiTableCell(mxCell jCell, String name, Table table, File headerFile) {

        super(jCell, name, table, headerFile);

    }

    public FyiTableCell(String name, Table table, File headerFile) {

        super(null, name, table, headerFile);

    }

    public FyiTableCell(FyiTableCell tableCell, mxCell jCell){

        super(jCell, tableCell.getName(), tableCell.getTable(),
                tableCell.getHeaderFile());

    }

    @Override
    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}
