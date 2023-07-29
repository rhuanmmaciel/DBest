package entities.cells;

import com.mxgraph.model.mxCell;
import entities.Column;
import sgbd.prototype.Prototype;
import sgbd.query.Operator;
import sgbd.table.Table;

import java.util.List;

public final class FyiTableCell extends TableCell {
    public FyiTableCell(mxCell jCell, String name, String style, List<Column> columns, Table table, Prototype prototype) {
        super(jCell, name, style, columns, table, prototype);
    }

    public FyiTableCell(mxCell jCell, String name, String style, Table table) {

        super(jCell, name, style, table);

    }

    @Override
    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}
