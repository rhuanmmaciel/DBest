package database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import enums.ColumnDataType;

public class TableUtils {

	public static List<ColumnDataType> getPossiblesDataType(List<String> columnData, char stringDelimiter) {

		List<ColumnDataType> types = new ArrayList<>(List.of(ColumnDataType.values()));
		List<ColumnDataType> possibleRemovedTypes = new ArrayList<>(List.of(ColumnDataType.CHARACTER,
				ColumnDataType.INTEGER, ColumnDataType.FLOAT, ColumnDataType.BOOLEAN));

		for (String inf : columnData) {

			if (!inf.equals("null")) {

				if (inf.length() > 1)
					types.remove(ColumnDataType.CHARACTER);

				try {

					Integer.parseInt(inf);

				} catch (NumberFormatException e) {

					types.remove(ColumnDataType.INTEGER);

				}

				try {

					Float.parseFloat(inf);

				} catch (NumberFormatException e) {

					types.remove(ColumnDataType.FLOAT);

				}

				if (inf.startsWith(String.valueOf(stringDelimiter)) && inf.endsWith(String.valueOf(stringDelimiter)))
					types.removeIf(x -> x != ColumnDataType.STRING && x != ColumnDataType.NONE);

			}

			if (!types.stream().anyMatch(x -> possibleRemovedTypes.contains(x)))
				return types;

		}

		return types;

	}

	public static boolean canBePrimaryKey(List<String> columnData) {

		if (columnData.contains("") || columnData.contains(null))
			return false;

		Set<String> uniqueData = new HashSet<>(columnData);

		if (columnData.size() != uniqueData.size())
			return false;

		return true;

	}

}
