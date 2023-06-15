package entities.cells;

import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxCell;

import sgbd.util.statics.Util;
import entities.Column;
import enums.ColumnDataType;
import sgbd.prototype.Prototype;
import sgbd.query.Operator;
import sgbd.query.sourceop.TableScan;
import sgbd.table.Table;

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
		
		List<sgbd.prototype.Column> prototypeColumns = table.getHeader().getPrototype().getColumns();

		List<Column> columns = new ArrayList<>();
		
		for(sgbd.prototype.Column pColumn : prototypeColumns) {

			ColumnDataType type;
			switch(Util.typeOfColumn(pColumn)) {
			
				case "int":
				
					type = ColumnDataType.INTEGER;
					break;
				
				case "float":
					
					type = ColumnDataType.FLOAT;
					break;
					
				case "string":
					
					type = pColumn.getSize() == 1 ? ColumnDataType.CHARACTER : ColumnDataType.STRING;
					
					break;
				default:
					
					type = ColumnDataType.NONE;
			
			}
			
			columns.add(new Column(pColumn.getName(), getName(), type, pColumn.isPrimaryKey()));
		}
		
		setColumns(columns);
		
	}

	@Override
	public boolean hasParentErrors() {
		return false;
	}
	
}
