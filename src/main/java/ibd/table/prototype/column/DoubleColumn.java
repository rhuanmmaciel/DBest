package ibd.table.prototype.column;

import ibd.table.prototype.metadata.Metadata;

public class DoubleColumn extends Column{
    
    public DoubleColumn(String name,int size,short flags) {
        super(name, size, flags);
        type = Column.DOUBLE_TYPE;
    }
    
    public DoubleColumn(String name) {
        this(name, (short) 8, Metadata.FLOATING_POINT);
    }
    
}
