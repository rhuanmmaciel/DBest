package entities.cells;

import com.mxgraph.model.mxCell;

import controller.ConstantController;

import entities.Column;

import sgbd.prototype.Prototype;
import sgbd.query.Operator;
import sgbd.query.unaryop.FilterColumnsOperator;
import sgbd.table.Table;

import java.util.List;

public final class CSVTableCell extends TableCell {

    public CSVTableCell(mxCell jCell, String name, String style, List<Column> columns, Table table, Prototype prototype) {
        super(jCell, name, style, columns, table, prototype);
    }

    public CSVTableCell(mxCell jCell, String name, String style, Table table) {
        super(jCell, name, style, table);
    }

    @Override
    public void setOperator(Operator operator) {
        this.operator = new FilterColumnsOperator(
            operator, List.of(String.format("%s.%s", this.getTable().getTableName(), ConstantController.PK_CSV_TABLE_NAME))
        );
    }
}
