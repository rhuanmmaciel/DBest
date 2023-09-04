package database;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mxgraph.model.mxCell;

import controller.ConstantController;
import entities.cells.Cell;
import entities.cells.CsvTableCell;
import entities.cells.FyiTableCell;
import entities.cells.TableCell;
import enums.FileType;
import enums.TableType;
import files.FileUtils;
import files.csv.CsvInfo;
import gui.frames.main.MainFrame;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.prototype.column.Column;
import sgbd.table.CSVTable;
import sgbd.table.SimpleTable;
import sgbd.table.Table;
import sgbd.table.components.Header;


public class TableCreator {

	private TableCell tableCell = null;
	private final boolean mustExport;

	public TableCreator(boolean mustExport){

		this.mustExport = mustExport;

	}

	public TableCreator(String tableName, List<entities.Column> columns, CsvInfo csvInfo, boolean mustExport){

		this(mustExport);

		createCSVTable(tableName, columns, csvInfo.separator(), csvInfo.stringDelimiter(), csvInfo.beginIndex(), csvInfo.path());

	}

	public TableCreator(String tableName, List<entities.Column> columns,
						Map<Integer, Map<String, String>> data, File headerFile,
						boolean mustExport){

		this(mustExport);

		createFyiTable(tableName, columns, data, headerFile);

	}

	private void createCSVTable(String tableName, List<entities.Column> columns, char separator,
									char stringDelimiter, int beginIndex, Path path){

		Prototype prototype = createPrototype(columns);

		Header header = new Header(prototype, tableName);

		header.set(Header.FILE_PATH, path.toString());

		CSVTable table = new CSVTable(header, separator, stringDelimiter, beginIndex);
		table.open();
		table.saveHeader(tableName+FileType.HEADER.EXTENSION);

		File headerFile = FileUtils.getFile(tableName+FileType.HEADER.EXTENSION);

		FileUtils.moveToTempDirectory(headerFile);
		headerFile = FileUtils.getFileFromTempDirectory(tableName+FileType.HEADER.EXTENSION).get();

		if(mustExport){

			tableCell = new CsvTableCell(null, tableName, TableType.CSV_TABLE.ID, columns, table, prototype, headerFile);
			Cell.removeFromCells(null);
			return;

		}

		mxCell jCell = (mxCell) MainFrame.getGraph().insertVertex(MainFrame.getGraph().getDefaultParent(), null,
				tableName, 0, 0, ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT,
				TableType.CSV_TABLE.ID);

		tableCell = new CsvTableCell(jCell, tableName, TableType.CSV_TABLE.ID, columns, table, prototype, headerFile);

	}

	private void createFyiTable(String tableName, List<entities.Column> columns,
								Map<Integer, Map<String, String>> data, File headerFile) {

		List<RowData> rows = new ArrayList<>(getRowData(columns, data));

		Prototype prototype = createPrototype(columns);

		Header header = new Header(prototype, tableName);

		Table table = SimpleTable.openTable(header);
		table.open();

		table.insert(rows);

		table.saveHeader(tableName+FileType.HEADER.EXTENSION);

		if(mustExport){

			tableCell = new FyiTableCell(null, tableName, TableType.FYI_TABLE.ID, columns, table, prototype, headerFile);
			Cell.removeFromCells(null);
			return;

		}

		mxCell jCell = (mxCell) MainFrame.getGraph().insertVertex(MainFrame.getGraph().getDefaultParent(), null,
				tableName, 0, 0, ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT,
				TableType.FYI_TABLE.ID);

		tableCell = new FyiTableCell(jCell, tableName, TableType.FYI_TABLE.ID, columns, table, prototype, headerFile);

	}

	public static Prototype createPrototype(List<entities.Column> columns){

		Prototype prototype = new Prototype();

		for (entities.Column column : columns) {

			short size;
			short flags;

			switch(column.getType()) {
				case INTEGER -> {
					size = 4;
					flags = Column.SIGNED_INTEGER_COLUMN;
				}case LONG ->{
					size = 8;
					flags = Column.SIGNED_INTEGER_COLUMN;
				}case FLOAT -> {
					size = 4;
					flags = Column.FLOATING_POINT;
				}case DOUBLE -> {
					size = 8;
					flags = Column.FLOATING_POINT;
				}case STRING -> {
					size = Short.MAX_VALUE;
					flags = Column.STRING;
				}case CHARACTER ->{
					size = 1;
					flags = Column.STRING;
				}default ->{
					size = 100;
					flags = Column.NONE;
				}
			}

			flags |= column.isPK() ? Column.PRIMARY_KEY : Column.CAN_NULL_COLUMN;

			prototype.addColumn(column.getName(), size, flags);

		}

		return prototype;

	}

	private List<RowData> getRowData(List<entities.Column> columns, Map<Integer, Map<String, String>> content) {

		List<RowData> rows = new ArrayList<>();

		for (Map<String, String> line : content.values()) {

			RowData rowData = new RowData();

			for (String data : line.keySet()) {

				if(data.equals("__IDX__")) continue;

				entities.Column column = columns.stream().filter(x -> x.getName().equals(data)).findFirst()
						.orElseThrow();

				if (!line.get(data).equals(ConstantController.NULL) && !line.get(data).isEmpty())
					switch (column.getType()) {
						case INTEGER -> rowData.setInt(column.getName(), (int) (Double.parseDouble(line.get(data).strip())));
						case LONG -> rowData.setLong(column.getName(), (long) (Double.parseDouble(line.get(data).strip())));
						case FLOAT -> rowData.setFloat(column.getName(), Float.parseFloat(line.get(data).strip()));
						case DOUBLE -> rowData.setDouble(column.getName(), Double.parseDouble(line.get(data).strip()));
						default -> rowData.setString(column.getName(), line.get(data).strip());
					}

			}

			rows.add(rowData);

		}

		return rows;

	}

	public TableCell getTableCell(){
		return tableCell;
	}

}
