package enums;

import entities.cells.CsvTableCell;
import entities.cells.FyiTableCell;
import entities.cells.TableCell;

public enum TableType {

    MEMORY_TABLE("memory"),
    CSV_TABLE("csv"),
    FYI_TABLE("fyi");

    TableType(String id){
        this.ID = id;
    }

    public static TableType getType(TableCell tableCell){

        if(tableCell instanceof FyiTableCell) return FYI_TABLE;

        if(tableCell instanceof CsvTableCell) return CSV_TABLE;

        return MEMORY_TABLE;

    }

    public final String ID;

}
