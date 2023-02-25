package entities;

import java.util.List;

import sgbd.prototype.Prototype;
import sgbd.query.Operator;
import sgbd.query.sourceop.TableScan;
import sgbd.table.Table;
import util.Columns;
import util.TableFormat;

public class TableCell extends Cell{

	private Table table;
	private Prototype prototype;
	
	public TableCell() {
		super(null, null, null);
	}
	
	public void setTable(Table table) {
		this.table = table;
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
	
	public List<String> getColumnsName(){
		
		return Columns.getColumns(prototype.getColumns());
		
	}
	
	public List<List<String>> getContent(){
		
		return TableFormat.getRows(new TableScan(table, getColumnsName()), getColumns());
		
	}
	
	public Operator getData() {
		
		return new TableScan(table);
	
	}

	@Override
	public void setColumns(List<Column> columns) {
		
		this.columns = columns;
		
	}
	
}
