package entities.cells;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxCell;

import controller.ConstantController;
import entities.Column;
import enums.ColumnDataType;
import sgbd.prototype.Prototype;
import sgbd.query.sourceop.TableScan;
import sgbd.table.Table;
import sgbd.table.components.Header;
import sgbd.util.global.Util;

public abstract sealed class TableCell extends Cell permits CsvTableCell, FyiTableCell {

	private final Table table;
	private final Prototype prototype;
	private final File headerFile;

	public TableCell(mxCell jCell, String name, String style, Table table, Prototype prototype, File headerFile){

		super(name, style, jCell, ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT);

		this.headerFile = headerFile;
		this.table = table;
		this.prototype = prototype;
		setOperator(new TableScan(table));

	}

	public TableCell(mxCell jCell, String name, String style, List<Column> columns, Table table, Prototype prototype,
					 File headerFile) {
		
		this(jCell, name, style, table, prototype, headerFile);
		setColumns(columns);

	}
	
	public TableCell(mxCell jCell, String name, String style, Table table, File headerFile) {

		this(jCell, name, style, table, table.getHeader().getPrototype(), headerFile);
		setColumns();

	}

	public File getHeaderFile(){
		return headerFile;
	}

	public Table getTable() {
		return table;
	}
	
	public Prototype getPrototype() {
		return prototype;
	}

	private void setColumns(List<Column> columns) {
		
		this.columns = columns;
		
	}
	
	public boolean hasParents() {
		return false;
	}
	
	public List<Cell> getParents() {

		return List.of();

	}
	
	public boolean hasError() {
		return false;
	}
	
	public void setColumns() {
		
		List<sgbd.prototype.column.Column> prototypeColumns = table.getHeader().getPrototype().getColumns()
				.stream()
				.filter(x -> this instanceof FyiTableCell || !x.getName().equals(ConstantController.PK_CSV_TABLE_NAME))
				.toList();

		List<Column> columns = new ArrayList<>();
		
		for(sgbd.prototype.column.Column pColumn : prototypeColumns) {

			ColumnDataType type = switch (Util.typeOfColumn(pColumn)) {
				case "int" -> ColumnDataType.INTEGER;
				case "long" -> ColumnDataType.LONG;
				case "float" -> ColumnDataType.FLOAT;
				case "double" -> ColumnDataType.DOUBLE;
				case "string" -> pColumn.getSize() == 1 ? ColumnDataType.CHARACTER : ColumnDataType.STRING;
				default -> ColumnDataType.NONE;
			};

			columns.add(new Column(pColumn.getName(), getName(), type, pColumn.isPrimaryKey()));
		}

		setColumns(columns);
		
	}

	@Override
	public boolean hasParentErrors() {
		return false;
	}
	
}
