package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
	
	private void excel(JFileChooser fileUpload, StringBuilder fileName, List<String> columnsName, List<List<String>> lines, List<Column> columns) {
		
		try {
		
			FileInputStream file = new FileInputStream(fileUpload.getSelectedFile().getAbsolutePath());
			
			fileName.append(fileUpload.getSelectedFile().getName().toUpperCase().substring(0, fileUpload.getSelectedFile().getName().indexOf(".")));
			
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
			    	switch (cell.getCellTypeEnum()) {
			        	
				    	case NUMERIC:
				        	
				        	line.add(String.valueOf(cell.getNumericCellValue()));
				        	break;
				        	
				        case STRING:
				        		
				        	line.add(cell.getStringCellValue());
				        	break;
				        	
						case BLANK:
						case BOOLEAN:
						case ERROR:
						case FORMULA:
						case _NONE:
				        	
				        default:
							
				        	System.out.println("Null");
				        	break;
			    	
			    	}
			    }
			    
			    lines.add(line);
			    
			    System.out.println("");
			
			}
			    
            file.close();
            workbook.close();
            
			List<List<String>> aux = new ArrayList<>();
			aux.add(columnsName);
			aux.addAll(lines);
			new FormFramePrimaryKey(aux);
			new FormFrameColumnType(columns, columnsName);
			
		} catch (IOException e) {

			e.printStackTrace();
		
		}
		
	}
	
	private void csv(JFileChooser fileUpload, StringBuilder fileName, List<String> columnsName, List<List<String>> lines, List<Column> columns){
		
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