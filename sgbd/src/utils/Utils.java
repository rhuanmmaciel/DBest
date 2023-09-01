package utils;

import enums.ColumnDataType;

import sgbd.prototype.query.Tuple;

import java.util.List;
import java.util.Objects;

public class Utils {

    public static boolean containsIgnoreCase(List<String> strings, String searchString) {
        if (strings == null) return false;

        for (String string : strings) {
            if (string.equalsIgnoreCase(searchString)) {
                return true;
            }
        }

        return false;
    }

    public static ColumnDataType getColumnDataType(Tuple tuple, String sourceName, String columnName) {
        if (tuple == null || sourceName == null || columnName == null) {
            return ColumnDataType.NONE;
        }

        return switch (sgbd.util.global.Util.typeOfColumn(tuple.getContent(sourceName).getMeta(columnName))) {
            case "int" -> ColumnDataType.INTEGER;
            case "long" -> ColumnDataType.LONG;
            case "float" -> ColumnDataType.FLOAT;
            case "double" -> ColumnDataType.DOUBLE;
            case "boolean" -> ColumnDataType.BOOLEAN;
            case "string" -> ColumnDataType.STRING;
            default -> ColumnDataType.NONE;
        };
    }

    public static boolean areEqual(Number x, Number y) {
        if (x == null || y == null) return false;

        return Objects.equals(x.doubleValue(), y.doubleValue());
    }

    public static boolean startsWithIgnoreCase(String string, String searchString) {
        if (string == null || searchString == null) return false;

        return string.toLowerCase().startsWith(searchString.toLowerCase());
    }

    public static boolean startsWithIgnoreCase(String string, List<String> searchStrings) {
        if (searchStrings == null) return false;

        return searchStrings.stream().anyMatch(prefix -> startsWithIgnoreCase(string, prefix));
    }

    public static String getFirstMatchingPrefixIgnoreCase(String string, List<String> prefixes) {
        return prefixes
            .stream()
            .filter(prefix -> startsWithIgnoreCase(string, prefix))
            .findFirst()
            .orElse(null);
    }

    public static String replaceIfStartsWithIgnoreCase(String string, List<String> searchStrings, String replacement) {
        if (string == null || replacement == null || searchStrings == null) return string;

        for (String searchString : searchStrings) {
            if (startsWithIgnoreCase(string, searchString)) {
                return String.format("%s%s", replacement, string.substring(searchString.length()));
            }
        }

        return string;
    }

    public static boolean hasElementWithPrefixAndSuffix(List<String> strings, String prefix, String suffix) {
        if (strings == null || prefix == null || suffix == null) return false;

        return strings.stream().anyMatch(string -> string.startsWith(prefix) && string.endsWith(suffix));
    }
}
