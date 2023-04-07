package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
			Map<Integer, Map<String, String>> content, boolean isColumnsReady) {

		List<RowData> rows = new ArrayList<>();
		
		if(!isColumnsReady)
			for (int i = 0; i < columns.size(); i++) {
	
				String name = columns.get(i).getName();
				ColumnDataType type = columns.get(i).getType();
				Boolean pk = name.equals(pkName);
	
				columns.remove(i);
				columns.add(i, new entities.Column(name, tableName, type, pk, false));
	
			}

		Boolean isPKCreated = !columns.stream().anyMatch(x -> x.isPK());
		
		int k = 1;
		for (Map<String, String> line : content.values()) {

			RowData rowData = new RowData();
			
			for (String data : line.keySet()) {
				
				entities.Column column = !isColumnsReady ?
						columns.stream().filter(x -> x.getName().substring(x.getName().indexOf("_")+1).equals(data)).findFirst().orElse(null) :
							columns.stream().filter(x -> x.getName().equals(data)).findFirst().orElse(null);

				if (column.getType() == ColumnDataType.INTEGER) {
					
					if(!line.get(data).equals("null") && !line.get(data).equals("")) 
						rowData.setInt(column.getName(), (int) (Double.parseDouble(line.get(data).strip())));

				} else if (column.getType() == ColumnDataType.FLOAT) {
					
					if(!line.get(data).equals("null") && !line.get(data).equals("")) 
						rowData.setFloat(column.getName(), Float.parseFloat(line.get(data).strip()));

				} else {
					
					if(!line.get(data).equals("null") && !line.get(data).equals("")) 
						rowData.setString(column.getName(), line.get(data).strip());

				}

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
			Map<Integer, Map<String, String>> data, boolean isColumnsReady) {

		List<RowData> rows = new ArrayList<>(getRowData(tableName, pkName, columns, data, isColumnsReady));

		Prototype prototype = new Prototype();

		int index = -1;
		for (int i = 0; i < columns.size(); i++) {

			if (columns.get(i).getName().toLowerCase().contains(pkName.toLowerCase()) && index < 0)
				index = i;

		}

		prototype.addColumn(columns.get(index).getName(), 100, Column.PRIMARY_KEY);
		entities.Column primaryKeyColumn = columns.get(index);
		columns.remove(index);

		for (entities.Column column : columns) {

			if (column.getType() == ColumnDataType.INTEGER) {
				
				prototype.addColumn(column.getName(), 100, Column.SIGNED_INTEGER_COLUMN | Column.CAN_NULL_COLUMN);

			} else if (column.getType() == ColumnDataType.FLOAT) {

				prototype.addColumn(column.getName(), 4, Column.FLOATING_POINT | Column.CAN_NULL_COLUMN);

			} else if (column.getType() == ColumnDataType.STRING) {

				prototype.addColumn(column.getName(), 100, Column.STRING | Column.CAN_NULL_COLUMN);

			}else if (column.getType() == ColumnDataType.CHARACTER) {

				prototype.addColumn(column.getName(), 1, Column.STRING | Column.CAN_NULL_COLUMN);

			}  else {

				prototype.addColumn(column.getName(), 100, Column.STRING | Column.CAN_NULL_COLUMN);

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
		tableCell.setPrototype(prototype);
		
		
	}

	public static void importTable(TableCell tableCell, AtomicReference<Table> table) {

		table.get().open();

		tableCell.setName(table.get().getTableName());
		tableCell.setStyle("tabela");
		tableCell.setTable(table.get());
		tableCell.setPrototype(table.get().getHeader().getPrototype());
		tableCell.setColumns();		
		
	}

}
