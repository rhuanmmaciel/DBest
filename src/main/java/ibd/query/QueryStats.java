package ibd.query;

public class QueryStats {

    public static long PK_SEARCH = 0;
    public static long NEXT_CALLS = 0;
    public static long MEMORY_USED = 0;

    public static long COMPARE_FILTER = 0;
    public static long COMPARE_DISTINCT_TUPLE = 0;

    public static long SORT_TUPLES = 0;

    public void clear() {
        PK_SEARCH = 0;
        NEXT_CALLS = 0;
        MEMORY_USED = 0;

        COMPARE_FILTER = 0;
        COMPARE_DISTINCT_TUPLE = 0;

        SORT_TUPLES = 0;

    }

}
