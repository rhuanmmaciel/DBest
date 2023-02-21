package util;

import java.util.ArrayList;
import java.util.List;

import entities.TableCell;
import gui.forms.FormFramePrimaryKey;
import sgbd.prototype.Column;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.table.SimpleTable;
import sgbd.table.Table;

public class TableCreator {
	
	private static List<RowData> getRowData(String tableName, List<String> columnsName, List<List<String>> lines){
		
		List<RowData> rows = new ArrayList<>();
		
		for(int i = 0; i < columnsName.size(); i++) {
			
			String name = columnsName.get(i);
			name += "."+tableName;
			columnsName.remove(i);
			columnsName.add(i, name);
			
		}
		
		int k = 0;
		for(List<String> line : lines) {

			RowData rowData = new RowData();
			int i = 0;
			for(String data : line) {
				
				if(FindType.isInt(data)) {
					
					rowData.setInt(columnsName.get(i), Integer.parseInt(data));
			
				}else if(FindType.isFloat(data)) {
					
					rowData.setFloat(columnsName.get(i), Float.parseFloat(data));
					
				}else {
					
					rowData.setString(columnsName.get(i), data);
					
				}
				i++;
				
			}
			if(FormFramePrimaryKey.getValues()[0] != null)
				rowData.setInt(FormFramePrimaryKey.getColumnName()+"."+tableName, FormFramePrimaryKey.getValues()[k++]);
			
			rows.add(rowData);
			
		}
		
		if(FormFramePrimaryKey.getValues()[0] != null)			
			columnsName.add(FormFramePrimaryKey.getColumnName()+"."+tableName);
		
		return rows;
		
	}
	
	public static void createTable(TableCell tableCell, String tableName, List<String> columnsName, List<List<String>> lines) {
		
		List<RowData> rows = new ArrayList<>(getRowData(tableName, columnsName, lines));
		
		Prototype prototype = new Prototype();
		 
		String primaryKeyName = FormFramePrimaryKey.getColumnName();
		
		int index = -1;
		for(int i = 0; i < columnsName.size(); i++) {
			
			if(columnsName.get(i).contains(primaryKeyName) && index < 0) index = i;
			
		}
				
		prototype.addColumn(columnsName.get(index), 15, Column.PRIMARY_KEY);
		columnsName.remove(index);
		
		columnsName.forEach(x -> {prototype.addColumn(x, 100, Column.NONE);});
		
	    Table table = SimpleTable.openTable(tableName, prototype);
	    table.open();
	    rows.stream().forEach(x -> {table.insert(x);});
	    
	    tableCell.setName(tableName);
	    tableCell.setStyle("tabela");
	    tableCell.setTable(table);
	    tableCell.setPrototype(prototype);
	    
	}
	
}
