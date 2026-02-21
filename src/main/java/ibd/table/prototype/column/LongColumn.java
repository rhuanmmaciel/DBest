package ibd.table.prototype.column;

import ibd.table.prototype.metadata.Metadata;

public class LongColumn extends Column{
    
    public LongColumn(String name,int size,short flags) {
        super(name, size, flags);
        type = Column.LONG_TYPE;
    }
    public LongColumn(String name, boolean primaryKey) {
        this(name, (short) 8, primaryKey? Metadata.PRIMARY_KEY:Metadata.NONE);
    }
    public LongColumn(String name){
        this(name,false);
    }
    
}
