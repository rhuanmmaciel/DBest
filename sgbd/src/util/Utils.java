package util;

import enums.ColumnDataType;
import sgbd.query.Tuple;
import sgbd.util.statitcs.Util;

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

    public static ColumnDataType getType(Tuple t1, String source1, String item1){

        switch (Util.typeOfColumn(t1.getContent(source1).getMeta(item1))){

            case "int" -> {
                return ColumnDataType.INTEGER;
            }

            case "float" -> {
                return ColumnDataType.FLOAT;
            }

            case "boolean" ->{
                return ColumnDataType.BOOLEAN;
            }

            case "string" -> {
                return ColumnDataType.STRING;
            }

            default -> {
                return ColumnDataType.NONE;
            }

        }

    }

    public static boolean compareNumbers(Number x, Number y){

        return Objects.equals(x.doubleValue(), y.doubleValue());

    }

    public static boolean startsWithIgnoreCase(String word, String prefix){

        return word.toLowerCase().startsWith(prefix.toLowerCase());

    }

    public static boolean startsWithIgnoreCase(String word, List<String> prefixes){

        return prefixes.stream().anyMatch(prefix -> startsWithIgnoreCase(word, prefix));

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
