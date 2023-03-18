package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import entities.Column;
import entities.TableCell;
import enums.FileType;
import gui.frames.forms.FormFrameColumnType;
import gui.frames.forms.FormFramePrimaryKey;

public class ImportFile {
	
	public ImportFile(TableCell tableCell, FileType fileType, List<String> tablesName, AtomicReference<Boolean> deleteCellReference){
		
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
			
			StringBuilder pkName = new StringBuilder();
			StringBuilder tableName = new StringBuilder();
			List<String> columnsName = new ArrayList<>();
			List<List<String>> lines = new ArrayList<>();
			List<Column> columns = new ArrayList<>();
			
			if(fileType == FileType.CSV) {
			
				csv(fileUpload, tableName, columnsName, lines, columns, tablesName, pkName, deleteCellReference);
			
			}else if(fileType == FileType.EXCEL) {
				
				excel(fileUpload, tableName, columnsName, lines, columns, tablesName, pkName, deleteCellReference);
				
			}
			
			if(!deleteCellReference.get())
				TableCreator.createTable(tableCell, tableName.toString(), pkName.toString(), columns, lines);
			
		}else {
			
			deleteCellReference.set(true);;
			
		}
		
	}
	
	private void excel(JFileChooser fileUpload, StringBuilder tableName, List<String> columnsName,
					   List<List<String>> lines, List<Column> columns, List<String> tablesName,
					   StringBuilder pkName, AtomicReference<Boolean> exitReference) {
		
		try {
		
			FileInputStream file = new FileInputStream(fileUpload.getSelectedFile().getAbsolutePath());
			
			tableName.append(fileUpload.getSelectedFile().getName().toUpperCase().substring(0, fileUpload.getSelectedFile().getName().indexOf(".")));
			
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);
			
			Iterator<Row> rowIterator = sheet.iterator();
			
			Row firstRow = rowIterator.next();
			Iterator<Cell> firstRowCellIterator = firstRow.cellIterator();
			
			while(firstRowCellIterator.hasNext()) {
				
				Cell cell = firstRowCellIterator.next();
				columnsName.add(cell.getStringCellValue().replace("\"", "").replace(" ", ""));
				
			}
			
			while (rowIterator.hasNext()) {
				
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
			    
				List<String> line = new ArrayList<>();
				
			    while (cellIterator.hasNext()) {
			    	
			    	Cell cell = cellIterator.next();
			    	switch (cell.getCellType()) {
			        	
				    	case NUMERIC:
				        	
				        	line.add(String.valueOf(cell.getNumericCellValue()));
				        	break;
				        	
				        case STRING:
				        		
				        	line.add(cell.getStringCellValue());
				        	break;
				        	
						case BLANK:
							
							line.add("");
							
						case BOOLEAN:
						case ERROR:
						case FORMULA:
						case _NONE:
				        	
				        default:
							
				        	break;
			    	
			    	}
			    }
			    
			    lines.add(line);
			    
			}
			    
            file.close();
            workbook.close();
            
			List<List<String>> aux = new ArrayList<>();
			aux.add(columnsName);
			aux.addAll(lines);
			
			new FormFramePrimaryKey(aux, pkName, exitReference);
			
			if(!exitReference.get())
				new FormFrameColumnType(columns, aux, tableName, tablesName, exitReference);
			
		} catch (IOException e) {

			e.printStackTrace();
		
		}
		
	}
	
	private void csv(JFileChooser fileUpload, StringBuilder tableName, List<String> columnsName,
					 List<List<String>> lines, List<Column> columns, List<String> tablesName,
					 StringBuilder pkName, AtomicReference<Boolean> exitReference){
		
		try(BufferedReader br = new BufferedReader(new FileReader(fileUpload.getSelectedFile().getAbsolutePath()))){
			
			columnsName.addAll(Arrays.asList(br.readLine().replace("\"", "").replace(" ", "").split(",")));
			tableName.append(fileUpload.getSelectedFile().getName().toUpperCase().substring(0, fileUpload.getSelectedFile().getName().indexOf(".")));
			
			String line = br.readLine();
			while(line != null) {
				
				lines.add(Arrays.asList(line.split(",")));
				
				line = br.readLine();
			}
			
			List<List<String>> aux = new ArrayList<>();
			aux.add(columnsName);
			aux.addAll(lines);
			
			new FormFramePrimaryKey(aux, pkName, exitReference);
			
			if(!exitReference.get())
				new FormFrameColumnType(columns, aux, tableName, tablesName, exitReference);
			
		}catch(IOException e) {
			
			e.printStackTrace();
		
		}
		
	}
	
}