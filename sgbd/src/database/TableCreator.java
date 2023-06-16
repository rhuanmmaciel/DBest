package database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mxgraph.model.mxCell;

import entities.cells.TableCell;
import enums.ColumnDataType;
import gui.frames.main.MainFrame;
import sgbd.prototype.Column;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.table.SimpleTable;
import sgbd.table.Table;
import sgbd.table.components.Header;

public class TableCreator {

	private static List<RowData> getRowData(List<entities.Column> columns,
			Map<Integer, Map<String, String>> content) {

		List<RowData> rows = new ArrayList<>();

		for (Map<String, String> line : content.values()) {

			RowData rowData = new RowData();

			for (String data : line.keySet()) {

				entities.Column column = columns.stream().filter(x -> x.getName().equals(data)).findFirst()
						.orElse(null);

				assert column != null;
				if (column.getType() == ColumnDataType.INTEGER) {

					if (!line.get(data).equals("null") && !line.get(data).equals(""))
						rowData.setLong(column.getName(), (long) (Double.parseDouble(line.get(data).strip())));

				} else if (column.getType() == ColumnDataType.FLOAT) {

					if (!line.get(data).equals("null") && !line.get(data).equals(""))
						rowData.setFloat(column.getName(), Float.parseFloat(line.get(data).strip()));

				} else {

					if (!line.get(data).equals("null") && !line.get(data).equals(""))
						rowData.setString(column.getName(), line.get(data).strip());

				}

			}

			rows.add(rowData);

		}

		return rows;

	}

	public static TableCell createTable(String tableName, List<entities.Column> columns,
			Map<Integer, Map<String, String>> data) {

		List<RowData> rows = new ArrayList<>(getRowData(columns, data));

		Prototype prototype = new Prototype();

		for (entities.Column column : columns) {
			
			short size;
			short flags;
			
			switch(column.getType()) {
			case INTEGER -> {
				size = 8;
				flags =Column.SIGNED_INTEGER_COLUMN;
			}case FLOAT -> {
				size = 4;
				flags = Column.FLOATING_POINT;
			}case STRING -> {
				size = 100;
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
		
		for(RowData row : rows) {
			
			table.insert(row);
			
		}
		
		table.saveHeader(tableName+".head");
		
		mxCell jCell = (mxCell) MainFrame.getGraph().insertVertex(MainFrame.getGraph().getDefaultParent(), null,
				tableName, 0, 0, 80, 30, "table");

		return new TableCell(jCell, tableName, "table", columns, table, prototype);
		
	}

}
