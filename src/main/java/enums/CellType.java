package enums;

import entities.cells.*;
import gui.frames.ErrorFrame;

public enum CellType {

    MEMORY_TABLE ("memory"),
    CSV_TABLE    ("csv"),
    FYI_TABLE    ("fyi"),
    OPERATION    ("operation");

    public final String id;

    CellType(String id) {
        this.id = id;
    }

    public static CellType fromTableCell(Cell tableCell) {

        if(tableCell instanceof FYITableCell) return FYI_TABLE;
        if(tableCell instanceof CSVTableCell) return CSV_TABLE;
        if(tableCell instanceof MemoryTableCell) return MEMORY_TABLE;
        return OPERATION;

    }
}
