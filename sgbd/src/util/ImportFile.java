package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.MainController;
import entities.Column;
import entities.cells.TableCell;
import enums.FileType;
import gui.frames.forms.create.FormFrameColumnType;
import gui.frames.forms.create.PrimaryKey;
import sgbd.table.Table;

public class ImportFile {

	private List<String> tablesName = new ArrayList<>();
	private JFileChooser fileUpload = new JFileChooser();
	private AtomicReference<Boolean> exitReference;
	private StringBuilder tableName = new StringBuilder();
	private StringBuilder pkName = new StringBuilder();
	private Map<Integer, Map<String, String>> content = new LinkedHashMap<>();
	private List<Column> columns = new ArrayList<>();
	private FileType fileType;
	private TableCell tableCell;

	{
		MainController.getCells().values().forEach(cell -> tablesName.add(cell.getName()));
		this.tableCell = null;
	}

	public ImportFile(FileType fileType, AtomicReference<Boolean> exitReference) {

		this.exitReference = exitReference;
		this.fileType = fileType;
		
		fileUpload.setCurrentDirectory(MainController.getLastDirectory());

		importFile();

	}
	
	public ImportFile(String fileName) {
		
		header(fileName);
		
	}
	
	private void importFile() {
		
		FileNameExtensionFilter filter = null;

		switch (fileType) {
	
		case CSV -> filter = new FileNameExtensionFilter("CSV files", "csv");
		case EXCEL -> filter = new FileNameExtensionFilter("Sheets files", "xlsx", "xls", "ods");
		case DAT -> filter = new FileNameExtensionFilter("Headers files", "head");
		case SQL -> throw new UnsupportedOperationException("Unimplemented case: " + fileType);
		default -> throw new IllegalArgumentException("Unexpected value: " + fileType);

		}

		fileUpload.setFileFilter(filter);

		if (fileUpload.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

			MainController.setLastDirectory(new File(fileUpload.getCurrentDirectory().getAbsolutePath()));
			
			switch(fileType) {
			
			case CSV -> csv();
			case EXCEL -> excel();
			case DAT -> {

				AtomicReference<Table> table = new AtomicReference<>();
				header(table);

				if (!exitReference.get())
					tableCell = new TableCell(80, 50, fileUpload.getSelectedFile().getName(), "tabela", table.get());;

			}
			case SQL -> throw new UnsupportedOperationException("Unimplemented case: " + fileType);
			default -> throw new IllegalArgumentException("Unexpected value: " + fileType);
			
			}

			if (!exitReference.get() && FileType.DAT != fileType)
				tableCell = TableCreator.createTable(tableName.toString(), pkName.toString(), columns, content, false);

		} else {

			exitReference.set(true);

		}
		
	}

	private void header(AtomicReference<Table> table) {

		if (!fileUpload.getSelectedFile().getName().toLowerCase().endsWith(".head")) {

			JOptionPane.showMessageDialog(null, "Por favor, selecione um arquivo .head");
			exitReference.set(true);
			return;

		}

		String file = fileUpload.getSelectedFile().getAbsolutePath();

		table.set(Table.loadFromHeader(file));

	}
	
	private void header(String fileName) {
		
		AtomicReference<Table> table = new AtomicReference<>();
		table.set(Table.loadFromHeader(fileName));

		tableCell = new TableCell(80, 50, fileName.substring(0, fileName.indexOf(".")), "tabela", table.get());;
		
	}

