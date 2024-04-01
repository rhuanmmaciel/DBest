package database;

import controllers.ConstantController;
import enums.ColumnDataType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class TableUtils {

    private TableUtils() {

    }

    public static boolean hasNull(List<String> columnData) {
		for (String data : columnData) {
			if (data.equals(ConstantController.NULL)) {
				return true;
			}
		}

        return false;
    }

    public static List<ColumnDataType> getPossiblesDataType(List<String> columnData, char stringDelimiter) {
        List<ColumnDataType> types = new ArrayList<>(List.of(ColumnDataType.values()));

        List<ColumnDataType> possibleRemovedTypes = new ArrayList<>(List.of(
			ColumnDataType.CHARACTER, ColumnDataType.INTEGER, ColumnDataType.LONG,
			ColumnDataType.FLOAT, ColumnDataType.DOUBLE, ColumnDataType.BOOLEAN
		));

        for (String data : columnData) {
            if (!data.equals(ConstantController.NULL)) {
				if (data.length() > 1) {
					types.remove(ColumnDataType.CHARACTER);
				}

                try {
                    Integer.parseInt(data.strip());
                } catch (NumberFormatException exception) {
                    types.remove(ColumnDataType.INTEGER);
                }

                try {
                    Long.parseLong(data.strip());
                } catch (NumberFormatException exception) {
                    types.remove(ColumnDataType.LONG);
                }

                try {
                    Double.parseDouble(data.strip());
                } catch (NumberFormatException exception) {
                    types.remove(ColumnDataType.DOUBLE);
                }

                try {
                    Float.parseFloat(data.strip());
                } catch (NumberFormatException exception) {
                    types.remove(ColumnDataType.FLOAT);
                }

				if (data.startsWith(String.valueOf(stringDelimiter)) && data.endsWith(String.valueOf(stringDelimiter))) {
					types.removeIf(x -> x != ColumnDataType.STRING && x != ColumnDataType.NONE);
				}
            }

			if (types.stream().noneMatch(possibleRemovedTypes::contains)) {
				return types;
			}
        }

        return types;
    }
    
}
