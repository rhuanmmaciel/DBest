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

import entities.Column;
import entities.TableCell;
import enums.FileType;
import gui.frames.forms.create.FormFrameColumnType;
import gui.frames.forms.create.PrimaryKey;
import sgbd.table.Table;

public class ImportFile {

	public ImportFile(TableCell tableCell, FileType fileType, List<String> tablesName,
			AtomicReference<Boolean> exitReference, AtomicReference<File> lastDirectoryRef) {

		JFileChooser fileUpload = new JFileChooser();

		fileUpload.setCurrentDirectory(lastDirectoryRef.get());

		FileNameExtensionFilter filter = null;

		if (fileType == FileType.CSV) {

			filter = new FileNameExtensionFilter("CSV files", "csv");

		} else if (fileType == FileType.EXCEL) {

			filter = new FileNameExtensionFilter("Sheets files", "xlsx", "xls", "ods");

		} else if (fileType == FileType.DAT) {

			filter = new FileNameExtensionFilter("Headers files", "head");

		}

		fileUpload.setFileFilter(filter);

		int res = fileUpload.showOpenDialog(null);

		if (res == JFileChooser.APPROVE_OPTION) {

			lastDirectoryRef.set(new File(fileUpload.getCurrentDirectory().getAbsolutePath()));
			StringBuilder pkName = new StringBuilder();
			StringBuilder tableName = new StringBuilder();
			Map<Integer, Map<String, String>> content = new LinkedHashMap<>();
			List<Column> columns = new ArrayList<>();

			if (fileType == FileType.CSV) {

				csv(fileUpload, tableName, content, columns, tablesName, pkName, exitReference);

			} else if (fileType == FileType.EXCEL) {

				excel(fileUpload, tableName, content, columns, tablesName, pkName, exitReference);

			} else if (fileType == FileType.DAT) {

				AtomicReference<Table> table = new AtomicReference<>();
				header(fileUpload, table, exitReference);

				if (!exitReference.get())
					TableCreator.importTable(tableCell, table);

			}

			if (!exitReference.get() && FileType.DAT != fileType)
				TableCreator.createTable(tableCell, tableName.toString(), pkName.toString(), columns, content, false);

		} else {

			exitReference.set(true);
			;

		}

	}

	private void header(JFileChooser fileUpload, AtomicReference<Table> table, AtomicReference<Boolean> exitReference) {

		if (!fileUpload.getSelectedFile().getName().toLowerCase().endsWith(".head")) {

			JOptionPane.showMessageDialog(null, "Por favor, selecione um arquivo .head");
			exitReference.set(true);
			return;

		}

		String file = fileUpload.getSelectedFile().getAbsolutePath();

		table.set(Table.loadFromHeader(file));

	}

	private void excel(JFileChooser fileUpload, StringBuilder tableName, Map<Integer, Map<String, String>> lines,
			List<Column> columns, List<String> tablesName, StringBuilder pkName,
			AtomicReference<Boolean> exitReference) {
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

	private void csv(JFileChooser fileUpload, StringBuilder tableName, Map<Integer, Map<String, String>> content,
			List<Column> columns, List<String> tablesName, StringBuilder pkName,
			AtomicReference<Boolean> exitReference) {

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

}