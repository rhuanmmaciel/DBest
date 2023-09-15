package enums;

import entities.cells.CSVTableCell;
import entities.cells.FYITableCell;
import entities.cells.TableCell;

public enum TableType {

    MEMORY_TABLE ("memory"),
    CSV_TABLE    ("csv"),
    FYI_TABLE    ("fyi");

    public final String id;

    TableType(String id) {
        this.id = id;
    }

    public static TableType fromTableCell(TableCell tableCell) {
        if (tableCell instanceof FYITableCell) return FYI_TABLE;

        if (tableCell instanceof CSVTableCell) return CSV_TABLE;

        return MEMORY_TABLE;
    }
}
