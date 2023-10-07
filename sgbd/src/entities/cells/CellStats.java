package entities.cells;

import engine.info.Parameters;
import sgbd.info.Query;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public record CellStats(
        long PK_SEARCH,
        long SORT_TUPLES,
        long COMPARE_FILTER,
        long COMPARE_JOIN,
        long COMPARE_DISTINCT_TUPLE,
        long IO_SEEK_WRITE_TIME,
        long IO_WRITE_TIME,
        long IO_SEEK_READ_TIME,
        long IO_READ_TIME,
        long IO_SYNC_TIME,
        long IO_TOTAL_TIME,
        long BLOCK_LOADED,
        long BLOCK_SAVED,
        long MEMORY_ALLOCATED_BY_BLOCKS,
        long MEMORY_ALLOCATED_BY_DIRECT_BLOCKS,
        long MEMORY_ALLOCATED_BY_INDIRECT_BLOCKS,
        long MEMORY_ALLOCATED_BY_RECORDS,
        long MEMORY_ALLOCATED_BY_COMMITTABLE_BLOCKS,
        long MEMORY_ALLOCATED_BY_BYTE_ARRAY
) {

    public CellStats getDiff(CellStats cellStats2) {
        return new CellStats(
                PK_SEARCH - cellStats2.PK_SEARCH,
                SORT_TUPLES - cellStats2.SORT_TUPLES,
                COMPARE_FILTER - cellStats2.COMPARE_FILTER,
                COMPARE_JOIN - cellStats2.COMPARE_JOIN,
                COMPARE_DISTINCT_TUPLE - cellStats2.COMPARE_DISTINCT_TUPLE,
                IO_SEEK_WRITE_TIME - cellStats2.IO_SEEK_WRITE_TIME,
                IO_WRITE_TIME - cellStats2.IO_WRITE_TIME,
                IO_SEEK_READ_TIME - cellStats2.IO_SEEK_READ_TIME,
                IO_READ_TIME - cellStats2.IO_READ_TIME,
                IO_SYNC_TIME - cellStats2.IO_SYNC_TIME,
                IO_TOTAL_TIME - cellStats2.IO_TOTAL_TIME,
                BLOCK_LOADED - cellStats2.BLOCK_LOADED,
                BLOCK_SAVED - cellStats2.BLOCK_SAVED,
                MEMORY_ALLOCATED_BY_BLOCKS - cellStats2.MEMORY_ALLOCATED_BY_BLOCKS,
                MEMORY_ALLOCATED_BY_DIRECT_BLOCKS - cellStats2.MEMORY_ALLOCATED_BY_DIRECT_BLOCKS,
                MEMORY_ALLOCATED_BY_INDIRECT_BLOCKS - cellStats2.MEMORY_ALLOCATED_BY_INDIRECT_BLOCKS,
                MEMORY_ALLOCATED_BY_RECORDS - cellStats2.MEMORY_ALLOCATED_BY_RECORDS,
                MEMORY_ALLOCATED_BY_COMMITTABLE_BLOCKS - cellStats2.MEMORY_ALLOCATED_BY_COMMITTABLE_BLOCKS,
                MEMORY_ALLOCATED_BY_BYTE_ARRAY - cellStats2.MEMORY_ALLOCATED_BY_BYTE_ARRAY
        );
    }

    public static CellStats getTotalCurrentStats(){
        return new CellStats(
                Query.PK_SEARCH,
                Query.SORT_TUPLES,
                Query.COMPARE_FILTER,
                Query.COMPARE_JOIN,
                Query.COMPARE_DISTINCT_TUPLE,
                Parameters.IO_SEEK_WRITE_TIME,
                Parameters.IO_WRITE_TIME,
                Parameters.IO_SEEK_READ_TIME,
                Parameters.IO_READ_TIME,
                Parameters.IO_SYNC_TIME,
                Parameters.IO_SYNC_TIME + Parameters.IO_SEEK_WRITE_TIME + Parameters.IO_READ_TIME + Parameters.IO_SEEK_READ_TIME + Parameters.IO_WRITE_TIME,
                Parameters.BLOCK_LOADED,
                Parameters.BLOCK_SAVED,
                Parameters.MEMORY_ALLOCATED_BY_BLOCKS,
                Parameters.MEMORY_ALLOCATED_BY_DIRECT_BLOCKS,
                Parameters.MEMORY_ALLOCATED_BY_INDIRECT_BLOCKS,
                Parameters.MEMORY_ALLOCATED_BY_RECORDS,
                Parameters.MEMORY_ALLOCATED_BY_COMMITTABLE_BLOCKS,
                Parameters.MEMORY_ALLOCATED_BY_BYTE_ARRAY
        );
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

