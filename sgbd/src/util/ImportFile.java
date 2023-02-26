package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import entities.Column;
import entities.TableCell;
import enums.FileType;
import gui.frames.forms.FormFrameColumnType;
import gui.frames.forms.FormFramePrimaryKey;

public class ImportFile {
	
	public ImportFile(TableCell tableCell, FileType fileType){
		
		JFileChooser fileUpload = new JFileChooser();
		
		FileNameExtensionFilter filter = null; 
		
		if(fileType == FileType.CSV) {
			
			filter = new FileNameExtensionFilter("CSV files", "csv");
			
		}else if(fileType == FileType.EXCEL) {
			
			filter = new FileNameExtensionFilter("Sheets files", "xlsx", "xls", "ods");
			
		}
		
		fileUpload.setFileFilter(filter);
		
		int res = fileUpload.showOpenDialog(null);
		
		if(res == JFileChooser.APPROVE_OPTION) {
			
			StringBuilder fileName = new StringBuilder();
			List<String> columnsName = new ArrayList<>();
			List<List<String>> lines = new ArrayList<>();
			List<Column> columns = new ArrayList<>();
			
			if(fileType == FileType.CSV) {
			
				csv(fileUpload, fileName, columnsName, lines, columns);
			
			}else if(fileType == FileType.EXCEL) {
				
				excel(fileUpload, fileName, columnsName, lines, columns);
				
			}
			
			TableCreator.createTable(tableCell, fileName.toString(), columns, lines);
			
		}else {
			
			tableCell = null;
			
		}
		
	}
	
	private static void excel(JFileChooser fileUpload, StringBuilder fileName, List<String> columnsName, List<List<String>> lines, List<Column> columns) {
		
		
	}
	
	private static void csv(JFileChooser fileUpload, StringBuilder fileName, List<String> columnsName, List<List<String>> lines, List<Column> columns){
		
		try(BufferedReader br = new BufferedReader(new FileReader(fileUpload.getSelectedFile().getAbsolutePath()))){
			
			columnsName.addAll(Arrays.asList(br.readLine().replace("\"", "").replace(" ", "").split(",")));
			fileName.append(fileUpload.getSelectedFile().getName().toUpperCase().substring(0, fileUpload.getSelectedFile().getName().indexOf(".")));
			
			String line = br.readLine();
			while(line != null) {
				
				lines.add(Arrays.asList(line.split(",")));
				
				line = br.readLine();
			}
			
			List<List<String>> aux = new ArrayList<>();
			aux.add(columnsName);
			aux.addAll(lines);
			new FormFramePrimaryKey(aux);
			new FormFrameColumnType(columns, columnsName);
			
		}catch(IOException e) {
			
			e.printStackTrace();
		
		}
		
	}
	
}