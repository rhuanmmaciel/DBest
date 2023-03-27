package util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import entities.TableCell;
import enums.ColumnDataType;
import sgbd.prototype.Column;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.table.SimpleTable;
import sgbd.table.Table;
import sgbd.table.components.Header;

public class TableCreator {

	private static List<RowData> getRowData(String tableName, String pkName, List<entities.Column> columns,
			List<List<String>> lines) {

		List<RowData> rows = new ArrayList<>();

		for (int i = 0; i < columns.size(); i++) {

			String name = columns.get(i).getName();
			ColumnDataType type = columns.get(i).getType();
			Boolean pk = name.equals(pkName);

			columns.remove(i);
			columns.add(i, new entities.Column(name, tableName, type, pk, false));

		}

		Boolean isPKCreated = !columns.stream().anyMatch(x -> x.isPK());
		
		int k = 1;
		for (List<String> line : lines) {

			RowData rowData = new RowData();
			int i = 0;
			
			for (String data : line) {

				if (data.isEmpty()) {
					
					rowData.setString(columns.get(i).getName(), data);

				} else if (columns.get(i).getType() == ColumnDataType.INTEGER) {

					rowData.setInt(columns.get(i).getName(), (int) (Double.parseDouble(data)));

				} else if (columns.get(i).getType() == ColumnDataType.FLOAT) {
					
					rowData.setFloat(columns.get(i).getName(), Float.parseFloat(data));

				} else {

					rowData.setString(columns.get(i).getName(), data);

				}
				i++;

			}

			if (isPKCreated) {

				rowData.setInt(tableName + "_" + pkName, k++);

			}

			rows.add(rowData);

		}

		if (isPKCreated)
			columns.add(new entities.Column(pkName, tableName, ColumnDataType.INTEGER, true, false));

		return rows;

	}

	public static void createTable(TableCell tableCell, String tableName, String pkName, List<entities.Column> columns,
			List<List<String>> lines) {

		List<RowData> rows = new ArrayList<>(getRowData(tableName, pkName, columns, lines));

		Prototype prototype = new Prototype();

		int index = -1;
		for (int i = 0; i < columns.size(); i++) {

			if (columns.get(i).getName().toLowerCase().contains(pkName.toLowerCase()) && index < 0)
				index = i;

		}

		prototype.addColumn(columns.get(index).getName(), 15, Column.PRIMARY_KEY);
		entities.Column primaryKeyColumn = columns.get(index);
		columns.remove(index);

		for (entities.Column column : columns) {

			if (column.getType() == ColumnDataType.INTEGER) {

				prototype.addColumn(column.getName(), 100, Column.SIGNED_INTEGER_COLUMN);

			} else if (column.getType() == ColumnDataType.FLOAT) {

				prototype.addColumn(column.getName(), 4, Column.FLOATING_POINT);

			} else if (column.getType() == ColumnDataType.STRING) {

				prototype.addColumn(column.getName(), 100, Column.STRING);

			}else if (column.getType() == ColumnDataType.CHARACTER) {

				prototype.addColumn(column.getName(), 1, Column.STRING);

			}  else {

				prototype.addColumn(column.getName(), 100, Column.STRING);

			}

		}

		Table table = SimpleTable.openTable(new Header(prototype, tableName));
		table.open();
		
		for(RowData row : rows) {
			
			table.insert(row);
			
		}
		
		tableCell.setName(tableName);
		tableCell.setStyle("tabela");
		columns.add(primaryKeyColumn);
		tableCell.setColumns(columns);
		tableCell.setTable(table);
		tableCell.setContent();
		tableCell.setPrototype(prototype);
		
	}

	public static void importTable(TableCell tableCell, AtomicReference<Table> table) {

		table.get().open();

		tableCell.setName(table.get().getTableName());
		tableCell.setStyle("tabela");
		tableCell.setTable(table.get());
		tableCell.setPrototype(table.get().getHeader().getPrototype());
		tableCell.setColumns();		
		tableCell.setContent();
		
	}

}
