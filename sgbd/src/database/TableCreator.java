package database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import controller.MainController;
import entities.cells.TableCell;
import gui.frames.main.MainFrame;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.prototype.column.Column;
import sgbd.table.SimpleTable;
import sgbd.table.Table;
import sgbd.table.components.Header;


public class TableCreator {

	private TableCell tableCell = null;

	private final boolean mustExport;

	public TableCreator(String tableName, List<entities.Column> columns,
						Map<Integer, Map<String, String>> data, AtomicReference<Boolean> exitReference,
						boolean mustExport){

		this.mustExport = mustExport;
		createTable(tableName, columns, data);

	}

	private void createTable(String tableName, List<entities.Column> columns,
			Map<Integer, Map<String, String>> data) {

		List<RowData> rows = new ArrayList<>(getRowData(columns, data));

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

		Table table = SimpleTable.openTable(new Header(prototype, tableName));
		table.open();

		table.insert(rows);

		table.saveHeader(tableName+".head");

		if(mustExport){

			tableCell = new TableCell(null, tableName, "table", columns, table, prototype);

			return;

		}


		mxCell jCell = (mxCell) MainFrame.getGraph().insertVertex(MainFrame.getGraph().getDefaultParent(), null,
				tableName, 0, 0, 80, 30, "table");

		tableCell = new TableCell(jCell, tableName, "table", columns, table, prototype);

	}

	private List<RowData> getRowData(List<entities.Column> columns, Map<Integer, Map<String, String>> content) {

		List<RowData> rows = new ArrayList<>();

		for (Map<String, String> line : content.values()) {

			RowData rowData = new RowData();

			for (String data : line.keySet()) {

				entities.Column column = columns.stream().filter(x -> x.getName().equals(data)).findFirst()
						.orElseThrow();

				if (!line.get(data).equals(MainController.NULL) && !line.get(data).equals(""))
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
