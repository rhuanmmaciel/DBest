package util;

import enums.ColumnDataType;
import sgbd.prototype.query.Tuple;
import sgbd.util.global.Util;

import java.util.List;
import java.util.Objects;

public class Utils {

    public static boolean containsIgnoreCase(List<String> list, String searchedElement) {
        for (String element : list) {
            if (element.equalsIgnoreCase(searchedElement)) {
                return true;
            }
        }
        return false;
    }

    public static ColumnDataType getType(Tuple tuple, String sourceName, String columnName){

        return switch (Util.typeOfColumn(tuple.getContent(sourceName).getMetadata(columnName))){

            case "int" ->  ColumnDataType.INTEGER;
            case "long" ->  ColumnDataType.LONG;
            case "float" -> ColumnDataType.FLOAT;
            case "double" -> ColumnDataType.DOUBLE;
            case "boolean" -> ColumnDataType.BOOLEAN;
            case "string" -> ColumnDataType.STRING;
            default -> ColumnDataType.NONE;

        };

    }

    public static boolean compareNumbers(Number x, Number y){

        if(x == null || y == null) return false;

        return Objects.equals(x.doubleValue(), y.doubleValue());

    }

    public static boolean startsWithIgnoreCase(String word, String prefix){
        return word.toLowerCase().startsWith(prefix.toLowerCase());

    }

    public static boolean startsWithIgnoreCase(String word, List<String> prefixes){

        return prefixes.stream().anyMatch(prefix -> startsWithIgnoreCase(word, prefix));

    }

    public static int getLastIndexOfPrefix(String prefix){
        return prefix.length();
    }

    public static String getStartPrefixIgnoreCase(String word, List<String> prefixes){

        return prefixes.stream().filter(prefix -> startsWithIgnoreCase(word, prefix)).findFirst().orElse(null);

    }

    public static String replaceIfStartsWithIgnoreCase(String word, List<String> out, String in){

        String formatted = word;

        if(word == null || in == null || out == null) return word;

        for(String each : out)
            if(startsWithIgnoreCase(word, each)){

                formatted = word.substring(each.length());
                formatted = in+formatted;

                return formatted;

            }

        return formatted;

    }

    public static boolean anyListElementStartsAndEndsWith(List<String> list, String testStart, String testEnd){

        return list.stream().anyMatch(x -> x.startsWith(testStart) && x.endsWith(testEnd));

    }
    
}
