 package files;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mxgraph.model.mxCell;

import controller.MainController;
import database.TableCreator;
import entities.Column;
import entities.cells.TableCell;
import enums.FileType;
import gui.frames.forms.importexport.CsvRecognizerForm;
import gui.frames.main.MainFrame;
import sgbd.table.Table;

public class ImportFile {

	private final JFileChooser fileUpload = new JFileChooser();
	private AtomicReference<Boolean> exitReference;
	private final StringBuilder tableName = new StringBuilder();
	private final Map<Integer, Map<String, String>> content = new LinkedHashMap<>();
	private final List<Column> columns = new ArrayList<>();
	private FileType fileType;
	private TableCell tableCell;

	{
		this.tableCell = null;
	}

	public ImportFile(FileType fileType, AtomicReference<Boolean> exitReference) {

		this.exitReference = exitReference;
		this.fileType = fileType;
		
		fileUpload.setCurrentDirectory(MainController.getLastDirectory());

		importFile();

	}
	
	public ImportFile(String fileName, mxCell jCell) {
		
		header(fileName, jCell);
		
	}
	
	private void importFile() {
		
		FileNameExtensionFilter filter;

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

				if (!exitReference.get()) {

					String fileName = fileUpload.getSelectedFile().getName().endsWith(".head")  ?
							fileUpload.getSelectedFile().getName().substring(0,fileUpload.getSelectedFile().getName().indexOf(".")):
							fileUpload.getSelectedFile().getName();

					mxCell jCell = (mxCell) MainFrame.getGraph().insertVertex(MainFrame.getGraph().getDefaultParent(), null,
							fileName, 0, 0, 80, 30, "table");
					table.get().open();
					tableCell = new TableCell(jCell, fileName, "table", table.get());
					
				}
			}
			case SQL -> throw new UnsupportedOperationException("Unimplemented case: " + fileType);
			default -> throw new IllegalArgumentException("Unexpected value: " + fileType);
			
			}

			if (!exitReference.get() && FileType.DAT != fileType) {
				TableCreator tableCreator = new TableCreator(tableName.toString(), columns, content, exitReference, false);
				tableCell = tableCreator.getTableCell();
			}
			return;
			
		}

		exitReference.set(true);

		
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
	
	private void header(String fileName, mxCell jTableCell) {
		
		AtomicReference<Table> table = new AtomicReference<>();
		table.set(Table.loadFromHeader(fileName));

		tableCell = new TableCell(jTableCell, fileName.substring(0, fileName.indexOf(".")), "tabela", table.get());
		
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
		
		new CsvRecognizerForm(fileUpload.getSelectedFile().getAbsolutePath(), tableName, columns, content, exitReference);
		
	}
	
	public TableCell getResult() {
		return tableCell;
	}

}