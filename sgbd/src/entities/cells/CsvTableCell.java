package entities.cells;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import controller.ConstantController;
import entities.Column;
import sgbd.prototype.Prototype;
import sgbd.query.Operator;
import sgbd.query.unaryop.FilterColumnsOperator;
import sgbd.source.table.Table;

import java.io.File;
import java.util.List;

public final class CsvTableCell extends TableCell {

    public CsvTableCell(mxCell jCell, String name, List<Column> columns, Table table, Prototype prototype,
                        File header) {

        super(jCell, name, columns, table, prototype, header);

    }

    public CsvTableCell(CsvTableCell csvTableCell, mxCell jCell) {

        super(jCell, csvTableCell.getName(), csvTableCell.getTable(),
                csvTableCell.getHeaderFile());

    }

    public CsvTableCell(String name, Table table, File headerFile) {

        super(new mxCell(null, new mxGeometry(), ConstantController.JCELL_CSV_STYLE), name, table, headerFile);

    }

    @Override
    public void setOperator(Operator operator) {
        this.operator = new FilterColumnsOperator(operator, List.of(getTable().getSourceName()+"."+
                ConstantController.PK_CSV_TABLE_NAME));
    }
}
