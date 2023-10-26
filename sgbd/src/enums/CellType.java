package enums;

import entities.cells.*;

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

        return switch (tableCell){

          case FYITableCell ignored -> FYI_TABLE;

          case CSVTableCell ignored -> CSV_TABLE;

          case OperationCell ignored -> OPERATION;

          case MemoryTableCell ignored -> MEMORY_TABLE;

        };

    }
}
