package util;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {

    public static boolean containsIgnoreCase(List<String> list, String searchedElement) {
        for (String element : list) {
            if (element.equalsIgnoreCase(searchedElement)) {
                return true;
            }
        }
        return false;
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