	private void excel() {
		/*
		 * try {
		 * 
		 * FileInputStream file = new
		 * FileInputStream(fileUpload.getSelectedFile().getAbsolutePath());
		 * 
		 * tableName.append(fileUpload.getSelectedFile().getName().toUpperCase()
		 * .substring(0, fileUpload.getSelectedFile().getName().indexOf("."))
		 * .replaceAll("[^a-zA-Z0-9_-]", ""));
		 * 
		 * XSSFWorkbook workbook = new XSSFWorkbook(file); XSSFSheet sheet =
		 * workbook.getSheetAt(0);
		 * 
		 * Iterator<Row> rowIterator = sheet.iterator();
		 * 
		 * Row firstRow = rowIterator.next(); Iterator<Cell> firstRowCellIterator =
		 * firstRow.cellIterator();
		 * 
		 * while(firstRowCellIterator.hasNext()) {
		 * 
		 * Cell cell = firstRowCellIterator.next();
		 * columnsName.add(cell.getStringCellValue().replace("\"", "").replace(" ",
		 * ""));
		 * 
		 * }
		 * 
		 * while (rowIterator.hasNext()) {
		 * 
		 * Row row = rowIterator.next(); Iterator<Cell> cellIterator =
		 * row.cellIterator();
		 * 
		 * List<String> line = new ArrayList<>();
		 * 
		 * while (cellIterator.hasNext()) {
		 * 
		 * Cell cell = cellIterator.next(); switch (cell.getCellType()) {
		 * 
		 * case NUMERIC:
		 * 
		 * line.add(String.valueOf(cell.getNumericCellValue())); break;
		 * 
		 * case STRING:
		 * 
		 * line.add(cell.getStringCellValue()); break;
		 * 
		 * case BLANK:
		 * 
		 * line.add("");
		 * 
		 * case BOOLEAN: case ERROR: case FORMULA: case _NONE:
		 * 
		 * default:
		 * 
		 * break;
		 * 
		 * } }
		 * 
		 * lines.add(line);
		 * 
		 * }
		 * 
		 * file.close(); workbook.close();
		 * 
		 * List<List<String>> aux = new ArrayList<>(); aux.add(columnsName);
		 * aux.addAll(lines);
		 * 
		 * new FormFramePrimaryKey(aux, pkName, exitReference);
		 * 
		 * if(!exitReference.get()) new FormFrameColumnType(columns, aux, tableName,
		 * tablesName, exitReference);
		 * 
		 * } catch (IOException e) {
		 * 
		 * e.printStackTrace();
		 * 
		 * }
		 */

	}

	private void csv() {

		if (!fileUpload.getSelectedFile().getName().toLowerCase().endsWith(".csv")) {

			JOptionPane.showMessageDialog(null, "Por favor, selecione um arquivo CSV.");
			exitReference.set(true);
			return;

		}

		try (BufferedReader br = new BufferedReader(new FileReader(fileUpload.getSelectedFile().getAbsolutePath()))) {

			List<String> columnsList = Arrays.asList(br.readLine().replace("\"", "").replace(" ", "").split(","));
			Set<String> columnsNameSet = new LinkedHashSet<>();
			List<String> columnsNameList = new ArrayList<>();

			for (String column : columnsList) {
				if (!columnsNameSet.add(column)) {
					int count = 1;
					String newColumnName = column + count;
					while (columnsNameSet.contains(newColumnName)) {
						count++;
						newColumnName = column + count;
					}
					column = newColumnName;
				}
				columnsNameSet.add(column);
				columnsNameList.add(column.replaceAll("[^a-zA-Z0-9]", ""));
			}

			tableName.append(fileUpload.getSelectedFile().getName()
					.substring(0, fileUpload.getSelectedFile().getName().indexOf(".")).replaceAll("[^a-zA-Z0-9]", ""));

			String line = br.readLine();
			int i = 0;
			while (line != null) {

				Map<String, String> row = new LinkedHashMap<>();

				Iterator<String> columnName = columnsNameSet.iterator();

				for (String inf : line.split(",")) {

					String name = columnName.next();
					row.put(name.replaceAll("[^a-zA-Z0-9]", ""), inf);

				}

				content.put(i++, row);
				line = br.readLine();
			}

			new PrimaryKey(content, pkName, exitReference);

			if (!exitReference.get())
				new FormFrameColumnType(columns, content, tableName, tablesName, exitReference);

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
	
	public TableCell getResult() {
		return tableCell;
	}

}