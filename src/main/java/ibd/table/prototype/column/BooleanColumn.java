package ibd.table.prototype.column;

import ibd.table.prototype.metadata.Metadata;

public class BooleanColumn extends Column {

    public BooleanColumn(String name, int size, short flags) {
        super(name, size, flags);
        type = Column.BOOLEAN_TYPE;
    }

    public BooleanColumn(String name) {
        this(name, (short) 1, Metadata.BOOLEAN);
    }

}
