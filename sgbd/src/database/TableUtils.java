package database;

import java.util.*;

import controller.ConstantController;
import controller.MainController;
import enums.ColumnDataType;

public class TableUtils {

	public static boolean hasNull(List<String> columnData){

		for(String data : columnData) if(data.equals(ConstantController.NULL)) return true;

		return false;

	}

	public static List<ColumnDataType> getPossiblesDataType(List<String> columnData, char stringDelimiter) {

		List<ColumnDataType> types = new ArrayList<>(List.of(ColumnDataType.values()));
		List<ColumnDataType> possibleRemovedTypes = new ArrayList<>(List.of(ColumnDataType.CHARACTER,
				ColumnDataType.INTEGER, ColumnDataType.LONG, ColumnDataType.FLOAT, ColumnDataType.DOUBLE,
				ColumnDataType.BOOLEAN));

		for (String inf : columnData) {

			if (!inf.equals(ConstantController.NULL)) {

				if (inf.length() > 1)
					types.remove(ColumnDataType.CHARACTER);

				try {

					Integer.parseInt(inf.strip());

				} catch (NumberFormatException e) {

					types.remove(ColumnDataType.INTEGER);

				}

				try {

					Long.parseLong(inf.strip());

				} catch (NumberFormatException e) {

					types.remove(ColumnDataType.LONG);

				}

				try {

					Double.parseDouble(inf.strip());

				} catch (NumberFormatException e) {

					types.remove(ColumnDataType.DOUBLE);

				}

				try {

					Float.parseFloat(inf.strip());

				} catch (NumberFormatException e) {

					types.remove(ColumnDataType.FLOAT);

				}

				if (inf.startsWith(String.valueOf(stringDelimiter)) && inf.endsWith(String.valueOf(stringDelimiter)))
					types.removeIf(x -> x != ColumnDataType.STRING && x != ColumnDataType.NONE);

			}

			if (types.stream().noneMatch(possibleRemovedTypes::contains))
				return types;

		}

		return types;

	}

	public static boolean canBePrimaryKey(List<List<String>> columns) {

		if(columns.isEmpty()) return false;

		int size = columns.get(0).size();
		for(List<String> eachColumn : columns)
			if(eachColumn.size() != size) return false;

		Map<Integer, List<String>> uniqueData = new LinkedHashMap<>();

		for(List<String> columnData : columns) {

			if (columnData.contains("") || columnData.contains(null) || columnData.contains(ConstantController.NULL))
				return false;

			int i = 0;
			for(String data : columnData) {

				if (uniqueData.get(i) == null)
					uniqueData.put(i, new ArrayList<>(List.of(data)));

				else {

					List<String> row = uniqueData.get(i);
					row.add(data);

				}
				i++;

			}

		}

		for(int i = 0; i < size; i++)
			for (int j = i + 1; j < size; j++){

				boolean found = true;

				for(int k = 0; k < uniqueData.get(i).size(); k++)
					if(!uniqueData.get(i).get(k).strip().equals(uniqueData.get(j).get(k).strip()))
						found = false;

				if(found) return false;

			}

		return true;

	}

}
