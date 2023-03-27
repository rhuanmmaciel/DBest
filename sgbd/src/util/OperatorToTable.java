package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import entities.Cell;
import entities.TableCell;
import enums.ColumnDataType;
import gui.frames.forms.create.FormFramePrimaryKey;
import sgbd.prototype.ComplexRowData;
import sgbd.prototype.Prototype;
import sgbd.prototype.Column;
import sgbd.prototype.RowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.table.SimpleTable;
import sgbd.table.Table;
import sgbd.table.components.Header;
import sgbd.util.Util;

public class OperatorToTable {

	public OperatorToTable(TableCell createdCell, Cell cell, String tableName, AtomicReference<Boolean> cancelService) {

		List<String> columnsName = new ArrayList<>(cell.getOnlyColumnsName());
		List<List<String>> allData = new ArrayList<>();
		List<entities.Column> columns = new ArrayList<>(cell.getColumns());

		StringBuilder pkName = new StringBuilder();

		SortColumns.list(cell.getData(), columnsName);
		allData.add(columnsName);
		allData.addAll(cell.getListContent());

		new FormFramePrimaryKey(allData, pkName, cancelService);

		for (int i = 0; i < columns.size(); i++) {

			String name = columns.get(i).getName().substring(columns.get(i).getName().indexOf("_") + 1);
			ColumnDataType type = columns.get(i).getType();
			Boolean pk = name.equals(pkName.toString());

			columns.remove(i);
			columns.add(i, new entities.Column(name, tableName, type, pk, false));

		}

		Operator aux = cell.getData();
		aux.open();

		Tuple tuple = aux.next();

		for (Map.Entry<String, ComplexRowData> line : tuple)
			for (Map.Entry<String, byte[]> data : line.getValue())
				columnsName.add(data.getKey());

		aux.close();

		aux = cell.getData();

		aux.open();

		tuple = aux.hasNext() ? aux.next() : null;

		List<RowData> rows = new ArrayList<>();

		Boolean isPKCreated = !columns.stream().anyMatch(x -> x.isPK());

		int k = 1;
		while (aux.hasNext() || tuple != null) {

			Tuple t = tuple == null ? aux.next() : tuple;
			RowData row = new RowData();

			for (Map.Entry<String, ComplexRowData> line : t) {
				for (Map.Entry<String, byte[]> data : line.getValue()) {

					entities.Column column = columns.stream()
													.filter(x -> data.getKey().substring(data.getKey().indexOf("_") + 1)
													.equals(x.getName().substring(x.getName().indexOf("_") + 1)))
													.findFirst().orElse(null);

					switch (Util.typeOfColumn(line.getValue().getMeta(data.getKey()))) {

					case "int":
						row.setInt(column.getName(), line.getValue().getInt(data.getKey()));
						break;

					case "float":

						row.setFloat(column.getName(), line.getValue().getFloat(data.getKey()));
						break;

					case "string":
					default:
						row.setString(column.getName(), line.getValue().getString(data.getKey()));

					}

				}

			}

			if (isPKCreated) {

				row.setInt(tableName + "_" + pkName, k++);

			}

			rows.add(row);

			tuple = null;

		}

		if (isPKCreated)
			columns.add(new entities.Column(pkName.toString(), tableName, ColumnDataType.INTEGER, true, false));

		Prototype prototype = new Prototype();

		int index = -1;
		for (int i = 0; i < columns.size(); i++) {

			if (columns.get(i).getName().toLowerCase().contains(pkName.toString().toLowerCase()) && index < 0)
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

			} else if (column.getType() == ColumnDataType.CHARACTER) {

				prototype.addColumn(column.getName(), 1, Column.STRING);

			} else {

				prototype.addColumn(column.getName(), 100, Column.NONE);

			}

		}

		Table table = SimpleTable.openTable(new Header(prototype, tableName));
		table.open();

		for (RowData row : rows) {

			table.insert(row);

		}

		createdCell.setName(tableName);
		createdCell.setStyle("tabela");
		columns.add(primaryKeyColumn);
		createdCell.setColumns(columns);
		createdCell.setTable(table);
		createdCell.setContent();
		createdCell.setPrototype(prototype);

	}

}
