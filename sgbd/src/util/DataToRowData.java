package util;

import java.util.ArrayList;
import java.util.List;

import gui.forms.FormFramePrimaryKey;
import sgbd.prototype.RowData;

public class DataToRowData {
	
	public static List<RowData> getRowData(String tableName, List<String> columnsName, List<List<String>> lines){
		
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
	
	/*
	public static void getRowData(List<RowData> rows, String[] columns, String[] columnsName, String tableName, int iterator){
		
		RowData data = new RowData();
		
		int j = 0;
		
		for(String i : columns) {
			
			if(FindType.isInt(i)) {
				
				data.setInt(columnsName[j]+"."+tableName, Integer.parseInt(i));
		
			}else if(FindType.isFloat(i)) {
				
				data.setFloat(columnsName[j]+"."+tableName, Float.parseFloat(i));
				
			}else {
				
				data.setString(columnsName[j]+"."+tableName, i);
				
			}
			
			j++;
			
		}
		
		if(FormFramePrimaryKey.getValues()[0] != null)
			data.setInt(FormFramePrimaryKey.getColumnName()+"."+tableName, FormFramePrimaryKey.getValues()[iterator++]);
		
		rows.add(data);		
		
	}*/
	
}
