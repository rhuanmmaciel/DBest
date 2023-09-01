package entities.cells;

import com.mxgraph.model.mxCell;

import controller.ConstantController;

import entities.Column;

import enums.ColumnDataType;

import sgbd.prototype.Prototype;
import sgbd.query.sourceop.TableScan;
import sgbd.table.Table;
import sgbd.util.global.Util;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class TableCell extends Cell permits CSVTableCell, FyiTableCell {

    private Table table;

    private Prototype prototype;

    protected TableCell(mxCell jCell, String name, String style, List<Column> columns, Table table, Prototype prototype) {
        super(name, style, jCell, ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT);

        this.setColumns(columns);
		this.setTable(table);
		this.setPrototype(prototype);
    }

    protected TableCell(mxCell jCell, String name, String style, Table table) {
        super(name, style, jCell, ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT);

		this.setTable(table);
		this.setPrototype(table.getHeader().getPrototype());
		this.setColumns();
    }

    private void setTable(Table table) {
        this.table = table;

		this.setOperator(new TableScan(table));
    }

    public Table getTable() {
        return this.table;
    }

    private void setPrototype(Prototype prototype) {
        this.prototype = prototype;
    }

    public Prototype getPrototype() {
        return this.prototype;
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
            .filter(column -> this instanceof CSVTableCell && !column.getName().equals(ConstantController.PK_CSV_TABLE_NAME))
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
