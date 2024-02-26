package entities.cells;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import enums.ColumnDataType;
import sgbd.prototype.Prototype;
import sgbd.query.sourceop.TableScan;
import sgbd.source.table.Table;
import sgbd.util.global.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract sealed class TableCell extends Cell permits CSVTableCell, FYITableCell, MemoryTableCell {

    private final Table table;

    private final Prototype prototype;

    private final File headerFile;

    protected TableCell(mxCell jCell, String name, Table table, Prototype prototype, File headerFile) {
        super(name, jCell, ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT);
        this.headerFile = headerFile;
        this.table = table;
        this.prototype = prototype;

        this.setOperator(new TableScan(table));
    }

    protected TableCell(
        mxCell jCell, String name, List<Column> columns, Table table, Prototype prototype, File headerFile
    ) {

        this(jCell, name, table, prototype, headerFile);

        this.setColumns(columns);
    }

    protected TableCell(mxCell jCell, String name, Table table, File headerFile) {
        this(jCell, name, table, table.getHeader().getPrototype(), headerFile);

        this.setColumns();
    }

    public Table getTable() {
        return this.table;
    }

    public Prototype getPrototype() {
        return this.prototype;
    }

    public File getHeaderFile() {
        return this.headerFile;
    }

    private void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public boolean hasParents() {
        return false;
    }

    @Override
    public List<Cell> getParents() {
        return new ArrayList<>();
    }

    @Override
    public boolean hasError() {
        return false;
    }

    public void setColumns() {
        List<sgbd.prototype.column.Column> prototypeColumns = this.table
            .getHeader()
            .getPrototype()
            .getColumns()
            .stream()
            .filter(column -> (!(this instanceof CSVTableCell) || !column.getName().equals(ConstantController.PRIMARY_KEY_CSV_TABLE_NAME)))
            .toList();

        List<Column> columns = new ArrayList<>();

        for (sgbd.prototype.column.Column prototypeColumn : prototypeColumns) {
            ColumnDataType dataType = switch (Util.typeOfColumn(prototypeColumn)) {
                case "int" -> ColumnDataType.INTEGER;
                case "long" -> ColumnDataType.LONG;
                case "float" -> ColumnDataType.FLOAT;
                case "double" -> ColumnDataType.DOUBLE;
                case "string" -> prototypeColumn.getSize() == 1 ? ColumnDataType.CHARACTER : ColumnDataType.STRING;
                default -> ColumnDataType.NONE;
            };
            columns.add(new Column(prototypeColumn.getName(), this.getName(), dataType, prototypeColumn.isPrimaryKey()));
        }

        this.setColumns(columns);
    }

    @Override
    public boolean hasParentErrors() {
        return false;
    }
}
