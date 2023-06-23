package entities.cells;

import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxCell;

import entities.Column;
import enums.ColumnDataType;
import sgbd.prototype.Prototype;
import sgbd.query.sourceop.TableScan;
import sgbd.table.Table;
import sgbd.util.global.Util;

public final class TableCell extends Cell{

	private Table table;
	private Prototype prototype;
	
	public TableCell(mxCell jCell, String name, String style, List<Column> columns, Table table, Prototype prototype) {
		
		super(name, style, jCell, 80, 50);
		setColumns(columns);
		setTable(table);
		setPrototype(prototype);

	}
	
	public TableCell(mxCell jCell, String name, String style, Table table) {
		
		super(name, style, jCell, 80, 50);
		setTable(table);
		setPrototype(table.getHeader().getPrototype());
		setColumns();
		
	}
	
	private void setTable(Table table) {

		this.table = table;
		setOperator(new TableScan(table));

	}
	
	public Table getTable() {

		return table;
	}
	
	private void setPrototype(Prototype prototype) {
		this.prototype = prototype;
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
		
		List<sgbd.prototype.column.Column> prototypeColumns = table.getHeader().getPrototype().getColumns();

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
