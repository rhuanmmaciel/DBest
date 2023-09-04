package entities.cells;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;
import controller.ConstantController;
import controller.MainController;
import entities.Column;
import sgbd.prototype.Prototype;
import sgbd.query.Operator;
import sgbd.query.unaryop.FilterColumnsOperator;
import sgbd.table.Table;

import java.io.File;
import java.util.List;

public final class CsvTableCell extends TableCell {

    public CsvTableCell(mxCell jCell, String name, String style, List<Column> columns, Table table, Prototype prototype,
                        File header) {

        super(jCell, name, style, columns, table, prototype, header);

    }

    public CsvTableCell(CsvTableCell csvTableCell, mxCell jCell) {

        super(jCell, csvTableCell.getName(), csvTableCell.getStyle(), csvTableCell.getTable(),
                csvTableCell.getHeaderFile());

    }

    @Override
    public void setOperator(Operator operator) {
        this.operator = new FilterColumnsOperator(operator, List.of(getTable().getTableName()+"."+
                ConstantController.PK_CSV_TABLE_NAME));
    }
}
