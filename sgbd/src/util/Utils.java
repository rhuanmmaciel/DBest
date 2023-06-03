package util;

import java.util.List;

public class Utils {

    public static boolean containsIgnoreCase(List<String> list, String searchElement) {
        for (String element : list) {
            if (element.equalsIgnoreCase(searchElement)) {
                return true;
            }
        }
        return false;
    }
    
}
