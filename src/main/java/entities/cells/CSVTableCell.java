package entities.cells;

import java.io.File;

import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

import controllers.ConstantController;

import entities.Column;
import ibd.query.Operation;
import ibd.table.Table;
import ibd.table.prototype.Prototype;


public final class CSVTableCell extends TableCell {

    public CSVTableCell(mxCell jCell, String name, List<Column> columns, Table table, Prototype prototype, File header) {
        super(jCell, name, columns, table, prototype, header);
    }

    public CSVTableCell(CSVTableCell csvTableCell, mxCell jCell) {
        super(jCell, csvTableCell.getName(), csvTableCell.getTable(), csvTableCell.getHeaderFile());
    }

    public CSVTableCell(String name, Table table, File headerFile) {
        super(new mxCell(name, new mxGeometry(), ConstantController.J_CELL_CSV_TABLE_STYLE), name, table, headerFile);
    }

    public CSVTableCell(mxCell jCell, String name, Table table, File headerFile) {
        super(jCell, name, table, headerFile);
    }

    @Override
    public void setOperator(Operation operator) {
//        this.operator = new FilterColumnsOperator(
//            operator,
//            List.of(String.format("%s.%s", this.getTable().getSourceName(), ConstantController.PRIMARY_KEY_CSV_TABLE_NAME))
//        );
    this.operator = operator;
    }
    
    @Override
    public Cell copy() {
        mxCell newCell;
        try {
            newCell = (mxCell) this.jCell.clone();
        } catch (CloneNotSupportedException e) {
            newCell = new mxCell(this.jCell.getValue(), this.jCell.getGeometry(), this.jCell.getStyle());
        }
        
        CSVTableCell copy = new CSVTableCell(newCell, this.getName(), this.getTable(), this.getHeaderFile());
        copy.alias = this.alias;
        return copy;
    }
}
