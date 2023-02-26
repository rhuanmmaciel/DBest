package util;

import java.util.ArrayList;
import java.util.List;

import entities.TableCell;
import enums.ColumnDataType;
import gui.frames.forms.FormFramePrimaryKey;
import sgbd.prototype.Column;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.table.SimpleTable;
import sgbd.table.Table;

public class TableCreator {
	
	private static List<RowData> getRowData(String tableName, List<entities.Column> columns, List<List<String>> lines){
		
		List<RowData> rows = new ArrayList<>();
		
		for(int i = 0; i < columns.size(); i++) {
			
			String name = columns.get(i).getName();
			name += "."+tableName;
			ColumnDataType type = columns.get(i).getType();
			columns.remove(i);
			columns.add(i, new entities.Column(name, type));
			
		}
		
		int k = 0;
		for(List<String> line : lines) {

			RowData rowData = new RowData();
			int i = 0;
			
			for(String data : line) {
				
				if(columns.get(i).getType() == ColumnDataType.INTEGER) {
					
					rowData.setInt(columns.get(i).getName(), Integer.parseInt(data));
			
				}else if(columns.get(i).getType() == ColumnDataType.FLOAT) {
					
					rowData.setFloat(columns.get(i).getName(), Float.parseFloat(data));
					
				}else {
					
					rowData.setString(columns.get(i).getName(), data);
					
				}
				i++;
				
			}
			if(FormFramePrimaryKey.getValues()[0] != null)
				rowData.setInt(FormFramePrimaryKey.getColumnName()+"."+tableName, FormFramePrimaryKey.getValues()[k++]);
			
			rows.add(rowData);
			
		}
		
		if(FormFramePrimaryKey.getValues()[0] != null)			
			columns.add(new entities.Column(FormFramePrimaryKey.getColumnName()+"."+tableName, ColumnDataType.INTEGER));
		
		return rows;
		
	}
	
	public static void createTable(TableCell tableCell, String tableName, List<entities.Column> columns, List<List<String>> lines) {
		
		List<RowData> rows = new ArrayList<>(getRowData(tableName, columns, lines));
		
		Prototype prototype = new Prototype();
		 
		String primaryKeyName = FormFramePrimaryKey.getColumnName();
		
		int index = -1;
		for(int i = 0; i < columns.size(); i++) {
			
			if(columns.get(i).getName().toLowerCase().contains(primaryKeyName.toLowerCase()) && index < 0) index = i;
			
		}
				
		prototype.addColumn(columns.get(index).getName(), 15, Column.PRIMARY_KEY);
		entities.Column primaryKeyColumn = columns.get(index);
		columns.remove(index);
		
		for(entities.Column column: columns) {
			
			if(column.getType() == ColumnDataType.INTEGER) {
				
				prototype.addColumn(column.getName(), 100, Column.SIGNED_INTEGER_COLUMN);		
			
			}else if(column.getType() == ColumnDataType.FLOAT) {
				
				prototype.addColumn(column.getName(), 100, Column.FLOATING_POINT);	
				
			}else if(column.getType() == ColumnDataType.STRING) {
				
				prototype.addColumn(column.getName(), 100, Column.STRING);	
				
			}else {
				
				prototype.addColumn(column.getName(), 100, Column.NONE);	
				
			}
			
		}
		
	    Table table = SimpleTable.openTable(tableName, prototype);
	    table.open();
	    rows.stream().forEach(x -> {table.insert(x);});
	    
	    tableCell.setName(tableName);
	    tableCell.setStyle("tabela");
	    tableCell.setTable(table);
	    tableCell.setPrototype(prototype);
	    columns.add(primaryKeyColumn);
	    tableCell.setColumns(columns);
	    
	}
	
}
