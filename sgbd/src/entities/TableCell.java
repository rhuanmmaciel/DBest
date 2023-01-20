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
	
	public TableCell(String name, String style, Object cell, Table table, Prototype prototype){
		
		super(name, style, cell);
		this.table = table;
		this.prototype = prototype;
		
	}

	public Table getTable() {
		return table;
	}

	public Prototype getPrototype() {
		return prototype;
	}
	
	public List<String> getColumns(){
		
		return Columns.getColumns(prototype.getColumns());
		
	}
	
	public List<List<String>> getContent(){
		
		return TableFormat.getRows(new TableScan(table, getColumns()));
		
	}
	
	public Operator getData() {
		
		return new TableScan(table, getColumns());
	
	}
	
}
