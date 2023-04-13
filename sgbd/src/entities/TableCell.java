package entities;

import java.util.ArrayList;
import java.util.List;

import enums.ColumnDataType;
import sgbd.prototype.Prototype;
import sgbd.query.Operator;
import sgbd.query.sourceop.TableScan;
import sgbd.table.Table;
import sgbd.util.Util;

public class TableCell extends Cell{

	private Table table;
	private Prototype prototype;
	
	public TableCell(int length, int width) {
		super(null, null, null, length, width);
	}
	
	public void setTable(Table table) {
		
		this.table = table;
		
		Operator operator = new TableScan(table);
		operator.open();
		
		setOperator(operator);
		
	}
	
	public Table getTable() {
		
		table.open();
		return table;
	}
	
	public void setPrototype(Prototype prototype) {
		this.prototype = prototype;
	}
	
	public Prototype getPrototype() {
		return prototype;
	}

	public void setColumns(List<Column> columns) {
		
		this.columns = columns;
		
	}
	
	public boolean hasParents() {
		return false;
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
			
			columns.add(new Column(pColumn.getName(), getName(), type, pColumn.isPrimaryKey(), true));
		}
		
		setColumns(columns);
		
	}

	@Override
	public boolean hasParentErrors() {
		return false;
	}
	
}
