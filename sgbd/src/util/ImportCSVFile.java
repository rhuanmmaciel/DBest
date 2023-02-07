package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.forms.FormFramePrimaryKey;
import sgbd.prototype.Column;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.table.SimpleTable;
import sgbd.table.Table;

public class ImportCSVFile {
	
	private static Prototype currentPrototype;
	private static Table currentTable;
	private static String currentFileName;
	private static List<String> columnsNameList = new ArrayList<>();
	private static List<RowData> rows = new ArrayList<>();
	private static List<List<String>> allData = new ArrayList<>();
	private static String primaryKeyName;
	private static String tableName;
	
	public static void importCSVFile(){
		
		JFileChooser fileUpload = new JFileChooser();
		//fileUpload.showOpenDialog(null);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
		fileUpload.setFileFilter(filter);
		
		int res = fileUpload.showOpenDialog(null);
		
		if(res == JFileChooser.APPROVE_OPTION) {
			
			createData(fileUpload);
			createTable(currentFileName, columnsNameList, rows);
			
		}
		
	}
	
	private static void getData(JFileChooser fileUpload) {
		
		try(BufferedReader br = new BufferedReader(new FileReader(fileUpload.getSelectedFile().getAbsolutePath()))){
			
			String columnsName[] = br.readLine().replace("\"", "").replace(" ", "").split(",");
			currentFileName = fileUpload.getSelectedFile().getName().toUpperCase().substring(0, fileUpload.getSelectedFile().getName().indexOf("."));		
			
			allData.add(Arrays.asList(columnsName));
			
			String line = br.readLine();
			while(line != null) {
				
				String columns[] = line.split(",");
				
				allData.add(Arrays.asList(columns));
				
				line = br.readLine();
			}
			
		}catch(IOException e){
			
			e.printStackTrace();
		
		}
		
	}
	
	public static void reset() {
		columnsNameList.clear();
		rows.clear();
		allData.clear();
	}
	
	private static List<RowData> createData(JFileChooser fileUpload){
		
		columnsNameList.clear();
		rows.clear();
		allData.clear();
		
		try(BufferedReader br = new BufferedReader(new FileReader(fileUpload.getSelectedFile().getAbsolutePath()))){
			
			getData(fileUpload);
			new FormFramePrimaryKey(allData);
			
			String columnsName[] = br.readLine().replace("\"", "").replace(" ", "").split(",");
			currentFileName = fileUpload.getSelectedFile().getName().toUpperCase().substring(0, fileUpload.getSelectedFile().getName().indexOf("."));
			
			int k = 0;
			String line = br.readLine();
			while(line != null) {
				
				String columns[] = line.split(",");
				
				RowData data = new RowData();
				
				int j = 0;
				
				for(String i : columns) {
					
					if(FindType.isInt(i)) {
						
						data.setInt(columnsName[j]+"."+currentFileName, Integer.parseInt(i));
				
					}else if(FindType.isFloat(i)) {
						
						data.setFloat(columnsName[j]+"."+currentFileName, Float.parseFloat(i));
						
					}else {
						
						data.setString(columnsName[j]+"."+currentFileName, i);
						
					}
					
					j++;
					
				}
				
				if(FormFramePrimaryKey.getValues()[0] != null)
					data.setInt(FormFramePrimaryKey.getColumnName()+"."+currentFileName, FormFramePrimaryKey.getValues()[k++]);
				
				rows.add(data);
				
				line = br.readLine();
			}
			
			columnsNameList.addAll(Arrays.asList(columnsName));
			if(FormFramePrimaryKey.getValues()[0] != null)			
				columnsNameList.add(FormFramePrimaryKey.getColumnName());
			
			for(int i = 0; i < columnsNameList.size(); i++) {
			
				String test = columnsNameList.get(i);
				columnsNameList.remove(i);
				columnsNameList.add(i, test.concat("."+currentFileName));
				
			}
						
		}catch(IOException e) {
			
			e.printStackTrace();
		
		}
		
		return rows;
	}

	public static void createTable(String tableName, List<String> columnsName, List<RowData> rows) {
		
		ImportCSVFile.tableName = tableName;
		currentPrototype = new Prototype();
		 
		primaryKeyName = FormFramePrimaryKey.getColumnName();
		
		int index = -1;
		for(int i = 0; i < columnsName.size(); i++) {
			
			if(columnsName.get(i).contains(primaryKeyName) && index < 0) index = i;
			
		}
				
		currentPrototype.addColumn(columnsName.get(index), 15, Column.PRIMARY_KEY);
		columnsName.remove(index);
		
		columnsName.forEach(x -> {currentPrototype.addColumn(x, 100, Column.NONE);});
		
	    currentTable = SimpleTable.openTable(tableName, currentPrototype);
	    currentTable.open();
	    rows.stream().forEach(x -> {currentTable.insert(x);});
	    
	}
	
	public static Table getTable() {
		
		return currentTable;
		
	}
	
	public static String getTableName() {
		
		return tableName;
		
	}
	
	public static Prototype getPrototype() {
		
		return currentPrototype;
		
	}
	
	public static String getFileName() {
		
		return currentFileName;
		
	}
	
}