package entities;

import java.util.List;

import entities.util.TableFormat;
import sgbd.prototype.Prototype;
import sgbd.query.Operator;
import sgbd.query.sourceop.TableScan;
import sgbd.table.Table;

public class TableCell extends Cell{

	private Table table;
	private Prototype prototype;
	
	public TableCell(int length, int width) {
		super(null, null, null, 0, 0, length, width);
	}
	
	public void setTable(Table table) {
		
		this.table = table;
		table.open();
		content = TableFormat.getRows(new TableScan(table, getColumnsName()), getColumns());
	}
	
	public Table getTable() {
		return table;
	}
	
	public void setPrototype(Prototype prototype) {
		this.prototype = prototype;
	}
	
	public Prototype getPrototype() {
		return prototype;
	}
	
	public Operator getData() {
		
		return new TableScan(table);
	
	}

	public void setColumns(List<Column> columns) {
		
		this.columns = columns;
		
	}
	
}
