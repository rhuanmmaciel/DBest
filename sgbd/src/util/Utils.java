package util;

import java.util.List;

public class Utils {

    public static boolean containsIgnoreCase(List<String> list, String searchedElement) {
        for (String element : list) {
            if (element.equalsIgnoreCase(searchedElement)) {
                return true;
            }
        }
        return false;
    }

    public static boolean listElementStartsWith(List<String> list, String test){

        return list.stream().anyMatch(x -> x.startsWith(test));

    }

    public static boolean listElementEndsWith(List<String> list, String test){

        return list.stream().anyMatch(x -> x.endsWith(test));

    }

    public static boolean anyListElementStartsAndEndsWith(List<String> list, String testStart, String testEnd){

        return list.stream().anyMatch(x -> x.startsWith(testStart) && x.endsWith(testEnd));

    }
    
}
