package enums;

import entities.cells.*;
import gui.frames.ErrorFrame;

public enum CellType {

    MEMORY_TABLE ("memory"),
    CSV_TABLE    ("csv"),
    FYI_TABLE    ("fyi"),
    JDBC_TABLE   ("jdbc"),
    XML_TABLE    ("xml"),
    OPERATION    ("operation");

    public final String id;

    CellType(String id) {
        this.id = id;
    }

    public static CellType fromTableCell(Cell tableCell) {

        if(tableCell instanceof FYITableCell) return FYI_TABLE;
        if(tableCell instanceof CSVTableCell) return CSV_TABLE;
        if(tableCell instanceof JDBCTableCell) return JDBC_TABLE;
        if(tableCell instanceof MemoryTableCell) return MEMORY_TABLE;
        if(tableCell instanceof XMLTableCell) return XML_TABLE;
        return OPERATION;

    }
}
