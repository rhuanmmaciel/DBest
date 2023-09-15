package entities.cells;

import java.io.File;
import java.util.List;

import com.mxgraph.model.mxCell;

import controllers.ConstantController;
import entities.Column;

import sgbd.prototype.Prototype;
import sgbd.query.Operator;
import sgbd.query.unaryop.FilterColumnsOperator;
import sgbd.table.Table;

public final class CSVTableCell extends TableCell {

    public CSVTableCell(mxCell jCell, String name, List<Column> columns, Table table, Prototype prototype, File header) {
        super(jCell, name, columns, table, prototype, header);
    }

    public CSVTableCell(CSVTableCell csvTableCell, mxCell jCell) {
        super(jCell, csvTableCell.getName(), csvTableCell.getTable(), csvTableCell.getHeaderFile());
    }

    public CSVTableCell(String name, Table table, File headerFile) {
        super(null, name, table, headerFile);
    }

    @Override
    public void setOperator(Operator operator) {
        this.operator = new FilterColumnsOperator(
            operator,
            List.of(String.format("%s.%s", this.getTable().getTableName(), ConstantController.PK_CSV_TABLE_NAME))
        );
    }
}
