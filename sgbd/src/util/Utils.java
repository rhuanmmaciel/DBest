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
    
}
