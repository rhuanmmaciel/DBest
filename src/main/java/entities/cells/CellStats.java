package entities.cells;

import engine.info.Parameters;
import ibd.query.QueryStats;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public record CellStats(
        long PK_SEARCH,
        long SORT_TUPLES,
        long COMPARE_FILTER,
        long RECORDS_READ,
        long NEXT_CALLS,
        long MEMORY_USED,
        
//        long COMPARE_DISTINCT_TUPLE,
//        long LOOK_UP_LOWERBOUND,
//        long LOOK_UP_UPPERBOUND,
//        long IO_SEEK_WRITE_TIME,
//        long IO_WRITE_TIME,
//        long IO_SEEK_READ_TIME,
//        long IO_READ_TIME,
//        long IO_SYNC_TIME,
//        long IO_TOTAL_TIME,
        long BLOCKS_ACCESSED,
        long BLOCKS_LOADED,
        long BLOCKS_SAVED
//        long MEMORY_ALLOCATED_BY_BLOCKS,
//        long MEMORY_ALLOCATED_BY_DIRECT_BLOCKS,
//        long MEMORY_ALLOCATED_BY_INDIRECT_BLOCKS,
//        long MEMORY_ALLOCATED_BY_RECORDS,
//        long MEMORY_ALLOCATED_BY_COMMITTABLE_BLOCKS,
//        long MEMORY_ALLOCATED_BY_BYTE_ARRAY

) {

    public CellStats getDiff(CellStats cellStats2) {
        return new CellStats(
                PK_SEARCH - cellStats2.PK_SEARCH,
                SORT_TUPLES - cellStats2.SORT_TUPLES,
                COMPARE_FILTER - cellStats2.COMPARE_FILTER,
                RECORDS_READ - cellStats2.RECORDS_READ,
                NEXT_CALLS - cellStats2.NEXT_CALLS,
                MEMORY_USED - cellStats2.MEMORY_USED,
//                COMPARE_DISTINCT_TUPLE - cellStats2.COMPARE_DISTINCT_TUPLE,
//                LOOK_UP_LOWERBOUND - cellStats2.LOOK_UP_LOWERBOUND,
//                LOOK_UP_UPPERBOUND - cellStats2.LOOK_UP_UPPERBOUND,
//                IO_SEEK_WRITE_TIME - cellStats2.IO_SEEK_WRITE_TIME,
//                IO_WRITE_TIME - cellStats2.IO_WRITE_TIME,
//                IO_SEEK_READ_TIME - cellStats2.IO_SEEK_READ_TIME,
//                IO_READ_TIME - cellStats2.IO_READ_TIME,
//                IO_SYNC_TIME - cellStats2.IO_SYNC_TIME,
//                IO_TOTAL_TIME - cellStats2.IO_TOTAL_TIME,
                BLOCKS_ACCESSED - cellStats2.BLOCKS_ACCESSED,
                BLOCKS_LOADED - cellStats2.BLOCKS_LOADED,
                BLOCKS_SAVED - cellStats2.BLOCKS_SAVED
//                MEMORY_ALLOCATED_BY_BLOCKS - cellStats2.MEMORY_ALLOCATED_BY_BLOCKS,
//                MEMORY_ALLOCATED_BY_DIRECT_BLOCKS - cellStats2.MEMORY_ALLOCATED_BY_DIRECT_BLOCKS,
//                MEMORY_ALLOCATED_BY_INDIRECT_BLOCKS - cellStats2.MEMORY_ALLOCATED_BY_INDIRECT_BLOCKS,
//                MEMORY_ALLOCATED_BY_RECORDS - cellStats2.MEMORY_ALLOCATED_BY_RECORDS,
//                MEMORY_ALLOCATED_BY_COMMITTABLE_BLOCKS - cellStats2.MEMORY_ALLOCATED_BY_COMMITTABLE_BLOCKS,
//                MEMORY_ALLOCATED_BY_BYTE_ARRAY - cellStats2.MEMORY_ALLOCATED_BY_BYTE_ARRAY
        );
    }

    public CellStats getSum(CellStats cellStats2){
        return new CellStats(
                PK_SEARCH + cellStats2.PK_SEARCH,
                SORT_TUPLES + cellStats2.SORT_TUPLES,
                COMPARE_FILTER + cellStats2.COMPARE_FILTER,
                RECORDS_READ + cellStats2.RECORDS_READ,
                NEXT_CALLS + cellStats2.NEXT_CALLS,
                MEMORY_USED + cellStats2.MEMORY_USED,
//                COMPARE_DISTINCT_TUPLE + cellStats2.COMPARE_DISTINCT_TUPLE,
//                LOOK_UP_LOWERBOUND + cellStats2.LOOK_UP_LOWERBOUND,
//                LOOK_UP_UPPERBOUND + cellStats2.LOOK_UP_UPPERBOUND,
//                IO_SEEK_WRITE_TIME + cellStats2.IO_SEEK_WRITE_TIME,
//                IO_WRITE_TIME + cellStats2.IO_WRITE_TIME,
//                IO_SEEK_READ_TIME + cellStats2.IO_SEEK_READ_TIME,
//                IO_READ_TIME + cellStats2.IO_READ_TIME,
//                IO_SYNC_TIME + cellStats2.IO_SYNC_TIME,
//                IO_TOTAL_TIME + cellStats2.IO_TOTAL_TIME,
                BLOCKS_ACCESSED + cellStats2.BLOCKS_ACCESSED,
                BLOCKS_LOADED + cellStats2.BLOCKS_LOADED,
                BLOCKS_SAVED + cellStats2.BLOCKS_SAVED
//                MEMORY_ALLOCATED_BY_BLOCKS + cellStats2.MEMORY_ALLOCATED_BY_BLOCKS,
//                MEMORY_ALLOCATED_BY_DIRECT_BLOCKS + cellStats2.MEMORY_ALLOCATED_BY_DIRECT_BLOCKS,
//                MEMORY_ALLOCATED_BY_INDIRECT_BLOCKS + cellStats2.MEMORY_ALLOCATED_BY_INDIRECT_BLOCKS,
//                MEMORY_ALLOCATED_BY_RECORDS + cellStats2.MEMORY_ALLOCATED_BY_RECORDS,
//                MEMORY_ALLOCATED_BY_COMMITTABLE_BLOCKS + cellStats2.MEMORY_ALLOCATED_BY_COMMITTABLE_BLOCKS,
//                MEMORY_ALLOCATED_BY_BYTE_ARRAY + cellStats2.MEMORY_ALLOCATED_BY_BYTE_ARRAY
        );
    }

    public static CellStats getTotalCurrentStats(){
        return new CellStats(
                QueryStats.PK_SEARCH,
                QueryStats.SORT_TUPLES,
                QueryStats.COMPARE_FILTER,
                Parameters.RECORDS_READ,
                QueryStats.NEXT_CALLS,
                QueryStats.MEMORY_USED,
//                Query.COMPARE_DISTINCT_TUPLE,
//                0,
//                100,
//                Parameters.IO_SEEK_WRITE_TIME,
//                Parameters.IO_WRITE_TIME,
//                Parameters.IO_SEEK_READ_TIME,
//                Parameters.IO_READ_TIME,
//                Parameters.IO_SYNC_TIME,
//                Parameters.IO_SYNC_TIME + Parameters.IO_SEEK_WRITE_TIME + Parameters.IO_READ_TIME + Parameters.IO_SEEK_READ_TIME + Parameters.IO_WRITE_TIME,
                Parameters.BLOCKS_ACCESSED,
                Parameters.BLOCKS_LOADED,
                Parameters.BLOCKS_SAVED
//                Parameters.MEMORY_ALLOCATED_BY_BLOCKS,
//                Parameters.MEMORY_ALLOCATED_BY_DIRECT_BLOCKS,
//                Parameters.MEMORY_ALLOCATED_BY_INDIRECT_BLOCKS,
//                Parameters.MEMORY_ALLOCATED_BY_RECORDS,
//                Parameters.MEMORY_ALLOCATED_BY_COMMITTABLE_BLOCKS,
//                Parameters.MEMORY_ALLOCATED_BY_BYTE_ARRAY
        );
    }
    
    public static CellStats getEmptyStats(){
        return new CellStats(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
        );
    }
    
    public static void reset(){
                QueryStats.PK_SEARCH = 0;
                QueryStats.SORT_TUPLES = 0;
                QueryStats.COMPARE_FILTER = 0;
                Parameters.RECORDS_READ = 0;
                QueryStats.NEXT_CALLS = 0;
                QueryStats.MEMORY_USED = 0;
//                Query.COMPARE_DISTINCT_TUPLE = 0;
//                Parameters.IO_SEEK_WRITE_TIME = 0;
//                Parameters.IO_WRITE_TIME = 0;
//                Parameters.IO_SEEK_READ_TIME = 0;
//                Parameters.IO_READ_TIME = 0;
//                Parameters.IO_SYNC_TIME = 0;
                Parameters.BLOCKS_ACCESSED = 0;
                Parameters.BLOCKS_LOADED = 0;
                Parameters.BLOCKS_SAVED = 0;
//                Parameters.MEMORY_ALLOCATED_BY_BLOCKS = 0;
//                Parameters.MEMORY_ALLOCATED_BY_DIRECT_BLOCKS = 0;
//                Parameters.MEMORY_ALLOCATED_BY_INDIRECT_BLOCKS = 0;
//                Parameters.MEMORY_ALLOCATED_BY_RECORDS = 0;
//                Parameters.MEMORY_ALLOCATED_BY_COMMITTABLE_BLOCKS = 0;
//                Parameters.MEMORY_ALLOCATED_BY_BYTE_ARRAY = 0;
    }

    public Map<String, Long> toMap() {
        Map<String, Long> result = new TreeMap<>();

        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                result.put(field.getName(), (Long) field.get(this));
            } catch (IllegalAccessException ignored) {

            }
        }

        return result;
    }

}

